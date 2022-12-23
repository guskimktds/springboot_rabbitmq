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
# org.springframework.context.support.ResourceBundleMessageSource
# ResourceBundle Class, MessageFormat Class 기반으로 만들어져 Bundle에 특정 명칭으로 접근이 가능합니다.
# org.springframework.context.support.ReloadableResourceBundelMessageSource
# Property  설정을 통해 Reloading 정보를 입력해 주기적인 Message Reloading을 수행합니다. 
# 그렇기 때문에 Application 종료없이도 실행 도중에 변경이 가능한 장점이 있습니다.


# LocalValidatorFactoryBean() 사용법

지난번에 작성한 Java Bean Validation 제대로 알고 쓰자에 이어서 Spring Boot 환경에서 Validation을 어떻게 사용할 수 있는지 확인해보겠습니다. Spring에서도 Hibernate Validator를 사용합니다. Java Bean Validation에 대해서 잘 모른다면 지난 글을 먼저 읽어보는 것을 추천합니다.

# 출처) https://kapentaz.github.io/java/Java-Bean-Validation-%EC%A0%9C%EB%8C%80%EB%A1%9C-%EC%95%8C%EA%B3%A0-%EC%93%B0%EC%9E%90/#

Dependency
Spring Boot에서는 spring-boot-starter-validation를 추가하면 Validation 관련 필요한 라이브러리가 모두 추가됩니다.

implementation("org.springframework.boot:spring-boot-starter-validation")  
implementation("org.springframework.boot:spring-boot-starter-web")

# AutoConfiguration
Spring Boot ValidationAutoConfiguration 클래스를 통해서 LocalValidatorFactoryBean와 MethodValidationPostProcessor를 자동으로 설정합니다.

LocalValidatorFactoryBean는 Spring에서 Validator를 사용하기 위해서 필요하고 MethodValidationPostProcessor는 메서드 파라미터 또는 리턴 값을 검증하기 위해서 사용됩니다.

AutoConfiguration으로 별다른 설정 없이 Spring Boot에서 바로 Validation을 사용할 수 있습니다.

# 경로 : Application.class 내 Bean 선언, @Validated 사용 가능
@Bean
public javax.validation.Validator localValidatorFactoryBean(){
    return new LocalValidatoryFactoryBean();
}

# 위 클래스에 설정은 아래와 같이 Bean 선언과 동일함
# 이를 통해 응용 프로그램에서 유효성 검사가 필요한 어느 곳에서든지 javax.validation.ValidatorFactory 
# 또는 javax.validation.Validator를 주입할 수 있습니다.
<bean id="validator"
    class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean"/>

# modelmapper 선언, Application.class
@Bean
public ModelMapper modelMapper() {
    return new ModelMapper();
}

# modelmapper 사용, service impl 에서 @Autowired
@Autowired
private ModelMapper modelMapper;

# dto 타입에 request 객체(AlimPushRequest)를 PushAndroidMessage.AlimMessage 로 매핑
# service impl 구현
PushAndroidMessage.AlimMessage alimMessage = modelMapper.map(alimPushRequest, PushAndroidMessage.AlimMessage.class); 

# dto 객체
public class AlimPushRequest extends BaseRequest {
    ...
    @Getter
    @Setter
    @JsonProperty("type")
    private String type;

    @NotBlank
    @Getter
    @Setter
    @JsonProperty("user_id")
    private String userId;
    ...
}

# 다른 타입에 객체
public class PushAndroidMessage extends BaseObject {
    ...
    @Data
    @EqualAndHashCode(callSuper=false)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(Include.NON_NULL)
    public static class AlimMessage extends PushAndroidMessage {
        private static final long serialVersionUID = .....;

        ...
        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("date")
        private String date;
        ...
    }

    ...
} 


