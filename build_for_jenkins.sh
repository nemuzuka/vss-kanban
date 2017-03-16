#!/bin/bash -xe

source var/lib/jenkins/.nvm/nvm.sh

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
