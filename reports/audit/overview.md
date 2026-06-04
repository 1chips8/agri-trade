# agri-trade 项目摸底报告

## 基本信息

| 字段 | 值 |
| --- | --- |
| repo_path | H:\COMputerSCIence\项目\agri-trade |
| generated_at | 2026-06-04T02:11:28.646814+00:00 |
| file_count_scanned | 2757 |
| approx_total_bytes | 694999326 |

## 语言和文件类型

| 语言 | 文件数 |
| --- | --- |
| Other | 2684 |
| Java | 53 |
| C/C++ | 8 |
| YAML | 5 |
| JavaScript | 5 |
| SQL | 2 |

## 依赖和环境线索

- `backend/Dockerfile`
- `backend/pom.xml`
- `docker-compose.yml`
- `frontend/Dockerfile`
- `frontend/package.json`

## README

- `README.md`
- `.tools/apache-maven-3.9.16/README.txt`
- `.tools/apache-maven-3.9.16/lib/ext/README.txt`
- `.tools/apache-maven-3.9.16/lib/ext/hazelcast/README.txt`
- `.tools/apache-maven-3.9.16/lib/ext/redisson/README.txt`
- `.tools/apache-maven-3.9.16/lib/jansi-native/README.txt`
- `.tools/jdk-17.0.19+10/conf/security/policy/README.txt`

## 核心链路线索

