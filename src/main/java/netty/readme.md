# Netty Echo Server (단일 메시지 처리 후 종료)

## 개요

이 프로젝트는 Netty 프레임워크를 사용하여 간단한 TCP Echo 서버를 구현합니다. 서버는 클라이언트로부터 단 하나의 TCP 메시지를 수신하고, 해당 메시지를 콘솔에 출력한 후 클라이언트에게 다시 에코(echo)합니다. 메시지 처리 후, 서버는 즉시 종료됩니다. 이 예제는 Netty의 기본적인 서버 설정, 핸들러 구현, 그리고 단일 요청 처리 후 서버를 종료하는 방법을 보여줍니다.

## 구현 상세

### `EchoServer.java`

`EchoServer` 클래스는 Netty 서버를 시작하고 관리하는 역할을 합니다.

*   **서버 부트스트랩 (Server Bootstrap)**: `ServerBootstrap`을 사용하여 서버를 설정합니다.
*   **이벤트 루프 그룹 (EventLoopGroup)**: `bossGroup`은 들어오는 연결을 수락하고, `workerGroup`은 수락된 연결의 트래픽을 처리합니다.
*   **채널 초기화 (Channel Initialization)**: `ChannelInitializer`를 통해 새로운 `SocketChannel`이 생성될 때 `EchoServerHandler`를 파이프라인에 추가합니다.
*   **즉시 종료 로직**: 서버는 지정된 포트에 바인딩된 후 `f.channel().close()`를 호출하여 즉시 스스로를 종료합니다. `f.channel().closeFuture().sync()`는 서버 종료 작업이 완료될 때까지 대기합니다.

### `EchoServerHandler.java`

`EchoServerHandler`는 클라이언트로부터 들어오는 데이터 이벤트를 처리하는 핸들러입니다.

*   **`channelActive(ChannelHandlerContext ctx)`**: 클라이언트가 서버에 연결될 때 호출되며, 연결 정보를 콘솔에 출력합니다.
*   **`channelRead(ChannelHandlerContext ctx, Object msg)`**: 클라이언트로부터 메시지가 수신될 때 호출됩니다.
    *   수신된 `ByteBuf` 메시지를 UTF-8 문자열로 변환하여 콘솔에 출력합니다.
    *   수신된 메시지를 `ctx.write(msg)`를 통해 클라이언트에게 다시 에코합니다.
    *   **메시지 처리 후 즉시 종료**: 메시지 에코 후 `ctx.close()`를 호출하여 해당 클라이언트 채널을 닫고, 이로 인해 서버의 전반적인 종료 프로세스가 시작됩니다.
*   **`channelReadComplete(ChannelHandlerContext ctx)`**: `channelRead` 호출 후, 현재 배치에 처리해야 할 메시지가 더 이상 없을 때 호출됩니다. `ctx.flush()`를 호출하여 대기 중인 모든 쓰기 작업을 즉시 네트워크로 전송합니다.
*   **`channelInactive(ChannelHandlerContext ctx)`**: 클라이언트와의 연결이 끊어졌을 때 호출되며, 연결 끊김 정보를 콘솔에 출력합니다.
*   **`exceptionCaught(ChannelHandlerContext ctx, Throwable cause)`**: 처리 중 예외가 발생했을 때 호출되며, 예외를 출력하고 채널을 닫습니다.

### `build.gradle`

*   **Netty 의존성**: `io.netty:netty-all` 라이브러리가 프로젝트에 포함되어 있습니다.
*   **`application` 플러그인**: `id 'application'` 플러그인이 적용되어 있으며, `mainClass = 'netty.EchoServer'`로 설정되어 있습니다. 이 설정을 통해 `./gradlew run` 명령으로 서버를 쉽게 실행할 수 있습니다.

## 사용 흐름

### 빌드 (Build)

프로젝트를 빌드하려면 다음 명령을 사용합니다. (이 명령은 `run` 명령에 자동으로 포함되므로, 별도로 실행할 필요는 없습니다.)

```bash
./gradlew build
```

### 서버 실행 (Run Server)

다음 명령을 실행하면 Netty Echo 서버가 8080 포트에서 시작됩니다. 서버는 시작된 후 즉시 종료되도록 설정되어 있으므로, 실행 후 바로 종료 메시지를 보게 될 것입니다.

```bash
./gradlew run
```

### 서버 테스트 (Test Server)

서버는 시작 후 즉시 종료되므로, 일반적인 방법으로는 테스트하기 어렵습니다. 서버의 "단일 메시지 처리 후 종료" 로직을 확인하려면, 서버가 **시작되는 순간** 매우 빠르게 연결하여 메시지를 보내야 합니다.

1.  **새 터미널에서 서버 실행**:
    ```bash
    ./gradlew run
    ```
    서버가 "Server started on port: 8080" 메시지를 출력하고 "Closing server..." 메시지를 출력하며 즉시 종료될 것입니다.
    만약 이 로직을 테스트하려면, `EchoServer.java`의 `run` 메소드에서 `f.channel().close();` 부분을 주석 처리하고 `f.channel().closeFuture().sync();`만 남겨두어 서버가 계속 실행되도록 한 다음, `telnet`이나 `nc`로 연결하여 메시지를 보낼 수 있습니다.
    `EchoServerHandler.java`에서 `ctx.close();`가 호출되면 클라이언트 연결이 끊어지고 서버도 종료될 것입니다.

    *   **`telnet` 사용 (서버 실행 상태 가정)**:
        ```bash
        telnet localhost 8080
        ```
    *   **`nc` (netcat) 사용 (서버 실행 상태 가정)**:
        ```bash
        nc localhost 8080
        ```

    접속한 후, 아무 텍스트나 입력하고 Enter 키를 누르면 서버 로그에 "Received: [입력한 텍스트]"가 출력되고, telnet/nc 클라이언트에도 동일한 텍스트가 다시 표시됩니다. 메시지를 보내면 서버가 종료될 것입니다.
