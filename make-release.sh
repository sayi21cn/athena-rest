#!/bin/bash

rm -fr ./releases
mkdir -p ./releases/athena-rest-0.0.1-release/md/
cp ./README.md ./releases/athena-rest-0.0.1-release/md/index.md

generate-md --layout ./mixu-gray --input ./releases/athena-rest-0.0.1-release/md/ --output ./releases/athena-rest-0.0.1-release/

mvn clean package -DskipTests -Dmaven.test.skip=true

cp ./athena-rest-framework/athena-rest-comm/target/athena-rest-comm-0.0.1.jar ./releases/athena-rest-0.0.1-release/
cp ./athena-rest-framework/athena-rest-container/target/athena-rest-container-0.0.1.jar ./releases/athena-rest-0.0.1-release/
cp ./athena-rest-framework/athena-rest-webapp/target/athena-rest-webapp-0.0.1.jar ./releases/athena-rest-0.0.1-release/

cp ./athena-rest-build/target/athena-rest-build-0.0.1-bin.tar.gz ./releases/athena-rest-0.0.1-release/

cp ./athena-rest-wizard/target/athena-rest-wizard-0.0.1-bin.zip ./releases/athena-rest-0.0.1-release/

cp athena-example/target/athena-example-rest-0.0.1.tar.gz ./releases/athena-rest-0.0.1-release/

cp ./athena-doc/* ./releases/athena-rest-0.0.1-release/

rm -fr ./releases/athena-rest-0.0.1-release/md/

tar zcvf ./releases/athena-rest-0.0.1-release.tar.gz ./releases/athena-rest-0.0.1-release