# ObjectMapper.class 응답받은 JSON 객체를 POJO 형태로 deserialization 시켜 내가 원하는 데이터로 변환
# ObjectMapper 클래스를 이용하여 JSON 객체를 역직렬화
# JSON 컨텐츠를 Java 객체로 deserialization 하거나 Java 객체를 JSON으로 serialization 할 때 사용하는 Jackson 라이브러리의 클래스
# ObjectMapper는 생성 비용이 비싸기 때문에 bean/static으로 처리하는 것이 좋다.
# 출처) https://velog.io/@zooneon/Java-ObjectMapper%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-JSON-%ED%8C%8C%EC%8B%B1%ED%95%98%EA%B8%B0

# Application 에 bean 선언
@Bean(name="objectMapper")
public ObjectMapper objectMapper(){
    return new ObjectMapper();
}

# ObjectMapper 사용, service impl 에서 @Autowired 로 사용
public class AccessLogSerivceImpl implements AccessLogService {
    private Logger accessLog = LoggerFactory.getLogger("accessLog"); // 기본 로그
    private Logger accessLogJson = LoggerFactory.getLogger("accessLogJson"); // json 형식으로 찍는 로그

    @Value("#{config['access.log.json.yn']}")
    private String accessLogJsonYn; // 외부경로 config 파일에서 읽음(Application.class 에서 선언됨)

    @Autowired
    private ObjectMapper objectMapper;

    private TypeReference<LinkedHashMap<String,Object>> typeRefLHM = new TypeReference<LinkedHashMap<String,Object>>() {};
    String[] toJsonColumns = new String[]{"requestPayload","responsePayload"};

    @Override
    public void insertAccessLog(AccessLogModel accessLogModel){
        if(StringUtils.isBlank(accessLogModel.getVersion()) || StringUtils.isBlank(accessLogModel.getApiCode())){
            log.warn("Version-apicode blank [{}]", accessLogModel.toString());
        }
        accessLog.info(accessLogModel.toString());
        if(조건){
            try {
                LinkedHashMap<String, Object> accessLogMap = objectMapper.convertValue(accessLogModel, param2);
                for(String col : toJsonColumns){
                    Object value = null;
                    try{
                        value=objectMapper.readValue((String) accessLogMap.get(column), param2)
                    }catch(ServiceResultException se){
                        value=accessLogMap.get(column);
                    }catch(Exception e){
                        value=accessLogMap.get(column);
                    }
                    finally {
                        accessLogMap.put(column, value);
                    }
                }
                accessLogJson.info(objectMapper.writeValueAsString(accessLogMap));
            }catch(ServiceResultException se){
                log.warn("ERROR ["+se.getMessage()+ "]", se);
            }catch(Exception e){
                log.warn("ERROR ["+e.getMessage()+ "]", e);
            }
        }
    }
    
}

# Scheduled executor service
# 반복적인 Batch 작업들은 Spring에서 제공하는 Scheduler를 이용하여 구현할 수 있다.기본 동작 원리는 java concurrent 패키지에서 제공하는 것과 동일하지만 Bean으로 등록되어 Container에 의해 관리되는 것이 다르다. 도한 cron expression을 제공하여 친숙(?)하게 스케쥴을 세팅할 수 있다.
/*
* 아래 ExecutorFactoryBean을 선언하면 해당 Executor Factory Bean내에서 만들어진 Thread(thread-pool-1-thread-1)에서 Scheduler들이 호출되고,만약 선언하지 않으면 기본 worker pool의 thread에서 Scheduler가 호출된다.
*/
@Bean 
public ScheduledExecutorFactoryBean scheduledExecutorSerivce(){
    //궁금하면 찍어보자
    //System.out.println("scheduledExecutorService => now " + " in thread-" + Thread.currentThread().getName());

    ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
    bean.setPoolSize(10);
    return bean;
}

