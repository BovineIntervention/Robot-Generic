# Installing the FlatBuffers Compiler

To use FlatBuffers, you'll first need the executable so you can compile the schema files into Java.

Go here and download the latest release:
https://github.com/google/flatbuffers/releases

Unzip and install `flatc.exe` in the `$PROJECT_ROOT$/tools` folder


# Update `build.gradle`

## Adding FlatBuffers dependency to `build.gradle`

Add the following dependency to build.gradle to load Google's Flatbuffer code

```
dependencies {
    implementation 'com.google.flatbuffers:flatbuffers-java:1.12.0'
}
```


## Add task to compile the flatbuffer schemas to `build.gradle`

Add the following task to build.gradle

```
task compileFlatbuffers {
    String fbs_folder = "src/main/java/frc/taurus/messages/schema" 
    
    FileTree files = fileTree(fbs_folder).matching{ include "**/*.fbs"}
    
    files.each{ File file ->
            println "Compiling: $file.name"
            exec {
                executable = "tools/flatc.exe"
                args = ["--java", "--gen-mutable", "-o", "src/main/java", file.absolutePath]
            }
    }
}
```