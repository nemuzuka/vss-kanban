-- ログインユーザ追加情報
CREATE TABLE login_user_appendix
(
	-- ログインユーザID
	login_user_info_id bigint NOT NULL,
	-- ソート順
	sort_num bigint NOT NULL,
	PRIMARY KEY (login_user_info_id)
) WITHOUT OIDS;

ALTER TABLE login_user_appendix
	ADD FOREIGN KEY (login_user_info_id)
	REFERENCES login_user_info (id)
	ON UPDATE RESTRICT
	ON DELETE CASCADE
;

COMMENT ON TABLE login_user_appendix IS 'ログインユーザ追加情報';
COMMENT ON COLUMN login_user_appendix.login_user_info_id IS 'ログインユーザID';
COMMENT ON COLUMN login_user_appendix.sort_num IS 'ソート順';

-- データマイグレーション
INSERT INTO login_user_appendix (login_user_info_id, sort_num)
SELECT id,9223372036854775807 FROM login_user_info;

