
/* Drop Indexes */

DROP INDEX IF EXISTS idx_login_user_info_01;



/* Drop Tables */

DROP TABLE IF EXISTS file_image;
DROP TABLE IF EXISTS kanban_attachment_file;
DROP TABLE IF EXISTS note_attachment_file;
DROP TABLE IF EXISTS note_comment_attachment_file;
DROP TABLE IF EXISTS attachment_file;
DROP TABLE IF EXISTS kanban_joined_user;
DROP TABLE IF EXISTS note_charged_user;
DROP TABLE IF EXISTS note_comment;
DROP TABLE IF EXISTS note_history;
DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS stage;
DROP TABLE IF EXISTS kanban;
DROP TABLE IF EXISTS login_user_appendix;
DROP TABLE IF EXISTS login_user_password;
DROP TABLE IF EXISTS login_user_info;




/* Create Tables */

-- 添付ファイル
CREATE TABLE attachment_file
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- 実ファイル名
	real_file_name varchar(256) NOT NULL,
	-- MimeType
	mime_type varchar(256) NOT NULL,
	-- 添付対象区分
	-- 1:かんばん
	-- 2:付箋
	attachment_target_type varchar(2) NOT NULL,
	-- ファイルサイズ
	file_size bigint NOT NULL,
	-- 横幅
	width bigint,
	-- 高さ
	height bigint,
	-- 横幅(サムネイル)
	thumbnail_width bigint,
	-- 高さ(サムネイル)
	thumbnail_height bigint,
	-- 最終更新日時
	last_update_at timestamp NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ファイルイメージ
CREATE TABLE file_image
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- 添付ファイルID
	attachment_file_id bigint NOT NULL,
	-- ファイルイメージ区分
	-- 1:オリジナル
	-- 2:サムネイル
	file_image_type varchar(2) NOT NULL,
	-- イメージ
	image bytea NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- かんばん
CREATE TABLE kanban
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- かんばんタイトル
	kanban_title varchar(256) NOT NULL,
	-- 説明
	kanban_description text NOT NULL,
	-- アーカイブステータス
	-- 0:通常
	-- 1:アーカイブ
	archive_status varchar(1) NOT NULL,
	-- 作成日時
	create_at timestamp NOT NULL,
	-- 最終更新日時
	last_update_at timestamp NOT NULL,
	-- バージョンNo
	lock_version bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- かんばん - 添付ファイル
CREATE TABLE kanban_attachment_file
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- かんばんID
	kanban_id bigint NOT NULL,
	-- 添付ファイルID
	attachment_file_id bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- かんばん - 参加者
CREATE TABLE kanban_joined_user
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- かんばんID
	kanban_id bigint NOT NULL,
	-- ログインユーザ情報ID
	login_user_info_id bigint NOT NULL,
	-- かんばん権限
	-- 1:管理者
	-- 0:一般
	kanban_authority varchar(1) NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ログインユーザ追加情報
CREATE TABLE login_user_appendix
(
	-- ログインユーザID
	login_user_info_id bigint NOT NULL,
	-- ソート順
	sort_num bigint NOT NULL,
	PRIMARY KEY (login_user_info_id)
) WITHOUT OIDS;


-- ログインユーザ情報
CREATE TABLE login_user_info
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- ログインID
	login_id varchar(256) NOT NULL,
	-- ユーザ名
	user_name varchar(256) NOT NULL,
	-- アプリケーション管理者フラグ
	-- 1:管理者
	-- 0:一般
	application_admin_flg varchar(1) NOT NULL,
	-- 作成日時
	create_at timestamp NOT NULL,
	-- 最終更新日時
	last_update_at timestamp NOT NULL,
	-- バージョンNo
	lock_version bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ログインユーザパスワード
CREATE TABLE login_user_password
(
	-- ログインユーザ情報ID
	login_user_info_id bigint NOT NULL,
	-- パスワード
	password varchar(256) NOT NULL,
	PRIMARY KEY (login_user_info_id)
) WITHOUT OIDS;


