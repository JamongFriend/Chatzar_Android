
---

## 2️⃣ `chatzar-android`용 README.md (Android 클라이언트)

```markdown
# Chatzar Android

랜덤 채팅 서비스 **Chatzar**의 안드로이드 클라이언트 레포지토리입니다.  
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
com.example.chatzar_android
├── App.kt                         // Application 클래스(Hilt/전역 초기화)
├── MainActivity.kt                // 앱 진입점
│
├── navigation/                    // 네비게이션(Route, NavGraph)
│   ├── Routes.kt
│   └── NavGraph.kt
│
├── core/                          // 앱 전역 공통 모듈
│   ├── di/                        // DI 모듈(Hilt)
│   ├── network/                   // Retrofit/OkHttp 공통 설정
│   │   ├── interceptor/           // Auth/Logging/Retry 등
│   │   └── adapter/               // Json Adapter(날짜, enum 등)
│   ├── websocket/                 // STOMP(WebSocket) 공통
│   │   ├── stomp/                 // connect/subscribe/send 래퍼
│   │   └── model/                 // 소켓 메시지 공통 모델
│   └── common/                    // 유틸/공용 UI/상태 모델
│       ├── util/
│       ├── ui/
│       └── result/                // ApiResult, UiState, Error 등
│
├── data/                          // 데이터 계층(서버/로컬)
│   ├── remote/
│   │   ├── api/                   // Retrofit interface
│   │   └── dto/                   // Request/Response DTO
│   ├── local/                     // Room 사용 시
│   │   ├── db/
│   │   ├── dao/
│   │   └── entity/
│   ├── mapper/                    // DTO <-> Domain 변환
│   └── repository/                // Repository 구현체
│
├── domain/                         // 도메인 계층(순수 로직)
│   ├── model/                     // 핵심 모델 (Member, ChatRoom, Message...)
│   ├── repository/                // Repository interface
│   └── usecase/                   // UseCase (Login, Match, SendMessage...)
│
└── feature/                        // 기능(화면) 단위
    ├── auth/
    │   ├── ui/                    // Screen(Compose)/Component
    │   ├── vm/                    // ViewModel
    │   └── model/                 // UiState/UiModel
    ├── match/
    │   ├── ui/
    │   ├── vm/
    │   └── model/
    ├── chat/
    │   ├── ui/
    │   ├── vm/
    │   └── model/
    └── friend/                     // 친구 기능(선택)
        ├── ui/
        ├── vm/
        └── model/
