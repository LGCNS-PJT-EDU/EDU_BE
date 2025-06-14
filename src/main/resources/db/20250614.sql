-- feedback_fail_log retry column 추가
ALTER TABLE feedback_fail_log
  ADD COLUMN retry BOOLEAN NOT NULL DEFAULT FALSE;

--- recom_fail_log retry column 추가
ALTER TABLE recom_fail_log
  ADD COLUMN retry BOOLEAN NOT NULL DEFAULT FALSE;