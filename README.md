# DMZ Zone 구축

이 디렉터리는 **Nginx를 이용한 DMZ Zone 구축**과 **Spring Boot로 Web Application Server 배포**를 학습하고 실습하는 공간이다.

---

## 프로젝트 목표

1. **Nginx를 통한 DMZ Zone 구축**: 안전한 3계층 네트워크 아키텍처를 구현한다
2. **Spring Boot WAS 배포**: 내부 Private Zone에서 회원 정보 조회 서비스를 운영한다
3. **보안 강화**: 외부-내부 구간의 안전한 통신 메커니즘을 이해한다
4. **Docker 네트워크 격리**: 공개망과 사설망을 물리적으로 분리한다

---

## 네트워크 아키텍처

### **3계층 구조**

```
Public Zone (Internet)
- 일반 사용자의 접속 구간
      ↓

DMZ Zone (Public-Net)
- Nginx 서버 위치
- 외부 접근 가능 (Port 80/443)
- 내부망 접근은 엄격히 통제
      ↓
      
Private Zone (Private-Net)
- WAS (Java/Node.js 등)
- Database
- 외부 인터넷과 완전히 격리
```

---

## 실습 시나리오: "안전한 회원 정보 조회 서비스"

### **1. 네트워크 설계 (Docker Virtual Network)**

Docker 브리지 네트워크를 두 개 만들어 완전히 격리된 효과를 준다.

- **Public-Net (172.19.0.0/16)**: 호스트와 통신하는 Nginx가 위치한다
- **Private-Net (172.20.0.0/16)**: 외부 접근이 완전히 차단된 Spring Boot WAS가 위치한다
- **Nginx**: 양쪽 네트워크에 연결되어 게이트웨이 역할을 수행한다

### **2. 통신 흐름**

```
1. User (Browser)
   └─> http://localhost:80 또는 http://localhost 접속

2. Nginx (Public Zone - DMZ 역할)
   └─> Public-Net(172.19.0.0/16)에서 80번 포트로 요청 수신
   └─> 정적 콘텐츠(/index.html) 또는 API 요청(/api/) 처리

3. Spring Boot WAS (Private Zone - 완전 격리)
   └─> Private-Net(172.20.0.0/16)에만 연결
   └─> Nginx의 프록시 요청만 수락 (컨테이너 이름으로 통신)
   └─> 메모리 저장소에서 회원 데이터 조회
   └─> JSON 응답을 Nginx에게 반환

4. Nginx (응답 반환)
   └─> WAS의 응답을 클라이언트에게 전달
```

### **3. 구성 요소**

| 계층 | 역할 | 도구 | 포트 | 네트워크 | 접근 범위 |
|------|------|------|------|---------|----------|
| Public Zone (DMZ) | 리버스 프록시 | Nginx | 80/443 | public-net | 호스트 접근 가능 |
| Public Zone (DMZ) | 게이트웨이 | Nginx | - | private-net | WAS 접근 가능 |
| Private Zone | Web Application Server | Spring Boot | 8080 | private-net | 호스트 접근 불가 |
| - | 정적 콘텐츠 | HTML/CSS/JS | - | - | Nginx에서 서빙 |

---

## 시작하기

### 전제 조건
- Docker 및 Docker Compose
- 기본적인 네트워크/보안 지식
- 포트 80/443이 사용 가능해야 함

### 빠른 시작

```bash
# 프로젝트 디렉터리 이동
cd /Users/seongjin/workspaces/Server/DMZ

# Docker Compose로 서비스 실행
docker-compose up -d

# 상태 확인
docker-compose ps

# 웹 브라우저에서 접속
# http://localhost 또는 http://localhost:80
```

### 실행 중인 서비스

