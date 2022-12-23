@Slf4j
@Service("commService")
public class CommServiceImpl implements CommService {
    @Override
    public String getRequestIp(HttpServletRequest request) {
        String req_ip = request.getHeader("X-FORWARDED-FOR");

        if(req_ip == null || req_ip.lehgth() == 0)
            req_ip = request.getHeader("Proxy-Client-IP");
        
        if(req_ip == null || req_ip.lehgth() == 0)
            req_ip = request.getHeader("WL-Proxy-Client-IP");
        
        if(req_ip == null || req_ip.lehgth() == 0)
            req_ip = request.getRemoteAddr();

        return req_ip;
    }
}