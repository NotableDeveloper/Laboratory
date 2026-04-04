# DMZ WAS - REST API 문서

## 개요

DMZ Private Zone에 배포된 Spring Boot 기반의 회원 정보 조회 서비스로 구성되어 있다.
**Base URL**: `http://localhost:8080/api/members`

---

## Health Check Endpoint

### 1. 헬스 체크
서버의 상태를 확인하는 엔드포인트이다.

**Request**
```
GET /api/members/health
```

**Response**
```json
{
  "status": "UP",
  "service": "DMZ WAS - Private Member Service"
}
```

**HTTP Status**: `200 OK`

---

## Member CRUD Endpoints

### 2. 모든 회원 정보 조회
전체 회원 목록을 조회한다.

**Request**
```
GET /api/members
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "memberId": 1,
      "name": "김철수",
      "email": "kim@company.com",
      "phone": "010-1234-5678",
      "department": "개발팀",
      "joinDate": "2023-01-15"
    },
    {
      "memberId": 2,
      "name": "이영희",
      "email": "lee@company.com",
      "phone": "010-2345-6789",
      "department": "마케팅팀",
      "joinDate": "2023-03-20"
    }
  ],
  "message": "회원 조회 성공"
}
```

**HTTP Status**: `200 OK`

---

### 3. 특정 회원 정보 조회
회원 ID를 기반으로 특정 회원 정보를 조회한다.

**Request**
```
GET /api/members/{memberId}
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| memberId | Long | Yes | 조회할 회원의 ID |

**Example**
```
GET /api/members/1
```

**Response (성공)**
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "name": "김철수",
    "email": "kim@company.com",
    "phone": "010-1234-5678",
    "department": "개발팀",
    "joinDate": "2023-01-15"
  },
  "message": "회원 조회 성공"
}
```

**HTTP Status**: `200 OK`

**Response (실패 - 회원 미존재)**
```json
{
  "success": false,
  "message": "해당 회원을 찾을 수 없습니다. (ID: 999)"
}
```

**HTTP Status**: `404 Not Found`

---

### 4. 새 회원 추가
새로운 회원을 시스템에 등록한다.

**Request**
```
POST /api/members
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "홍길동",
  "email": "hong@company.com",
  "phone": "010-9999-8888",
  "department": "IT팀",
  "joinDate": "2024-04-04"
}
```

**Body Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | 회원 이름 |
| email | String | Yes | 이메일 주소 |
| phone | String | Yes | 전화번호 |
| department | String | Yes | 부서명 |
| joinDate | String | Yes | 입사 날짜 (YYYY-MM-DD) |

**Response**
```json
{
  "success": true,
  "data": {
    "memberId": 5,
    "name": "홍길동",
    "email": "hong@company.com",
    "phone": "010-9999-8888",
    "department": "IT팀",
    "joinDate": "2024-04-04"
  },
  "message": "회원 추가 성공"
}
```

**HTTP Status**: `201 Created`

---

### 5. 회원 정보 수정
기존 회원의 정보를 업데이트한다.

**Request**
```
PUT /api/members/{memberId}
Content-Type: application/json
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| memberId | Long | Yes | 수정할 회원의 ID |

**Request Body**
```json
{
  "name": "김철수 수정",
  "email": "kim.updated@company.com",
  "phone": "010-1111-2222",
  "department": "개발팀",
  "joinDate": "2023-01-15"
}
```

**Body Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | String | Yes | 회원 이름 |
| email | String | Yes | 이메일 주소 |
| phone | String | Yes | 전화번호 |
| department | String | Yes | 부서명 |
| joinDate | String | Yes | 입사 날짜 |

**Example**
```
PUT /api/members/1
```

**Response (성공)**
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "name": "김철수 수정",
    "email": "kim.updated@company.com",
    "phone": "010-1111-2222",
    "department": "개발팀",
    "joinDate": "2023-01-15"
  },
  "message": "회원 정보 수정 성공"
}
```

**HTTP Status**: `200 OK`

