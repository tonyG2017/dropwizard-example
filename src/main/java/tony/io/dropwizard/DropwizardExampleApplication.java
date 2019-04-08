package tony.io.dropwizard;

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

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DropwizardExampleApplication extends Application<DropwizardExampleConfiguration> {

    public static void main(final String[] args) throws Exception {
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

    @Override
    public void initialize(final Bootstrap<DropwizardExampleConfiguration> bootstrap) {
        // TODO: application initialization
        bootstrap.addBundle(new AssetsBundle("/dist", "/ui", "index.html", "ui"));

        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final DropwizardExampleConfiguration configuration,
                    final Environment environment) {
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
        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        environment.healthChecks().register("health-check-template", healthCheck);
        environment.jersey().register(resource);
        environment.jersey().register(new PersonResource(dao));
        environment.jersey().register(new PeopleResource(dao));
    }

}
