# Shop QR — 인수인계 문서

문서 목적: 새 담당자가 저장소·배포·운영을 이어받을 때 필요한 맥락과 절차를 한곳에 정리한다.  
작성 기준: 저장소 루트 기준 (`docker-compose.yml`, `pom.xml`, `src/`, `.github/`).

---

## 1. 프로젝트 개요

- **이름**: Shop QR (shop-qr)
- **역할**: 식수(식사) QR 기반 확인, 일별 메뉴 표시, 관리자 승인·통계.
- **UI**: 별도 프론트엔드 저장소 없음. **Spring Boot + Thymeleaf** 서버 렌더링.
- **외부 연동**: 태블릿 등은 **REST JSON API** (`MealController`) 사용.

---

## 2. 기술 스택

| 구분 | 내용 |
|------|------|
| 백엔드 | Java 21, Spring Boot 3.3.x |
| 웹 | Spring MVC, Thymeleaf, Spring Security (세션·폼 로그인) |
| DB | PostgreSQL 16 (Docker 이미지) |
| ORM | Spring Data JPA, Hibernate |
| API 문서 | SpringDoc OpenAPI 2.x → Swagger UI |
| 테스트(선택) | JUnit 5, `spring-boot-starter-test`, Rest Assured (test scope) |
| 컨테이너 | Docker, Docker Compose |

---

## 3. 저장소 구조 (요약)

```
work/
├── pom.xml                  # Maven (루트가 애플리케이션 루트)
├── Dockerfile
├── src/main/java/com/shopqr/
├── src/main/resources/
│   ├── application.yml
│   ├── static/css/
│   └── templates/
├── docker-compose.yml       # db + Spring 서비스
├── .env.example
├── .env                     # Git 제외
├── scripts/
└── .github/workflows/
```

---

## 4. 실행 방법

### 4.1 사전 준비

1. 루트에 `.env` 생성: `.env.example`을 복사 후 `POSTGRES_PASSWORD` 등 반드시 설정.
2. Docker Desktop(또는 Docker Engine) + Compose plugin.

### 4.2 기동

```bash
docker compose up -d --build
```

- **웹 UI**: 브라우저에서 `http://localhost:8070` (호스트 8070 → 컨테이너 8080).
- **Swagger UI**: `http://localhost:8070/swagger-ui.html`
- **DB**: 호스트 `5432`로 매핑됨. 운영 시 **외부에 5432를 열지 않는 것**을 권장(방화벽·보안 그룹).

### 4.3 로컬 개발 (JAR만)

- DB를 띄운 뒤 `DB_HOST=localhost` 등 `.env`와 동일한 변수를 맞추고 저장소 루트에서 `mvn spring-boot:run` (Maven 설치 필요).

---

## 5. 환경 변수·비밀

| 변수 | 용도 |
|------|------|
| `POSTGRES_USER` | DB·앱 공통 사용자명 |
| `POSTGRES_PASSWORD` | DB 비밀번호 (비우지 말 것) |
| `POSTGRES_DB` | 데이터베이스 이름 |
| `DB_HOST` | Compose에서는 `db` (서비스명). 로컬 단독 실행 시 `localhost`. |

- `application.yml`은 위 변수로 JDBC URL·계정을 구성한다.
- **Git에 `.env`를 커밋하지 않는다.** (`.gitignore`에 포함)

---

## 6. 주요 URL·역할

| 경로 | 설명 |
|------|------|
| `/`, `/login`, `/signup` | 로그인·회원가입 |
| `/user/home`, `/user/qr.png` | 일반 사용자 홈, 본인 QR 이미지 |
| `/admin/dashboard` | 관리자(메뉴 등록, 승인 대기, 통계) |
| `/api/meal/qr-token/...`, `/api/meal/scan` | 외부 기기용 REST (Swagger 참고) |
| `/v3/api-docs`, `/swagger-ui.html` | OpenAPI·Swagger (Security에서 permitAll) |

**역할**: `User.Role` — `ADMIN`, `MANAGER`, `USER`.  
로그인 성공 시 `RoleBasedAuthenticationSuccessHandler`가 ADMIN은 `/admin/dashboard`, 그 외는 `/user/home`으로 보낸다.

**권한 없이 관리자 URL 접근 시**: `AdminAccessDeniedHandler`가 콘솔에 `권한 없음` 로그를 남기고 `/`로 리다이렉트한다.

---

## 7. 데이터·초기 데이터

- Docker 볼륨 `pgdata`에 PostgreSQL 데이터가 저장된다. `docker compose down -v` 시 **DB 데이터 삭제**에 해당한다.
- `DataInitializer`: DB가 비어 있을 때 샘플 사용자·관리자·당일 메뉴를 넣을 수 있다(코드 참고). 운영 전 **비밀번호·시드 데이터 검토** 권장.

---

## 8. CI/CD (GitHub Actions)

- **CI** (`.github/workflows/ci.yml`): 루트에서 `mvn -B verify`.
- **Deploy** (`.github/workflows/deploy.yml`): SSH로 서버에서 `git pull` 후 `docker compose build` / `up -d`.  
  필요한 Secrets: `SSH_HOST`, `SSH_USER`, `SSH_PRIVATE_KEY`, `DEPLOY_PATH`.  
  상세는 `.github/SETUP_REPOSITORY.txt` 참고.

---

## 9. 운영 시 체크리스트 (인수인계용)

- [ ] `.env` 또는 서버 전용 시크릿으로 DB 비밀번호·계정 확인.
- [ ] 최초 배포 또는 DB 볼륨 재생성 시 `POSTGRES_*`와 기존 볼륨이 **일치하는지** 확인 (불일치 시 컨테이너 기동 실패·인증 오류).
- [ ] 방화벽: 대외는 필요한 포트만(예: 8070, 22). DB 포트는 내부만.
- [ ] HTTPS 필요 시 리버스 프록시(Caddy, Nginx 등) 별도 구성.
- [ ] 로그 레벨: 운영에서는 `org.springframework.security` DEBUG 해제 검토 (`application.yml`).
- [ ] 백업: `pg_dump` 또는 볼륨 스냅샷 정책 수립.

---

## 10. 백로그 (후속 작업 후보)

제품·운영 개선을 다음 담당자가 이어갈 수 있도록 **후보**만 적어 둔다. 우선순위는 팀에서 재조정한다.

| 우선순위 (가정) | 항목 |
|-----------------|------|
| 높음 | 운영 프로필 분리 (`application-prod.yml`), 로그·SQL DEBUG 끄기 |
| 높음 | 관리자/사용자 비밀번호 정책·초기 시드 제거 또는 환경 변수화 |
| 중간 | HTTPS(리버스 프록시) 및 `server.forward-headers-strategy` 검토 |
| 중간 | Swagger를 운영에서 제한하거나 Basic Auth·IP 제한 |
| 중간 | REST Assured 기반 API 통합 테스트 시나리오 보강 |
| 낮음 | QR 만료·중복 식사 로직 비즈니스 규칙 문서화 및 단위 테스트 |
| 낮음 | DB 마이그레이션 도구(Flyway/Liquibase) 도입 검토 (`ddl-auto: update` 대체) |

---

## 11. 문의·연락

- 저장소: `https://github.com/momo-class55/work` (인수인계 시 실제 원격 URL을 확인할 것).
- 기술 스택·버전은 루트 `pom.xml`이 최종 기준이다.

---

*본 문서는 코드베이스 상태를 기준으로 작성되었으며, 배포 환경에 따라 포트·도메인·Secrets 이름은 달라질 수 있다.*
