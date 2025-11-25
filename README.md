
---

## 2️⃣ `chatjar-android`용 README.md (Android 클라이언트)

```markdown
# Chatjar Android

랜덤 채팅 서비스 **Chatjar**의 안드로이드 클라이언트 레포지토리입니다.  
유저는 앱을 통해 회원가입/로그인, 랜덤 매칭, 1:1 채팅, 친구 및 그룹 채팅 기능을 사용할 수 있습니다.

---

## 🧩 주요 기능

- 회원가입 / 로그인 화면
- 랜덤 매칭 시작/취소
- 매칭된 유저와 1:1 채팅 화면
- 친구 목록 조회 / 친구 추가
- 친구와의 1:1 채팅
- 친구를 초대한 그룹 채팅

---

## 🛠 기술 스택

- **Language**: Java (또는 Kotlin 도입 예정 시 표기)
- **IDE**: Android Studio
- **Min SDK**: (예정, 예: 24)
- **Network (REST)**: Retrofit2 + OkHttp
- **Real-time (WebSocket)**:
  - STOMP 클라이언트 라이브러리 (예정)
- **아키텍처 패턴**: (예정) MVVM
- **UI**: XML 기반 레이아웃

---

## 📱 화면 구성 (예정)

- **Splash / Intro**
- **Login / Sign-up**
- **Home**
  - 랜덤 매칭 버튼
  - 친구 목록 진입
- **Chat Room (1:1)**
- **Group Chat Room**
- **Friend List / Friend Search**

---

## 📁 프로젝트 구조 (예정)

```text
app/src/main/java/com.chatjar.android
 ├─ ui
 │   ├─ auth       # 로그인 / 회원가입 화면
 │   ├─ home       # 메인 / 매칭 버튼
 │   ├─ chat       # 채팅 관련 화면 및 Adapter
 │   └─ friend     # 친구 목록 / 친구 검색
 ├─ data
 │   ├─ api        # Retrofit 인터페이스
 │   ├─ model      # 서버와 주고 받는 DTO, ViewModel용 데이터
 │   └─ socket     # WebSocket / STOMP 클라이언트
 ├─ util           # 공용 유틸, 토큰 저장 등
 └─ ...
