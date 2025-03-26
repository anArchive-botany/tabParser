#!/usr/bin/env bash
# 

MAIN_JAVA=`find src/main/java/it/aspix -name '*.java' -exec cat {} \;  | wc -l`;
MAIN_JAVA_DENSE=`find src/main/java/it/aspix -name '*.java' -exec cat {} \; | ~/bin/rimuoviCommenti.sed | sed '/^[[:space:]]*$/d' | wc -l`;

TESTI_DESCRITTIVI=`find src/main/resources -name '*.txt' -exec cat {} \;  | wc -l`;

printf "main java        : %6d (%d codice)\n" "$MAIN_JAVA" "$MAIN_JAVA_DENSE"
printf "testi descrittivi: %6d\n" "$TESTI_DESCRITTIVI"
