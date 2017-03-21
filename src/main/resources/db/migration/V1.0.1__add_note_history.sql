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


ALTER TABLE note_history
	ADD FOREIGN KEY (note_id)
	REFERENCES note (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;


COMMENT ON TABLE note_history IS 'ふせん - 履歴';
COMMENT ON COLUMN note_history.id IS 'id(自動採番)';
COMMENT ON COLUMN note_history.note_id IS 'ふせんID';
COMMENT ON COLUMN note_history.stage_id IS 'ステージID';
COMMENT ON COLUMN note_history.last_update_login_user_info_id IS '最終更新ユーザID';
COMMENT ON COLUMN note_history.last_update_at IS '最終更新日時';
