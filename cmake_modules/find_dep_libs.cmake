IF(APPLE)
   FIND_PROGRAM(OTOOL otool /usr/bin) 

   MACRO(find_dep_libs IN_LIBS OUT_LIBS)
      FOREACH(DYLIB ${IN_LIBS})
         #message("find depedencies for ${DYLIB}")
         EXECUTE_PROCESS(
            COMMAND ${OTOOL} -L ${DYLIB} 
            COMMAND perl -nle "print \$1 if /(lib[^\\/]+\\.dylib)[^:]/"
            COMMAND awk "{print \$1}"
            COMMAND tr "\n" ";"
            OUTPUT_VARIABLE libs
            OUTPUT_STRIP_TRAILING_WHITESPACE
         )
         #message("libs: ${libs}")
         FOREACH(lib ${libs})
            STRING(REGEX MATCH "^lib(.*)\\.dylib" isDylib ${lib})
            IF(isDylib)
               STRING(REGEX REPLACE "^lib(.*)\\.dylib" "\\1" lib_name ${lib})
               FIND_LIBRARY(${lib_name} "${lib_name}")
               SET(ABS_DYLIB ${${lib_name}})
            ELSE()
               SET(ABS_DYLIB ${lib})
            ENDIF()
            #MESSAGE("add ${ABS_DYLIB}")
            LIST(APPEND ${OUT_LIBS} ${ABS_DYLIB})
         ENDFOREACH(lib ${libs})
      ENDFOREACH(DYLIB ${IN_LIBS})
      LIST(REMOVE_DUPLICATES ${OUT_LIBS})
   ENDMACRO(find_dep_libs)

ENDIF(APPLE)
