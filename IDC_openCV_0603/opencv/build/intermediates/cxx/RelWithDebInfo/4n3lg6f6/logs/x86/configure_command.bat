@echo off
"C:\\Users\\ksh\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\99_study\\Android\\IDC_openCV (2)\\IDC_openCV\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\ksh\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\ksh\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\ksh\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\ksh\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\99_study\\Android\\IDC_openCV (2)\\IDC_openCV\\opencv\\build\\intermediates\\cxx\\RelWithDebInfo\\4n3lg6f6\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\99_study\\Android\\IDC_openCV (2)\\IDC_openCV\\opencv\\build\\intermediates\\cxx\\RelWithDebInfo\\4n3lg6f6\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BC:\\99_study\\Android\\IDC_openCV (2)\\IDC_openCV\\opencv\\.cxx\\RelWithDebInfo\\4n3lg6f6\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
