
# IdDog Android

Android App with two screens, one showing a Login screen.
Another showing a list of dog images based on the chosen dog category.

### Architectural approach

MVVM with Kotlin Coroutines for asynchronous computation.
Koin for dependency injection
Glide for image loading/caching
Retrofit2 with OkHttp for remote API http connections

Also, this project contains Interactors/Commands, Repositories, DataSources, Architecture Components, etc.

### A complete list of external dependencies
[dependencies](https://github.com/akmerejf/iddog/blob/master/app/build.gradle)

## Getting Started

Clone this repo to your local machine and open it in Android Studio.
Wait until gradle sync completes


### Prerequisites

What things you need to install the software and how to install them

```
Kotlin 1.3.72
```

### Installing

> open project in Android Studio

### on Mac and Linux
> open terminal at project directory
``` 
./gradlew build 
```

> run the project

> navigate, test and *enjoy*


## Running the tests


> run the unit tests (on Mac)

```
./gradlew test
```

*or*

> Navigate to > /app/src/test/java

then right click folder and select ```Run Tests in Java```

> run the instrumented tests (on Mac)

```
./gradlew connectedAndroidTest
```

*or*

> Navigate to > /app/src/androidTest/java

then right click folder and select ```Run All Tests```

## Authors

* **Akme-re Almeida** - *Initial work* - 

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
