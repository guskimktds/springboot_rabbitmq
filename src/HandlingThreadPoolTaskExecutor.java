public class HandlingThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    private static Logger log = LoggerFactory.getLogger(HandlingThreadPoolTaskExecutor.class);

    private static final long serialVersionUID = ...;

    public <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                Map<String, String> previous = MDC.
            }
        }
    }
}