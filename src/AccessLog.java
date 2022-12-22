@Slf4j
@Component
@SuppressWarnings("all")
public class AccessLog extends AbstractAccessLog {
    @Autowired(required = true)
    public synchronized void setAccessLogService(AccessLogService _accesslogService) {
        accesslogService = _accesslogService;
    }
}