# Thread Pool Task Executor
# Application.class 선언
# ThreadPoolTaskExecutor 는 스프링에서 제공하는 async(비동기)처리 executor
# 스레드풀을 이용한 멀티스레드 구현
@Bean
public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
    HandlingThreadPoolTaskExecutor executor = new HandlingThreadPoolTaskExecutor();
    executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());  // 동시실행 스레드 개수
    executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors()*2); // 스레드풀 최대
    executor.setQueueCapacity(executorQueueCapacity);  // CorePoolSize 개수를 넘으면 Max 까지 Queue 에 Task 가 쌓임
    executor.setAllowCoreThreadTimeOut(true); // keepAliveSecond 이 지나면 코어개수 10 이여도 줄어듬
    executor.setThreadNamePrefix("threadPoolTaskExecutor-");
    executor.initialize(); //initialize 하기전에는 executor 를 사용할수 없다.
    return executor;
}

# serverpath 는 tomcat 기동 시 env.sh 에 환경변수로 설정됨
# JAVA_OPTS="$JAVA_OPTS -Dvsaas.env=/data"
# JAVA_OPTS="$JAVA_OPTS -Dspring.profile.active=dev|stage|prod"
@Value("file://${환경변수에 설정된경로}/config/gus/${spring.profile.active}/config.xml")
private Resource configResource;
@Value("#{config['executor.queue.capacity']}")
private int executorQueueCapacity;

# 공통 config XML 파일 로드 시 내부 패키지 내 설정파일 load(외부는 위에 내용 정리참고)
# Application.class 에 선언
@Value("classpath:com/gus/study/commons/propety/config-${spring.profile.active}.xml")
private Resource commonConfigResource;

# Spring batch 와 Schedule 
# 별도의 batch job class 생성하고 배치처리 메소드 구현
@Slf4j
@Component("GusBatchJob")
@PropertySource(value={"file://${환경변수에 설정된경로}/config/gus/${spring.profile.active}/config.xml"})
public class GusBatchJob {
    ...
    // 3초마다 실행
    @Scheduled(cron="3 * * * * *")
    public void deleteBatch(){
        //validation
        //insert log
        try{
            //기능구현 deletebatch 기능
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            logger.info("[START] "+ jobName + "." + methodName + " executing at " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"");
            //실제 dml 처리
            logger.info("[END] "+ jobName + "." + methodName + " executing at " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"");
        }     
    }
    ...
}

# 서버 호스트명 찾는 메소드 구현
# get Host name
public String getHostname(){
    String hostname = null;
    Process process = null;
    BufferedReader br = null;
    try {
        process = Runtime.getRuntime().exec("hostname");
        br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String lineStr = null;
        while((lineStr = br.readLine()) != null){
            hostname = lineStr.trim();
        }
    }catch(IOException e){

    }finally{
        if(br != null){
            try{
                br.close();
                br = null;
            }catch(IOException e){
                br = null;
            }
        }

        if(process != null){
            process.destroy();
        }
    }
    return hostname;
}

# MulitpartResolver bean 선언


## Database Configuration 구현
## Database 연결
# DatabaseConfiguration.class
# @EnableTransactionManagement(proxyTargetClass = true) 사용이유(목적)

@Slf4j
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@PropertySource(value = {"file://${환경변수에 설정된경로}/config/gus/${spring.profile.active}/database.xml"})
public class DatabaseConfiguration {

    //MyBatis Config 파일 경로 설정
    private static final String MYBATIS_CONFIG_FILE = "패키지 내 설정 경로/mybatis-config.xml";
    //MyBatis Mapper 파일 경로 설정MYBATIS_CONFIG_FILE
    private static final String MYBATIS_MAPPER_PATH = "classpath:패키지 내 설정 경로/mapper/*.xml";

    @Autowired
    private Environment env;  //@PropertySource 경로에 propety 를 load 해서 key, value 를 가져오기 위한 용도로 선언

    @Autowired
    private Properties commonConfig;  //Application 에 선언한 common config 를 로드하여 bean 객체를 생성, 패키지내 config 파일 로드 

