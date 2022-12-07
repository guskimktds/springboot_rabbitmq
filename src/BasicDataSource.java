public class BasicDataSource extends org.apache.commons.dbcp2.BasicDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(BasicDataSource.class);

    public void setComment(String comment){
        LOG.info("DB Comment:: {}", StringUtils.trim(comment));
    }

    @Override
    public void setUrl(final String url){
        super.setUrl(StringUtils.trim(url));
    }

    @Override
    public void setUsername(final String username){
        super.setUsername(StringUtils.trim(username));
    }

    @Override
    public void setPassword(final String password){
        super.setPassword(StringUtils.trim(password));
    }
}