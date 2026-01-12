-- member 테이블 생성
CREATE TABLE member
(
  id           UUID         NOT NULL,
  student_number varchar(255) NOT NULL,
  name         varchar(255) NOT NULL,
  role         varchar(50)  NOT NULL,

  created_at   timestamptz  NOT NULL DEFAULT NOW(),
  updated_at   timestamptz  NOT NULL DEFAULT NOW(),

  deleted      boolean      NOT NULL DEFAULT FALSE,
  deleted_at   timestamptz  NULL,

  CONSTRAINT pk_member PRIMARY KEY (id),
  CONSTRAINT chk_member_role CHECK ( role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

-- 활성 회원에 대해서 student_number 유니크 적용
CREATE UNIQUE INDEX uq_member_student_number_active
  ON member (student_number)
  WHERE deleted = FALSE;

-- 조회 인덱스 (학번 + 삭제여부)
CREATE INDEX idx_member_student_number_deleted
  ON member (student_number, deleted);