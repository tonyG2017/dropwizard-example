package tony.io.dropwizard.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.Column;
        import javax.persistence.Entity;
        import javax.persistence.GeneratedValue;
        import javax.persistence.GenerationType;
        import javax.persistence.Id;
        import javax.persistence.NamedQueries;
        import javax.persistence.NamedQuery;
        import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "people")
@NamedQueries(
        {
                @NamedQuery(
                        name = "tony.io.dropwizard.core.Person.findAll",
                        query = "SELECT p FROM Person p"
                )
        })
@Getter
@Setter
@ToString

public class Person {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "fullName cannot be null")
    @Size(min = 2, max = 20)
    @Column(name = "fullName", nullable = false)
    private String fullName;

    @NotNull(message = "jobTitle cannot be null")
    @Size(min = 2, max = 20)
    @Column(name = "jobTitle", nullable = false)
    private String jobTitle;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "passWord", nullable = true)
    private String passWord;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }

        final Person that = (Person) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.fullName, that.fullName) &&
                Objects.equals(this.jobTitle, that.jobTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, jobTitle);
    }
}
