cmake_minimum_required(VERSION 3.4.1)
project(OpenCVProject)

# dummy target to bring libc++_shared.so into packages
add_library(opencv_jni_shared STATIC dummy.cpp)
