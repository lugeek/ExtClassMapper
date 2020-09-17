# ExtClassMapper

Annotate class with `group` and `target`, generate the index `Map<String, Map<String,Class<?>>` in building progress , then the class can indexed by `group` and `target`.

[![Maven Central](https://img.shields.io/maven-central/v/com.lugeek/extclassmapper.svg)](https://mvnrepository.com/artifact/com.lugeek/extclassmapper)

Configuration
--------
1. dependencies  
```groovy
dependencies {
  implementation 'com.lugeek:extclassmapper:{latest-version}'
  annotationProcessor 'com.lugeek:extclassmapper-compiler:{latest-version}'
}
```

2. Init  
```java
  ECMapper.init(this);
```

3. Usage  
```groovy
defaultConfig {
    ...
    javaCompileOptions {
        annotationProcessorOptions {
            arguments = [moduleName: project.getName()]
        }
    }
}
```  
* ☝︎ Specify `moduleName` in `build.gradle` file to resolve multi module conflict while generate file in building progress.  

```java
@ExtClassMapper(value = {"test","test2"}, group = "testGroup")
public class Test {
}
```  

* annotate the `target` with value for class
* annotate the `group` with group for class
* duplicated target for same group is not allowed
* default group name is `default` if not assigned

```java
// get all targets with classes in group
ECMapper.getsInstance().getGroup(group);
// get class for target in 'default' group
ECMapper.getsInstance().getClz(target);
// get class in specific group and target
ECMapper.getsInstance().getClz(group, target);
```