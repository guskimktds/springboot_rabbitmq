import org.slf4j.MDC;
...

@Component("StatusCheckBatchJob")
public class StatusCheckBatchJob {
 public static final String LOG_KEY = "LOG_KEY";
 private static final Logger logger = LoggerFactory.getLogger("kmsLog");

 @Autowired
 private ApiRedisServiceImpl apiRedisServiceImpl;

 @Scheduled(cron = "10 * * * * ?")
 public void checkRedisStatus(){
    logger.info("# [REDIS_STATUS_CHECK] start. 1 ");

    try{
        AccessLog.start();
        String logKey = AccessLog.getAccessLogModel().getLogKey();
        MDC.put(LOG_KEY, logKey);
        apiRedisServiceImpl.checkRedisStatusBatchJob();
        
    }catch (ServiceException e){
        logger.info("# [REDIS_STATUS_CHECK] 1 {}.", e.getMessage());
    }catch (Exception e){
        logger.info("# [REDIS_STATUS_CHECK] 1 {}.", e.getMessage());
    }finally {
        AccessLog.end();
        MDC.remove(LOG_KEY);
    }

    logger.info("# [REDIS_STATUS_CHECK] end. 9 ");
 }

}