-- diagnosis, choice 테이블 삭제
DROP TABLE IF EXISTS choice;
DROP TABLE IF EXISTS diagnosis;

-- diagnosis 테이블 생성
CREATE TABLE diagnosis (
    diagnosis_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    created_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- choice 테이블 생성
CREATE TABLE choice (
    choice_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    choice_num INT NOT NULL,
    choice TEXT NOT NULL,
    value VARCHAR(255) NOT NULL,
    created_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diagnosis_id BIGINT NOT NULL,
    CONSTRAINT fk_choice_diagnosis FOREIGN KEY (diagnosis_id) REFERENCES diagnosis(diagnosis_id)
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

-- roadmap, roadmap_management, subject, track 테이블 삭제
DROP TABLE IF EXISTS roadmap;
DROP TABLE IF EXISTS roadmap_management;
DROP TABLE IF EXISTS subject;
DROP TABLE IF EXISTS track;


-- track(과정) 테이블 생성
CREATE TABLE IF NOT EXISTS track(
    track_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    track_nm VARCHAR(255) NOT NULL,
    UNIQUE KEY uk_track (track_nm)
);

-- subject(과목) 테이블 생성
CREATE TABLE IF NOT EXISTS subject(
    sub_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sub_nm VARCHAR(255) NOT NULL,
    sub_type VARCHAR(50) NOT NULL,
    sub_essential VARCHAR(1) NOT NULL,
    base_sub_order INT NOT NULL,
    sub_overview TEXT,
    track_id BIGINT NOT NULL,
    CONSTRAINT fk_subject_track FOREIGN KEY (track_id) REFERENCES track(track_id)
);

-- roadmap_management(로드맵 관리) 테이블 생성
CREATE TABLE IF NOT EXISTS roadmap_management(
    roadmap_management_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    roadmap_nm VARCHAR(255),
    roadmap_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    lecture_amount INT,
    price_level INT,
    likes_books BOOLEAN
);

-- roadmap(로드맵) 테이블 생성
CREATE TABLE IF NOT EXISTS roadmap(
    roadmap_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_sub INT NOT NULL,
    user_id BIGINT NOT NULL,
    sub_id BIGINT NOT NULL,
    roadmap_management_id BIGINT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_roadmap_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_roadmap_subject FOREIGN KEY (sub_id) REFERENCES subject(sub_id),
    CONSTRAINT fk_roadmap_roadmap_management FOREIGN KEY (roadmap_management_id) REFERENCES roadmap_management(roadmap_management_id)
);

-- track(과정) 정보 주입
INSERT IGNORE INTO track (track_id, track_nm) VALUES
                                 (1, '기초지식'),
                                 (2, 'VCS'),
                                 (3, 'HTTP 통신 및 상태 동기화 고도화'),
                                 (4, 'React 기본'),
                                 (5, 'Vue 기본'),
                                 (6, 'Angular 기본'),
                                 (7, '디자인'),
                                 (8, '상태관리'),
                                 (9, 'React 심화'),
                                 (10, 'Vue 심화'),
                                 (11, 'Angular 심화'),
                                 (12, '번들링과 빌드 최적화'),
                                 (13, '배포 자동화 및 인프라 연계'),
                                 (14, '컴포넌트 테스트 방법론'),
                                 (15, '화면 테스트 방법론'),
                                 (16, 'Linux & Internet'),
                                 (17, 'Java'),
                                 (18, 'Python'),
                                 (19, 'JavaScript'),
                                 (20, 'Kotlin'),
                                 (21, 'SQL문'),
                                 (22, 'More About Database'),
                                 (23, 'Scaling Database'),
                                 (24, 'Spring & Spring Boot(Java)'),
                                 (25, 'Node.js & Express.js'),
                                 (26, 'Django'),
                                 (27, 'Flask'),
                                 (28, 'Spring & Spring Boot(Kotlin)'),
                                 (29, 'Java Advanced'),
                                 (30, 'Kotlin Advanced'),
                                 (31, 'Java & Kotlin Advanced'),
                                 (32, 'Node.js Advanced'),
                                 (33, 'Django Advanced'),
                                 (34, 'Flask Advanced');

-- subject(과목) 정보 주입
INSERT IGNORE INTO subject (sub_id, sub_nm, track_id, sub_type, sub_essential, base_sub_order) VALUES
       (1, 'HTML', 1, 'FE', 'Y', 1),
       (2, 'CSS', 1, 'FE', 'Y', 2),
       (3, 'JavaScript', 1, 'FE', 'Y', 3),
       (4, 'TypeScript', 1, 'FE', 'Y', 4),
       (5, 'Virtual DOM', 1, 'FE', 'Y', 5),
       (6, 'Git & GitHub', 2, 'FE', 'Y', 6),
       (7, 'Git Hook (Husky, lint-staged) 자동화', 2, 'FE', 'Y', 7),
       (8, 'Axios 인스턴스 관리, 공통 인터셉터 구성', 3, 'FE', 'Y', 8),
       (9, 'REST API 기반 에러 처리 / 재시도 로직', 3, 'FE', 'Y', 9),
       (10, 'React', 4, 'FE', 'N', 10),
       (11, 'Redux', 4, 'FE', 'N', 11),
       (12, 'Zustand', 4, 'FE', 'N', 12),
       (13, 'Vue', 5, 'FE', 'N', 13),
       (14, 'Vuex & Pinia', 5, 'FE', 'N', 14),
       (15, 'Angular', 6, 'FE', 'N', 15),
       (16, 'NgRx', 6, 'FE', 'N', 16),
       (17, '컴포넌트 디자인 시스템(Storybook)', 7, 'FE', 'N', 17),
       (18, 'Tailwind CSS', 7, 'FE', 'N', 18),
       (19, 'SCSS', 7, 'FE', 'N', 19),
       (20, 'styled', 7, 'FE', 'N', 20),
       (21, 'EsLint & Prettier', 8, 'FE', 'N', 21),
       (22, 'Next.js', 9, 'FE', 'N', 22),
       (23, 'React 렌더링 최적화 (React.memo, useMemo, useCallback)', 9, 'FE', 'N', 23),
       (24, 'React Query 심화 (Prefetch, Query Keys, Invalidations)', 9, 'FE', 'N', 24),
       (25, 'Vue Test Utils (Vue)', 10, 'FE', 'N', 25),
       (26, 'Nuxt.js (Vue)', 10, 'FE', 'N', 26),
       (27, 'Angular Universal (Angular SSR)', 11, 'FE', 'N', 27),
       (28, 'Jasmine (Angular)', 11, 'FE', 'N', 28),
       (29, 'Webpack 개념과 설정', 12, 'FE', 'Y', 29),
       (30, 'GitHub Actions로 빌드/배포 자동화', 13, 'FE', 'N', 30),
       (31, 'Dockerize된 프론트엔드 앱 배포 (Nginx 등)', 13, 'FE', 'N', 31),
       (32, 'AWS S3 + CloudFront 기반 SPA 배포', 13, 'FE', 'N', 32),
       (33, 'Unit Test, Snapshot Test, Integration Test', 14, 'FE', 'N', 33),
       (34, 'E2E Test, Visual Regression Test', 15, 'FE', 'N', 34),
       (35, '리눅스 명령어', 16, 'BE', 'Y', 1),
       (36, 'HTTP, HTTPS, DNS, TCP/IP 기본 개념', 16, 'BE', 'Y', 2),
       (37, 'Git & GitHub', 2, 'BE', 'Y', 3),
       (38, 'Git Hook (Husky, lint-staged) 자동화', 2, 'BE', 'Y', 4),
       (39, 'Java', 17, 'BE', 'N', 5),
       (40, 'Python', 18, 'BE', 'N', 6),
       (41, 'JavaScript', 19, 'BE', 'N', 7),
       (42, 'Kotlin', 20, 'BE', 'N', 8),
       (43, 'SQL문', 21, 'BE', 'Y', 9),
       (44, 'More About Database(RDB 종류, NoSQL)', 22, 'BE', 'N', 10),
       (45, 'Scaling Databases(쿼리튜닝, 정규화)', 23, 'BE', 'N', 11),
       (46, 'Spring & Spring Boot(Java)', 24, 'BE', 'N', 12),
       (47, 'Node.js & Express.js', 25, 'BE', 'N', 13),
       (48, 'Django', 26, 'BE', 'N', 14),
       (49, 'Flask', 27, 'BE', 'N', 15),
       (50, 'Spring & Spring Boot(Kotlin)', 28, 'BE', 'N', 16),
       (51, 'Java + Spring 라이브러리 & 유틸', 29, 'BE', 'N', 17),
       (52, 'Kotlin + Spring 라이브러리 & 유틸', 30, 'BE', 'N', 18),
       (53, 'Java,Kotlin + Spring 운영 & 배포', 31, 'BE', 'N', 19),
       (54, 'Node.js 라이브러리 & 유틸', 32, 'BE', 'N', 20),
       (55, 'Node.js 운영 & 배포', 32, 'BE', 'N', 21),
       (56, 'Django 라이브러리 & 유틸', 33, 'BE', 'N', 22),
       (57, 'Django 운영 & 배포', 33, 'BE', 'N', 23),
       (58, 'Flask 라이브러리 & 유틸', 34, 'BE', 'N', 24),
       (59, 'Flask 운영 & 배포', 34, 'BE', 'N', 25);