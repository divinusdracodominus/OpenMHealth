cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/native/include)

add_library( app-glue
             STATIC
             ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c )

find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              log-lib
              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log)

find_library(zlib ZLIB::ZLIB)
target_link_libraries( app-glue ${log-lib} ${zlib} )
