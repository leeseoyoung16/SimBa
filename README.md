## ⚙️ 기술 스택

### 👩‍💻 백엔드
- Java 17  
- Spring Boot 3.5.3  
- Spring Web / Spring Data JPA  
- Spring Security  
- Lombok / Validation  
- MySQL  
- Swagger (API 문서화)  
- Postman (API 테스트)  

### 🤖 AI / 외부 API
- Google Gemini API (리뷰 요약 기능)  

  ---
## 🛠️ 진행 상황

| **차시** | **항목** | **상태** | **비고** |
| --- | --- | --- | --- |
| 1차시 | 기능 개발 & 유저 플로우 | ✅ | 유저 플로우 작성 |
| 2차시 | ERD 작성 | ✅ | ERD 작성 |
| 3차시 | 로그인, 회원가입 기능 | ✅ | 세션 기반 로그인 및 회원가입 기능 개발 |
| 4차시 | Store | ✅ | 가게 CRUD 기능 |
| 5차시 | Mission | ✅ | 미션 CRUD 기능 |
| 6차시 | Review | ✅ | 리뷰 CRUD 기능 |
| 7차시 | Voucher | ✅ | 바우처 CRUD 기능 |
| 8차시 | Summary | ✅ | 리뷰 요약 ai 기능 |
| 9차시 | 수정 | ✅ | itda 프리픽스 api 설정 |
| 10차시 | 수정 | ✅ | 로그아웃 기능 추가 |

---
## 📝 프로젝트 정리
[노션 페이지](https://longhaired-stove-0a0.notion.site/Simba-244c5950949880a5989bd47ea21236f2?pvs=74)


---
## api 정리

## 🔐 사용자 인증

| 메서드 | URI | 설명 | 권한 |
| --- | --- | --- | --- |
| **POST** | `/itda/auth/register` | 회원 가입 | ALL |
| **POST** | `/itda/auth/login` | 로그인 | ALL |
| **POST** | `/itda/auth/logout` | 로그아웃 | USER |

---

## 🏪 가게(Store)

| 메서드 | URI | 설명 | 권한 |
| --- | --- | --- | --- |
| **POST** | `/itda/stores` | 가게 등록 | USER |
| **DELETE** | `/itda/stores/{storeId}` | 가게 삭제 | USER(본인), ADMIN  |
| **GET** | `/itda/stores?category={category}` | 카테고리별 가게 조회 (`RESTAURANT`, `CAFE`, `ETC` ) | ALL |
| **GET** | `/itda/stores` | 가게 목록 조회 | ALL |
| **GET** | `/itda/stores/{storeId}` | 가게 단건 조회 | ALL |
| **GET** | `/itda/stores/{storeId}/summary` | 가게 별 리뷰 (최신 500개 기준) 한줄 요약 | ALL |

## 🎯 미션(Mission)

| 메서드 | URI | 설명 | 권한 |
| --- | --- | --- | --- |
| **POST** | `/itda/missions` | 미션 생성 | OWNER |
| **PATCH** | `/itda/missions/{missionId}` | 미션 수정(미션명, 미션 설명, 미션 끝나는 시간) | OWNER(본인) |
| **DELETE** | `/itda/missions/{missionId}` | 미션 삭제 | OWNER(본인) |
| **GET** | `/itda/missions/{missionId}` | 미션 단건 조회 | ALL |
| **GET** | `/itda/missions/joinable` | 현재 진행 가능 미션 전체 조회 | ALL |
| **GET** | `/itda/missions?storeId={storeId}` | 가게 별 미션 조회 | ALL |
| **GET** | `/itda/missions?status={status}` | 미션 상태 별 조회(`SCHEDULED`, `ONGOING`, `ENDED`) | ALL |

## ✉️ 리뷰(Review)

| 메서드 | URI | 설명 | 권한 |
| --- | --- | --- | --- |
| **POST** | `/itda/missions/{missionId}/reviews` | 리뷰 생성
 (별점·내용·이미지 업로드) | USER |
| **PATCH** | `/itda/reviews/{reviewId}` | 리뷰 부분 수정(별점/내용/이미지) **PENDING일 때만 가능** | USER(본인) |
| **DELETE** | `/itda/reviews/{reviewId}` | 리뷰 삭제  | USER(본인) / ADMIN |
| **PATCH** | `/itda/reviews/{reviewId}/status={status}` | 리뷰 상태 변경 (`APPROVED`/`REJECTED`) | OWNER(본인) / ADMIN |
| **GET** | `/itda/reviews/{reviewId}` | 리뷰 단건 조회 | ALL |
| **GET** | `/itda/reviews?userId={userId}` | 사용자 별 리뷰 목록 조회 | ALL |
| **GET** | `/itda/reviews?storeId={storeId}` | 가게 별 리뷰 목록 조회 | ALL |

## 🎟️ 바우처(Voucher)

| 메서드 | URI | 설명 | 권한 |
| --- | --- | --- | --- |
| **GET** | `/itda/me/vouchers?filter={filter}` | 내 바우처 목록 조회. (`ISSUED`(default), `USED`, `EXPIRED`) | USER(본인) |
| **GET** | `/itda/me/vouchers/{voucherId}` | 내 바우처 단건 조회 | USER(본인) |
| **PATCH** | `/itda/me/vouchers/{voucherId}/use` | 내 바우처 사용 처리  | USER(본인) |
| **DELETE** | `/itda/vouchers/{voucherId}` | 바우처 삭제 (운영용) | ADMIN |


