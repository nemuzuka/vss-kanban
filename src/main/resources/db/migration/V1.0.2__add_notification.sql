DROP INDEX IF EXISTS idx_note_notification_01;
DROP TABLE IF EXISTS note_notification;
DROP TABLE IF EXISTS note_watch_user;

-- ふせん通知
CREATE TABLE note_notification
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- ふせんID
	note_id bigint NOT NULL,
	-- ログインユーザ情報ID
	login_user_info_id bigint NOT NULL,
	-- 通知内容
	notification_body text NOT NULL,
	-- 遷移先URL
	notification_url varchar(512) NOT NULL,
	-- 作成日時
	create_at timestamp NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;


-- ふせん通知ユーザ
CREATE TABLE note_watch_user
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- ふせんID
	note_id bigint NOT NULL,
	-- ログインユーザ情報ID
	login_user_info_id bigint NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;

ALTER TABLE note_notification
	ADD FOREIGN KEY (note_id)
	REFERENCES note (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


ALTER TABLE note_watch_user
	ADD FOREIGN KEY (note_id)
	REFERENCES note (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;

CREATE INDEX idx_note_notification_01 ON note_notification (login_user_info_id);

COMMENT ON TABLE note_notification IS 'ふせん通知';
COMMENT ON COLUMN note_notification.id IS 'id(自動採番)';
COMMENT ON COLUMN note_notification.note_id IS 'ふせんID';
COMMENT ON COLUMN note_notification.login_user_info_id IS 'ログインユーザ情報ID';
COMMENT ON COLUMN note_notification.notification_body IS '通知内容';
COMMENT ON COLUMN note_notification.notification_url IS '遷移先URL';
COMMENT ON COLUMN note_notification.create_at IS '作成日時';
COMMENT ON TABLE note_watch_user IS 'ふせん通知ユーザ';
COMMENT ON COLUMN note_watch_user.id IS 'id(自動採番)';
COMMENT ON COLUMN note_watch_user.note_id IS 'ふせんID';
COMMENT ON COLUMN note_watch_user.login_user_info_id IS 'ログインユーザ情報ID';
