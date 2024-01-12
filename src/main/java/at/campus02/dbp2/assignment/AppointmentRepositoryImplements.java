//package at.campus02.dbp2.assignment;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.TypedQuery;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//public class AppointmentRepositoryImplements implements AppointmentRepository {
//
//    private  EntityManager manager;
//    public AppointmentRepositoryImplements(EntityManagerFactory factory) {
//        this.manager = factory.createEntityManager();
//    }
//
//    //-------------------------------CRUD-----------------------------------------------------------
//    @Override
//    public boolean create(Customer customer) {
//        if (customer == null || customer.getEmail() == null || read(customer.getEmail()) != null)
//            return false;
//
//        manager.getTransaction().begin();
//        manager.persist(customer);
//        manager.getTransaction().commit();
//        return true;
//    }
//
//    @Override
//    public Customer read(String email) {
//        if (email == null)
//            return null;
//
//        return manager.find(Customer.class, email);
//    }
//
//    @Override
//    public Customer update(Customer customer) {
//        if (customer == null)
//            return null;
//        if (read(customer.getEmail()) == null) {
//            throw new IllegalArgumentException("Customer does not exist, cannot update!");
//        }
//        manager.getTransaction().begin();
//        Customer managed = manager.merge(customer);
//        manager.getTransaction().commit();
//        return managed;
//    }
//
//    @Override
//    public boolean delete(Customer customer) {
//        if (customer == null)
//            return false;
//
//        if (read(customer.getEmail()) == null)
//            throw new IllegalArgumentException("Cannot find Customer");
//
//        List<Appointment> customerAppointments = getAppointmentsFor(customer);
//        for (Appointment a : customerAppointments) {
//            a.setCustomer(null);
//            manager.getTransaction().begin();
//            manager.merge(a);
//            manager.getTransaction().commit();
//        }
//
//        manager.getTransaction().begin();
//        manager.remove(manager.merge(customer));
//        manager.getTransaction().commit();
//
//        return true;
//    }
//
//    @Override
//    public boolean create(Provider provider) {
//        if (provider == null)
//            return false;
//        if (provider.getId() != null) {
//            return false;
//        }
//        manager.getTransaction().begin();
//        manager.persist(provider);
//
//        for (Appointment appointment : provider.getAppointments()) {
//            appointment.setProvider(provider);
//            manager.persist(appointment);
//        }
//
//        manager.getTransaction().commit();
//        return true;
//    }
//
//    @Override
//    public Provider read(Integer id) {
//        if (id == null)
//            return null;
//        return manager.find(Provider.class, id);
//    }
//
//    @Override
//    public Provider update(Provider provider) {
//        if (provider == null) {
//            return null;
//        }
//        if (provider.getId() == null || read(provider.getId()) == null) {
//            throw new IllegalArgumentException("Provider does not exist, cannot update!");
//        }
//        manager.getTransaction().begin();
//        List<Appointment> appListUpdated = provider.getAppointments();
//        for (int i = 0; i < appListUpdated.size() - 1; i++) {
//            for (int j = i + 1; j < appListUpdated.size(); j++) {
//                if (appListUpdated.get(i) == appListUpdated.get(j))
//                    appListUpdated.remove(appListUpdated.get(j));
//            }
//        }
//        for (Appointment a : appListUpdated) {
//            List<Appointment> appListDB = read(provider.getId()).getAppointments();
//            if (!appListDB.contains(a)) {
//                manager.persist(a);
//            }
//        }
//        manager.clear();
//        Provider managed = manager.merge(provider);
//        manager.getTransaction().commit();
//
//        return managed;
//    }
//
//
//    @Override
//    public boolean delete(Provider provider) {
//        if (provider == null)
//            return false;
//        if (read(provider.getId()) == null) {
//            throw new IllegalArgumentException("Provider does not exist, cannot delete!");
//        }
//        manager.getTransaction().begin();
//        manager.remove(manager.merge(provider));
//        manager.getTransaction().commit();
//
//        return true;
//    }
//
//    @Override
//    public List<Customer> findCustomersBy(String lastname, String firstname) {
//        if (lastname == null || lastname.isEmpty())
//            throw new IllegalArgumentException("Lastname must not be null");
//
//        if (firstname == null || firstname.isEmpty()) {
//            TypedQuery<Customer> query = manager.createNamedQuery(
//                    "Customer.findByLastname",
//                    Customer.class
//            );
//            query.setParameter("lastname", "%" + lastname + "%");
//            return query.getResultList();
//        }
//
//        if (lastname != null && firstname != null) {
//            TypedQuery<Customer> query = manager.createQuery(
//                    "select c from Customer c where upper(c.lastname) like upper(:lastname) " +
//                            "and upper(c.firstname) like upper(:firstname)",
//                    Customer.class
//            );
//            query.setParameter("lastname", "%" + lastname + "%");
//            query.setParameter("firstname", "%" + firstname + "%");
//            return query.getResultList();
//        }
//
//        return Collections.emptyList();
//    }
//
//    @Override
//    public List<Provider> findProvidersBy(ProviderType type, String addressPart) {
//        if (type == null || addressPart == null)
//            return Collections.emptyList();
//
//        TypedQuery<Provider> query = manager.createQuery(
//                "select p from Provider p " +
//                        "where p.type = :type " +
//                        "and lower(p.address) LIKE lower(:addressPart)",
//                Provider.class
//        );
//
//        query.setParameter("type", type);
//        query.setParameter("addressPart", "%" + addressPart + "%");
//
//        return query.getResultList();
//    }
//
//    @Override
//    public List<Appointment> findAppointmentsAt(String addressPart) {
//        if (addressPart == null)
//            return Collections.emptyList();
//
//        TypedQuery<Appointment> query = manager.createQuery(
//                "select a from Appointment a " +
//                        "where lower(a.provider.address) like lower(:addressPart) " +
//                        "and a.customer is null ",
//                Appointment.class
//        );
//
//        query.setParameter("addressPart", "%" + addressPart + "%");
//
//        return query.getResultList();
//    }
//
//    @Override
//    public List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to) {
//        if (from == null) {
//            from = LocalDateTime.of(2000, 1, 1, 0, 0);
//        }
//        if (to == null) {
//            to = LocalDateTime.of(3000, 1, 1, 0, 0);
//        }
//
//        TypedQuery<Appointment> query = manager.createQuery(
//                "select a from Appointment a " +
//                        "where a.time >= :from and a.time <= :to and a.customer is null",
//                Appointment.class
//        );
//
//        query.setParameter("from", from);
//        query.setParameter("to", to);
//
//        return query.getResultList();
//    }
//
//    @Override
//    public List<Appointment> getAppointmentsFor(Customer customer) {
//        if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        TypedQuery<Appointment> query = manager.createQuery("select a from Appointment a where a.customer = :customer",
//                Appointment.class);
//        query.setParameter("customer", customer);
//
//        return query.getResultList();
//    }
//
//    @Override
//    public boolean reserve(Appointment appointment, Customer customer) {
//        if (customer == null || appointment == null) {
//            return false;
//        }
//        if (customer.getEmail() == null) {
//            return false;
//        }
//        if (read(customer.getEmail()) == null) {
//            return false;
//        }
//        if (appointment.getCustomer() != null) {
//            return false;
//        }
//        if (appointment.getProvider() == null) {
//            return false;
//        } else
//            appointment.setCustomer(customer);
//
//        manager.getTransaction().begin();
//        manager.merge(appointment);
//        manager.getTransaction().commit();
//
//        return true;
//    }
//
//    @Override
//    public boolean cancel(Appointment appointment, Customer customer) {
//        if (customer == null || appointment == null) {
//            return false;
//        }
//        if (customer.getEmail() == null) {
//            return false;
//        }
//        if (read(customer.getEmail()) == null) {
//            return false;
//        }
//        if (appointment.getCustomer() == null) {
//            return false;
//        }
//        if (appointment.getCustomer() != customer) {
//            return false;
//        }
//        if (appointment.getProvider() == null) {
//            return false;
//        } else
//            appointment.setCustomer(null);
//        manager.getTransaction().begin();
//        manager.merge(appointment);
//        manager.getTransaction().commit();
//
//        return true;
//    }
//
//    @Override
//    public void close() {
//        if (manager != null && manager.isOpen()) {
//            manager.close();
//        }
//    }
//}
