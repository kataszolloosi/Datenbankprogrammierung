package at.campus02.dbp2.assignment;

import org.apache.derby.iapi.store.raw.log.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppointmentRepositoryFactory implements AppointmentRepository{

    private final EntityManager manager;
    public static AppointmentRepositoryFactory repository;
    private AppointmentRepositoryFactory(EntityManagerFactory factory) {
        manager = factory.createEntityManager();
    }

    public static AppointmentRepository get(EntityManagerFactory factory) {
        if (repository == null) {
            repository = new AppointmentRepositoryFactory(factory);
        }
        return repository;
    }

    //-------------------------------CRUD-----------------------------------------------------------
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
        customer = manager.merge(customer);

        // Retrieve the appointments associated with the customer
        List<Appointment> appointments = getAppointmentsFor(customer);

        // Remove references from appointments
        for (Appointment appointment : appointments) {
            appointment.setCustomer(null);
            manager.merge(appointment);
        }

        manager.remove(customer);
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

        for (Appointment appointment : provider.getAppointments()) {
            appointment.setProvider(provider);
            manager.persist(appointment);
        }

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

        if (firstname == null || firstname.isEmpty()) {
            TypedQuery<Customer> query = manager.createNamedQuery(
                    "Customer.findByLastname",
                    Customer.class
            );
            query.setParameter("lastname", "%" + lastname + "%");
            return query.getResultList();
        }

        // Adjusted code to handle the case where both lastname and firstname are provided
        if (lastname != null && firstname != null) {
            TypedQuery<Customer> query = manager.createQuery(
                    "SELECT c FROM Customer c WHERE UPPER(c.lastname) LIKE UPPER(:lastname) AND UPPER(c.firstname) LIKE UPPER(:firstname)",
                    Customer.class
            );
            query.setParameter("lastname", "%" + lastname + "%");
            query.setParameter("firstname", "%" + firstname + "%");
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
                        "where lower(a.provider.address) like lower(:addressPart) ",
                //+ "and a.time is not null ",
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
                        "where a.time >= :from and a.time <= :to ",
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

        TypedQuery<Appointment> query = manager.createQuery("SELECT a FROM Appointment a WHERE a.customer = :customer", Appointment.class);
        query.setParameter("customer", customer);
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
