-- member 테이블 생성
CREATE TABLE member
(
  id           UUID         NOT NULL,
  student_name varchar(255) NOT NULL,
  name         varchar(255) NOT NULL,
  role         varchar(50)  NOT NULL,

  created_at   timestamptz  NOT NULL DEFAULT NOW(),
  updated_at   timestamptz  NOT NULL DEFAULT NOW(),

  deleted      boolean      NOT NULL DEFAULT FALSE,
  deleted_at   timestamptz  NULL,

  CONSTRAINT pk_member PRIMARY KEY (id),
  CONSTRAINT uq_member_student_name UNIQUE (student_name),
  CONSTRAINT chk_member_role CHECK ( role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

-- 조회 인덱스 (학번 + 삭제여부)
CREATE INDEX idx_member_student_name_deleted
  ON member (student_name, deleted);