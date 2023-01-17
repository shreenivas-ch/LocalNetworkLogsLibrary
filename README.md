# LocalNetworkLogsLibrary
### Track all API requests.
Integration of this library into your application will be helpful to testers and QAs who don't have access to which all APIs getting called in the background. 

[![](https://jitpack.io/v/shreenivas-ch/LocalNetworkLogsLibrary.svg)](https://jitpack.io/#shreenivas-ch/LocalNetworkLogsLibrary)

<p align="center">
<img src="https://github.com/shreenivas-ch/LocalNetworkLogsLibrary/blob/020e1a65ed985644a4a6a371a5164a313c6ff20d/ss1.png" alt="alt text" width="200" hspace="40"><img src="https://github.com/shreenivas-ch/LocalNetworkLogsLibrary/blob/020e1a65ed985644a4a6a371a5164a313c6ff20d/ss2.png" alt="alt text" width="200" hspace="40">
</p>

## Min SDK Version - 19

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


