package at.campus02.dbp2.assignment;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.util.Objects;

@NamedQuery(
        name = "Customer.findByLastname",
        query = "select c from Customer c "+
                " where lower(c.lastname) like lower(:lastname) " +
                " order by c.lastname")

@Entity
public class Customer {
    @Id
    private String email;
    private String firstname;
    private String lastname;

    public Customer() {

    }

    public Customer(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(getEmail(), customer.getEmail()) && Objects.equals(getFirstname(), customer.getFirstname()) && Objects.equals(getLastname(), customer.getLastname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getFirstname(), getLastname());
    }

}
