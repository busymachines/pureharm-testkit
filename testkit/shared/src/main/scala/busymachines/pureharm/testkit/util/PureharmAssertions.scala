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

package busymachines.pureharm.testkit.util

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._
import scala.reflect.ClassTag
import munit._

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 24 Jun 2020
  */
trait PureharmAssertions { self: Assertions =>

  private def getOrThrow[A](a: Either[Throwable, A]): A = a.unsafeGet()

  @scala.deprecated("Use interceptFailure", "0.2.0")
  def assertFailure[E <: Throwable](a: Attempt[_])(implicit ct: ClassTag[E], loc: Location): E =
    interceptFailure[E](a)

  def interceptFailure[E <: Throwable](a: Attempt[_])(implicit ct: ClassTag[E], loc: Location): E =
    intercept[E](getOrThrow(a))

  def interceptFailureMessage[E <: Throwable](
    expectedExceptionMessage: String
  )(
    a:                        Attempt[_]
  )(implicit ct:              ClassTag[E], loc: Location): E =
    interceptMessage[E](expectedExceptionMessage)(getOrThrow(a))

  def assertSuccess[T](a: Attempt[T])(expected: T)(implicit loc: Location): Unit =
    a match {
      case Left(e)       => fail(s"excepted Right($e) but was Left($e)")
      case Right(actual) => assertEquals(obtained = actual, expected = expected)
    }

  def assertLeft[L](a: Either[L, _])(expected: L)(implicit loc: Location): Unit =
    a match {
      case Left(actual) => assertEquals(obtained = actual, expected = expected)
      case Right(r)     => fail(s"expected Left($expected) but got: Right($r)")
    }

  def assertRight[R](a: Either[_, R])(expected: R)(implicit loc: Location): Unit =
    a match {
      case Left(e)       => fail(s"excepted Right($e) but was Left($e)")
      case Right(actual) => assertEquals(obtained = actual, expected = expected)
    }

  def assertSome[T](o: Option[T])(expected: T)(implicit loc: Location): Unit =
    o match {
      case None         => fail(s"Expected Some(...), but value was None")
      case Some(actual) => assertEquals(obtained = actual, expected = expected)
    }
}
