package tony.io.dropwizard;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import tony.io.dropwizard.db.PeopleDAO;

/**
 * Created by zagao on 2019/8/28.
 */
@Slf4j
public class DropwizardExampleModule extends AbstractModule{
    private final DropwizardExampleConfiguration config;
    private final Environment env;
    private final HibernateBundle<DropwizardExampleConfiguration> hibernateBundle;
    public DropwizardExampleModule(DropwizardExampleConfiguration config, Environment env, HibernateBundle<DropwizardExampleConfiguration> hibernateBundle){
        this.config = config;
        this.env = env;
        this.hibernateBundle = hibernateBundle;
    }
    @Override
    protected void configure(){
        log.info("Binding");
        bind(String.class).annotatedWith(Names.named("template")).toInstance(config.getTemplate());
        bind(String.class).annotatedWith(Names.named("defaultName")).toInstance(config.getDefaultName());
        bind(PeopleDAO.class).in(Singleton.class);
    }
    @Provides
    public SessionFactory provideSessionFactory(){
       return hibernateBundle.getSessionFactory();
    }
}
