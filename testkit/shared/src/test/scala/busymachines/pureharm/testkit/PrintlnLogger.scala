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

object PrintlnLogger extends org.typelevel.log4cats.StructuredLogger[IO] {

  private def p(s: String)(ctx: Map[String, String] = Map.empty): IO[Unit] =
    if (ctx.isEmpty)
      IO(println(s"-----> $s"))
    else IO(println(s"-----> mdc=${ctx.mkString("[", ", ", "]")} -----> $s"))

  def trace(ctx: Map[String, String])(msg: => String): IO[Unit] = p(msg)(ctx)
  def trace(ctx: Map[String, String], t:   Throwable)(msg: => String): IO[Unit] = p(msg)(ctx)
  def debug(ctx: Map[String, String])(msg: => String): IO[Unit] = p(msg)(ctx)
  def debug(ctx: Map[String, String], t:   Throwable)(msg: => String): IO[Unit] = p(msg)(ctx)
  def info(ctx:  Map[String, String])(msg: => String): IO[Unit] = p(msg)(ctx)
  def info(ctx:  Map[String, String], t:   Throwable)(msg: => String): IO[Unit] = p(msg)(ctx)
  def warn(ctx:  Map[String, String])(msg: => String): IO[Unit] = p(msg)(ctx)
  def warn(ctx:  Map[String, String], t:   Throwable)(msg: => String): IO[Unit] = p(msg)(ctx)
  def error(ctx: Map[String, String])(msg: => String): IO[Unit] = p(msg)(ctx)
  def error(ctx: Map[String, String], t:   Throwable)(msg: => String): IO[Unit] = p(msg)(ctx)

  def debug(t: Throwable)(message: => String): IO[Unit] = p(message)()
  def error(t: Throwable)(message: => String): IO[Unit] = p(message)()
  def info(t:  Throwable)(message: => String): IO[Unit] = p(message)()
  def trace(t: Throwable)(message: => String): IO[Unit] = p(message)()
  def warn(t:  Throwable)(message: => String): IO[Unit] = p(message)()

  // Members declared in org.typelevel.log4cats.MessageLogger
  def debug(message: => String): IO[Unit] = p(message)()
  def error(message: => String): IO[Unit] = p(message)()
  def info(message:  => String): IO[Unit] = p(message)()
  def trace(message: => String): IO[Unit] = p(message)()
  def warn(message:  => String): IO[Unit] = p(message)()

}
