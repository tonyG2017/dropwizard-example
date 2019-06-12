package tony.io.dropwizard.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.hibernate.UnitOfWork;
import tony.io.dropwizard.core.Person;
import tony.io.dropwizard.db.PersonDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleResource {
    private  final PersonDAO personDAO;
    public PeopleResource(PersonDAO personDAO){
        this.personDAO =personDAO;
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON) // Swagger-UI Request Body application/json or -H "Content-Type: application/json"
    public Person createPerson(Person person){
        return personDAO.create(person);
    }

    @GET
    @UnitOfWork
    public List<Person> listPeople(){
        return personDAO.findAll();
    }

}
