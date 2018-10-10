# jme-es-bullet
A bullet Zay-ES integration for jMonkeyEngine

## How to build ##
This library uses [gradle](https://gradle.org/gradle-download/) as build system, and comes with the gradle wrapper included.
So no prior installation on your computer is required! You can just use the `gradlew` or `gradlew.bat` executables in the root of the sources.

To run a full build, use:
```
./gradlew build
```

To get an overview of all gradle tasks use:
```
./gradlew tasks
```

## How to use it ##
This library is still under active development and is not yet placed on a CDN like jcenter or mavencentral.
However, it is possible to build the library and place it in your local maven repository.

To build the library and install it in your local maven repository, run:
```
./gradlew publishtomavenlocal
```

Once installed in your local maven repository, you can include this library in your gradle project by adding this in your `build.gradle` file.
```
repositories {
    mavenLocal()
}
dependencies {
    compile "org.impstack:jme-es-bullet:+"
}
```