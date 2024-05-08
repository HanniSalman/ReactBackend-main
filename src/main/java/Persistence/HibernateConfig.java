package Persistence;

import Persistence.Model.Hotel;
import Persistence.Model.Role;
import Persistence.Model.Room;
import Persistence.Model.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HibernateConfig {

    private static EntityManagerFactory entityManagerFactory;

    private static EntityManagerFactory buildEntityFactoryConfig() {

        try {
            Configuration configuration = new Configuration();

            Properties props = new Properties();

            props.put("hibernate.connection.url", System.getenv("DB_URL"));
            props.put("hibernate.connection.username", System.getenv("DB_USER"));
            props.put("hibernate.connection.password", System.getenv("DB_PASSWORD"));
            props.put("hibernate.show_sql", "true"); // show sql in console
            props.put("hibernate.format_sql", "true"); // format sql in console
            props.put("hibernate.use_sql_comments", "true"); // show sql comments in console

            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // dialect for postgresql
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver"); // driver class for postgresql
            props.put("hibernate.archive.autodetection", "class"); // hibernate scans for annotated classes
            props.put("hibernate.current_session_context_class", "thread"); // hibernate current session context
            props.put("hibernate.hbm2ddl.auto", "update"); // hibernate creates tables based on entities


            return getEntityManagerFactory(configuration, props);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory setupHibernateConfigurationForTesting() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
            props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test-db");
            props.put("hibernate.connection.username", "postgres");
            props.put("hibernate.connection.password", "postgres");
            props.put("hibernate.archive.autodetection", "class");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.hbm2ddl.auto", "create-drop");
            return getEntityManagerFactory(configuration, props);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory getEntityManagerFactory(Configuration configuration, Properties props) {
        configuration.setProperties(props);

        getAnnotationConfiguration(configuration);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        System.out.println("Hibernate Java Config serviceRegistry created");

        SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
        return sf.unwrap(EntityManagerFactory.class);
    }

    private static void getAnnotationConfiguration(Configuration configuration) {
        // add annotated classes
        // configuration.addAnnotatedClass(<YOUR ENTITY>.class);
        configuration.addAnnotatedClass(Hotel.class);
        configuration.addAnnotatedClass(Room.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
    }

    //Singleton princippet
    private static EntityManagerFactory getEntityManagerFactoryConfigDevelopment() {
        if (entityManagerFactory == null) entityManagerFactory = buildEntityFactoryConfig();
        return entityManagerFactory;
    }

    private static EntityManagerFactory getEntityManagerFactoryConfigTest() {
        if (entityManagerFactory == null) entityManagerFactory = setupHibernateConfigurationForTesting();
        return entityManagerFactory;
    }

    public static EntityManagerFactory getEntityManagerFactoryConfig(boolean isTest) {
        if (isTest) getEntityManagerFactoryConfigTest();
        return getEntityManagerFactoryConfigDevelopment();
    }

}
