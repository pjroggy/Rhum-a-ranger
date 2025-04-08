package projetPj.rhum_a_ranger.rhumTest;

import org.springframework.context.annotation.Profile;
import org.springframework.boot.test.context.TestConfiguration;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManagerFactory;

@TestConfiguration
@Profile("test")
public class H2TestConfig {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void activateH2Compatibility() {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        sessionFactory.getJdbcServices().getJdbcEnvironment().getDialect()
                .getDefaultProperties().put("hibernate.globally_quoted_identifiers", "true");
    }
}