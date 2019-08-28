package tony.io.dropwizard;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zagao on 2019/8/28.
 */
@Slf4j
public class DropwizardExampleModule extends AbstractModule{
    private final DropwizardExampleConfiguration config;
    private final Environment env;
    public DropwizardExampleModule(DropwizardExampleConfiguration config, Environment env){
        this.config = config;
        this.env = env;
    }
    @Override
    protected void configure(){
        log.info("Binding");
        bind(String.class).annotatedWith(Names.named("template")).toInstance(config.getTemplate());
        bind(String.class).annotatedWith(Names.named("defaultName")).toInstance(config.getDefaultName());
    }
}
