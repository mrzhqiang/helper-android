# helper
Android development helper
### Include
- [x] HttpDate 来自 okhttp-3.8.0
- [x] TimeHelper 时间格式化、判断日期、友好地显示时间
- [x] AccountHelper 随机密码
- [x] NameHelper 首字母生成颜色图标
- [x] SharedPrefsHelper 共享首选项的对象序列化存储
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
  compile 'com.github.mrzhqiang:helper:1.2'
}
~~~