# springboot 에서 rabbitmq 구현 

# springboot basic
# config xml 파일 로드하기
@Value("file://${해당.env파일경로}/config/.../${spring.profiles.active}/config.xml)
private Resource configResource;

# 공통 Config XML 파일 로드하기
@Value("classpath:com/gus/spring/commons/property/config-${spring.profile.acitve}.xml)
private Resource commonConfigResource;