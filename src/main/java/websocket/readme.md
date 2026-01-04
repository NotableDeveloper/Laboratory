# Netty 웹소켓 에코 서버

이 디렉토리에는 Netty를 사용하여 구현된 웹소켓 에코 서버가 포함되어 있습니다.

## 패키지 구성

`websocket` 패키지는 다음과 같은 클래스들로 구성되어 있습니다.

*   `WebSocketServer.java`: 애플리케이션의 메인 진입점입니다. Netty `ServerBootstrap`을 설정하고, 이벤트 루프(`bossGroup`, `workerGroup`)를 구성하며, 지정된 포트(기본값: 8081)에 서버를 바인딩합니다. 새로운 클라이언트 연결이 생성될 때마다 `WebSocketServerInitializer`를 사용하여 채널 파이프라인을 초기화합니다.

*   `WebSocketServerInitializer.java`: Netty 채널 파이프라인을 초기화하는 클래스입니다. HTTP 및 웹소켓 트래픽을 처리하기 위해 다음과 같은 핸들러들을 파이프라인에 추가합니다.
    *   `HttpServerCodec`: HTTP 요청과 응답을 인코딩/디코딩합니다.
    *   `HttpObjectAggregator`: 여러 조각으로 나뉜 HTTP 메시지를 하나의 `FullHttpRequest` 또는 `FullHttpResponse` 객체로 합칩니다.
    *   `HttpHandler`: `WebSocketClient.html` 파일을 제공하고 기본적인 HTTP 요청을 처리하는 커스텀 핸들러입니다.
    *   `WebSocketServerCompressionHandler`: 웹소켓 압축을 지원합니다.
    *   `WebSocketServerProtocolHandler`: 웹소켓 핸드셰이크와 핑/퐁과 같은 프로토콜 수준의 메시지를 처리합니다. `/websocket` 경로의 요청을 처리하도록 설정되어 있습니다.
    *   `WebSocketFrameHandler`: 실제 웹소켓 데이터 프레임(이 예제에서는 텍스트 메시지)을 처리하는 커스텀 핸들러입니다.

*   `HttpHandler.java`: 사용자가 서버의 루트 URL(`/`)에 접속했을 때 `WebSocketClient.html` 파일을 제공하는 역할을 합니다. 클래스패스 리소스에서 HTML 파일을 읽어 HTTP 응답으로 전송합니다. 그 외의 경로로 들어온 요청은 파이프라인의 다음 핸들러로 전달합니다.

*   `WebSocketFrameHandler.java`: "에코" 기능의 핵심 로직을 담당합니다. `WebSocketFrame` 객체를 처리하며, `TextWebSocketFrame`을 수신하면 텍스트를 추출하여 대문자로 변환한 후, 새로운 `TextWebSocketFrame`에 담아 클라이언트에게 다시 전송합니다. 지원하지 않는 프레임 타입에 대해서는 예외를 발생시킵니다.

## 실행 방법

1.  **프로젝트 빌드:**
    프로젝트의 루트 디렉토리에서 터미널을 열고 다음 명령어를 실행하여 프로젝트를 빌드합니다.

    ```bash
    ./gradlew build
    ```

2.  **서버 실행:**
    빌드가 완료된 후, `WebSocketServer` 클래스의 `main` 메소드를 실행하여 서버를 시작할 수 있습니다.

    IDE에서 직접 실행하거나 다음 gradle 명령어를 사용할 수 있습니다.

    ```bash
    ./gradlew run
    ```
    (참고: `build.gradle` 파일에 `main` 클래스가 지정되어 있지 않다면 `run` 태스크를 설정해야 할 수 있습니다.)

    또는, 빌드 후 다음 명령어를 사용하여 커맨드 라인에서 애플리케이션을 실행할 수도 있습니다.

    ```bash
    java -cp "build/classes/java/main;build/libs/*" websocket.WebSocketServer
    ```

## 테스트 방법

1.  서버가 실행되면, 웹 브라우저를 열고 `http://127.0.0.1:8081/` 로 접속하거나 웹소켓 클라이언트(예: [https://www.piesocket.com/websocket-tester](https://www.piesocket.com/websocket-tester) 와 같은 온라인 도구)를 엽니다.
2.  웹페이지의 "Connect" 버튼을 누르거나, 웹소켓 클라이언트에서 `ws://127.0.0.1:8081/websocket` 주소로 접속합니다.
3.  텍스트 메시지를 보내면, 서버는 해당 메시지를 대문자로 변환하여 다시 클라이언트에게 보냅니다.