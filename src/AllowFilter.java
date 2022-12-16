/**
 * Cross Domain Filter
 */
public class AllowFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        return;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        httpServletResponse httpResponse = ((HttpServletResponse) response);

        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Auth-Token, content-type, Accept, X-PJAX, *");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.addHeader("X-XSS-Protection", "0");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy(){
        return;
    }
}