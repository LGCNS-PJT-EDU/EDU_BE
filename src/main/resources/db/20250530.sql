-- choice 테이블 생성
DROP TABLE IF EXISTS choice;

CREATE TABLE choice (
    choice_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    choice_num INT NOT NULL,
    choice TEXT NOT NULL,
    value VARCHAR(255) NOT NULL,
    created_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diagnosis_id BIGINT NOT NULL,
    CONSTRAINT fk_choice_diagnosis FOREIGN KEY (diagnosis_id) REFERENCES diagnosis(diagnosis_id)
);

-- diagnosis 테이블 생성
DROP TABLE IF EXISTS diagnosis;

CREATE TABLE diagnosis (
    diagnosis_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    created_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 공통 질문 1
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (1, '어느 분야에서 성장하고 싶으신가요?', 'COMMON');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(1, 1, '프론트엔드', 'FE'),
(1, 2, '백엔드', 'BE');

-- 공통 질문 2
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (2, '당신이 원하는 강의 분량은 어느정도 인가요?', 'COMMON');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(2, 1, '가볍게 30분!', '0'),
(2, 2, '짧고 굵게 1시간!', '1'),
(2, 3, '찐득하게 3시간!', '2'),
(2, 4, '깊게 공부하는게 좋아 5시간!', '3'),
(2, 5, '나는야 공부벌레 10시간!', '4');

-- 공통 질문 3
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (3, '한 강의에 얼마까지 투자할 생각이 있으신가요?', 'COMMON');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(3, 1, '무료', '0'),
(3, 2, '5만원 이하', '1'),
(3, 3, '5만원 ~ 10만원', '2'),
(3, 4, '10만원 ~ 20만원', '3'),
(3, 5, '20만원 ~ 50만원', '4'),
(3, 6, '50만원 이상', '5');

-- 공통 질문 4
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (4, '책을 통해 공부하는 것을 좋아하시나요?', 'COMMON');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(4, 1, '네.', 'Y'),
(4, 2, '아니오.', 'N');

-- 프론트엔드 질문 1
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (5, '배우고 싶은 프레임워크가 무엇인가요?', 'FE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(5, 1, 'React', 'React'),
(5, 2, 'Vue', 'Vue'),
(5, 3, 'Angular', 'Angular');

-- 프론트엔드 질문 2
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (6, '코드를 깔끔하고 일관되게 관리하는 방법(EsLint & Prettier)에 대해 배우고 싶나요?', 'FE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(6, 1, '네', 'Y'),
(6, 2, '아니오', 'N');

-- 프론트엔드 질문 3
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (7, 'UI를 깔끔하고 체계적으로 만들기 위한 디자인 시스템과 스타일링 방법을 배워보고 싶나요?', 'FE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(7, 1, '네', 'Y'),
(7, 2, '아니오', 'N');

-- 프론트엔드 질문 4
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (8, '프론트엔드 배포를 자동화하는 과정(CI/CD)에 대해 배우고 싶나요?', 'FE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(8, 1, '네', 'Y'),
(8, 2, '아니오', 'N');

-- 프론트엔드 질문 5
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (9, '컴포넌트 단위의 UI 테스트 방법론(예: Storybook, Jest + React Testing Library)을 배우고 싶나요?', 'FE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(9, 1, '네', 'Y'),
(9, 2, '아니오', 'N');

-- 프론트엔드 질문 6
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (10, '사용자의 실제 사용 흐름(E2E 테스트, Cypress 등)을 테스트하는 방법에 대해 배우고 싶나요?', 'FE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(10, 1, '네', 'Y'),
(10, 2, '아니오', 'N');

-- 백엔드 질문 1
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (11, '선호하는 프레임워크는 무엇인가요?', 'BE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(11, 1, 'Java/Spring', 'Java/Spring'),
(11, 2, 'Python/Flask', 'Python/Flask'),
(11, 3, 'Python/Django', 'Python/Django'),
(11, 4, 'JavaScript/Node.js', 'Js/Node'),
(11, 5, 'Kotlin/Spring', 'Kotlin/Spring');

-- 백엔드 질문 2
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (12, 'RDB 종류별 장단점을 학습하고 싶으신가요?', 'BE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(12, 1, '네', 'Y'),
(12, 2, '아니오', 'N');

-- 백엔드 질문 3
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (13, '쿼리 최적화, 데이터 정규화에 대해 학습하고 싶으신가요?', 'BE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(13, 1, '네', 'Y'),
(13, 2, '아니오', 'N');

-- 백엔드 질문 4
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (14, '프레임워크 라이브러리 종류를 학습하고 싶으신가요?', 'BE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(14, 1, '네', 'Y'),
(14, 2, '아니오', 'N');

-- 백엔드 질문 5
INSERT INTO diagnosis (diagnosis_id, question, question_type) VALUES (15, '프로젝트 운영 배포 방법을 학습하고 싶으신가요?', 'BE');
INSERT INTO choice (diagnosis_id, choice_num, choice, value) VALUES
(15, 1, '네', 'Y'),
(15, 2, '아니오', 'N');