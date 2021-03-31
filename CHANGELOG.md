# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# unreleased

### :warning: Breaking changes :warning:

- replace scalatest w/ [munit-cats-effect](https://github.com/typelevel/munit-cats-effect) `1.0.1`. Do not forget to add `testFrameworks += new TestFramework("munit.Framework")` to your build, as per [usage instructions](https://scalameta.org/munit/docs/getting-started.html)

### Deprecations

- `PureharmTestWithResource`. You can just use the `munit` style within the `PureharmTest`. Simply do:

```scala
import busymachines.pureharm.testkit._

final class MyTest extends PureharmTest {
  private val myResource =
    ResourceFixture((to: TestOptions) => Resource.liftF(testLogger.info(s"Making: $to") >> Timer[IO].sleep(10.millis)))

  myResource.test("with resource")((_: Unit) => testLogger.info("Executing test w/ resource"))
}
```

### New Scala versions:

- 3.0.0-RC1 for ScalaJS

# 0.1.0

Split out from [pureharm](https://github.com/busymachines/pureharm) as of version `0.0.7`.

:warning: Breaking changes :warning:

- due to future plans to support scalajs, dependency on `org.typelevel %% "log4cats-slf4j"` was removed. And `TestLogger` now has to be provided in client code.
