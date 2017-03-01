## VSS Kanban 

環境構築手順(とりあえず動かしたい人向け)

### 事前準備

- Java8 
- PostgreSQL(9.5) 
- node.js(v7.7.0)

をインストールして下さい。(PostgreSQLにユーザ / databaseも作成しておきます)

### 1. clone or zip展開
ローカルにcloneしたプロジェクトかzipを展開して配置します。以降の作業は、カレントディレクトリを(vss-kanban)とします

### 2. jsビルド

     cd ./frontend
     npm install
     npm run build
     cd ../

### 3. DB接続文字列変更

     src/main/resources/application.conf

のdelelopmentのDB接続文字列を変更

### 4. table作成

    ./skinny db:migrate development


### 5. アプリケーション起動

    ./skinny run

`http://localhost:8080` でアプリケーションにアクセスできます。


### 6. 初期ユーザ作成

初期管理ユーザを作成する為に

`http://localhost:8080/maintenance` にアクセスします。

ログインID: admin@vss-kanban / パスワード: vss-kanban-admin
ユーザが作成されるので、それでログインして下さい。
**※このユーザのパスワードは速やかに変更するか削除してください！**

