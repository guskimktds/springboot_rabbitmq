# springboot 에서 rabbitmq 구현 

# springboot basic
# config xml 파일 로드하기
@Value("file://${해당.env파일경로}/config/.../${spring.profiles.active}/config.xml)
private Resource configResource;

@Bean
public Properties config() throws InvalidPropertiesFormatException, IOException {
    Properties props = new Properties();
    props.loadFromXML(configResource.getInputStream());
    // Config에 Property를 등록하는 구문
    // Property.setCommon(props);
    return props;
}

# 공통 Config XML 파일 로드하기
@Value("classpath:com/gus/spring/commons/property/config-${spring.profile.acitve}.xml)
private Resource commonConfigResource;

@Bean
public Properties commonConfig() throws InvalidPropertiesFormatException, IOException {
    Properties props = new Properties();
    props.loadFromXML(commonConfigResource.getInputStream());
    // Commons Config에 Property를 등록하는 구문
    // Property.setCommon(props);
    return props;
}

# Spring boot Application class
@Slf4j
@SpringBootApplication
@ComponentScan("com.gus.study")
@EnableAsync
@SuppressWarnings("all")
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args){
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Application.class).run(args);
    }
}

# Common Logging (log4j, logback) 설정
resources/application.properties 내 외부파일경로(logging.config)설정 정보(gus 는 서비스명, gus.env==/data 경로지정됨)
logging.config=${gus.env}/config/gus-ap/${spring.profiles.active}/logback.xml 

logback.xml documentation

# 인터넷 참고) 
https://tecoble.techcourse.co.kr/post/2021-08-07-logback-tutorial/
https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=gngh0101&logNo=221073569537

<?xml version="1.0" encoding="UTF-8"?>
<!-- 30초 마다 스캔해서 설정파일을 적용. auto reload-->
<configuration scan="true" scanPeriod="30 seconds" debug="true">
 <!-- 이 곳에 추가할 기능을 넣는다. contextListener 는 어플리케이션(컨텍스트)가 시작되거나 중지될때, 그알람을 받는 리스너-->
   <contextListener class="ch.qos.logback.classic.jul.LevelChangePropagator">
    <resetJUL>true</resetJUL>
   </contextListener>

   <appender name="guslog" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/log/gus/gus.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/log/gus/gus.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>10</maxHistory>
        </rollingPolicy>
        <encoder>
                <pattern>[%d{HH:mm:ss.SSS}] [%.7thread] [%p] [%X{LOG_KEY}] %logger{36}\(%M:%line\) %m %n</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
                <level>debug</level>
        </filter>
    </appender>
    <logger name="com.gus" additivity="false">
        <level value="DEBUG" />
        <appender-ref ref="gusLog" />
    </logger>
    <logger name="accessLog" additivity="false">
        <level value="INFO" />
        <appender-ref ref="accessLog" />
     </logger>
       
    <root>
            <level value="WARN" />
            <appender-ref ref="lazyLog" />
    </root>

</configuration>

# 참고) http://progtrend.blogspot.com/2017/07/spring-boot-logback.html
/*
 Spring Boot에서 Logback 설정 파일은 일반적으로 logback-spring.xml 파일을 만들어서 classpath 루트에 두면 된다. 즉 'src/main/resources'에 logback-spring.xml 파일을 만들어서 넣어두면 알아서 찾아서 적용이 된다. 하지만 'src/main/resources'에 넣어두는 파일은 jar 배포시 jar안에 들어가게 되기 때문에 매번 실행 전에 로그 레벨 등의 설정을 바꿔서 적용하겠다는 의도로는 적합하지 않은 방법이다.  그렇게 하려면 로그 설정 파일은 jar파일 외부에 따로 존재하도록 해야한다.

 로그 설정 파일을 따로 만들어 두고 그 파일을 Spring Boot에서 읽어가도록 하는 방법은 application.properties 파일에 로그 설정 파일을 지정하는 것이다. 아래와 같이 logging.config 속성을 정의해주면 그 속성에 지정된 파일을 찾아서 로그 설정 파일로 이용한다.
*/

# application.properties
logging.config=logback.xml

# loggin common class


# MessageSourceAccessor 사용
# path : Application.class(java) 내
# 웹개발을 하면서 화면단에 alert함수를 이용해 클라이언트에게 특정메세지를 보여줘야 할때
# 이때 java에서 메세지값을 하드코딩으로 넣고 리턴해주는 경우도 있는데 그렇게 하게되면 하나의 메세지내용을 수정할 때 그 메세지가 입력된 모든파일을 다 찾아가면서 바꿔줘야 한다. 이때 이러한 문제를 해결해주기 위해 MessageSource라는걸 제공해주는데 이 MessageSoure의 기능중 하나인 다국어 지원
# 다국어처리 참고) https://kim-jong-hyun.tistory.com/26

/**
 * Property 경로를 설정한다. 단 확장자(.properties)는 빼고 설정, response-status, response-status_en, response-status_ko
 */
private static final String commonResponseStatusProperties = "com/gus/commons/property/response-status";

@Bean
public MessageSourceAccessor messageSourceAccessor() {
    //Resource파일을 읽는다.
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames(commonResponseStatusProperties);
    messageSource.setDefaultEncoding("UTF-8");

    // MessageSourceAccessor 확장기능을 사용하기 위한 객체선언
    MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);

    // messageSourceAccessor 를 사용하기 위한 별도의 Util Class 를 구현함(MessageUtils.class)
    MessageUtils messageUtils = new MessageUtils();
    messageUtils.setMessageSourceAccessor(messageSourceAccessor);

    return messageSourceAccessor;
} 

# MessageSourceAccessor는 MessageSource 내부에 변수를 가지고 있으면서 기능을 확장하여 사용자가 MessageSource기능을 편리하게 사용할 수 있도록 구현된 클래스다.
# MessageSourceAccessor의 확장기능은 크게 DefaultLocale을 맴버변수로 가지고 있어 MessageSource로 메시지를 읽어올때 Locale에 해당하는 값의 메시지를 읽어온다. 또한 MessageSource에 해당하는 코드의 메시지가 없을경우 "" 값을 전달하여 사용자가 사용할 때 NullPointException을 방지하는 기능을 제공한다.
# 출처) https://blog.naver.com/PostView.naver?blogId=songintae92&logNo=221352329967&parentCategoryNo=&categoryNo=15&viewDate=&isShowPopularPosts=true&from=search

# [Spring] MesageSource 설정하기 - 공통 메시지 처리 (with 다국어처리)
# 출처) https://devks.tistory.com/42