| 类别 | 命中文件数 | 代表路径 |
| --- | --- | --- |
| api_backend | 40 | .tools/apache-maven-3.9.16/lib/javax.annotation-api-1.3.2.jar<br>.tools/apache-maven-3.9.16/lib/javax.annotation-api.license<br>.tools/apache-maven-3.9.16/lib/maven-plugin-api-3.9.16.jar<br>.tools/apache-maven-3.9.16/lib/maven-resolver-api-1.9.27.jar<br>.tools/apache-maven-3.9.16/lib/slf4j-api-1.7.36.jar<br>.tools/apache-maven-3.9.16/lib/slf4j-api.license<br>.tools/apache-maven-3.9.16/lib/wagon-provider-api-3.5.3.jar<br>.tools/jdk-17.0.19+10/jmods/jdk.httpserver.jmod |
| async_jobs | 12 | .tools/m2-repository/com/rabbitmq/amqp-client/5.14.3/_remote.repositories<br>.tools/m2-repository/com/rabbitmq/amqp-client/5.14.3/amqp-client-5.14.3.jar<br>.tools/m2-repository/com/rabbitmq/amqp-client/5.14.3/amqp-client-5.14.3.jar.sha1<br>.tools/m2-repository/com/rabbitmq/amqp-client/5.14.3/amqp-client-5.14.3.pom<br>.tools/m2-repository/com/rabbitmq/amqp-client/5.14.3/amqp-client-5.14.3.pom.sha1<br>.tools/m2-repository/org/springframework/amqp/spring-rabbit/2.4.17/_remote.repositories<br>.tools/m2-repository/org/springframework/amqp/spring-rabbit/2.4.17/spring-rabbit-2.4.17.jar<br>.tools/m2-repository/org/springframework/amqp/spring-rabbit/2.4.17/spring-rabbit-2.4.17.jar.sha1 |
| config | 40 | .tools/apache-maven-3.9.16/conf/settings.xml<br>.tools/apache-maven-3.9.16/lib/maven-settings-3.9.16.jar<br>.tools/apache-maven-3.9.16/lib/maven-settings-builder-3.9.16.jar<br>.tools/m2-repository/cn/dev33/sa-token-spring-boot-autoconfig/1.37.0/_remote.repositories<br>.tools/m2-repository/cn/dev33/sa-token-spring-boot-autoconfig/1.37.0/sa-token-spring-boot-autoconfig-1.37.0.jar<br>.tools/m2-repository/cn/dev33/sa-token-spring-boot-autoconfig/1.37.0/sa-token-spring-boot-autoconfig-1.37.0.jar.sha1<br>.tools/m2-repository/cn/dev33/sa-token-spring-boot-autoconfig/1.37.0/sa-token-spring-boot-autoconfig-1.37.0.pom<br>.tools/m2-repository/cn/dev33/sa-token-spring-boot-autoconfig/1.37.0/sa-token-spring-boot-autoconfig-1.37.0.pom.sha1 |
| data_pipeline | 5 | .tools/m2-repository/org/springframework/boot/spring-boot-loader-tools/2.7.18/_remote.repositories<br>.tools/m2-repository/org/springframework/boot/spring-boot-loader-tools/2.7.18/spring-boot-loader-tools-2.7.18.jar<br>.tools/m2-repository/org/springframework/boot/spring-boot-loader-tools/2.7.18/spring-boot-loader-tools-2.7.18.jar.sha1<br>.tools/m2-repository/org/springframework/boot/spring-boot-loader-tools/2.7.18/spring-boot-loader-tools-2.7.18.pom<br>.tools/m2-repository/org/springframework/boot/spring-boot-loader-tools/2.7.18/spring-boot-loader-tools-2.7.18.pom.sha1 |
| database_state | 40 | .tools/apache-maven-3.9.16/lib/ext/redisson/README.txt<br>.tools/apache-maven-3.9.16/lib/maven-model-3.9.16.jar<br>.tools/apache-maven-3.9.16/lib/maven-model-builder-3.9.16.jar<br>.tools/apache-maven-3.9.16/lib/maven-repository-metadata-3.9.16.jar<br>.tools/jdk-17.0.19+10/bin/jdb<br>.tools/jdk-17.0.19+10/bin/jhsdb<br>.tools/jdk-17.0.19+10/jmods/java.sql.jmod<br>.tools/jdk-17.0.19+10/jmods/java.sql.rowset.jmod |
| devops_deploy | 29 | .tools/apache-maven-3.9.16/lib/jspecify-1.0.0.jar<br>.tools/apache-maven-3.9.16/lib/jspecify.license<br>.tools/apache-maven-3.9.16/lib/plexus-cipher-2.0.jar<br>.tools/apache-maven-3.9.16/lib/plexus-cipher.license<br>.tools/jdk-17.0.19+10/jmods/jdk.internal.vm.ci.jmod<br>.tools/jdk-17.0.19+10/legal/jdk.internal.vm.ci/ADDITIONAL_LICENSE_INFO<br>.tools/jdk-17.0.19+10/legal/jdk.internal.vm.ci/ASSEMBLY_EXCEPTION<br>.tools/jdk-17.0.19+10/legal/jdk.internal.vm.ci/LICENSE |
| evaluation | 40 | .tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.18/_remote.repositories<br>.tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.18/metrics-bom-4.2.18.pom<br>.tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.18/metrics-bom-4.2.18.pom.sha1<br>.tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.19/_remote.repositories<br>.tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.19/metrics-bom-4.2.19.pom<br>.tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.19/metrics-bom-4.2.19.pom.sha1<br>.tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.22/_remote.repositories<br>.tools/m2-repository/io/dropwizard/metrics/metrics-bom/4.2.22/metrics-bom-4.2.22.pom |
| frontend_mobile | 40 | .tools/apache-maven-3.9.16/lib/plexus-component-annotations-2.2.0.jar<br>.tools/apache-maven-3.9.16/lib/plexus-component-annotations.license<br>.tools/jdk-17.0.19+10/lib/libsplashscreen.so<br>.tools/m2-repository/org/apache/httpcomponents/httpclient/4.5.14/_remote.repositories<br>.tools/m2-repository/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar<br>.tools/m2-repository/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar.sha1<br>.tools/m2-repository/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.pom<br>.tools/m2-repository/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.pom.sha1 |
| inference_demo | 40 | .tools/apache-maven-3.9.16/lib/javax.annotation-api-1.3.2.jar<br>.tools/apache-maven-3.9.16/lib/javax.annotation-api.license<br>.tools/apache-maven-3.9.16/lib/maven-plugin-api-3.9.16.jar<br>.tools/apache-maven-3.9.16/lib/maven-resolver-api-1.9.27.jar<br>.tools/apache-maven-3.9.16/lib/slf4j-api-1.7.36.jar<br>.tools/apache-maven-3.9.16/lib/slf4j-api.license<br>.tools/apache-maven-3.9.16/lib/wagon-provider-api-3.5.3.jar<br>.tools/jdk-17.0.19+10/jmods/jdk.httpserver.jmod |
| model | 40 | .tools/apache-maven-3.9.16/lib/maven-model-3.9.16.jar<br>.tools/apache-maven-3.9.16/lib/maven-model-builder-3.9.16.jar<br>.tools/jdk-17.0.19+10/conf/net.properties<br>.tools/jdk-17.0.19+10/jmods/java.net.http.jmod<br>.tools/jdk-17.0.19+10/jmods/jdk.net.jmod<br>.tools/jdk-17.0.19+10/legal/java.net.http/ADDITIONAL_LICENSE_INFO<br>.tools/jdk-17.0.19+10/legal/java.net.http/ASSEMBLY_EXCEPTION<br>.tools/jdk-17.0.19+10/legal/java.net.http/LICENSE |
| security_auth | 20 | .tools/jdk-17.0.19+10/jmods/jdk.security.auth.jmod<br>.tools/jdk-17.0.19+10/legal/jdk.security.auth/ADDITIONAL_LICENSE_INFO<br>.tools/jdk-17.0.19+10/legal/jdk.security.auth/ASSEMBLY_EXCEPTION<br>.tools/jdk-17.0.19+10/legal/jdk.security.auth/LICENSE<br>.tools/m2-repository/com/oracle/database/jdbc/ojdbc-bom/21.5.0.0/_remote.repositories<br>.tools/m2-repository/com/oracle/database/jdbc/ojdbc-bom/21.5.0.0/ojdbc-bom-21.5.0.0.pom<br>.tools/m2-repository/com/oracle/database/jdbc/ojdbc-bom/21.5.0.0/ojdbc-bom-21.5.0.0.pom.sha1<br>backend/src/main/java/com/agritrade/auth/AuthController.java |
| testing_quality | 40 | .tools/apache-maven-3.9.16/lib/jspecify-1.0.0.jar<br>.tools/apache-maven-3.9.16/lib/jspecify.license<br>.tools/m2-repository/org/mockito/mockito-bom/4.5.1/_remote.repositories<br>.tools/m2-repository/org/mockito/mockito-bom/4.5.1/mockito-bom-4.5.1.pom<br>.tools/m2-repository/org/mockito/mockito-bom/4.5.1/mockito-bom-4.5.1.pom.sha1<br>.tools/m2-repository/org/mockito/mockito-core/4.5.1/_remote.repositories<br>.tools/m2-repository/org/mockito/mockito-core/4.5.1/mockito-core-4.5.1.jar<br>.tools/m2-repository/org/mockito/mockito-core/4.5.1/mockito-core-4.5.1.jar.sha1 |