-- 付箋
CREATE TABLE note
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- ステージID
	stage_id bigint NOT NULL,
	-- かんばんID
	kanban_id bigint NOT NULL,
	-- 付箋タイトル
	note_title varchar(256) NOT NULL,
	-- 説明
	note_description text NOT NULL,
	-- 期日
	fix_date date,
	-- ソート順
	sort_num bigint NOT NULL,
	-- アーカイブステータス
	-- 0:通常
	-- 1:アーカイブ
	archive_status varchar(1) NOT NULL,
	-- 登録ユーザID
	create_login_user_info_id bigint NOT NULL,
	-- 作成日時
	create_at timestamp NOT NULL,
	-- 最終更新ユーザID
	last_update_login_user_info_id bigint NOT NULL,
	-- 最終更新日時
	last_update_at timestamp NOT NULL,
	-- バージョンNo
	lock_version bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- 付箋 - 添付ファイル
CREATE TABLE note_attachment_file
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- 付箋ID
	note_id bigint NOT NULL,
	-- 添付ファイルID
	attachment_file_id bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- 付箋担当者
CREATE TABLE note_charged_user
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- 付箋ID
	note_id bigint NOT NULL,
	-- ログインユーザ情報ID
	login_user_info_id bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ふせん - コメント
CREATE TABLE note_comment
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- ふせんID
	note_id bigint NOT NULL,
	-- コメント
	comment_text text NOT NULL,
	-- 登録ユーザID
	create_login_user_info_id bigint NOT NULL,
	-- 作成日時
	create_at timestamp NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ふせんコメント - 添付ファイル
CREATE TABLE note_comment_attachment_file
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- ふせんコメントID
	note_comment_id bigint NOT NULL,
	-- 添付ファイルID
	attachment_file_id bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ふせん - 履歴
CREATE TABLE note_history
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- ふせんID
	note_id bigint NOT NULL,
	-- ステージID
	stage_id bigint NOT NULL,
	-- 最終更新ユーザID
	last_update_login_user_info_id bigint NOT NULL,
	-- 最終更新日時
	last_update_at timestamp NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ステージ
