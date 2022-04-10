# Redis Transaction

Redis는 In-Memory 형태로 이루어진 Key/Value 형태의 NoSQL이다. Redis는 캐시의 형태로 많이 사용한다.  대부분 캐시로 사용되지만 가끔 RDS와 AOF 기능으로  영구 저장소로 극소수 사용하기도 한다.

Redis Transaction

redis 의 트랜잭션을 유지하기 위해서는 순차성을 가져야 하고 명령어가 치고 들어오지 못하게 Lock이 필요하다.

Redis에서 트랜잭션은 조금 어색할 수도 있다. 어색해도 활용만 잘하면 더 유용하고 다양한 상황에서 사용이 가능하다.

트랜잭션을 유지하기 위해서는 순차성을 가져야 하고 도중에 명령어가 치고 들어오지 못하게 LOCK이 필요하다. Redis에서는 MULTI,EXEC, DISCARD, WATCH 명령어를 사용하면 된다.

- MULTI
    - Redis의 트랜잭션을 시작하는 커맨드, 트랜잭션을 시작하면 Redis는 이후 커맨드에 바로 실행 되지 않고 queue에 쌓인다.



    ```sql
    >> MULTI
    "OK"
    >> SET KIM PAGE1
    "QUEUE"
    >> SET LEE PAGE2
    "QUEUE"
    >> GET KIM
    "QUEUE"
    ```


예제를 보고 동작하는 방식을 보자. MULTI 커맨드를 입력하면 트랜잭션을 사용할 수 있다. 트랜잭션이 시작된 후 들어오는 명령어는 바로 실행되는 것이 아니고 우선 QUEUE에 쌓이게 된다. 만약 QUEUE에 쌓인 작업 값은 EXEC 커맨드를 통해 일괄적으로 실행된다.SET과 GET 모두 QUEUE에 쌓인다.

예를들어 위처럼 KIM이라는 key를 가진 데이터를 조회하면 QUEUE에 “PAGE1”이라는 경과 값이 들어간다.

- EXEC
    - 정상적으로 처리되어 queue에 쌓여 있는 명령어를 일괄적으로 실행한다. 예를 들자면 RDBMS의 Commit과 동일하다.

    ```sql
    >> MULTI
    "OK"
    >> SET KIM PAGE1
    "QUEUE"
    >> SET LEE PAGE2
    "QUEUE"
    >> GET KIM
    "QUEUE"
    >> EXEC
    1)"OK"
    2)"OK"
    3)"PAGE1"
    ```


    MULTI부분 예제에서 EXEC 커맨드를 실행하면 QUEUE에 쌓여 있던 작업들이 일관적으로 실행된다. 이 결과값을 보면 GET에 대한 요청도 SET 요청과 함께 QUEUE에 쌓여 있던것을 확인할 수 있다.

```sql
>> GET KIM
"PAGE1"
>> GET LEE
"PAGE2"
```

    정상적으로 커맨드가 실행 되었다면 트랜잭션 내부의 커맨드가 정상적으로 실행된 것이기 때문에 GET을 했을 때 정상적으로 출력 되는것을 볼수 있다.

- DISCARD
    - queue에 쌓여 있는 명령어를 폐기한다. RDBMS의 Rollback과 동일

    ```sql
    >> MULTI
    "OK"
    >> SET KIM PAGE1
    "QUEUE"
    >> SET LEE PAGE2
    "QUEUE"
    >> GET KIM
    "QUEUED"
    >> DISCARD
    "OK"
    ```


    Redis의 Rollback은 RDBMS와 조금 방식이 다르다. MULTI 커맨드를 사용한 후 이용하여 DISCARD 명령어를 명시적으로 실행한다. DISCARD 명령어를 실행하면 QUEUE에 쌓인 명령어가 일괄적으로 없어지게 된다.

#### 내용을 약간 추가하자만 다른 자료구조로 명령어를 실행하면 오류가 발생하지만 같은 QUEUE에 들어간 다른 정상적인 작업들은 정상적으로 실행된다.

Redis 공식문서를 참조하면 이러한 상황을 허용하는 이유는 대부분 개발과정에서 일어날 수 있는 에러이며 production에서는 거의 발생하지 않는 에러이기 때문에 Rollback을 채택하지 않으므로서 Redis의 장점인 빠른 성능을 유지할 수 있다고 한다.

- WATCH
    - 해당 값을 변경하고 있을 때 다른 사람이 동일하게 key를 건드리게 되면 잘못된 값이 입력될 수 있기 때문에 watch라는 명령어를 통해서 값 변경을 1번으로 제한 할 수 있다.
    - Redis에서 LOCK을 담당하는 명령어이다. 해당 명령어는 낙관적 락(Optinistic LOCK) 기반이다.
    - WATCH 명령어를 사용하면 이 후 UNWATCH 되기전에는 1번의 EXEC 또는 Transaction이 아닌 다른 커맨드만 허용한다.