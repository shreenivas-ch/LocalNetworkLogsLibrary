# LocalNetworkLogsLibrary

[![](https://jitpack.io/v/shreenivas-ch/LocalNetworkLogsLibrary.svg)](https://jitpack.io/#shreenivas-ch/LocalNetworkLogsLibrary)

## Min SDK Version - 19

LocalNetworkLogsLibrary - Track all API requests using retrogfit.

## Add library to your project

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```gradle
dependencies {
	implementation 'com.github.shreenivas-ch:LocalNetworkLogsLibrary:1.0.1'
}
```

## How to use library

Add below code inside your Application Class's onCreate method

```kotlin
LocalNetworkLogsManager.getInstance().initiate(this)
```

Add below code to your retrofit client
```kotlin
var httpLoggingInterceptor = LocalNetworkLogsManager.getInstance().getHttpLoggingInterceptor(true)
httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
clientBuilder.addNetworkInterceptor(httpLoggingInterceptor)
```


