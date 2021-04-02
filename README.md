# pureharm-testkit

See [changelog](./CHANGELOG.md).

## Scala versions

- 2.13, JS + JVM
- 3.0.0-RC1, JS + JVM
- 3.0.0-RC2, JS + JVM

## modules

- `"com.busymachines" %% s"pureharm-testkit" % "0.2.0"`. Which has these as its main dependencies:
  - [munit](https://github.com/scalameta/munit/releases) `0.7.23`
  - [log4cats-core](https://github.com/typelevel/log4cats/releases) `1.2.2`
  - [pureharm-core-anomaly](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
  - [pureharm-core-sprout](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
  - [pureharm-effects-cats](https://github.com/busymachines/pureharm-effects-cats/releases) `0.2.0`

## usage

Follow the [munit](https://scalameta.org/munit/docs/getting-started.html) setup to add the appropriate test framework to your build.

```scala
testFrameworks += new TestFramework("munit.Framework")
```

Then, create your own "testkit" package. And use that everywhere.

```scala
package myapp

import busymachines.pureharm.testkit

package object test extends testkit.PureharmTestkitAliases

//-------- EOF --------

package myapp.test

/**
 * Add any custom assertions, flavors, styles, opiniated decisions
 * here, and continue using this.
 */
abstract class MyAppTest extends testkit.PureharmTestkit

//-------- EOF --------

package myapp.somemodule

import myapp.test._

final class MyTest extends MyAppTest {
  test("no resource")(IO.unit)

  private val myResource =
    ResourceFixture((to: TestOptions) => Resource.eval(testLogger.info(s"Making: $to") >> Timer[IO].sleep(10.millis)))

  myResource.test(TestOptions("with resource").tag(Slow))((_: Unit) => testLogger.info("Executing test w/ resource"))
}
```

Still under construction. See [release notes](https://github.com/busymachines/pureharm-testkit/releases) and tests for examples.

## Copyright and License

All code is available to you under the Apache 2.0 license, available
at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0) and also in
the [LICENSE](./LICENSE) file.
