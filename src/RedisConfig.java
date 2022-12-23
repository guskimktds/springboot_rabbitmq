@Slf4j
@Configuration
public class RedisConfig {
    @Value("#{config['redis.hosts']}")
    private String hosts;

    @Value("#{config['redis.password']}")
    private String password;

    @Value("#{config['redis.connect.connectTimeout']}")
    private String redisConnectTimeout;

    @Value("#{config['redis.connect.soTimeout']}")
    private String redisSoTimeout;

    @Value("#{config['redis.connect.maxAttempts']}")
    private String redismaxAttempts;

    @Value("#{config['redis.pool.idle.max']}")
    private String redisPoolMaxIdle;

    @Value("#{config['redis.pool.idle.min']}")
    private String redisPoolMinIdle;

    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxIdle(NumberUtils.toInt(redisPoolMaxIdle));
        jedisPoolConfig.setMinIdle(NumberUtils.toInt(redisPoolMinIdle));
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);

        return jedisPoolConfig;
    }
}