    //Data Source 설정
    @Bean(name = "dataSource")
    public DataSource dataSource() {

        String environment = commonConfig.getProperty("server.environment");

        BasicDataSource dataSource = new BasicDataSoruce();
        dataSource.setComment(StringUtils.trim(env.getProperty(String.format("postgresql.%s.comment", dbsource))));
        dataSource.setDriverClassName(StringUtils.trim(env.getProperty(String.format("postgresql.%s.jdbc.driver", dbsource))));
        dataSource.setUrl(StringUtils.trim(env.getProperty(String.format("postgresql.%s.jdbc.url", dbsource))));
        dataSource.setUsername(StringUtils.trim(env.getProperty(String.format("postgresql.%s.username", dbsource))));
        dataSource.setPassword(StringUtils.trim(env.getProperty(String.format("postgresql.%s.password", dbsource))));
        dataSource.setValidationQuery(StringUtils.trim(env.getProperty(String.format("postgresql.%s.validation.query", dbsource))));
        ...
        int initSize = NumberUtils.toInt(env.getProperty(String.format("postgresql.%s.initial.size", dbsource)));
        if(initSize > 0){
            dataSource.setInitialSize(initSize);
            //... setMaxTotal, setMaxWaitMillis, setMaxIdle, setMinIdle
        }
        return new DataSourceSpy(dataSource);
    }

}

## SqlSessionFactory 사용법
# 

@Bean(name="sqlSessionFactory")
public SqlSessionFactory sqlSessionFactory() throws Exception {
    String environment = commonConfig.getProperty("server.environment");  //패키지 내 config xml 설정에서 로드
    log.debug("{} sqlSessionFactory; Server Environment:: {}", this.getClass().getName(), environment);

    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(this.dataSource());

    // MyBatis 설정파일 위치 설정
    bean.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG_FILE));

    // MyBatis 설정파일 위치 설정
    bean.setMapperLocations(new PathMatchResourcePatternResolver().getResource(MYBATIS_CONFIG_FILE));
}


# @JsonIgnoreProperties(ignoreUnknown = true) 선언
# json 데이터를 받아와서 객체로 맵핑할 때 클래스에 선언되지 않은 프로퍼티가 json에 있으면 오류 발생 (json 구성 = 클래스 구성)
 => org.codehaus.jackson.map.exc.UnrecognizedPropertyException
이럴 때 예외 발생시키지 말고 무시하기 위해 @JsonIgnoreProperties(ignoreUnknown = true) 추가

# null 필드를 노출하지 않으려면
@JsonInclude(JsonInclude.Include.NON_NULL) 어노테이션을 필드에 선언해주면 된다.

import com.fasterxml.jackson.annotation.JsonInclude;
public class A {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    private Integer age; // 미선언
}
# 아래와 같이 리턴 표시됨, 미선언 필드만 null 표시
"A": {
    "age": null
}

# 보통 자바는 카멜 케이스를, JSON형식은 스테이크 케이스 방식
# @JsonProperty 는 필드에, @JsonNaming 는 클래스에 어노테이션을 사용
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
// @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class) deprecated
public static class UserRequest {
    ...
    //또는 아래와 같이
    //@JsonProperty 필드마다 적용
    @JsonProperty("cam_id")
    private String camId;  //선언은 스네이크케이스, 변수는 카멜케이스
}

# Web MVC Architecture 구성하기
# Servlet Context 또는 classpath 하위의 경로에 있는 정적 리소스는 자동으로 반환을 하도록 설정되어 있다 
  /static
  /public
  /resources
  /META-INF/resources

