package tony.io.dropwizard.resources;

/**
 * Created by zagao on 2019/1/11.
 */
//import com.example.helloworld.api.Saying;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Metered;
import io.swagger.v3.oas.annotations.Operation;
import tony.io.dropwizard.api.Saying;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    //static final MetricRegistry metrics = new MetricRegistry();
    //static final Meter requests = metrics.meter("requests");

    public HelloWorldResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    @Operation(description="Says hello.")
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        log.info(value);
        //requests.mark();
        return new Saying(counter.incrementAndGet(), value);
    }
}