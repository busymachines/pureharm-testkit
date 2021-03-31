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
import scala.concurrent.duration._

final class PureharmTestTest extends PureharmTest {
  implicit override val testLogger: TestLogger = TestLogger(PrintlnLogger)

  test("simple test") {
    testLogger.info("Executing first test!")
  }

  private val myResource =
    ResourceFixture((to: TestOptions) => Resource.liftF(testLogger.info(s"Making: $to") >> Timer[IO].sleep(10.millis)))

  myResource.test("with resource")((_: Unit) => testLogger.info("Executing test w/ resource"))

  // test("throw exception in body") {
  //   throw new RuntimeException("Comment me after running once")
  // }

  // test("invalid exception body") {
  //   423
  // }

  // val failedResource = {
  //   import busymachines.pureharm.anomaly.InvalidInputAnomaly
  //   ResourceFixture((_: TestOptions) =>
  //     Resource.eval(
  //       Timer[IO].sleep(10.millis).flatMap(_ => InvalidInputAnomaly("failed resource").raiseError[IO, Unit])
  //     )
  //   )
  // }

  // failedResource.test("failed init")(_ => testLogger.info("never gonna used a failed resource"))
}