# 특정경로에 정적자원을 저장해두고, 그 경로에서 응답을 해주는 경우 addResourceHandlers 메소드를 오버라이드함으로써 설정한다.
# WebMvcConfigurer를 상속받은 클래스(또는 WebMvcConfigurerAdapter 를 상속받은 클래스)에 Configuration, EnableWebMvc 애너테이션을 추가
# 출처 : https://creampuffy.tistory.com/119
...
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    // 개발 시점에 사용 가능한 코드.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/resources/**")
          .addResourceLocations("/resources/");	
    }
    
    // 배포 시점에 사용 가능한 코드.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry
      	 .addResourceHandler("/files/**")
      	 .addResourceLocations("file:/opt/files/");
         
         // 윈도우라면
         .addResourceLocations(“file:///C:/opt/files/“);
 	}
}
...

# Spring MVC 실행과정 및 Controller 작성
실행 과정
1. 주소창에 http://~/xxxx 입력
2. DispatcherServlet → Controller : Controller에서 path가 /xxxx인 메소드 실행
3. Controller → DispatcherServlet : InternalResourceViewResolver가 가져온 return 값에 "/WEB-INF/views/"와 ".jsp"를 붙임
4. DispatcherServlet → View template : return 받은 "/WEB-INF/views/xxxx.jsp" 경로 파일 실행
(아래 DispatcherServlet 설정 참고)

# 여기서 DispatcherServlet 은 DispatcherServlet(=WebMvcContextConfiguration.java) 설정
# 즉, 아래와 같이 WebMvcConfigurerAdapter 를 구현한 클래스이고, addResourceHandlers 메소드는 정적리소스를 저장해놓고 응답처리하기 위한 메소드
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "kr.or.connect.mvcexam.controller" })
public class WebMvcContextConfiguration extends WebMvcConfigurerAdapter {
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/META-INF/resources/webjars/").setCachePeriod(31556926); 
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926); 
    }
  ...

## WebMvcConfiguration.java (=WebMvcConfigurerAdapter 를 구현한)에 ViewResolver  
## setCachePeriod 는 http 캐시(cache) 유효기간을 초단위로 지정 (604800 = 7일, 31556926 = 365일)

## HTTP 캐시를 제어하는 기능
## If-Modified-Since 헤더값과 리소스의 최종 수정 일시를 비교
만약 리소스가 갱신되지 않았으면 HTTP 304상태 (Not Modified) 반환
기본구현에서는 캐시의 유효기간이 설정되지 않아 캐시에 대한 동작은 브라우저 사양에 의존

## 기본구현
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**")
      .addResourceLocations("classpath:/static/")
      .setCachePeriod(604800); // 유효기간을 초단위로 지정 (604800 = 7일)
}

## 캐시를 세밀하게 제어할 때 (CacheControl클래스 이용)
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**")
      .addResourceLocations("classpath:/static/")
      .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());  //CacheControl클래스 이용
}

## ResourceHttpRequestHandler 란?
# 정적리소스에 접근하는 다양한 방법을 제공
# 버전 정보가 포함된 경로로 정적리소스를 접근하는 방법
# Gzip으로 압축된 정적 리소스에 접근하는 방법
# Webjars로 관리되는 정적 리소스에 대해 버전 번호를 은폐시켜 접근하는 방법
## 출처) https://moonscode.tistory.com/125


## ResourceHttpRequestHandler 는 종류가 2가지임
# 1) ResourceResolver 인터페이스
  정적리소스에 접근할 수 있도록 URL과 서버 상의 물리적인 정적 리소스를 매핑 
  구현클래스는 아래와 같다.
    VersionResourceResolver, GzipResourceResolver, WebJarsResourceResolver
    예시) 버전정보를 포함한 경로로 정적 리소스 접근
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/")
        .resourceChain(true)
        .addResolver((new VersionResourceResolver()).addContentVersionStrategy("/**"));
    }
# 2) ResoureceTransformer 인터페이스
  정적 리소스의 콘텐츠 데이터를 변환하는 역할
  구현클래스는 아래와 같다.
    CssLinkResourceTransformer, AppCacheManifestTransformer

