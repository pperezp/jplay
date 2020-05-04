#!/bin/bash
# si quiere compilar --> sh run.sh -c

if [ "$1" = "-h" ]; then
    echo "Sólo ejecutar: ./run.sh"
    echo "Ejecutar y compilar: ./run.sh -c"
fi

if [ "$1" = "-c" ]; then
    sh compile.sh
fi

if [ "$1" != "-h" ]; then
    cd xjplay
    java -jar -Dsun.java2d.opengl=true target/xjplay-1.0-SNAPSHOT-jar-with-dependencies.jar
fi


