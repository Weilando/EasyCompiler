#!/bin/bash

gradle build

if [ ! -f libs/jasmin.jar ]; then
    echo "Download jasmin"
    mkdir -p libs
    cd libs
    wget -O jasmin-2.4.zip https://sourceforge.net/projects/jasmin/files/jasmin/jasmin-2.4/jasmin-2.4.zip/download
    unzip -uq jasmin-2.4.zip
    mv jasmin-2.4/jasmin.jar jasmin.jar
    rm -r jasmin-2.4*
fi
