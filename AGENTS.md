# Codebase Guidance

This file documents key information about the project architecture, build commands, code style, and security practices.

## Project overview

A Java library for protecting sensitive data (SSNs, credit card numbers, PII) from inadvertent disclosure through logging, stack traces, and `toString()` calls. Wrapper types are safe by default — data is only revealed when explicitly requested via format-string precision. Published as two artifacts: `com.maybeitssquid:sensitive` (core framework) and `com.maybeitssquid:tin` (US Taxpayer Identification Numbers).

## Commands

```bash
./gradlew build                   # compile, test, spotless check, javadoc
./gradlew test                    # run tests (both subprojects)
./gradlew :sensitive:test         # test one subproject
./gradlew test --tests "..."      # run a single test class
./gradlew spotlessApply           # auto-format (required before commit)
./gradlew dependencyCheckAnalyze  # OWASP vulnerability scan (slow; fails at CVSS ≥ 7)
```

On Windows, use `gradlew.bat` (or `.\gradlew` in PowerShell).

Build uses Java 25 toolchain, compiles to Java 17 bytecode (`release = "17"`). CI tests on Java 17, 21, and 25.

## Versioning and Releases

Versions are derived from git tags using [`gradle-git-version`](https://github.com/palantir/gradle-git-version):

- **On a tag** (e.g., `v1.0.0`) → version = `1.0.0`
- **After a tag** → version = tag + distance + commit hash (e.g., `1.0.1-3-gABC1234` = 3 commits after v1.0.0)
- **No tags yet** → version synthesized from git history (e.g., `0.0.1-dev-88-gXYZ`)

**To create a release:**

```bash
# Ensure all commits are pushed
git push origin main

# Create and push the tag (triggers automatic version picking in build)
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0

# Build and publish
./gradlew clean build publish
```

**To delete a release tag:**

```bash
git tag -d v1.0.0              # Delete locally
git push origin :v1.0.0        # Delete from remote
```

Configuration cache is disabled (`org.gradle.configuration-cache=false`) to allow git invocation during the build.

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

## Code style

Spotless enforces Google Java Format. Run `./gradlew spotlessApply` before committing. `module-info.java` files are excluded from formatting.

## Security patches

For CVE patch management, see the `gradle-security-patch` skill. Use `/gradle-security-patch` to pin a CVE fix in the version catalog.
