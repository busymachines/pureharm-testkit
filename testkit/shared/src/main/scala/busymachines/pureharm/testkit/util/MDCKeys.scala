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

import scala.concurrent.duration.FiniteDuration
import busymachines.pureharm.testkit._

/** Common keys used in MDC contexts for loggers... allows us to be somewhat consistent
  * through the entire app especially since in logs these get sorted, so we can prefix
  * them universally
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 24 Jun 2020
  */
object MDCKeys {
  private val OutcomeK: String = "outcome"
  private val TestName: String = "testName"
  private val Duration: String = "duration"
  private val Line:     String = "line"
  private val What:     String = "what"

  private def lineNumber(i: Int):            (String, String) = Line     -> i.toString
  private def testName(s:   String):         (String, String) = TestName -> s"'$s'"
  private def duration(d:   FiniteDuration): (String, String) = Duration -> d.toString
  private def outcome(o:    TestOutcome):    (String, String) = OutcomeK -> o.productPrefix
  private def exec:  (String, String) = What -> "exec"
  private def setup: (String, String) = What -> "setup"

  private def neutral(test: TestOptions) = Map(MDCKeys.testName(test.name), MDCKeys.lineNumber(test.location.line))

  def testExec(test: TestOptions): Map[String, String] =
    neutral(test).+(this.exec)

  def testExec(test: TestOptions, out: TestOutcome, duration: FiniteDuration): Map[String, String] =
    this.testExec(test) ++ Map(MDCKeys.outcome(out), MDCKeys.duration(duration))

  def testSetup(test: TestOptions): Map[String, String] =
    neutral(test).+(this.setup)

  def testSetup(test: TestOptions, duration: FiniteDuration): Map[String, String] =
    neutral(test).+(this.setup).+(this.duration(duration))

  def testSetup(test: TestOptions, o: TestOutcome, duration: FiniteDuration): Map[String, String] =
    neutral(test).+(this.setup).+(this.outcome(o)).+(this.duration(duration))
}
