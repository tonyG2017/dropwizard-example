package tony.io.dropwizard.resources;

import com.google.inject.Inject;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import tony.io.dropwizard.core.Person;
import tony.io.dropwizard.db.PeopleDAO;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Slf4j
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
        log.info("Create a new person:"+person);
        return peopleDAO.create(person);
    }

//    @GET
//    @UnitOfWork
//    public List<Person> listPeople(){
//        return peopleDAO.findAll();
//    }

    @GET
    @UnitOfWork
    public List<Person> listPeople(@QueryParam("fullName")Optional<String> fullName,
                                   @QueryParam("jobTitle")Optional<String> jobTitle){
        log.info("List persons with full name: {} and job title: {}",fullName, jobTitle);
        return peopleDAO.findWithFilters(fullName,jobTitle);
    }

    @Path("/{personId}")
    @GET
    @UnitOfWork
    public Person getPerson(@PathParam("personId") LongParam personId) {
        log.info("Get person by Id: {}", personId.get());
        return findSafely(personId.get());
    }

    @Path("/{personId}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response updatePerson(@PathParam("personId") LongParam personId, @Valid Person person) {
        log.info("Update person whose id is {} with info {}",personId.get(),person);
            person.setId(personId.get());
        try {
            peopleDAO.update(person);
            return Response.ok()
                    .entity(person)
                    .build();
        }catch (Exception e){
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("/{personId}/increaseSalaryByPessimisticLock")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response addSalaryWithPessimisticLock(@PathParam("personId") LongParam personId, @Valid int salaryIncrease) {
        log.info("Increase {} dollars  person whose id is {}",salaryIncrease,personId.get());

        try {
            peopleDAO.addSalaryPessimisticLock(personId.get(),salaryIncrease);
            return Response.ok()
                    .build();
        }catch (Exception e){
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("/{personId}/increaseSalaryWithoutLock")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response addSalaryWithoutlock(@PathParam("personId") LongParam personId, @Valid int salaryIncrease) {
        log.info("Increase {} dollars  person whose id is {}",salaryIncrease,personId.get());

        try {
            peopleDAO.addSalaryWithoutLock(personId.get(),salaryIncrease);
            return Response.ok()
                    .build();
        }catch (Exception e){
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("/{personId}/increaseSalaryOptimisticLock")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response addSalaryOptimistictlock(@PathParam("personId") LongParam personId, @Valid int salaryIncrease) {
        log.info("Increase {} dollars  person whose id is {}",salaryIncrease,personId.get());

        try {
            peopleDAO.addSalaryOptimisticLock(personId.get(),salaryIncrease);
            return Response.ok()
                    .build();
        }catch (Exception e){
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(e.getMessage())
                    .build();
        }
    }

//    @Path("/names/{fullName}")
//    @GET
//    @UnitOfWork
//    public List<Person> getPerson(@PathParam("fullName") String fullName) {
//        log.info("*******fullName*************"+fullName);
//        return peopleDAO.findByName(fullName);
//    }

    private Person findSafely(long personId) {
        return peopleDAO.findById(personId).orElseThrow(() -> new NotFoundException("No such user."));
    }

    @Path("/{personId}")
    @DELETE
    @UnitOfWork
    public Response deletePerson(@PathParam("personId") LongParam personId){
        log.info("Delete person with ID {}", personId.get());
        try{
            Person person=peopleDAO.deleteById(personId.get());
            log.info("****deletePerson****Resource********"+person);
            return Response.ok()
                    .entity(person)
                    .build();
        }catch (Exception e) {
            return Response.status(HttpStatus.NOT_FOUND_404)
                    .entity(e.getMessage())
                    .build();
        }

    }

}
