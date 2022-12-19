@Slf4j
public class ResponseLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        return;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpServletRequest);
            BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpServletResponse);

            chain.doFilter(bufferedRequest, bufferedResponse);

            Object responseJson = mapper.readValue(bufferedResponse.getContent(), Object.class);
            String strJson = (responseJson == null) ? "" : mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseJson);
            final StringBuilder logMessage = new StringBuilder("\n")
                        .append("====== Response Start =======").append("\n")
                        .append("[ Requested URL: ] ").append(httpServletRequest.getServletPath()).append("\n")
                        .append("[ Response Json: ]\n")
                        .append(strJson).append("\n")
                        .append("====== Response End =======").append("\n");
            log.info(logMessage.toString());
        } catch (ServletException e) {
            log.warn("Fail doFilter...", e.getMessage());
        } catch (Throwable a) {
            log.warn("Fail doFilter...", e.getMessage());
        }
    }

    @Override
    public void destroy() {
        return;
    }


    private static final class BufferedRequestWrapper extends HttpServletRequestWrapper {
        //to-do
    }

    private static final class BufferedResponseWrapper extends HttpServletResponseWrapper {
        //to-do
    }

    private static final class BufferedSevletInputStream extends ServletInputStream {
        //to-do
    }

    private static final class TeeSevletOutputStream extends ServletOutputStream {
        //to-do
    }
}