## 출처) https://moonscode.tistory.com/125

## Content Negotiation 이란? 
# REST에서는 하나의 리소스에 대해서 여러 형태의 Representation을 가질수 있다.
# 요청에 대한 응답을 application/json 형태로 할 수도 있고, application/xml 형태로 할 수도 있다.
# 클라이언트가 요청을 전달할 때 HTTP Header 중에서 Accept라는 이름을 이용해서 원하는 응답 형태를 명시하면
# 서버에서는 클라이언트가 원하는 형태로 결과를 전달

## configureContentNegotiation 란?
WebMvcConfigurer의 configureContentNegotiation를 이용해서 미디어 타입을 설정할 수 있습니다.
즉, WebMvcConfigurerAdapter 를 상속받은 클래스 내 configureContentNegotiation을 오버라이드(@Override)해서 원하는 미디어타입으로 응답을 처리
또는 WebMvcConfigurer 를 구현한 클래스(WebMvcConfig)에서 아래와 같이 처리

예시)WebMvcConfigurer 구현
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(true)
                  .ignoreAcceptHeader(false)
                  .defaultContentType(MediaType.APPLICATION_JSON)
                  .mediaType("json", MediaType.APPLICATION_JSON)
                  .mediaType("xml", MediaType.APPLICATION_XML);
    }
}

예시) WebMvcConfigurerAdapter 를 상속받은 클래스
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false)
                  .favorParameter(true)
                  .parameterName("mediaType")
                  .ignoreAcceptHeader(true)
                  .useJaf(false)
                  .defaultContentType(MediaType.APPLICATION_JSON)
                  .mediaType("xml",MediaType.APPLICATION_XML)
                  .mediaType("json",MediaType.APPLICATION_JSON);

    }
}

