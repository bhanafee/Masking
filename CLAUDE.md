# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./gradlew build          # compile, spotless check, test, javadoc
./gradlew test           # tests only (both subprojects)
./gradlew :sensitive:test                        # test one subproject
./gradlew :sensitive:test --tests "*.SensitiveTest.methodName"  # single test
./gradlew spotlessApply  # auto-format code (must pass before build)
./gradlew dependencyCheckAnalyze  # OWASP CVE scan (fails at CVSS >= 7)
```

The build compiles targeting Java 17 (`java-release = "17"`) using a Java 21 toolchain. CI also tests on Java 25.

## Architecture

Two JPMS modules published as separate Gradle subprojects:

- **`:sensitive`** (`com.maybeitssquid:sensitive`) — core framework: `Sensitive<T>`, `Segmented<T>`, `Renderer<T>`, `Renderers`
- **`:tin`** (`com.maybeitssquid:tin`) — US TIN implementations; depends on `:sensitive`

### Key design decisions

**`Sensitive<T>`** wraps a value in a `Supplier<T>` (not the value directly) to allow pluggable serialization behavior. The convenience `Sensitive(T value)` constructor wraps the value in `DoNotSerialize`, an inner class that stores the value in a `transient` field, causing serialization to lose the value rather than expose it. Using a lambda supplier (`Sensitive<>(() -> "secret")`) keeps the value serializable.

**`toString()` is `final`** — it calls `"%s".formatted(this)`, which invokes `formatTo()`. Subclasses cannot override it.

**`Renderer<T>`** is a functional interface `(T value, int precision) -> CharSequence`. `precision = -1` means default (show last half); `precision >= 0` is the exact count of unmasked trailing characters. Renderers must be stateless — define them as `private static final` constants, not instance fields.

**`getAltRenderer()`** is invoked when format flag `#` is set (e.g., `%#s`). The base class delegates to `getRenderer()`; override to provide a human-readable alternate form (e.g., with delimiters).

**`Segmented<T>`** extends `Sensitive<T[]>`. It stores and returns defensive copies of the array. `getValue(int index)` avoids the clone overhead when accessing a single element.

**`UsTIN`** (in `:tin`) extends `Segmented<CharSequence>` and provides two static renderers: `MASKED` (concatenates segments then masks) for `%s`, and `MASKED_DELIMITED` (joins with `-` then masks, preserving delimiters) for `%#s`.

### Security patches pattern

`gradle/libs.versions.toml` uses a `patch-*` naming convention for CVE pins in `[libraries]` and collects them in the `security-patches` bundle in `[bundles]`. The root `build.gradle` applies these as `implementation` constraints across all subprojects. New CVE patches follow this same pattern.

## Code style

Spotless enforces Google Java Format. Run `./gradlew spotlessApply` before committing. `module-info.java` files are excluded from formatting.
