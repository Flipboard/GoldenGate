GoldenGate
==========
GoldenGate is an Android annotation processor for generating type safe javascript bindings (Bridges). The library is very similar in usage to something like retrofit in that only an interface has to be declared and annotated (though retrofit does not do any compile time code generating). This annotated interface is at compile time used to generate an type safe wrapper around a webview for interfacing with the javascript.

Installation
------------
Replace version `x.x.x` with the latest version under releases
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    apt 'com.flipboard.goldengate:compiler:x.x.x'
    compile 'com.flipboard.goldengate:api:x.x.x'
}
```
Usage
-----
Before starting you will need to configure how object should be serialized to json. By default GoldenGate will use gson but does not package this dependency in case you would like to use something else. If you want to use gson add it as a dependency to your build.gradle.
```groovy
dependencies {
    compile 'com.google.code.gson:gson:x.x.x'
}
```
If you would like to use some other library like jackson or maybe a custom json implementation you can register a JsonSerializer with JavascriptBridge.
```groovy
JavaScriptBridge.setJsonSerializer(new JsonSerializer(){
    @Override
    <T> String toJson(T stuff) {
        // do stuff
    }

    @Override
    <T> T fromJson(String json, Class<T> type) {
        // do stuff
    }
});
```

Start by creating an interface and annotate it with `@Bridge` and also add a method which you want to call in javascript.
```java
@Bridge
interface MyJavascript {
	void alert(String message);
}
```

This will automatically generate a class called `MyJavascriptBridge` which is the implementation which wraps a webview and implements the interface we just defined. Now we have a compile time checked type safe way of opening a javascript alert.
```java
Webview webview = ...;
MyJavascript bridge = new MyJavascriptBridge(webview);
bridge.alert("Hi there!");
```

The above example is just a fire and forget example. We often want to get some result back. For this we have `Callback<T>`, because javascript runs asynchronously we can't just return this value and must therefor use a callback. The callback argument must allways be the argument specified last in the method decleration. Here is an example of using `Callback<T>`.
```java
@Bridge
interface MyJavascript {
	void calculateSomeValue(Callback<Integer> value);
}

Webview webview = ...;
MyJavascript bridge = new MyJavascriptBridge(webview);
bridge.calculateSomeValue(new Callback<Integer>() {
	@Override
	void onResult(Integer result) {
		// do something with result
	}
});
```

That's it for simple usage! There are two other annotations for customized usage, `@Method` and `@Property`. `@Method` can be used to override the name of the method on the javascript side of the bridge (The java name of the method is automatically chosen if this annotation is not supplied).
```java
@Bridge
interface MyJavascript {
	@Method("console.Log")
	void alert(String message);
}
```

The `@Property` annotation should be used for when setting or getting a property on the javascript side of things. In this case the method may only have one parameter (either a callback for result or a value which should be set). Just like with the `@Method` delaration a custom name can be chosen for the property. The default name for properties however is the name of the parameter to the method.
```java
@Bridge
interface MyJavascript {
	@Property("window.innerHeight")
	void getWindowHeight(Callback<Integer> height);
}
```

And lastly if things aren't working as expected there is a `@Debug` annotation that can be added to your `@Bridge` annotated interface which will cause the javascript being executed to be logged to the console beforehand.
```java
@Debug
@Bridge
interface MyJavascript {
	void alert(String message);
}
```

Contributing
------------
We welcome pull requests for bug fixes, new features, and improvements to GoldenGate. Contributors to the main GoldenGate repository must accept Flipboard's Apache-style [Individual Contributor License Agreement (CLA)](https://docs.google.com/forms/d/1gh9y6_i8xFn6pA15PqFeye19VqasuI9-bGp_e0owy74/viewform) before any changes can be merged.

