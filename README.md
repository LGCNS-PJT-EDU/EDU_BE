# TakeIT BE

AI 기반 맞춤 성장 학습 플랫폼 **TakeIT**의 백엔드 서버입니다.  
사용자 진단, 학습 콘텐츠 추천, 면접 피드백 등 주요 기능을 제공하며,  
Spring Boot 기반 RESTful API와 JWT 인증, AWS 배포 환경을 구성합니다.

---

## 🚀 주요 기능

### ✅ 사용자 인증 및 권한 관리
- 회원가입, 로그인 (JWT 기반)
- 소셜 로그인 (Google, Naver, Kakao)
- 사용자 권한(Role)에 따른 접근 제어

### 📊 사전/사후 진단
- 선택형 + 서술형 진단 시스템
- 진단 결과 기반 점수/그래프/분석 리포트 제공
- 성장률 분석 및 피드백 자동화

### 🧠 AI 기반 인터뷰 피드백
- STT 기반 사용자 응답 수집
- GPT 기반 서술형 피드백 생성 및 저장
- 사전/사후 면접 비교 분석

### 🎯 커리큘럼 & 로드맵 추천
- 사용자의 진단 결과 기반 개인 맞춤형 로드맵 제공
- 로드맵 단계별 진행률 관리 및 수정 기능 지원

---

## 🛠 기술 스택

### 🔹 언어 & 빌드
![Java](https://img.shields.io/badge/Java%2021-007396?style=flat&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)

### ⚙️ 프레임워크 & 라이브러리
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.2-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-21759B?style=flat)

### 🛢 데이터베이스 & 캐시
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)

### 🔐 인증 및 보안
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-3C8CFF?style=flat&logo=oauth&logoColor=white)
![Google](https://img.shields.io/badge/Google-4285F4?style=flat&logo=google&logoColor=white)
![Kakao](https://img.shields.io/badge/Kakao-FFCD00?style=flat)
![Naver](https://img.shields.io/badge/Naver-03C75A?style=flat)

### ☁️ 인프라 & 배포
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=flat&logo=amazonaws&logoColor=white)
![RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=flat)
![S3](https://img.shields.io/badge/AWS%20S3-569A31?style=flat)
![CloudFront](https://img.shields.io/badge/CloudFront-FF9933?style=flat)

### 📈 모니터링
![Spring Actuator](https://img.shields.io/badge/Spring%20Actuator-6DB33F?style=flat)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white)

### 🚀 CI/CD
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![ArgoCD](https://img.shields.io/badge/ArgoCD-FE4C61?style=flat&logo=argo&logoColor=white)
![Helm](https://img.shields.io/badge/Helm-277A9F?style=flat&logo=helm&logoColor=white)

### 📄 문서화
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)
![springdoc-openapi](https://img.shields.io/badge/springdoc--openapi-6DB33F?style=flat)


<br/>

## 📁 디렉토리 구조
```
src/
├── main/
│ ├── java/
│ │ └── com.education.takeit/
│ │ ├── admin/ # 관리자 기능 도메인
│ │ ├── diagnosis/ # 진단 관련 도메인
│ │ ├── exam/ # 평가 관련 기능
│ │ ├── feedback/ # AI 피드백 생성 및 제공 로직
│ │ ├── global/ # 전역 설정 및 공통 컴포넌트
│ │ │ ├── client/ # 외부 API 연동 
│ │ │ ├── config/ # Spring 설정 
│ │ │ ├── dto/ # 공통 응답/요청 DTO 정의
│ │ │ ├── exception/ # 전역 예외 처리 핸들러
│ │ │ ├── security/ # JWT, OAuth 보안 설정
│ │ ├── interview/ # 면접 질문 및 음성 응답 처리
│ │ ├── kafka/ # Kafka 메시지 발행/구독 로직
│ │ ├── oauth/ # OAuth 로그인 (Google, Naver, Kakao)
│ │ ├── recommend/ # 추천컨텐츠
│ │ ├── roadmap/ # 사용자 맞춤형 로드맵 로직
│ │ ├── solution/ # 정답 및 해설 관련 로직
│ │ ├── user/ # 사용자 정보 및 계정 관리
│ │ └── TakeitApplication # SpringBootApplication 진입점
│
├── resources/
│ ├── db/ # SQL, 마이그레이션 스크립트 등
│ ├── application.yml # 공통 설정
│ ├── application-local.yml # 로컬 환경 설정
│ ├── application-dev.yml # 개발 서버 설정
│ └── application-test.yml # 테스트 환경 설정
```

## 🧪 테스트

- 단위 테스트: JUnit5, Mockito
- 통합 테스트: SpringBootTest
- 테스트 커버리지 측정: JaCoCo

---

## 🔐 인증 구조

- **JWT Access + Refresh Token 발급 및 갱신**
- **OAuth2 인증 로직 직접 구현 (Google, Naver, Kakao)**
- **HMAC 기반 토큰 서명 및 인증 필터 커스터마이징**

---

## 📄 API 문서

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI Spec: `/v3/api-docs`

---

[GitHub For Jira 커밋 연동 자동화](https://velog.io/@tyo1012/LG-CNS-AM-Inspire-Camp-1%EA%B8%B0-%EC%B5%9C%EC%A2%85-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-5.-GitHub-For-Jira-%EC%BB%A4%EB%B0%8B-%EC%97%B0%EB%8F%99-%EC%9E%90%EB%8F%99%ED%99%94)
[DB 마이그레이션 설정](https://velog.io/@tyo1012/LG-CNS-AM-Inspire-Camp-1%EA%B8%B0-%EC%B5%9C%EC%A2%85-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-7.-DB-%EB%A7%88%EC%9D%B4%EA%B7%B8%EB%A0%88%EC%9D%B4%EC%85%98-%EC%84%A4%EC%A0%95)
