# helper
Android development helper
### Include
- [x] HttpDate 来自 okhttp-3.8.0
- [x] TimeHelper 时间格式化、判断日期、友好地显示时间
### Dependencies？
Root build.gradle
~~~
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
~~~
Application build.gradle
~~~
dependencies {
  ...
  compile 'com.github.mrzhqiang:helper:1.1'
}
~~~