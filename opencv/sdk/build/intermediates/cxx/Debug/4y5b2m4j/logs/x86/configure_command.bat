@echo off
"C:\\Users\\Esteban Pacheco\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HD:\\Android_Studio\\SolucionarErrores\\opencv\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=26" ^
  "-DANDROID_PLATFORM=android-26" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\Esteban Pacheco\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Esteban Pacheco\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Esteban Pacheco\\AppData\\Local\\Android\\Sdk\\ndk\\27.0.12077973\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Esteban Pacheco\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=D:\\Android_Studio\\SolucionarErrores\\opencv\\sdk\\build\\intermediates\\cxx\\Debug\\4y5b2m4j\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=D:\\Android_Studio\\SolucionarErrores\\opencv\\sdk\\build\\intermediates\\cxx\\Debug\\4y5b2m4j\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BD:\\Android_Studio\\SolucionarErrores\\opencv\\sdk\\.cxx\\Debug\\4y5b2m4j\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
