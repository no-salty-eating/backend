# QUICK-COMMERCE
![퀵커머스로고](https://github.com/user-attachments/assets/b1fd75bb-3038-4337-a9e8-3c8445d15e70)


📃상세내용 : https://www.notion.so/teamsparta/3-Quick-Commerce-fa5f015f37c04514a04695c1ee8833f2

----
### 대규모의 트래픽이 발생하는 요청에 대해 원활한 처리를 가능하게 하는 B2C 이커머스 서비스 ###
 개발기간 : 2024.12.26 ~ 2024.01.27
- #### 프로젝트 목표 ####
  - 타임세일, 선착순 쿠폰 등 동시에 대규모 트래픽이 발생하는 요청에 대해 빠르고 원활한 처리를 제공하도록 MSA 구조로 제작된 서비스
  - 흔히 접하던 모놀리식 구조가 아닌 MSA기반의 시스템을 설계하고 구현하면서, 다양한 기술과 방법론을 적용
  - 데이터 일관성을 유지하기 위한 트랜잭션 도입
- #### 프로젝트 상세 ####
  - 주문, 결제
    - Webflux, Coroutine 기반의 Reactive 서비스 구축
    - 서버 셧다운으로 인한 데이터 유실을 방어하기 위해 Redis 를 이용하여 주문,결제 데이터 관리
    - Toss Payment 결제 실패 시 지수 Backoff 와 Jitter 를 이용한 무작위 재시도
    - Idempotence Config 를 이용한 결제 중복 처리 방지
    - Elastic Search 를 사용하여 조회 성능 개선 및 서비스 안정성 개선  
  - 인프라 구축
    - GitHub Actions를 활용한 CI/CD 구성
    - AWS ECR 과 ECS 를 활용한 서비스 배포
    - Amazon RDS DB 구현
  - 상품 도메인 관리
    - redis에 상품 상세 정보 캐싱
    - redis hash를 활용 상품 재고 정보 등록
    - kafka를 사용한 재고 DB재고 차감 관리
  - Spring Scheduler를 이용한 타임세일 상품 관리
    - timesale 시작/종료를 redis + scheduler로 관리
  - 사용자 
    - JWT를 사용한 인증/인가 처리
  - 쿠폰
    - Redisson 분산 락을 도입하여 동시성 처리
    - Kafka를 사용한 메시지 큐 도입으로 안정성 향상
    - Redis 캐시를 사용하여 선착순 쿠폰의 재고 관리 속도 개선

----
### 👩‍💻 팀원 역할 분담 ###
 - **윤홍찬 :** 상품 / 타임세일 상품
 - **이준석 :** 주문 / 결제, CI / CD 및 배포
 - **임지은 :** 사용자 / 쿠폰
----
### 🔧 개발환경 ###
- **IDE :** Intellij
- **Language :** Java 17
- **Build Tool :** Gradle
- **Framework :** Spring boot 3.3.7, Spring Cloud 2023.0.4, Hibernate 6.x, Kotlin
- **Repository :** MySQL, MariaDB, r2dbc, Redis
- **Infra :** Github, Github Action, Github Pacakges, AWS ECR, AWS ECS, AWS RDS, Docker
- **Library :** Spring Scheduler, Spring Webflux, ElasticSearch, Kibana
----
### 📝 ERD ###
![image](https://github.com/user-attachments/assets/eb0acf7c-e84d-4cf2-8dec-0b59c80c8bea)


----
### 📚 인프라 구성도 ###
![image](https://github.com/user-attachments/assets/93666103-6e5b-4db6-b483-ffb4e2cc9954)



