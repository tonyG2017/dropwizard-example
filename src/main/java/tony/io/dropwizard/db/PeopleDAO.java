package tony.io.dropwizard.db;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;
import tony.io.dropwizard.core.Person;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
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
        log.info("Creating " + " " + person.getFullName() + " " + person.getJobTitle()+ " " + person.getPassWord());
        return persist(person);
    }

    public void update(Person person) throws Exception{
        Session session = this.currentSession();
        Person perisistedPerson = session.get(Person.class, (Serializable) Objects.requireNonNull(person.getId()));
        if(perisistedPerson == null)
            throw (new Exception(
                    String.format("Person with id:%s NotFound", person.getId())
            ));
        perisistedPerson.copy(person);
        log.info("Updating person: " + person);
        session.update(perisistedPerson);
    }

    @SuppressWarnings("unchecked")
    public List<Person> findAll() {
        return list((Query<Person>) namedQuery("tony.io.dropwizard.core.Person.findAll"));
    }


    //https://www.javaguides.net/2019/02/hibernate-5-create-read-update-and-delete-operations-example.html
    public Person deleteById(Long id) throws Exception{

//     try{
//        try(Session session = this.currentSession()){
// The session already active
// Using try(source) will not delete the specified person
// and will give Caused by: java.lang.IllegalStateException: org.hibernate.resource.jdbc.internal.LogicalConnectionManagedImpl@6c2db099 is closed
         Session session = this.currentSession();
        //Transaction already active, so no need to beginTransaction.
//         Transaction   tx = session.beginTransaction();  //开启事务//通过HQL查询创建一个处于持久化状态的对象
         Person person = session.get(Person.class, (Serializable) Objects.requireNonNull(id));
         if(person == null)
             throw (new Exception(
                     String.format("Person with id:%s NotFound" , id)
             ));
         log.info("******DELETE******"+person);

            session.delete(person);  //删除指定的用户信息
         return person;
//            tx.commit();//提交事务
//        }catch(Exception e){
//            log.error("****deleteById**Exception***\n"+e.getMessage());
////            if(tx!=null){
////                tx.rollback();  //回滚事务
////            }
//        }

    }
}
