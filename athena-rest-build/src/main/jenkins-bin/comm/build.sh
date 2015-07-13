#!/bin/bash

rm -fr athena-rest-build-0.0.1-bin*
wget http://10.77.144.192:11081/nexus/content/repositories/releases/com/wanda/athena/athena-rest-build/0.0.1/athena-rest-build-0.0.1-bin.tar.gz

mkdir athena-rest-build-0.0.1-bin
tar xzvf athena-rest-build-0.0.1-bin.tar.gz -C athena-rest-build-0.0.1-bin

cd athena-rest-build-0.0.1-bin

find . -type f -name "*" -exec dos2unix {} \;

chmod -R 755 .

