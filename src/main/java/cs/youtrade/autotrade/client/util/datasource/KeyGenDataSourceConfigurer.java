package cs.youtrade.autotrade.client.util.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

import static cs.youtrade.autotrade.client.util.datasource.CustomHibernatePropertiesConfig.hibernateProperties;

@Configuration
@EnableJpaRepositories(
        basePackages = "cs.youtrade.autotrade.client.keygen",
        entityManagerFactoryRef = "keyGenTgEntityManagerFactory",
        transactionManagerRef = "keyGenTgTransactionManager"
)
public class KeyGenDataSourceConfigurer {
    @Bean
    @ConfigurationProperties("spring.datasource.keygen")
    public DataSourceProperties keyGenTgDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource keyGenTgDataSource() {
        return keyGenTgDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate keyGenTgJdbcTemplate(@Qualifier("keyGenTgDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean keyGenTgEntityManagerFactory(
            @Qualifier("keyGenTgDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(dataSource)
                .packages("cs.youtrade.autotrade.client.keygen.entities")
                .properties(hibernateProperties())
                .build();
    }

    @Bean
    public PlatformTransactionManager keyGenTgTransactionManager(
            @Qualifier("keyGenTgEntityManagerFactory") LocalContainerEntityManagerFactoryBean keyGenTgEntityManagerFactory
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(keyGenTgEntityManagerFactory.getObject()));
    }
}
