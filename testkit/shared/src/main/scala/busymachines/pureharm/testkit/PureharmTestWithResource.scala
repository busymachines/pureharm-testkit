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

import cats.effect._

@scala.deprecated(
  "Use PureharmTest directly with the 'val myResource = ResourceFixture(testOptions => ???: Resource[IO, T])' and then: 'myResource.test{res => testBody}' style instead",
  "0.2.0",
)
abstract class PureharmTestWithResource extends PureharmTest {
  final type MetaData = TestOptions

  type ResourceType

  /** Instead of the "before and after" stuff simply init, and close
    * everything in this Resource...
    *
    * @param meta
    *  Use this information to create table names or something
    */
  def resource(testOptions: TestOptions): Resource[IO, ResourceType]

  private val resourceV = ResourceFixture(testOptions => resource(testOptions))

  protected def test(
    testName: String
  )(
    testFun:  ResourceType => IO[Unit]
  )(implicit
    position: Location
  ): Unit = resourceV.test(testName)(testFun)
}
