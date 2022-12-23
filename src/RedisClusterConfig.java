@Slf4j
@Configuration
public class RedisClusterConfig {
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

    @Bean
    public JedisCluster jedisCluster(JedisPoolConfig jedisPoolConfig){
        JedisCluster jedisCluster = null;
        try {
            hosts = hsots.replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "").replaceAll("\t", "");
            /* Host And Port */
            List<String> hostAndPortStrList = Arrays.asList(hosts.split(","));
            Set<HostAndPort> nodes = new HashSet<HostAndPort>();

            for(String hostAndPortStr : hostAndPortStrList){
                if(StringUtils.isNotBlank(hostAndPortStr)){
                    String[] hostAndPortSplit = hostAndPortStr.split(":");
                    String host = hostAndPortSplit[0];
                    int port = NumberUtils.toInt(hostAndPortSplit[1]);
                    nodes.add(new HostAndPort(host, port));
                }
            }

            /** Jedis Cluster Setting */
            int connectionTimeout = StringUtils.isNotBlank(redisConnectTimeout) ? NumberUtils.toInt(redisConnectTimeout) : Protocol.DEFAULT_TIMEOUT;
            int soTimeout = StringUtils.isNotBlank(redisSoTimeout) ? NumberUtils.toInt(redisSoTimeout) : Protocol.DEFAULT_TIMEOUT;
            int maxAttempts = StringUtils.isNotBlank(redismaxAttempts) ? NumberUtils.toInt(redismaxAttempts) : 3 //retry 3

            jedisCluster = new JedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts, password, jedisPoolConfig);

        }catch (ServiceException e){
            jedisCluster = null;
        }catch(Exception ex) {
            jedisCluster = null;
        }

        return jedisCluster;
    }

    // 출처 : https://dakafakadev.tistory.com/109
}