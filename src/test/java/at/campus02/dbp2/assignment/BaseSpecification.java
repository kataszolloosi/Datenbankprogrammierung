package at.campus02.dbp2.assignment;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class BaseSpecification {

    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;
    AppointmentRepository repository;

    // <editor-fold desc="Common Testdata">
    final protected String email = "email.address@campus02.at";
    final protected String lastname = "lastname";
    final protected String firstname = "firstname";

    final protected String graz_doc1 = "Annenstraße 14, 8020 Graz";
    final protected String graz_doc2 = "Herrengasse 23, 8010 Graz";
    final protected String graz_test = "Nullstraße 12, 8010 Graz";
    final protected String leibnitz_test = "Hauptplatz 1, 8430 Leibnitz";


    // </editor-fold>

    @BeforeEach
    public void before() {
        entityManagerFactory = Persistence.createEntityManagerFactory("assignment");
        entityManager = entityManagerFactory.createEntityManager();
        repository = AppointmentRepositoryFactory.get(entityManagerFactory);
    }

    @AfterEach
    public void after() {
        if (repository != null) {
            repository.close();
        }
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    static Customer prepareCustomer(String lastname, String firstname, String email) {
        Customer customer = new Customer(email);
        customer.setLastname(lastname);
        customer.setFirstname(firstname);
        return customer;
    }

    static Provider prepareProvider(ProviderType type, String address) {
        Provider provider = new Provider();
        provider.setType(type);
        provider.setAddress(address);
        return provider;
    }

    static Appointment prepareAppointment(LocalDateTime offerTime) {
        Appointment appointment = new Appointment();
        appointment.setTime(offerTime);
        return appointment;
    }

    protected Customer createCustomer(Customer customer) {
        entityManager.getTransaction().begin();
        entityManager.persist(customer);
        entityManager.getTransaction().commit();
        return customer;
    }

    protected Provider createProvider(Provider provider) {
        entityManager.getTransaction().begin();
        for (Appointment appointment : provider.getAppointments()) {
            appointment.setProvider(provider);
        }
        entityManager.persist(provider);
        entityManager.getTransaction().commit();
        entityManager.refresh(provider);
        return provider;
    }


    <T> EntityManager getEntityManagerFromInterface(T manager) throws IllegalAccessException {
        EntityManager em = null;
        Field[] fields = manager.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(EntityManager.class)) {
                field.setAccessible(true);
                em = (EntityManager) field.get(manager);
                break;
            }
        }
        return em;
    }

}