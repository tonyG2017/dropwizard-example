package tony.io.dropwizard.db;

import com.google.inject.Inject;
import tony.io.dropwizard.core.Person;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class PeopleDAO extends AbstractDAO<Person> {
    @Inject
    public PeopleDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Person create(Person person) {
        System.out.println("tony creating " + " " + person.getFullName() + " " + person.getJobTitle()+ " " + person.getPassWord());
        return persist(person);
    }

    @SuppressWarnings("unchecked")
    public List<Person> findAll() {
        return list((Query<Person>) namedQuery("tony.io.dropwizard.core.Person.findAll"));
    }
}
