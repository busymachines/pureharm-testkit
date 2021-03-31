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

import busymachines.pureharm.anomaly._
import munit.TestOptions

final case class TestInitCatastrophe(
  override val message:  String,
  options:  TestOptions,
  override val causedBy: Option[Throwable] = Option.empty,
) extends Catastrophe(message, causedBy) {

  override def parameters: Anomaly.Parameters = super.parameters ++ Anomaly.Parameters(
    "location" -> options.location.toString,
    "tags"     -> options.tags.toSeq.map(_.toString()),
    "testName" -> options.name,
  )
}
