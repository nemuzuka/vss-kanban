#!/bin/bash -xe

# jsのコンパイルを実施
# [前提条件]node.jsがインストール済みであること

source /var/lib/jenkins/.nvm/nvm.sh

cd ./frontend

if [ -e node_modules ]; then
    npm update
else
    npm install
fi

npm run build
cd ../