- **Nginx (DMZ)**: http://localhost:80
  - 메인 페이지: 회원 정보 조회 인터페이스
  - API 엔드포인트: /api/members/*

- **Spring Boot WAS (Private Zone)**: localhost:8080 (호스트에서 접근 불가)
  - 컨테이너 이름: dmz-was
  - 포트: 8080 (Private Zone 내에서만 접근 가능)

### 서비스 중지 및 재시작

```bash
# 서비스 중지
docker-compose down

# 서비스 재시작
docker-compose restart

# 로그 확인
docker-compose logs -f nginx
docker-compose logs -f was
```

---

## 디렉터리 구조

```
DMZ/
├── README.md (이 파일)
├── docker-compose.yml (Docker 서비스 정의)
├── WAS_API_SPECIFICATION.md (API 명세서 - was/ 내부 참조)
│
├── config/
│   └── nginx/
│       ├── README.md (Nginx 설정 문서)
│       ├── nginx.conf (Nginx 메인 설정)
│       └── html/
│           └── index.html (회원 정보 조회 메인 페이지)
│
└── was/
    ├── README.md (WAS 설정 및 API 문서)
    ├── Dockerfile (Spring Boot 이미지 정의)
    ├── pom.xml (Maven 의존성)
    ├── src/
    │   ├── main/
    │   │   ├── java/com/dmz/was/
    │   │   │   ├── DmzWasApplication.java (메인 클래스)
    │   │   │   ├── controller/
    │   │   │   │   └── MemberController.java (REST API)
    │   │   │   ├── service/
    │   │   │   │   └── MemberService.java (비즈니스 로직)
    │   │   │   └── model/
    │   │   │       └── Member.java (데이터 모델)
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    └── logs/ (런타임 로그)
```

### 주요 설정 파일

- **docker-compose.yml**: Docker 서비스 정의
  - Nginx (public-net, private-net)
  - Spring Boot WAS (private-net only)

- **config/nginx/nginx.conf**: Nginx 서버 설정
  - 포트 80/443 수신
  - /api/ 리버스 프록시
  - 정적 콘텐츠 서빙

- **was/README.md**: WAS 상세 문서
  - API 명세서
  - 설정 가이드

---

## 핵심 기능

### Nginx (DMZ 게이트웨이)
- 호스트의 80/443 포트에서 HTTP 요청 수신
- 메인 페이지(index.html) 서빙
- /api/* 요청을 private-net의 WAS로 리버스 프록시
- 자동 헬스 체크 및 상태 표시

### Spring Boot WAS (회원 정보 조회 서비스)
- **전체 회원 조회**: GET /api/members
- **특정 회원 조회**: GET /api/members/{id}
- **회원 추가**: POST /api/members
- **회원 정보 수정**: PUT /api/members/{id}
- **회원 삭제**: DELETE /api/members/{id}
- **헬스 체크**: GET /api/members/health

### 데이터 모델 (Member)
```json
{
  "memberId": 0,
  "name": "string",
  "email": "string",
  "phone": "string",
  "department": "string",
  "joinDate": "YYYY-MM-DD"
}
```

### 초기 샘플 데이터
- 김철수 (개발팀)
- 이영희 (마케팅팀)
- 박민준 (영업팀)
- 정수진 (인사팀)

---

## 보안 특성

### 격리 수준
- **Public Zone (public-net)**: Nginx만 호스트 접근
- **Private Zone (private-net)**: WAS는 호스트에서 직접 접근 불가
- **네트워크 분리**: Docker 네트워크로 물리적 격리 구현

### 통신 제어
- WAS는 Nginx를 통한 요청만 수락 (같은 private-net)
- 외부 사용자는 Nginx를 통해서만 WAS 접근 가능
- 모든 API 요청은 Nginx의 리버스 프록시를 거침

### 현재 보안 수준
- ✅ 네트워크 격리
- ✅ 리버스 프록시 게이트웨이
- ✅ 접근 제어 (네트워크 레벨)
- ⚠️ 인증/인가 미구현
- ⚠️ HTTPS(SSL/TLS) 미구현
- ⚠️ 입력 값 검증 기본 수준

---

## 문제 해결

### 포트 충돌
```bash
# 80번 포트 사용 확인
sudo lsof -i :80

# 포트 변경 (docker-compose.yml)
ports:
  - "8080:80"  # 호스트의 8080을 컨테이너의 80으로 매핑
```

### WAS 연결 실패
```bash
# 네트워크 확인
docker network ls
docker network inspect dmz_private-net

# 컨테이너 확인
docker ps
docker logs dmz-was
```

### 페이지 로드 오류
```bash
# Nginx 로그 확인
docker logs -f dmz-nginx

# 설정 검증
docker exec dmz-nginx nginx -t
```

---

## 참고 자료

- [Nginx 공식 문서](https://nginx.org/en/docs/)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Docker 네트워크 가이드](https://docs.docker.com/network/)
- [DMZ 보안 아키텍처](https://en.wikipedia.org/wiki/Demilitarized_zone_(computing))