CREATE TABLE stage
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- かんばんID
	kanban_id bigint NOT NULL,
	-- ステージ名
	stage_name varchar(256) NOT NULL,
	-- アーカイブステータス
	-- 0:通常
	-- 1:アーカイブ
	archive_status varchar(1) NOT NULL,
	-- ソート順
	sort_num bigint NOT NULL,
	-- 完了扱いのステージか
	-- 1:完了扱いのステージ
	-- 0:完了扱いのステージでない
	complete_stage varchar(1) NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE file_image
	ADD FOREIGN KEY (attachment_file_id)
	REFERENCES attachment_file (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE kanban_attachment_file
	ADD FOREIGN KEY (attachment_file_id)
	REFERENCES attachment_file (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_attachment_file
	ADD FOREIGN KEY (attachment_file_id)
	REFERENCES attachment_file (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_comment_attachment_file
	ADD FOREIGN KEY (attachment_file_id)
	REFERENCES attachment_file (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE kanban_attachment_file
	ADD FOREIGN KEY (kanban_id)
	REFERENCES kanban (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE kanban_joined_user
	ADD FOREIGN KEY (kanban_id)
	REFERENCES kanban (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note
	ADD FOREIGN KEY (kanban_id)
	REFERENCES kanban (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE stage
	ADD FOREIGN KEY (kanban_id)
	REFERENCES kanban (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE login_user_appendix
	ADD FOREIGN KEY (login_user_info_id)
	REFERENCES login_user_info (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE login_user_password
	ADD FOREIGN KEY (login_user_info_id)
	REFERENCES login_user_info (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_attachment_file
	ADD FOREIGN KEY (note_id)
	REFERENCES note (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_charged_user
	ADD FOREIGN KEY (note_id)
	REFERENCES note (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_comment
	ADD FOREIGN KEY (note_id)
	REFERENCES note (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_history
	ADD FOREIGN KEY (note_id)
	REFERENCES note (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_comment_attachment_file
	ADD FOREIGN KEY (note_comment_id)
	REFERENCES note_comment (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note
	ADD FOREIGN KEY (stage_id)
	REFERENCES stage (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;



/* Create Indexes */

CREATE UNIQUE INDEX idx_login_user_info_01 ON login_user_info (login_id);



/* Comments */

COMMENT ON TABLE attachment_file IS '添付ファイル';
COMMENT ON COLUMN attachment_file.id IS 'id(自動採番)';
COMMENT ON COLUMN attachment_file.real_file_name IS '実ファイル名';
COMMENT ON COLUMN attachment_file.mime_type IS 'MimeType';
COMMENT ON COLUMN attachment_file.attachment_target_type IS '添付対象区分
1:かんばん
2:付箋';
COMMENT ON COLUMN attachment_file.file_size IS 'ファイルサイズ';
COMMENT ON COLUMN attachment_file.width IS '横幅';
COMMENT ON COLUMN attachment_file.height IS '高さ';
COMMENT ON COLUMN attachment_file.thumbnail_width IS '横幅(サムネイル)';
COMMENT ON COLUMN attachment_file.thumbnail_height IS '高さ(サムネイル)';
COMMENT ON COLUMN attachment_file.last_update_at IS '最終更新日時';
COMMENT ON TABLE file_image IS 'ファイルイメージ';
COMMENT ON COLUMN file_image.id IS 'id(自動採番)';
COMMENT ON COLUMN file_image.attachment_file_id IS '添付ファイルID';
COMMENT ON COLUMN file_image.file_image_type IS 'ファイルイメージ区分
1:オリジナル
2:サムネイル';
COMMENT ON COLUMN file_image.image IS 'イメージ';
COMMENT ON TABLE kanban IS 'かんばん';
COMMENT ON COLUMN kanban.id IS 'id(自動採番)';
COMMENT ON COLUMN kanban.kanban_title IS 'かんばんタイトル';
COMMENT ON COLUMN kanban.kanban_description IS '説明';
COMMENT ON COLUMN kanban.archive_status IS 'アーカイブステータス
0:通常
1:アーカイブ';
COMMENT ON COLUMN kanban.create_at IS '作成日時';
COMMENT ON COLUMN kanban.last_update_at IS '最終更新日時';
COMMENT ON COLUMN kanban.lock_version IS 'バージョンNo';
COMMENT ON TABLE kanban_attachment_file IS 'かんばん - 添付ファイル';
COMMENT ON COLUMN kanban_attachment_file.id IS 'id(自動採番)';
COMMENT ON COLUMN kanban_attachment_file.kanban_id IS 'かんばんID';
COMMENT ON COLUMN kanban_attachment_file.attachment_file_id IS '添付ファイルID';
COMMENT ON TABLE kanban_joined_user IS 'かんばん - 参加者';
COMMENT ON COLUMN kanban_joined_user.id IS 'id(自動採番)';
COMMENT ON COLUMN kanban_joined_user.kanban_id IS 'かんばんID';
COMMENT ON COLUMN kanban_joined_user.login_user_info_id IS 'ログインユーザ情報ID';
COMMENT ON COLUMN kanban_joined_user.kanban_authority IS 'かんばん権限
1:管理者
0:一般';
COMMENT ON TABLE login_user_appendix IS 'ログインユーザ追加情報';
COMMENT ON COLUMN login_user_appendix.login_user_info_id IS 'ログインユーザID';
COMMENT ON COLUMN login_user_appendix.sort_num IS 'ソート順';
COMMENT ON TABLE login_user_info IS 'ログインユーザ情報';
COMMENT ON COLUMN login_user_info.id IS 'id(自動採番)';
COMMENT ON COLUMN login_user_info.login_id IS 'ログインID';
COMMENT ON COLUMN login_user_info.user_name IS 'ユーザ名';
COMMENT ON COLUMN login_user_info.application_admin_flg IS 'アプリケーション管理者フラグ
1:管理者
0:一般';
COMMENT ON COLUMN login_user_info.create_at IS '作成日時';
COMMENT ON COLUMN login_user_info.last_update_at IS '最終更新日時';
COMMENT ON COLUMN login_user_info.lock_version IS 'バージョンNo';
COMMENT ON TABLE login_user_password IS 'ログインユーザパスワード';
COMMENT ON COLUMN login_user_password.login_user_info_id IS 'ログインユーザ情報ID';
COMMENT ON COLUMN login_user_password.password IS 'パスワード';
COMMENT ON TABLE note IS '付箋';
COMMENT ON COLUMN note.id IS 'id(自動採番)';
COMMENT ON COLUMN note.stage_id IS 'ステージID';
COMMENT ON COLUMN note.kanban_id IS 'かんばんID';
COMMENT ON COLUMN note.note_title IS '付箋タイトル';
COMMENT ON COLUMN note.note_description IS '説明';
COMMENT ON COLUMN note.fix_date IS '期日';
COMMENT ON COLUMN note.sort_num IS 'ソート順';
COMMENT ON COLUMN note.archive_status IS 'アーカイブステータス
0:通常
1:アーカイブ';
COMMENT ON COLUMN note.create_login_user_info_id IS '登録ユーザID';
COMMENT ON COLUMN note.create_at IS '作成日時';
COMMENT ON COLUMN note.last_update_login_user_info_id IS '最終更新ユーザID';
COMMENT ON COLUMN note.last_update_at IS '最終更新日時';
COMMENT ON COLUMN note.lock_version IS 'バージョンNo';
COMMENT ON TABLE note_attachment_file IS '付箋 - 添付ファイル';
COMMENT ON COLUMN note_attachment_file.id IS 'id(自動採番)';
COMMENT ON COLUMN note_attachment_file.note_id IS '付箋ID';
COMMENT ON COLUMN note_attachment_file.attachment_file_id IS '添付ファイルID';
COMMENT ON TABLE note_charged_user IS '付箋担当者';
COMMENT ON COLUMN note_charged_user.id IS 'id(自動採番)';
COMMENT ON COLUMN note_charged_user.note_id IS '付箋ID';
COMMENT ON COLUMN note_charged_user.login_user_info_id IS 'ログインユーザ情報ID';
COMMENT ON TABLE note_comment IS 'ふせん - コメント';
COMMENT ON COLUMN note_comment.id IS 'id(自動採番)';
COMMENT ON COLUMN note_comment.note_id IS 'ふせんID';
COMMENT ON COLUMN note_comment.comment_text IS 'コメント';
COMMENT ON COLUMN note_comment.create_login_user_info_id IS '登録ユーザID';
COMMENT ON COLUMN note_comment.create_at IS '作成日時';
COMMENT ON TABLE note_comment_attachment_file IS 'ふせんコメント - 添付ファイル';
COMMENT ON COLUMN note_comment_attachment_file.id IS 'id(自動採番)';
COMMENT ON COLUMN note_comment_attachment_file.note_comment_id IS 'ふせんコメントID';
COMMENT ON COLUMN note_comment_attachment_file.attachment_file_id IS '添付ファイルID';
COMMENT ON TABLE note_history IS 'ふせん - 履歴';
COMMENT ON COLUMN note_history.id IS 'id(自動採番)';
COMMENT ON COLUMN note_history.note_id IS 'ふせんID';
COMMENT ON COLUMN note_history.stage_id IS 'ステージID';
COMMENT ON COLUMN note_history.last_update_login_user_info_id IS '最終更新ユーザID';
COMMENT ON COLUMN note_history.last_update_at IS '最終更新日時';
COMMENT ON TABLE stage IS 'ステージ';
COMMENT ON COLUMN stage.id IS 'id(自動採番)';
COMMENT ON COLUMN stage.kanban_id IS 'かんばんID';
COMMENT ON COLUMN stage.stage_name IS 'ステージ名';
COMMENT ON COLUMN stage.archive_status IS 'アーカイブステータス
0:通常
1:アーカイブ';
COMMENT ON COLUMN stage.sort_num IS 'ソート順';
COMMENT ON COLUMN stage.complete_stage IS '完了扱いのステージか
1:完了扱いのステージ
0:完了扱いのステージでない';