## Notebook / Docker / Test 线索

### Notebooks
- 无

### Docker
- `backend/Dockerfile`
- `docker-compose.yml`
- `frontend/Dockerfile`

### Tests
- `.tools/m2-repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar`
- `.tools/m2-repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar.sha1`
- `.tools/m2-repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.pom`
- `.tools/m2-repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.pom.sha1`
- `.tools/m2-repository/org/springframework/boot/spring-boot-starter-test/2.7.18/spring-boot-starter-test-2.7.18.jar`
- `.tools/m2-repository/org/springframework/boot/spring-boot-starter-test/2.7.18/spring-boot-starter-test-2.7.18.jar.sha1`
- `.tools/m2-repository/org/springframework/boot/spring-boot-starter-test/2.7.18/spring-boot-starter-test-2.7.18.pom`
- `.tools/m2-repository/org/springframework/boot/spring-boot-starter-test/2.7.18/spring-boot-starter-test-2.7.18.pom.sha1`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test-autoconfigure/2.7.18/spring-boot-test-autoconfigure-2.7.18.jar`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test-autoconfigure/2.7.18/spring-boot-test-autoconfigure-2.7.18.jar.sha1`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test-autoconfigure/2.7.18/spring-boot-test-autoconfigure-2.7.18.pom`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test-autoconfigure/2.7.18/spring-boot-test-autoconfigure-2.7.18.pom.sha1`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test/2.7.18/spring-boot-test-2.7.18.jar`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test/2.7.18/spring-boot-test-2.7.18.jar.sha1`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test/2.7.18/spring-boot-test-2.7.18.pom`
- `.tools/m2-repository/org/springframework/boot/spring-boot-test/2.7.18/spring-boot-test-2.7.18.pom.sha1`
- `.tools/m2-repository/org/springframework/spring-test/5.3.31/spring-test-5.3.31.jar`
- `.tools/m2-repository/org/springframework/spring-test/5.3.31/spring-test-5.3.31.jar.sha1`
- `.tools/m2-repository/org/springframework/spring-test/5.3.31/spring-test-5.3.31.pom`
- `.tools/m2-repository/org/springframework/spring-test/5.3.31/spring-test-5.3.31.pom.sha1`

## 潜在数据/状态/模型/资源路径

- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.10/_remote.repositories`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.10/spring-data-bom-2021.2.10.pom`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.10/spring-data-bom-2021.2.10.pom.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.15/_remote.repositories`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.15/spring-data-bom-2021.2.15.pom`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.15/spring-data-bom-2021.2.15.pom.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.18/_remote.repositories`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.18/spring-data-bom-2021.2.18.pom`
- `.tools/m2-repository/org/springframework/data/spring-data-bom/2021.2.18/spring-data-bom-2021.2.18.pom.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-commons/2.7.18/_remote.repositories`
- `.tools/m2-repository/org/springframework/data/spring-data-commons/2.7.18/spring-data-commons-2.7.18.jar`
- `.tools/m2-repository/org/springframework/data/spring-data-commons/2.7.18/spring-data-commons-2.7.18.jar.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-commons/2.7.18/spring-data-commons-2.7.18.pom`
- `.tools/m2-repository/org/springframework/data/spring-data-commons/2.7.18/spring-data-commons-2.7.18.pom.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-keyvalue/2.7.18/_remote.repositories`
- `.tools/m2-repository/org/springframework/data/spring-data-keyvalue/2.7.18/spring-data-keyvalue-2.7.18.jar`
- `.tools/m2-repository/org/springframework/data/spring-data-keyvalue/2.7.18/spring-data-keyvalue-2.7.18.jar.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-keyvalue/2.7.18/spring-data-keyvalue-2.7.18.pom`
- `.tools/m2-repository/org/springframework/data/spring-data-keyvalue/2.7.18/spring-data-keyvalue-2.7.18.pom.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-redis/2.7.18/_remote.repositories`
- `.tools/m2-repository/org/springframework/data/spring-data-redis/2.7.18/spring-data-redis-2.7.18.jar`
- `.tools/m2-repository/org/springframework/data/spring-data-redis/2.7.18/spring-data-redis-2.7.18.jar.sha1`
- `.tools/m2-repository/org/springframework/data/spring-data-redis/2.7.18/spring-data-redis-2.7.18.pom`
- `.tools/m2-repository/org/springframework/data/spring-data-redis/2.7.18/spring-data-redis-2.7.18.pom.sha1`
- `backend/src/main/resources/db/schema.sql`
- `backend/target/classes/db/schema.sql`

## 目录树摘要

```text
agri-trade/
  .agents/
  .codex/
  .tools/
  backend/
  frontend/
  nginx/
  .gitignore
  README.md
  docker-compose.yml
  农产品交易系统标准开发设计文档.md
    apache-maven-3.9.16/
    jdk-17.0.19+10/
    m2-repository/
    jdk17.tar.gz
    maven.tar.gz
      bin/
      boot/
      conf/
      lib/
      LICENSE
      NOTICE
      README.txt
      bin/
      conf/
      include/
      jmods/
      legal/
      lib/
      man/
      NOTICE
      release
      aopalliance/
      asm/
      backport-util-concurrent/
      ch/
      classworlds/
      cn/
      com/
      commons-codec/
      commons-io/
      io/
      jakarta/
      javax/
      junit/
      net/
      org/
    src/
    target/
    Dockerfile
    pom.xml
      main/
      classes/
      generated-sources/
      maven-archiver/
      maven-status/
      agri-trade-backend-0.0.1-SNAPSHOT.jar
      agri-trade-backend-0.0.1-SNAPSHOT.jar.original
    src/
    Dockerfile
    index.html
    package-lock.json
    package.json
    vite.config.js
      stores/
      views/
      App.vue
      api.js
      main.js
      router.js
      styles.css
    default.conf
```

## 下一步人工确认

- 找到最小可运行命令：API、页面、CLI、worker、测试、训练或 demo 至少一个。
- 确认依赖、环境变量、数据库/数据文件、端口和外部服务。
- 确认 baseline/demo 是否能在本地、Docker、云服务器或 GPU 环境上跑通。
- 确认自己要做的面试亮点：改造点、demo、测试、报告或实验计划。
