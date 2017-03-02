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


### 6. 初期アプリケーション管理ユーザ作成

初期アプリケーション管理ユーザを作成する為に

`http://localhost:8080/maintenance` にアクセスします。

- ログインID: admin@vss-kanban
- パスワード: vss-kanban-admin

のユーザが作成されるので、初回はそれでログインし、管理者機能より他のユーザを作成して下さい。

**※このユーザのパスワードは速やかに変更するか削除してください！**



### Demo Site

<a href="http://vss-kanban.vss.jp.net/" target="_blank">こちらから</a>お試し頂けます。

| ログインID | パスワード |
|:----------|:----------|
|kanban@vss.jp.net|abcd1234|
|trello|more-than-trello|
|robin|gogo-robin|

**※アプリケーション管理者機能は個別に環境を立ててお試しください**


### 注意点

今のところ、IE / Edgeには対応していません。
CSSフレームワークとして[Buluma](http://bulma.io/ "Bulma")を使用しているのですが、一部IE / Edge で正常に動作しない機能が確認されている為です。
