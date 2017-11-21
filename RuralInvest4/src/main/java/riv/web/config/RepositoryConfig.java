package riv.web.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@Configuration
@EnableTransactionManagement
public class RepositoryConfig {
    @Value("${jdbc.driverClassName}")	private String driverClassName;
    @Value("${jdbc.url}")				private String url;
    @Value("${jdbc.username}")			private String username;
    @Value("${jdbc.password}")			private String password;
    
    @Value("${hibernate.dialect}")		private String hibernateDialect;
    @Value("${hibernate.show_sql}")		private String hibernateShowSql;
    @Value("${hibernate.hbm2ddl.auto}")	private String hibernateHbm2ddlAuto;
    @Value("${hibernate.autocommit}")	private String hibernateAutocommit;
        

//	@Autowired
//	private ServletContext servletContext;
	
    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();        
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
		ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
    
    @Bean
    @Autowired
    public AbstractPlatformTransactionManager transactionManager(LocalSessionFactoryBean bean) {
    	HibernateTransactionManager tm = new HibernateTransactionManager();
    	tm.setSessionFactory(bean.getObject());
    	return tm;
    }
    
    @Bean
    @Autowired
    public HibernateTemplate getHibernateTemplate(SessionFactory sessionFactory) {
        HibernateTemplate hibernateTemplate = new HibernateTemplate(sessionFactory);
        return hibernateTemplate;
    }
        
    @Bean(name="sessionFactory")
    public LocalSessionFactoryBean getSessionFactory() {
    	LocalSessionFactoryBean asfb = new LocalSessionFactoryBean();
        asfb.setDataSource(getDataSource());
        asfb.setHibernateProperties(getHibernateProperties());        
        asfb.setPackagesToScan(new String[]{"riv"});
        return asfb;
    }

    
    
    
    
    @Bean
    public Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.show_sql", hibernateShowSql);
        properties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
        properties.put("hibernate.connection.autocommit", hibernateAutocommit);
        return properties;
    }
    
}