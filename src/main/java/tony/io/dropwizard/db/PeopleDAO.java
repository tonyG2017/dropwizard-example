package tony.io.dropwizard.db;

import com.google.inject.Inject;
import tony.io.dropwizard.core.Person;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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


    //https://blog.csdn.net/qq_35224639/article/details/80611540
    public List<Person> findWithFilters(Optional<String> fullName, Optional<String > jobTitle){
//        Criteria c = criteria().add(Restrictions.eq("name", name));
//        return list(c);
//    }
        CriteriaQuery<Person> criteriaQuery = criteriaQuery();

        Root<Person> from = criteriaQuery.from(Person.class);
        fullName.ifPresent(fullNameStr->{
            criteriaQuery.select(from).where(from.get("fullName").in(fullNameStr));
        });

        jobTitle.ifPresent(jobTitleStr->{
            criteriaQuery.select(from).where(from.get("jobTitle").in(jobTitleStr));
        });
        List<Person> personList = list(criteriaQuery);
        return personList;
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
