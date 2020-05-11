1) Installing the FlatBuffers Compiler
======================================
To use FlatBuffers, you'll first need the executable so you can compile the schema files into Java.

Go here and download the latest release:
https://github.com/google/flatbuffers/releases

Unzip and install flatc.exe in the $PROJECT_ROOT$/tools folder


2) Compiling the schema files into Java
=======================================
Run the batch file in the subsystems subfolder


3) Adding a Gradle plugin for FlatBuffers
=========================================

follow instructions at https://github.com/gregwhitaker/gradle-flatbuffers-plugin

Summary:
    Add the following to build.gradle
    plugins {
        id "io.netifi.flatbuffers" version "1.0.7"
    }