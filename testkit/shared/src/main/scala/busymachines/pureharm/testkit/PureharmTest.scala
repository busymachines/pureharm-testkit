/*
 * Copyright 2019 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package busymachines.pureharm.testkit

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._
import busymachines.pureharm.testkit.util._
import munit.FunSuite
import scala.concurrent.duration._

/** Base class that is recommended to be extended in a "testkit" module in your own application, and added the
  * corresponding flavor.
  *
  * All tests are in IO[Unit], and no special syntax is offered to return any other types of values.
  *
  * @author
  *   Lorand Szakacs, https://github.com/lorandszakacs
  * @since 24
  *   Jun 2020
  */
abstract class PureharmTest extends FunSuite with PureharmAssertions with PureharmTestRuntimeLazyConversions {

  implicit def testLogger: TestLogger

  /** @see
    *   PureharmTestRuntimeLazyConversions for details as to why this is a def
    */
  implicit def runtime: PureharmTestRuntime = PureharmTestRuntime

  override def munitTimeout: Duration = 60.seconds

  final override def test(name: String)(body: => Any)(implicit loc: Location): Unit = {
    this.test(new TestOptions(name))(body)
  }

  final override def test(options: TestOptions)(body: => Any)(implicit loc: Location): Unit = {
    val io: IO[Unit] = testIO(options)(body)
    super.test(options)(io)
  }

  override def munitValueTransforms: List[ValueTransform] =
    super.munitValueTransforms ++ List(pureharmMunitIOTransform)

  private val pureharmMunitIOTransform: ValueTransform =
    new ValueTransform(
      "IO",
      { case e: IO[_] => e.unsafeToFuture() },
    )

  /** This method ensures that all tests are properly defined in IO, and no willy nilly exceptions are thrown,
    */
  private def testIO(options: TestOptions)(body: => Any): IO[Unit] = {
    val test = IO
      .delay(body)
      .adaptError { case e =>
        TestInitCatastrophe(
          s"""|-
              |Test body threw exception.
              |
              |location:
              |  ${options.location}
              |
              |exception:
              |${e.toString}
              |
              |---
              |Please write a pure test in IO that does not throw exceptions.
              |-
              |-
              |""".stripMargin,
          options,
          Option(e),
        )
      }
      .flatMap {
        case value:             IO[_]                       => value.void
        case syncIOValue:       SyncIO[_]                   => syncIOValue.toIO.void
        //this happens when we use the resource fixture, FunFixture.async, delegates to the
        //overriden methods defined here.
        case fixtureTestResult: scala.concurrent.Promise[_] =>
          IO.fromFuture(IO(fixtureTestResult.future)).void
        case any:               Any                         =>
          IO.raiseError[Unit](
            TestInitCatastrophe(
              s"""|-
                  |Unsupported return type ${any.getClass.getName()}.
                  |
                  |location:
                  |  ${options.location}
                  |
                  |Please just write a test in IO[Unit].
                  |
                  |-
                  |""".stripMargin,
              options,
            )
          )
      }

    for {
      _    <- testLogger.info(MDCKeys.testExec(options))("starting test")
      tatt <- test.timedAttempt()
      (duration, attempt) = tatt
      _ <- attempt match {
        case Left(e)  =>
          testLogger.error(MDCKeys.testExec(options, TestOutcome.Failed, duration))(
            s"failed test. Reason:\n ${e.toString()}"
          )
        case Right(_) =>
          testLogger.info(MDCKeys.testExec(options, TestOutcome.Succeeded, duration))(s"successful test")
      }
      _ <- attempt.liftTo[IO]
    } yield ()
  }

  /** */
  object ResourceFixture {

    import scala.concurrent.Promise

    def apply[T](
      resource: TestOptions => Resource[IO, T]
    ): SyncIO[FunFixture[T]] = SyncIO[FunFixture[T]] {
      //this promise holds the resource cleanup IO
      val promise = Promise[IO[Unit]]()

      FunFixture.async(
        setup    = { testOptions =>
          val setupIO: IO[T] =
            for {
              allocated <- resource(testOptions).allocated.timedAttempt().flatMap { case (duration, att) =>
                att match {
                  case Left(e)  =>
                    testLogger.error(MDCKeys.testSetup(testOptions, TestOutcome.InitError, duration))("init: failed") >>
                      TestInitCatastrophe(
                        s"Failed to acquire resource for testName=${testOptions.name}. Reason:\n${e.toString}",
                        testOptions,
                        Option(e),
                      ).raiseError[IO, (T, IO[Unit])]
                  case Right(v) =>
                    testLogger
                      .info(MDCKeys.testSetup(testOptions, duration))("init: success")
                      .as(v: (T, IO[Unit]))
                }
              }
              (resourceFixture, release) = allocated
              _         <- IO(promise.success(release))
            } yield resourceFixture: T

          setupIO.unsafeToFuture()
        },
        teardown = { (_: T) => IO.fromFuture(IO(promise.future)).flatten.unsafeToFuture() },
      )
    }
  }

  implicit class PureharmSyncIOOps[T](private val fixture: SyncIO[FunFixture[T]]) {

    def test(name:  String)(
      body:         T => IO[Unit]
    )(implicit loc: Location): Unit = {
      val options = TestOptions(name)
      runFixture().test(options)(body)
    }

    def test(options: TestOptions)(
      body:           T => IO[Unit]
    )(implicit loc:   Location): Unit = {
      runFixture().test(options)(body)
    }

    private def runFixture(): FunFixture[T] = fixture.unsafeRunSync()
  }
}
