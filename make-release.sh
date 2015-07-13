#!/bin/bash

#mvn clean package assembly:assembly -DskipTests -Dmaven.test.skip=true

#tar zcvf target/vesta-id-generator-0.0.1-release/bin/vesta-lib-0.0.1.tar.gz -C target/vesta-id-generator-0.0.1-release/lib . 
#tar zcvf target/vesta-id-generator-0.0.1-release/bin/vesta-src-0.0.1.tar.gz -C target/vesta-id-generator-0.0.1-release/src .
#tar zcvf target/vesta-id-generator-0.0.1-release/bin/vesta-all-src-0.0.1.tar.gz ./vesta* pom.xml make-release.sh .gitignore deploy-maven.sh assembly.xml todo.txt

#mkdir ./target/vesta-id-generator-0.0.1-release/doc
#generate-md --layout mixu-book --input ./vesta-doc --output ./target/vesta-id-generator-0.0.1-release/doc
#generate-md --layout ./vesta-theme --input ./vesta-doc --output ./target/vesta-id-generator-0.0.1-release/doc

#rm -fr releases/
#mkdir releases
#tar zcvf releases/vesta-id-generator-0.0.1-release.tar.gz -C target vesta-id-generator-0.0.1-release


rm -fr ./release
mkdir -p ./release/athena-rest-0.0.1-release/md/
cp ./README.md ./release/athena-rest-0.0.1-release/md/index.md

generate-md --layout ./mixu-gray --input ./release/athena-rest-0.0.1-release/md/ --output ./release/athena-rest-0.0.1-release/

mvn clean package -DskipTests -Dmaven.test.skip=true

cp ./athena-rest-framework/athena-rest-comm/target/athena-rest-comm-0.0.1.jar ./release/athena-rest-0.0.1-release/
cp ./athena-rest-framework/athena-rest-container/target/athena-rest-container-0.0.1.jar ./release/athena-rest-0.0.1-release/
cp ./athena-rest-framework/athena-rest-webapp/target/athena-rest-webapp-0.0.1.jar ./release/athena-rest-0.0.1-release/

cp ./athena-rest-build/target/athena-rest-build-0.0.1-bin.tar.gz ./release/athena-rest-0.0.1-release/

cp ./athena-rest-wizard/target/athena-rest-wizard-0.0.1-bin.zip ./release/athena-rest-0.0.1-release/

cp athena-example/target/athena-example-rest-0.0.1.tar.gz ./release/athena-rest-0.0.1-release/

cp ./athena-doc/* ./release/athena-rest-0.0.1-release/

rm -fr ./release/athena-rest-0.0.1-release/md/
