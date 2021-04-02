# pureharm-testkit

See [changelog](./CHANGELOG.md).

## Scala versions

- 2.13, JS + JVM
- 3.0.0-RC1, JS + JVM
- 3.0.0-RC2, JS + JVM

## modules

- `"com.busymachines" %% s"pureharm-testkit" % "0.1.0"`. Which has these as its main dependencies:
  - [munit](https://github.com/scalameta/munit/releases) `0.7.23`
  - [log4cats-core](https://github.com/typelevel/log4cats/releases) `1.2.2`
  - [pureharm-core-anomaly](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
  - [pureharm-core-sprout](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
  - [pureharm-effects-cats](https://github.com/busymachines/pureharm-effects-cats/releases) `0.2.0`

## usage

Create your own "testkit" package. And use that everywhere.

```scala
package myapp

import busymachines.pureharm.testkit

package object test extends testkit.PureharmTestkitAliases

//-------- EOF --------

package myapp.test

abstract class MyAppTest extends testkit.PureharmTestkit

//-------- EOF --------

package myapp.somemodule

import myapp.test._

final class MyTest extends MyAppTest {
  private val myResource =
    ResourceFixture((to: TestOptions) => Resource.eval(testLogger.info(s"Making: $to") >> Timer[IO].sleep(10.millis)))

  myResource.test(TestOptions("with resource").tag(Slow))((_: Unit) => testLogger.info("Executing test w/ resource"))

  test("no resource")(IO.unit)
}
```

Under construction. See [release notes](https://github.com/busymachines/pureharm-core/releases) and tests for examples.

## Copyright and License

All code is available to you under the Apache 2.0 license, available
at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0) and also in
the [LICENSE](./LICENSE) file.
