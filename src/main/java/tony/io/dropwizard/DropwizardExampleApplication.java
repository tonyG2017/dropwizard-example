package tony.io.dropwizard;

import ca.mestevens.java.configuration.bundle.TypesafeConfigurationBundle;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import tony.io.dropwizard.core.Person;
import tony.io.dropwizard.db.PersonDAO;
import tony.io.dropwizard.resources.HelloWorldResource;
import tony.io.dropwizard.health.TemplateHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import tony.io.dropwizard.resources.PeopleResource;
import tony.io.dropwizard.resources.PersonResource;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DropwizardExampleApplication extends Application<DropwizardExampleConfiguration> {

    public static void main(final String[] args) throws Exception {
        log.info("Starting DropwizardExampleApplication"); //log variable is provided by @Slf4j
        new DropwizardExampleApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropwizardExample";
    }

    private final HibernateBundle<DropwizardExampleConfiguration> hibernateBundle = new HibernateBundle<DropwizardExampleConfiguration>(Person.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(DropwizardExampleConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    //Work
    public static final List<Class<?>> RESOURCE_CLASSES = ImmutableList.<Class<?>>builder()
            .add(HelloWorldResource.class)
            .add(PeopleResource.class)
            .add(PersonResource.class)
            .build();
    /*
    //Not work
    public static final List<Class<?>> RESOURCE_CLASSES = new ArrayList<Class<?>>();
            RESOURCE_CLASSES.add(HelloWorldResource.class);
            RESOURCE_CLASSES.add(PeopleResource.class);
            RESOURCE_CLASSES.add(PersonResource.class);
    */

    /*
    //Work
    public static final List<Class<?>> RESOURCE_CLASSES = Arrays.asList(
                HelloWorldResource.class,
                PeopleResource.class,
                PersonResource.class
            );
    */

    @Override
    public void initialize(final Bootstrap<DropwizardExampleConfiguration> bootstrap) {
        // TODO: application initialization
        bootstrap.addBundle(new TypesafeConfigurationBundle());

        bootstrap.addBundle(new AssetsBundle("/dist", "/ui", "index.html", "ui"));

        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final DropwizardExampleConfiguration configuration,
                    final Environment environment) {
        try {

            // Configure dependency injection
            log.info("Configuring Guice Injector");
            Injector injector = Guice.createInjector(
                    new DropwizardExampleModule(configuration, environment, hibernateBundle)
            );
            OpenAPI oas = new OpenAPI();
            Info info = new Info()
                    .title("Hello World API")
                    .description("RESTful greetings for you.")
                    .termsOfService("http://example.com/terms")
                    .contact(new Contact().email("john@example.com"));

            oas.info(info);
            SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                    .openAPI(oas)
                    .prettyPrint(true)
                    .resourcePackages(Stream.of("tony.io.dropwizard")
                            .collect(Collectors.toSet()));
            environment.jersey().register(new OpenApiResource()
                    .openApiConfiguration(oasConfig));
            // TODO: implement application
            final TemplateHealthCheck healthCheck =
                    new TemplateHealthCheck(configuration.getTemplate());
            environment.healthChecks().register("health-check-template", healthCheck);
            //Get and register the appropriate instance for the given injection type, HelloWorldResource.class
            for (Class<?> clazz: RESOURCE_CLASSES){
                environment.jersey().register(injector.getInstance(clazz));
            }
            log.info("DropwizardExampleApplication initialization completed");
        } catch (Throwable t) {
            log.error("DropwizardExampleApplication failed to start", t);
            throw t;
        }
    }

}
