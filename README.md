# luava

An experimental Java library for embedding a Lua VM.

[![Build](https://github.com/itsallcode/luava/actions/workflows/build.yml/badge.svg)](https://github.com/itsallcode/luava/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=bugs)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=coverage)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aluava&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aluava)

This project allows executing Lua scripts from a Java application. It uses [Foreign Function & Memory API (JEP 454)](https://openjdk.org/jeps/454) for accessing the Lua C API.

## Development

### Native Interface

Build scripts generate native interface classes in `build/generated/sources/jextract` using [Jextract](https://github.com/openjdk/jextract). Scripts download and cache Jextract automatically during the build.

### Check for Dependency Updates

```sh
./gradlew dependencyUpdates
```

### Run Tests

```sh
./gradlew check
```
