package tony.io.dropwizard.resources;

import com.google.inject.Inject;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import tony.io.dropwizard.core.Person;
import tony.io.dropwizard.db.PeopleDAO;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleResource {
    private  final PeopleDAO peopleDAO;
    @Inject
    public PeopleResource(PeopleDAO peopleDAO){
        this.peopleDAO =peopleDAO;
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON) // Swagger-UI Request Body application/json or -H "Content-Type: application/json"
    public Person createPerson(@Valid Person person){
        return peopleDAO.create(person);
    }

    @GET
    @UnitOfWork
    public List<Person> listPeople(){
        return peopleDAO.findAll();
    }

    @Path("/{personId}")
    @GET
    @UnitOfWork
    public Person getPerson(@PathParam("personId") LongParam personId) {
        return findSafely(personId.get());
    }

    private Person findSafely(long personId) {
        return peopleDAO.findById(personId).orElseThrow(() -> new NotFoundException("No such user."));
    }

}
