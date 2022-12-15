public class WebMvcConfiguration extends WebMvcConfigurerAdapter implements InitializingBean, DisposableBean {
    @Autowired
    private ValidationService ValidationService;

    //요청에 따른 응답 값에 대한 미디어타입 설정
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


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.debug("{} addResourceHandlers...", this.getClass().getName());

        registry.addResourceHandlers("/resources/css/**").addResourceLocations("/resources/css");
        registry.addResourceHandlers("/resources/js/comn/**").addResourceLocations("/resources/js/comn/");
        registry.addResourceHandlers("/resources/js/comn/**").addResourceLocations("/resources/js/comn/").setCachePeriod(31556926);
        ...
        registry.addResourceHandlers("/webjars/**").addResourceLocations("classtpath:/META-INF/resources/webjars/").setCachePeriod(31556926);
        //setCachePeriod 는 
        
    }

    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
        log.debug("{} contentNegotiatingViewResolver...", this.getClass().getName());

        //View Resolver
        List<ViewResolver> viewResolvers = Lists.newArrayList();
    }
}

//출처 https://www.inflearn.com/questions/108303/setcacheperiod-%EB%A9%94%EC%86%8C%EB%93%9C%EC%97%90-%EB%8C%80%ED%95%B4