
/* Drop Indexes */

DROP INDEX IF EXISTS idx_login_user_info_01;



/* Drop Tables */

DROP TABLE IF EXISTS kanban_joined_user;
DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS lane;
DROP TABLE IF EXISTS kanban;
DROP TABLE IF EXISTS login_user_password;
DROP TABLE IF EXISTS login_user_info;




/* Create Tables */

-- かんばん
CREATE TABLE kanban
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- かんばん名
	kanban_name varchar(256) NOT NULL,
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


-- レーン
CREATE TABLE lane
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- かんばんID
	kanban_id bigint NOT NULL,
	-- レーン名
	lane_name varchar(256) NOT NULL,
	-- アーカイブステータス
	-- 0:通常
	-- 1:アーカイブ
	archive_status varchar(1) NOT NULL,
	-- ソート順
	sort_num bigint NOT NULL,
	-- 終了扱いのレーンか
	-- 1:終了扱いのレーン
	-- 0:終了扱いのレーンでない
	complete_lane varchar(1) NOT NULL,
	PRIMARY KEY (id)
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
	-- レーンID
	lane_id bigint NOT NULL,
	-- かんばんID
	kanban_id bigint NOT NULL,
	-- 付箋タイトル
	note_title varchar(256) NOT NULL,
	-- 説明
	note_description text NOT NULL,
	-- バージョンNo
	lock_version bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;



/* Create Foreign Keys */

ALTER TABLE kanban_joined_user
	ADD FOREIGN KEY (kanban_id)
	REFERENCES kanban (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE lane
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


ALTER TABLE note
	ADD FOREIGN KEY (lane_id)
	REFERENCES lane (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE login_user_password
	ADD FOREIGN KEY (login_user_info_id)
	REFERENCES login_user_info (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;



/* Create Indexes */

CREATE UNIQUE INDEX idx_login_user_info_01 ON login_user_info (login_id);



/* Comments */

COMMENT ON TABLE kanban IS 'かんばん';
COMMENT ON COLUMN kanban.id IS 'id(自動採番)';
COMMENT ON COLUMN kanban.kanban_name IS 'かんばん名';
COMMENT ON COLUMN kanban.kanban_description IS '説明';
COMMENT ON COLUMN kanban.archive_status IS 'アーカイブステータス
0:通常
1:アーカイブ';
COMMENT ON COLUMN kanban.create_at IS '作成日時';
COMMENT ON COLUMN kanban.last_update_at IS '最終更新日時';
COMMENT ON COLUMN kanban.lock_version IS 'バージョンNo';
COMMENT ON TABLE kanban_joined_user IS 'かんばん - 参加者';
COMMENT ON COLUMN kanban_joined_user.id IS 'id(自動採番)';
COMMENT ON COLUMN kanban_joined_user.kanban_id IS 'かんばんID';
COMMENT ON COLUMN kanban_joined_user.login_user_info_id IS 'ログインユーザ情報ID';
COMMENT ON COLUMN kanban_joined_user.kanban_authority IS 'かんばん権限
1:管理者
0:一般';
COMMENT ON TABLE lane IS 'レーン';
COMMENT ON COLUMN lane.id IS 'id(自動採番)';
COMMENT ON COLUMN lane.kanban_id IS 'かんばんID';
COMMENT ON COLUMN lane.lane_name IS 'レーン名';
COMMENT ON COLUMN lane.archive_status IS 'アーカイブステータス
0:通常
1:アーカイブ';
COMMENT ON COLUMN lane.sort_num IS 'ソート順';
COMMENT ON COLUMN lane.complete_lane IS '終了扱いのレーンか
1:終了扱いのレーン
0:終了扱いのレーンでない';
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
COMMENT ON COLUMN note.lane_id IS 'レーンID';
COMMENT ON COLUMN note.kanban_id IS 'かんばんID';
COMMENT ON COLUMN note.note_title IS '付箋タイトル';
COMMENT ON COLUMN note.note_description IS '説明';
COMMENT ON COLUMN note.lock_version IS 'バージョンNo';



