# Nginx 설정 문서

## 개요

이 디렉터리는 DMZ Zone의 Nginx 서버 설정을 관리한다. Nginx는 Public Zone에 위치하여 외부 요청을 받아 Private Zone의 WAS로 리버스 프록시하는 역할을 수행한다.

---

## 파일 구조

```
config/nginx/
├── README.md              # 이 파일
├── nginx.conf             # Nginx 메인 설정 파일
└── html/
    └── index.html         # 회원 정보 조회 메인 페이지
```

---

## 네트워크 아키텍처

```
Host (Port 80/443)
    ↓
Nginx (Public Zone - public-net)
    ↓ (리버스 프록시)
    ↓ (Private Zone - private-net)
WAS (Port 8080)
```

- **Public Zone (public-net)**: Nginx가 호스트와 통신한다.
- **Private Zone (private-net)**: WAS가 완전히 격리되어 있다.
- **Nginx**: 두 네트워크의 게이트웨이 역할을 수행한다.

---

## Nginx 설정 파일 (nginx.conf)

### 기본 설정

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;
```

- `user nginx`: Nginx 프로세스를 nginx 사용자로 실행한다.
- `worker_processes auto`: 사용 가능한 CPU 코어 수에 맞춰 워커 프로세스를 자동으로 설정한다.
- `error_log`: 에러 로그를 `/var/log/nginx/error.log`에 기록한다.

### 이벤트 설정

```nginx
events {
    worker_connections 1024;
}
```

- `worker_connections 1024`: 각 워커 프로세스가 동시에 처리할 수 있는 최대 연결 수를 설정한다.

### HTTP 블록 주요 설정

```nginx
http {
    sendfile on;                      # 커널 버퍼를 직접 클라이언트에 전송
    tcp_nopush on;                    # TCP 패킷을 최적화하여 전송
    tcp_nodelay on;                   # TCP 딜레이 비활성화
    keepalive_timeout 65;             # Keep-Alive 타임아웃 설정
    types_hash_max_size 2048;         # MIME 타입 해시 테이블 크기
    client_max_body_size 20M;         # 클라이언트 요청 본문 최대 크기
}
```

---

## 서버 설정 (Server Block)

### 포트 및 기본 설정

```nginx
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;
}
```

- `listen 80`: 80번 포트에서 HTTP 요청을 수신한다.
- `server_name _`: 모든 도메인/IP의 요청을 수신한다.
- `root`: 정적 파일의 루트 디렉터리를 설정한다.

### 메인 페이지 (Location /)

```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

- 정적 파일을 먼저 찾고, 없으면 `/index.html`을 제공한다.
- SPA(Single Page Application)를 지원하기 위한 설정이다.

### API 리버스 프록시 (Location /api/)

```nginx
location /api/ {
    proxy_pass http://was:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_redirect off;
}
```

**기본 프록시 설정**
- `proxy_pass http://was:8080`: Private Zone의 WAS로 요청을 전달한다.
  - `was`는 Docker Compose에서 정의한 컨테이너 이름이다.
  - private-net에 연결되어 있으므로 DNS 해석이 된다.

**헤더 설정**
- `proxy_set_header Host`: 원본 호스트 정보를 전달한다.
- `proxy_set_header X-Real-IP`: 클라이언트의 실제 IP를 전달한다.
- `proxy_set_header X-Forwarded-For`: 프록시를 거친 클라이언트 IP 체인을 기록한다.
- `proxy_set_header X-Forwarded-Proto`: 원본 프로토콜(HTTP/HTTPS)을 전달한다.
- `proxy_redirect off`: 리다이렉트 URL 변경을 비활성화한다.

### 에러 페이지

```nginx
error_page 404 /404.html;
error_page 500 502 503 504 /50x.html;

location = /50x.html {
    root /usr/share/nginx/html;
}
```

- 404 및 5xx 에러 발생 시 정적 에러 페이지를 제공한다.

---

## 로깅 설정

```nginx
log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                '$status $body_bytes_sent "$http_referer" '
                '"$http_user_agent" "$http_x_forwarded_for"';

access_log /var/log/nginx/access.log main;
```

- 모든 HTTP 요청을 `/var/log/nginx/access.log`에 기록한다.
- 로그 포맷에는 클라이언트 IP, 요청 시간, 상태 코드, 요청 크기 등이 포함된다.

---

## 주요 기능

### 1. 정적 콘텐츠 서빙
- `/`: 회원 정보 조회 메인 페이지 (HTML/CSS/JavaScript)

### 2. 리버스 프록시
- `/api/`: 모든 API 요청을 내부 WAS로 전달한다.
- WAS의 주소를 외부에 노출하지 않는다.

### 3. 보안
- Private Zone의 WAS는 Nginx를 통해서만 접근 가능하다.
- 외부에서 WAS에 직접 접근할 수 없다.

### 4. 성능 최적화
- Keep-Alive 연결을 유지한다.
- TCP 설정으로 네트워크 성능을 최적화한다.

---

## 설정 수정 가이드

### API 엔드포인트 추가

새로운 API 경로를 추가하려면 다음과 같이 설정한다:

```nginx
location /api/v2/ {
    proxy_pass http://was:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

### HTTPS 설정 추가

SSL 인증서를 추가하려면:

```nginx
server {
    listen 443 ssl http2;
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    # ... 기타 설정
}

# HTTP를 HTTPS로 리다이렉트
server {
    listen 80;
    return 301 https://$server_name$request_uri;
}
```

### 로드 밸런싱 설정

여러 WAS 인스턴스가 있는 경우:

```nginx
upstream was_backend {
    server was1:8080;
    server was2:8080;
    server was3:8080;
}

location /api/ {
    proxy_pass http://was_backend;
    proxy_set_header Host $host;
    # ... 기타 헤더 설정
}
```

---

## 트러블슈팅

### WAS에 연결할 수 없음

**원인**: 네트워크 설정 오류 또는 WAS가 실행되지 않음

**해결 방법**:
```bash
# Docker 네트워크 확인
docker network ls
docker network inspect dmz_private-net

# WAS 컨테이너 확인
docker ps | grep dmz-was

# Nginx 로그 확인
docker logs dmz-nginx
```

### 포트 충돌

**원인**: 호스트의 80번 또는 443번 포트가 이미 사용 중

**해결 방법**:
```bash
# 포트 확인
sudo lsof -i :80
sudo lsof -i :443

# docker-compose.yml에서 포트 변경
# ports:
#   - "8080:80"      # 호스트의 8080을 컨테이너의 80으로 매핑
#   - "8443:443"
```

### 성능 저하

**최적화 방법**:
1. `worker_processes` 값을 증가시킨다.
2. `worker_connections` 값을 증가시킨다.
3. `keepalive_timeout` 값을 조정한다.
4. 캐싱 설정을 추가한다.

---

## Docker 실행 명령어

### 설정 파일 검증

```bash
docker run --rm -v /path/to/nginx.conf:/etc/nginx/nginx.conf nginx nginx -t
```

### 컨테이너 시작

```bash
docker-compose up nginx
```

### 로그 확인

```bash
docker logs -f dmz-nginx
```

### 설정 리로드

```bash
docker exec dmz-nginx nginx -s reload
```

---