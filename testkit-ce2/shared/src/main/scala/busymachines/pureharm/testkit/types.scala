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

import scala.annotation.implicitNotFound

import busymachines.pureharm.effects._
import busymachines.pureharm.sprout._
import org.typelevel.log4cats._

object types {

  @implicitNotFound(
    msg = """
      |TestLogger should be available once you implement the abstract member in either:
      |  - busymachines.pureharm.testkit.FixturePureharmTest
      |  - busymachines.pureharm.testkit.PureharmTest
      |
      |The purpose of TestLogger is to log everything related to test-setup/
      |tear-down to enrich whatever munit tells you
      |"""
  )
  type TestLogger = TestLogger.Type
  object TestLogger extends SproutSub[StructuredLogger[IO]]
}
