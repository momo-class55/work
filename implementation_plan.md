# [Shop QR System (식수 관리 시스템)]

식당의 효율적인 장부 관리와 기업 고객의 식수 체크를 자동화하기 위한 QR 기반 솔루션입니다. 
Apple Style의 직관적인 UI, 중복 식사 방지 로직, 그리고 AES256 QR 보안을 핵심 가치로 합니다.

## 핵심 기능 및 구현 현황

### 1. 보안 및 인증 (Security & Auth)
- **비밀번호 암호화**: Spring Security와 BCryptPasswordEncoder를 사용하여 사용자 비밀번호를 안전하게 관리.
- **CORS 설정**: 프론트엔드(Port 8081)와 백엔드(Port 8080) 간의 원활한 통신을 위한 전역 CORS 및 컨트롤러 레벨 설정 완료.
- **권한 관리**: `ADMIN`, `USER` 권한 분리 및 관리자 승인 로직 구현.
- **자동 로그인**: `localStorage`를 활용하여 브라우저 재접속 시에도 세션 유지 및 자동 로그인 기능 구현.
- **아이디 저장**: 로그인 시 '아이디 저장' 체크 시 전화번호를 기억하여 편의성 제공.

### 2. 사용자 경험 (UX/UI)
- **Apple Aesthetics**: 깨끗한 화이트 배경과 둥근 모서리(24px), 블루 포인트 컬러를 활용한 프리미엄 디자인.
- **QR 코드 자동 노출**: 로그인 즉시 사용자의 식수 체크용 QR 코드를 화면에 표시하여 결제 단계 단축.
- **실시간 메뉴 정보**: 서버로부터 오늘의 메뉴와 날짜를 동적으로 받아와 메인 카드에 표시.
- **로그아웃 로직**: 로그아웃 시 자동 로그인 정보를 파기하되, '아이디 저장' 설정에 따라 전화번호는 유지.

### 3. 백엔드 아키텍처 (Backend Architecture)
- **기술 스택**: Java 21, Spring Boot 3.3.4, Spring Data JPA.
- **데이터베이스**: PostgreSQL 16 (Docker Compose 기반).
- **QR 생성**: `Google ZXing` 라이브러리를 활용한 서버 사이드 QR 이미지 렌더링.
- **데이터 초기화**: `DataInitializer`를 통해 테스트용 사용자(`01012345678`), 관리자(`01000000000`), 그리고 오늘의 메뉴 자동 생성.

---

## 작업 완료 사항 (Checked)

- [x] **Frontend Entry Point**: `index.js` 생성 및 Expo 설정 오류 해결.
- [x] **CORS Issue**: 백엔드 SecurityConfig 및 Controller 어노테이션 수정.
- [x] **UI Enhancement**: QR 코드 즉시 노출 및 메뉴 날짜 표시.
- [x] **Persistence**: 자동 로그인 및 아이디 저장 기능 (LocalStorage).
- [x] **Test Data**: User, Admin, DailyMenu 샘플 데이터 구축.

## 향후 계획 (Next Steps)

- **알림 서비스**: 매일 오전 9시 점심 메뉴 푸시 알림 연동.
- **통계 고도화**: 관리자 대시보드 내 기업별/일별 식수 통계 그래프(ApexCharts 등) 도입.
- **QR 보안 강화**: AES256 암호화 및 타임스탬프 검증 로직 상세 적용.

---

## 실행 방법

### Backend
`mvn spring-boot:run` (Port: 8080)

### Frontend
`npm run web` (Port: 8081)

### Database
`docker-compose up -d` (Port: 5432)