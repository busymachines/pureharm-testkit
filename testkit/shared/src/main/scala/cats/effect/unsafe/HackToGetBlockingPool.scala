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

package cats.effect.unsafe

import scala.concurrent.ExecutionContext

object HackToGetBlockingPool {
  /** Since the blocking pool in the cats-effect runtime is package private we get to it by declaring a package with the
    * same name, and accessing it. 100% do not recommend using this without knowing specifically what you're doing.
    *
    * In this particular case, we want our _testing_ runtime to run some unknown, potentially blocking, scala std
    * Future-y things on the blocking pool to reduce the possibility of footgunning ourselves.
    *
    * All this so we can avoid creating another pool specifically for this. Which gives us bad user ergonomics.
    */
  def blockingPoolFromIORuntime(iort: IORuntime): ExecutionContext = iort.blocking
}
