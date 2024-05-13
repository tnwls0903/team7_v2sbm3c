/**********************************/
/* Table Name: 질문게시글댓글 */
/**********************************/
DROP TABLE QNA_COMMENT;

CREATE TABLE QNA_COMMENT(
		QCMT_NO NUMBER(10) NOT NULL PRIMARY KEY,
		PRO_NO NUMBER(10),  -- FK
		QCON_NO NUMBER(10), -- FK
		QCMT_CONTENTS VARCHAR2(300) NOT NULL,
		QCMT_DATE DATE NOT NULL,
  FOREIGN KEY (QCON_NO) REFERENCES QNA_CONTENTS (QCON_NO),
  FOREIGN KEY (PRO_NO) REFERENCES PROFILE (PRO_NO)
);

COMMENT ON TABLE QNA_COMMENT is '질문게시글 댓글';
COMMENT ON COLUMN QNA_COMMENT.QCMT_NO is '댓글 번호';
COMMENT ON COLUMN QNA_COMMENT.PRO_NO is '회원 번호';
COMMENT ON COLUMN QNA_COMMENT.QCON_NO is '질문게시글 번호';
COMMENT ON COLUMN QNA_COMMENT.QCMT_CONTENTS is '내용';
COMMENT ON COLUMN QNA_COMMENT.QCMT_DATE is '등록일';


DROP SEQUENCE QNA_COMMENT_SEQ;

CREATE SEQUENCE QNA_COMMENT_SEQ
  START WITH 1              -- 시작 번호
  INCREMENT BY 1            -- 증가값
  MAXVALUE 9999999999       -- 최대값: 9999999999 --> NUMBER(10) 대응
  CACHE 2                   -- 2번은 메모리에서만 계산