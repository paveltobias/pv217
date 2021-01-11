#!/bin/bash

repo_root=$(pwd)
svc_dirs=$(ls -1 | grep '-service$')

for dir in $svc_dirs; do
    cd "$repo_root/$dir"
    mvn clean install
    mvn package
done
