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
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setViewClass(JstlView.class);
        viewResolver.add(viewResolver);

        // Json View Resolver
        List<View> jsonViewResolvers = Lists.newArrayList();
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        // jsonView.setPrettyPrint(true);
        jsonView.setPrefixJson(true);
        jsonViewResolvers.add(jsonView);

        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setViewResolvers(viewResolvers);
        resolver.setDefaultViews(jsonViewResolvers);
        resolver.setContentNegotiationManager(manager);
        return resolver; 
    }

    /**
     * favicon 404 에러처리 
     */
    @Bean
    public WebMvcConfigurerAdapter faviconWebMvcConfiguration() {
        return new WebMvcConfigurerAdapter(){
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry){
                registry.setOrder(Integer.MIN_VALUE);
                registry.addResourceHandler("favicon.ico").addResourceLocations("/").setCachePeriod(0);
            }
        };
    }

/**
 * 404 에러를 ControllerAdvice 에서 처리 할 수 있도록 설정한다. 
 */
    @Bean
    public DispatcherServlet dispatcherServlet(){
        DispatcherServlet ds = new DispatcherServlet();
        ds.setThrowExceptionIfNoHandlerFound(true);
        return ds;
    }

    /**
     * Post 요청 시 한글 깨짐 보완
     */
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        log.debug("{} characterEncodingFilter...", this.getClass().getName());
        final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    /**
     * Cross Domain Filter 처리
     */
     @Bean
     public AllowFilter allowFilter(){
        log.debug("{} allowFilter...", this.getClass().getName());
        final AllowFilter allowFilter = new AllowFilter();
        return allowFilter;
     }
}

//출처 https://www.inflearn.com/questions/108303/setcacheperiod-%EB%A9%94%EC%86%8C%EB%93%9C%EC%97%90-%EB%8C%80%ED%95%B4