#!/bin/bash -xe

cd ./frontend

if [ -e node_modules ]; then
    npm update
else
    npm install
fi

npm run build
cd ../

./skinny db:migrate test
./skinny package