## 클라이언트로부터 수신된 요청의 미디어 타입을 가지고 결정하는데, 그 과정은 아래와 같다.
# 1. favorParameter 속성 값이 true(기본값 false)이고, 요청 파라미터에 미디어 타입을 정의하는 값이 포함되어 있다면 그 값을 미디어 타입으로 사용합니다. 파라미터의 변수명은 'format' 입니다.
(예 : http://localhost:8080/board?format=xml, http://localhost:8080/board?format=json)
# 2. 미디어 타입을 찾지 못한 경우 ignoreAcceptHeader의 속성 값이 false(기본값 false)이면 HTTP Header 값의 Accept를 미디어 타입으로 사용합니다.
# 3. 미디어 타입을 찾지 못한 경우 ContentNegotiationConfigurer에 defaultContentType 속성 값이 정의되어 있다면 그 값을 미디어 타입으로 사용합니다.
## 유사한 설명 추가
# 첫 째, favorPathExtension이라는 불리언 타입의 프로퍼티를 보고 이 값이 true이면 URL의 확장자를 통해 mediatype을 결정하겠다는 것이다. (디폴트 값은 true)
# 둘 째, URL 호출 시 특정 파라미터로 리턴포맷을 결정하는 것을 허용하는 것이다. 예를 들어, a.do?format=xml이면 스프링에서 format 파라미터의 값을 읽어서 결정한다. favorParameter 프로퍼티와 parameterName이라는 프로퍼티를 이용해서 할 수 있다. (디폴트는 favorParameter는 false, parameterName은 format)

# contentNegotiatingViewResolver 란? mvc view resolver setting 



# XML (Marshalling View)
# 출처) https://kaka7537.gitbooks.io/spring-diary/content/contentnegotiatingviewresolver.html
자바 객체의 변환에 있어 흔히 사용되는 단어가 마샬링과 언마샬링이다. 마샬링은 자바 객체를 XML로 변환하는 작업이고 언마샬링은 그 반대를 말한다. CNVR에서 마샬링 작업에 사용되는 View 클래스는 org.springframework.web.servlet.view.xml.MarshallingView 클래스이다. 하지만 이것만으로 자바 객체를 XML로 변환할 수 없다. 자바 객체를 XML로 변환하는 기능을 제공하는 라이브러리를 생성자를 통하여 전달하여야 한다. 스프링에서 제공하는 XMl 변환 기능을 수행하는 클래스, 즉 마샬러 중에 하나는 org.springframework.oxm.jaxb.Jaxb2Marshaller 클래스이다.

마샬러가 어떤 클래스들을 XML로 변환할 것인지 작업대상을 정해주어야 한다.

<bean class="org.springframework.web.servlet.view.xml.MarshallingView">
    <constructor-arg>
        <bean class="org.springframework.oxm.jaxb.Jaxb2Marshaller">
            <property name="classesToBeBound">
                <list>
                    <value>myprj.vo.Result</value>
                    <value>myprj.vo.Info</value>
                </list>
            </property>
        </bean>
    </constructor-arg>
</bean>

# JSON (MappingJacksonJsonView)
# 출처) https://kaka7537.gitbooks.io/spring-diary/content/contentnegotiatingviewresolver.html
스프링에서 JSON 포맷을 지원하는 View는 org.springframework.web.servlet.view.json.MappingJacksonJsonView 클래스이다. 이 View를 사용하면 스프링의 컨트롤러가 객체를 리턴하는 경우 객체의 내용을 분석하여 JSON 포맷으로 변환하여 보내주게 된다.

# FiterRegistrationBean 이란 ? 
# 필터 클래스를 만들면 이 필터를 Spring Bean 으로 등록해야 합니다. 
# 만약에 ResponseLoggingFilter 를 구현했다면, 아래와 같이 등록한다. 
@Bean
public FiterRegistrationBean getFilterRegistrationBean() {
FilterRegistrationBean registrationBean = new FilterRegistrationBean(new ResponseLoggingFilter());
    // registrationBean.addUrlPatterns("/*"); //서블릿 등록 빈 처럼 패터늘 지정해 줄 수 있다.
return registrationBean;
}

##  
# DataSourceAutoConfiguration 
# read, read+write DB Configuration
# @Bean(name = "dataSource") 과 @Bean(name = "dataSourceDbwriter") 로 class 를 나누고
# MyBatis Config, Mapper 등 경로를 분리하고 해당  Data Source 를 설정한다.  
public class DatabaseConfiguration {

    //MyBatis Config
    private static final String MYBATIS_CONFIG_FILE = "com/study/gus/persistence/mybatis-config.xml";
    //MyBatis Mapper
    private static final String MYBATIS_MAPPER_PATH = "classpath:com/study/gus/persistence/mapper/*.xml";

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        ...
        return new DataSourceSpy(dataSource);
    }
}

public class DatabaseWriterConfiguration {

    //MyBatis Config
    private static final String MYBATIS_CONFIG_FILE = "com/study/gus/persistence/mybatis-config.xml";
    //MyBatis Mapper
    private static final String MYBATIS_MAPPER_PATH = "classpath:com/study/gus/persistence/dbwriter/*.xml";

    @Bean(name = "dataSourceDbwriter")
    public DataSource dataSource() {
        ...
        return new DataSourceSpy(dataSource);
    }
}


# DataSourceSpy 란? 
# DataSourceSpy란 클래스는 log4jdbc에서 제공하는 DataSource 인터페이스를 구현한 클래스
# Sql Log를 출력하기 위해 직/간접적으로 사용되는 메소드들의 인터페이스
# 출처 : https://okky.kr/articles/338191


##
# Redis Cluster & Configuration
# 출처 : https://theserverside.tistory.com/302
public JedisPoolConfig jedisPoolConfig(){...}


## XFF(X-Forwarded-For), Client IP 확인하는 방법
## 출처 : https://www.lesstif.com/software-architect/proxy-client-ip-x-forwarded-for-xff-http-header-20775886.html
 