**Response (실패 - 회원 미존재)**
```json
{
  "success": false,
  "message": "해당 회원을 찾을 수 없습니다. (ID: 999)"
}
```

**HTTP Status**: `404 Not Found`

---

### 6. 회원 정보 삭제
회원을 시스템에서 제거한다.

**Request**
```
DELETE /api/members/{memberId}
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| memberId | Long | Yes | 삭제할 회원의 ID |

**Example**
```
DELETE /api/members/4
```

**Response (성공)**
```json
{
  "success": true,
  "message": "회원 삭제 성공 (ID: 4)"
}
```

**HTTP Status**: `200 OK`

**Response (실패 - 회원 미존재)**
```json
{
  "success": false,
  "message": "해당 회원을 찾을 수 없습니다. (ID: 999)"
}
```

**HTTP Status**: `404 Not Found`

---

## 응답 형식

모든 API는 다음의 기본 응답 형식을 따르고 있다.

```json
{
  "success": boolean,
  "data": object | null,
  "message": string
}
```

**필드 설명**
| 필드 | 타입 | 설명 |
|------|------|------|
| success | Boolean | 요청 성공 여부 |
| data | Object | 응답 데이터 (조회/생성/수정의 경우만 포함) |
| message | String | 응답 메시지 |

---

## curl을 이용한 테스트 예제

### 헬스 체크
```bash
curl -X GET http://localhost:8080/api/members/health
```

### 모든 회원 조회
```bash
curl -X GET http://localhost:8080/api/members
```

### 특정 회원 조회
```bash
curl -X GET http://localhost:8080/api/members/1
```

### 새 회원 추가
```bash
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "박민식",
    "email": "park@company.com",
    "phone": "010-5555-6666",
    "department": "영업팀",
    "joinDate": "2024-04-04"
  }'
```

### 회원 정보 수정
```bash
curl -X PUT http://localhost:8080/api/members/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "김철수 수정",
    "email": "kim.updated@company.com",
    "phone": "010-1111-2222",
    "department": "개발팀",
    "joinDate": "2023-01-15"
  }'
```

### 회원 정보 삭제
```bash
curl -X DELETE http://localhost:8080/api/members/4
```

### JSON 포맷팅하여 출력
```bash
curl -s http://localhost:8080/api/members | jq .
```

---

## Member 모델 정의

```json
{
  "memberId": 0,
  "name": "string",
  "email": "string",
  "phone": "string",
  "department": "string",
  "joinDate": "string (YYYY-MM-DD)"
}
```

**필드 설명**
| 필드 | 타입 | 설명 |
|------|------|------|
| memberId | Long | 회원 고유 ID (자동 할당) |
| name | String | 회원 이름 |
| email | String | 이메일 주소 |
| phone | String | 전화번호 |
| department | String | 부서명 |
| joinDate | String | 입사 날짜 (YYYY-MM-DD 형식) |

---

## 초기 샘플 데이터

| ID | 이름 | 이메일 | 전화번호 | 부서 | 입사일 |
|------|------|--------|-----------|--------|----------|
| 1 | 김철수 | kim@company.com | 010-1234-5678 | 개발팀 | 2023-01-15 |
| 2 | 이영희 | lee@company.com | 010-2345-6789 | 마케팅팀 | 2023-03-20 |
| 3 | 박민준 | park@company.com | 010-3456-7890 | 영업팀 | 2023-06-10 |
| 4 | 정수진 | jung@company.com | 010-4567-8901 | 인사팀 | 2023-09-05 |

---

## 주의사항

- **메모리 저장소**: 현재 모든 데이터는 메모리에 저장된다. 애플리케이션을 재시작하면 초기 데이터로 초기화된다.
- **Private Zone**: 이 WAS는 DMZ를 통해서만 접근할 수 있으며, 외부에서 직접 접근할 수 없다.
- **실시간 통신**: 현재 데이터베이스가 없다. 실제 운영 환경에서는 데이터베이스(MySQL, PostgreSQL 등)를 추가해야 한다.

---