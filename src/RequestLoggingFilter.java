...
@Slf4j
public class RequestLogginFilter extends AbstractRequestLoggingFilter {

    @Override
    protected void beforeRequest(httpServletRequest request, String message){
        log.debug(message);
    }

    @Override
    protected void afterRequest(httpServletRequest request,String message){
        log.debug(message);
    }
}