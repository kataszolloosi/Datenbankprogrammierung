package at.campus02.dbp2.assignment;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AppointmentRepositoryImpl implements AppointmentRepository {
    private EntityManager manager;

    public AppointmentRepositoryImpl(EntityManager entityManager) {
        this.manager = entityManager;
    }

    @Override
    public boolean create(Customer customer) {
        if (customer == null || customer.getEmail() == null || read(customer.getEmail()) != null)
            return false;

        manager.getTransaction().begin();
        manager.persist(customer);
        manager.getTransaction().commit();
        return true;

    }

    @Override
    public Customer read(String email) {
        if (email == null)
            return null;
        return manager.find(Customer.class, email);
    }

    @Override
    public Customer update(Customer customer) {
        if (customer == null)
            return null;
        if (read(customer.getEmail()) == null) {
            throw new IllegalArgumentException("Customer does not exist, cannot update!");
        }
        manager.getTransaction().begin();
        Customer managed = manager.merge(customer);
        manager.getTransaction().commit();
        return managed;
    }

    @Override
    public boolean delete(Customer customer) {
        if (customer == null)
            return false;
        if (read(customer.getEmail()) == null) {
            throw new IllegalArgumentException("Customer does not exist, cannot delete!");
        }
        manager.getTransaction().begin();
        manager.remove(manager.merge(customer));
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public boolean create(Provider provider) {
        if (provider == null)
            return false;
        if (provider.getId() != null) {
            return false;
        }
        manager.getTransaction().begin();
        manager.persist(provider);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public Provider read(Integer id) {
        if (id == null)
            return null;
        return manager.find(Provider.class, id);
    }

    @Override
    public Provider update(Provider provider) {
        if (provider == null)
            return null;
        if (read(provider.getId()) == null) {
            throw new IllegalArgumentException("Provider does not exist, cannot update!");
        }
        manager.getTransaction().begin();
        Provider managed = manager.merge(provider);
        manager.getTransaction().commit();
        return managed;
    }

    @Override
    public boolean delete(Provider provider) {
        if (provider == null)
            return false;
        if (read(provider.getId()) == null) {
            throw new IllegalArgumentException("Provider does not exist, cannot delete!");
        }
        manager.getTransaction().begin();
        manager.remove(manager.merge(provider));
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public List<Customer> findCustomersBy(String lastname, String firstname) {
        if (lastname == null || lastname.isEmpty())
            throw new IllegalArgumentException("Lastname must not be null");
        if (firstname == null || firstname.isEmpty()){
            TypedQuery<Customer> query = manager.createNamedQuery(
                    "Customer.findByLastname" ,
                    Customer.class
            );
            query.setParameter("lastname", "%" + lastname + "%");
            return query.getResultList();
        }
        if (lastname == null && firstname == null){
            TypedQuery<Customer> query = manager.createQuery(
                    "SELECT c FROM Customer c",
                    Customer.class
            );
            return query.getResultList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Provider> findProvidersBy(ProviderType type, String addressPart) {
        if (type == null || addressPart == null)
            return Collections.emptyList();

        TypedQuery<Provider> query = manager.createQuery(
                "select p from Provider p " +
                        "where p.type = :type " +
                        "and lower(p.address) LIKE lower(:addressPart)",
                Provider.class
        );

        query.setParameter("type", type);
        query.setParameter("addressPart", "%" + addressPart + "%");

        return query.getResultList();
    }

    @Override
    public List<Appointment> findAppointmentsAt(String addressPart) {
        if (addressPart == null)
            return Collections.emptyList();

        TypedQuery<Appointment> query = manager.createQuery(
                "select a from Appointment a " +
                        "where lower(a.provider.address) like lower(:addressPart) " +
                        "and a.time is null",
                Appointment.class
        );
        query.setParameter("addressPart", "%" + addressPart + "%");
        return query.getResultList();
    }

    @Override
    public List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            from = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (to == null) {
            to = LocalDateTime.of(3000, 1, 1, 0, 0);
        }

        TypedQuery<Appointment> query = manager.createQuery(
                "select a from Appointment a " +
                        "where a.time >= :from and a.time <= :to " +
                        "and a.time is null",
                Appointment.class
        );

        query.setParameter("from", from);
        query.setParameter("to", to);

        return query.getResultList();
    }

    @Override
    public List<Appointment> getAppointmentsFor(Customer customer) {
        if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Appointment> query = manager.createQuery(
                "select a from Appointment a " +
                        "WHERE a.customer.email = :customerEmail",
                Appointment.class
        );

        query.setParameter("customerEmail", customer.getEmail());

        return query.getResultList();
    }

    @Override
    public boolean reserve(Appointment appointment, Customer customer) {
        if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty()
                || appointment == null) {
            return false;
        }

        if (!manager.contains(appointment)) {
            return false;
        }

        TypedQuery<Customer> customerQuery = manager.createQuery(
                "select c from Customer c where c.email = :customerEmail",
                Customer.class
        );
        customerQuery.setParameter("customerEmail", customer.getEmail());
        return true;
    }

    @Override
    public boolean cancel(Appointment appointment, Customer customer) {
        if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty() || appointment == null) {
            return false;
        }

        // Check if the appointment exists
        if (!manager.contains(appointment)) {
            return false;
        }

        // Find the customer by email
        TypedQuery<Customer> customerQuery = manager.createQuery(
                "select c from Customer c where c.email = :customerEmail",
                Customer.class
        );
        customerQuery.setParameter("customerEmail", customer.getEmail());
        return true;
    }

    @Override
    public void close() {
        if (manager != null && manager.isOpen()) {
            manager.close();
        }
    }
}
