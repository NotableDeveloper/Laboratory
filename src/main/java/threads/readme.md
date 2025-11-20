# 스레드 구현 방식 비교: Legacy vs. Latest

이 디렉토리의 `legacy`와 `latest` 패키지는 Java에서 멀티스레딩을 구현하는 두 가지 다른 접근 방식을 보여줍니다.

- `threads.legacy`: `Thread` 클래스를 직접 상속하고 `wait`/`notify`를 사용하여 스레드 간 통신을 수동으로 구현하는 전통적인 방식입니다.
- `threads.latest`: `ExecutorService`와 `BlockingQueue` 등 `java.util.concurrent` 패키지의 고수준 API를 사용하는 현대적인 방식입니다.

## 1. `threads.legacy` 패키지

이 패키지는 저수준의 스레딩 제어 기법을 사용합니다.

- **스레드 생성**: `Thread` 클래스를 직접 상속하여 `PrintWorker`와 `WriteWorker`를 구현합니다.
- **스레드 관리**: `Thread.start()`로 스레드를 시작하고, `Thread.interrupt()`와 `Thread.join()`을 사용하여 스레드를 직접 제어하고 종료합니다.
- **데이터 공유**: `wait()`, `notifyAll()`, `synchronized` 키워드를 사용하여 스레드에 안전한 `LinkedQueue`를 직접 구현했습니다. 이 큐는 작업자 스레드 간의 데이터 파이프라인 역할을 합니다.

이 방식은 스레드 동작의 모든 측면을 직접 제어해야 하므로 코드가 복잡해지고, 교착 상태(deadlock)나 경쟁 조건(race condition)과 같은 동시성 문제를 유발할 가능성이 높습니다.

이러한 저수준 제어는 `wait()` 호출이 `notify()` 호출보다 먼저 실행되도록 보장하는 로직, 혹은 신호가 유실되는 '신호 유실(lost wakeup)' 문제를 방지하기 위한 추가적인 상태 검사 등, 개발자가 고려해야 할 사항을 기하급수적으로 늘립니다. 예를 들어, `LinkedQueue`의 `get()` 메소드 내부를 보면 `while(queue.isEmpty()){ wait(); }` 라는 코드가 있습니다. 이 코드는 큐가 비어있을 때 스레드를 대기 상태로 만들지만, 만약 다른 스레드가 `notify()`를 호출하는 신호를 놓치게 되면(Lost Wakeup), 이 스레드는 영원히 대기 상태에 머무를 수 있습니다. 이런 문제를 피하려면 복잡한 추가 플래그와 동기화 블록이 필요합니다.

**유지보수의 어려움**: 만약 여기서 '선입선출'이 아닌 '작업 우선순위'에 따라 아이템을 처리해야 한다는 새로운 요구사항이 추가된다면 어떻게 될까요? `LinkedQueue` 전체를 `PriorityQueue` 기반으로 새로 작성해야 하며, `wait`/`notify` 로직 또한 우선순위 큐의 특성에 맞게 재설계해야 합니다. 이는 버그를 유발할 가능성이 매우 높은 위험한 수정 작업입니다. 디버깅 또한 매우 어렵습니다. 스레드의 실행 순서가 매번 달라질 수 있기 때문에 문제를 재현하기 어렵고, `synchronized` 블록의 범위 설정 오류 하나가 전체 시스템을 교착 상태에 빠뜨릴 수 있습니다.

## 2. `threads.latest` 패키지

이 패키지는 Java 5부터 도입된 `java.util.concurrent` 라이브러리를 활용하여 스레딩을 보다 효율적이고 안전하게 관리합니다.

- **스레드 생성 및 관리**: `ExecutorService` (`Executors.newFixedThreadPool`)를 사용하여 스레드 풀을 생성하고 관리합니다. 개발자는 스레드의 생명주기를 직접 관리할 필요 없이 `Runnable` 태스크(`PrintTask`, `WriteTask`)를 생성하여 스레드 풀에 제출하기만 하면 됩니다.
- **데이터 공유**: 직접 구현한 큐 대신, 스레드 안전성이 보장되는 `LinkedBlockingQueue`를 사용합니다. 이 클래스는 내부적으로 `ReentrantLock`과 `Condition`을 사용하여 `legacy` 방식의 `LinkedQueue`보다 더 효율적이고 안정적인 성능을 제공합니다.

`ExecutorService`를 사용하면 스레드 재사용을 통해 리소스 관리 효율성이 높아지며, 코드가 간결해져 비즈니스 로직에 더 집중할 수 있습니다. 개발자는 '스레드를 어떻게 실행하고, 언제 깨우고, 언제 잠재울 것인가'를 고민하는 대신, '어떤 작업을 실행할 것인가'라는 비즈니스 로직에만 집중하면 됩니다. 예를 들어, `executor.submit(writeTask);` 단 한 줄의 코드는 스레드를 생성하고, 작업을 할당하고, 실행하는 모든 과정을 프레임워크에 위임합니다. 개발자는 스레드의 상태를 추적할 필요가 전혀 없습니다.

복잡하고 오류가 발생하기 쉬운 스레드 동기화 코드는 `LinkedBlockingQueue`와 같은 검증된 클래스에 위임됩니다. 
**유지보수의 용이성**: `legacy` 방식과 동일하게 '우선순위 큐' 요구사항이 추가되었다고 가정해봅시다. `latest` 방식에서는 단 한 줄만 수정하면 됩니다. `new LinkedBlockingQueue<>()`를 `new PriorityBlockingQueue<>()`로 교체하기만 하면, 나머지 코드는 전혀 수정할 필요 없이 안정적으로 동작합니다. 이는 고수준 API를 사용하는 것이 얼마나 유연하고 유지보수에 유리한지를 명확히 보여줍니다.

## 3. 주요 차이점 비교

| 구분 | `threads.legacy` | `threads.latest` |
| --- | --- | --- |
| **스레드 관리** | `Thread` 클래스를 직접 상속하여 수동 관리 | `ExecutorService`를 통한 자동 관리 |
| **작업 단위** | `Thread` 객체 자체 | `Runnable` 또는 `Callable` 인터페이스 구현체 |
| **데이터 동기화**| `wait`, `notify`, `synchronized`로 직접 구현 | `java.util.concurrent`의 자료구조 (`BlockingQueue`) |
| **복잡성** | 높음 (Deadlock, Race Condition 등 위험) | 낮음 (API가 동시성 제어 추상화) |
| **자원 효율성** | 스레드를 매번 생성/소멸하여 비효율적일 수 있음 | 스레드 풀을 통해 스레드 재사용, 효율성 높음 |

## 결론

`threads.latest` 패키지에서 사용된 `ExecutorService`와 동시성 컬렉션은 스레드를 더 안전하고 간단하게 관리할 수 있는 강력한 도구입니다. 특별히 저수준의 제어가 필요한 상황이 아니라면, 현대적인 `java.util.concurrent` API를 사용하는 것이 안정성, 유지보수성, 성능 면에서 훨씬 권장됩니다.