## Slf4j MDC (Mapped Diagnostic Context) 란?

## package-info.java 란
# javadocs를 생성
# package-info.java는 자바 소스 패키지에 추가 할 수있는 자바 파일입니다. 이름에 따라 “패키지”수준에서 정보를 제공하는 데 사용됩니다. 패키지에 사용 된 설명서와 주석이 포함되어 있습니다.
javadoc 예제는 이미 답변에 제공되어 있으며 아래 부분에서는 주석의 경우 작동 방식을 설명합니다.
예를 들어, 아래 파일에서는 joda.time.DateTime의 발생을 org.jadira.usertype.dateandtime.joda.PersistentDateTime으로 “대체”하는 데 사용됩니다.
# 출처 : http://daplus.net/java-package-info-java%EA%B0%80-%EC%9C%A0%EC%9A%A9%ED%95%9C-%EC%9D%B4%EC%9C%A0%EB%8A%94-%EB%AC%B4%EC%97%87%EC%9E%85%EB%8B%88%EA%B9%8C/


## ThreadPoolTaskExecutor
# 비동기 방식의 멀티스레드 처리
# 출처 : https://kim-jong-hyun.tistory.com/104

컨트롤러에서는 MultipartFile을 List로 받아서 upload
serivce 단에서 upload 기능구현 
multipartFile 처리 부분만 신경 쓰면 됨

# 멀티파트파일(MultipartFile)이란? 사용자가 업로드한 파일을 핸들러가 쉽게 처리할 수 있는 객체이다.
# 매개변수를 사용하려면 MultipartResolver Bean 이 등록되어야 하며, Springboot 는 자동등록을 지원하나, springMVC 에 수동으로 등록해야한다.
# 출처 : https://kkumalog.tistory.com/74

## Correlation id & MDC 
하나의 프로그램은 여러개의 메서드들로 조합이 된다. 하나의 요청을 처리하기 위해서는 여러개의 메서드들이 순차적으로 실행이 되는데, 멀티 쓰레드 프로그램에서 여러개의 쓰레드 동시에 각각의 요청을 처리할때, 각 메서드에 로그를 남기게 되면, 멀티 쓰레드 프로그램에서는 쓰레드들이 서로 컨택스트를 바꿔가며 실행이 되기 때문에, 로그 메시지가 섞이게 된다
# 멀티요청 중에 각각의 요청에 대한 id 를 부여하고 해당 id 로 조회가능하게 로그를 묶어서 처리가 가능하다. 
# 이를 Correlation id 라고 한다. 출처) https://bcho.tistory.com/1316
# ThreadLocal
요청을 받은 메서드에서 Correlation ID를 생성한 후, 다른 메서드를 호출할때 마다 이 ID를 인자로 넘기는 방법이 있지만, 이 방법은 현실성이 떨어진다. ID를 넘기기 위해서 모든 메서드에 공통적으로 ID 필드를 가져야 하기 때문이다.
이런 문제는 자바에서는 ThreadLocal이라는 변수를 통해서 해결할 수 있다.
ThreadLocal을 쓰레드의 로컬 컨텍스트 변수로 Thread 가 존재하는 한 계속해서 남아 있는 변수이다.  
그래서 요청을 처음 받았을때 Correlation ID를 생성하고, 이를 ThreadLocal에 저장했다가 로그를 쓸때 매번 이 ID를 ThreadLocal에서 꺼내서 같이 출력하면 된다. 
# MDC
ThreadLocal 과 같은걸 일일이 구현하기에는 불편할 수 있기 때문에, slf4j,logback,log4j2등의 자바 로그 프레임워크에서는 이런 기능을 MDC(Mapped Diagnostic Context)로 제공한다. 
단순하게 CorrelationID 뿐만 아니라 map 형식으로 여러 메타 데이타를 넣을 수 있다.


