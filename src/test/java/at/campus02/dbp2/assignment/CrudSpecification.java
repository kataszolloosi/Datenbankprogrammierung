package at.campus02.dbp2.assignment;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CrudSpecification extends BaseSpecification {

    @Test
    public void c01_createCustomerWithNullReturnsFalse() {
        // when
        boolean success = repository.create((Customer) null);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void c02_createCustomerWithoutEmailReturnsFalse() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, null);

        // when
        boolean success = repository.create(customer);

        // then
        assertThat(success, is(false));
        assertThat(
                entityManager
                        .createQuery("select c from Customer c where c.firstname=:firstname and c.lastname=:lastname",
                                Customer.class)
                        .setParameter("firstname", firstname)
                        .setParameter("lastname", lastname)
                        .getResultList().isEmpty(),
                is(true));
    }

    @Test
    public void c03_createCustomerPersistsCustomerInDatabaseAndReturnsTrue() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);

        // when
        boolean success = repository.create(customer);
        entityManager.clear();

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Customer.class, customer.getEmail()), is(customer));

    }

    @Test
    public void c04_createCustomerWithExistingEmailReturnsFalse() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);

        // when
        boolean success = repository.create(customer);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void c05_readCustomerWithNullReturnsNull() {
        // when
        Customer fromDb = repository.read((String) null);

        // then
        assertThat(fromDb, is(nullValue()));
    }

    @Test
    public void c06_readCustomerReadsEntityFromDatabase() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);
        entityManager.clear();

        // when
        Customer fromDb = repository.read(customer.getEmail());

        // then
        assertThat(customer, is(fromDb));
        assertThat(entityManager.find(Customer.class, customer.getEmail()), is(fromDb));
    }

    @Test
    public void c07_updateCustomerWithNullReturnsNull() {
        // when
        Customer updated = repository.update((Customer) null);

        // then
        assertThat(updated, is(nullValue()));
        assertThat(entityManager.createQuery("select c from Customer c").getResultList().isEmpty(), is(true));
    }

    @Test
    public void c08_updateCustomerStoresNewValuesInDatabase() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);
        entityManager.clear();
        String different = "different";

        // when
        customer.setFirstname(different);
        customer.setLastname(different);
        Customer updated = repository.update(customer);

        // then
        assertThat(updated, is(customer));
        assertThat(updated.getFirstname(), is(different));
        assertThat(updated.getLastname(), is(different));
        assertThat(updated, is(entityManager.find(Customer.class, customer.getEmail())));
    }

    @Test
    public void c09_updateCustomerNotExistingThrowsIllegalArgumentException() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> repository.update(customer));
    }

    @Test
    public void c10_deleteCustomerRemovesFromDatabaseAndReturnsTrue() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);
        entityManager.clear();

        // when
        boolean success = repository.delete(customer);

        // then
        assertThat(success, is(true));
        Customer fromDb = entityManager.find(Customer.class, customer.getEmail());
        assertThat(fromDb, is(nullValue()));
    }

    @Test
    public void c11_deleteNotExistingCustomerThrowsIllegalArgumentException() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> repository.delete(customer));
    }

    @Test
    public void c12_deleteNullAsCustomerReturnsFalse() {
        // when
        boolean result = repository.delete((Customer) null);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void c13_createProviderWithNullReturnsFalse() {
        // when
        boolean success = repository.create((Provider) null);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void c14_createProviderPersistsProviderInDatabaseAndReturnsTrue() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);

        // when
        boolean success = repository.create(provider);

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Provider.class, provider.getId()), is(provider));
    }

    @Test
    public void c15_createProviderWithExistingIdReturnsFalse() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        createProvider(provider);

        // when
        boolean success = repository.create(provider);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void c16_createProviderAlsoPersistsAppointmentsInDatabase() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 15, 14, 0));
        appointment1.setProvider(provider);
        appointment2.setProvider(provider);
        provider.getAppointments().add(appointment1);
        provider.getAppointments().add(appointment2);

        // when
        boolean success = repository.create(provider);

        // then
        assertThat(success, is(true));
        assertThat(appointment1.getId(), is(notNullValue()));
        assertThat(appointment2.getId(), is(notNullValue()));
        Provider fromDb = entityManager.find(Provider.class, provider.getId());
        entityManager.refresh(fromDb);
        assertThat(fromDb.getAppointments(), containsInAnyOrder(appointment1, appointment2));
    }

    @Test
    public void c17_createProviderAlsoPersistsAppointmentsInDatabaseEvenIfProviderNotSetOnAppointment() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 15, 14, 0));
        provider.getAppointments().add(appointment1);
        provider.getAppointments().add(appointment2);

        // when
        boolean success = repository.create(provider);

        // then
        assertThat(success, is(true));
        assertThat(appointment1.getId(), is(notNullValue()));
        assertThat(appointment2.getId(), is(notNullValue()));
        Provider fromDb = entityManager.find(Provider.class, provider.getId());
        entityManager.refresh(fromDb);
        assertThat(fromDb.getAppointments(), containsInAnyOrder(appointment1, appointment2));
    }

    @Test
    public void c18_readProviderWithNullReturnsNull() {
        // when
        Provider fromDb = repository.read((Integer) null);

        // then
        assertThat(fromDb, is(nullValue()));
    }

    @Test
    public void c19_readProviderReadsEntityFromDatabase() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 15, 14, 0));
        provider.getAppointments().add(appointment1);
        provider.getAppointments().add(appointment2);
        createProvider(provider);
        entityManager.clear();

        // when
        Provider fromDb = repository.read(provider.getId());

        // then
        assertThat(provider, is(fromDb));
        assertThat(entityManager.find(Provider.class, provider.getId()), is(fromDb));
        assertThat(fromDb.getAppointments(), containsInAnyOrder(appointment1, appointment2));
    }

    @Test
    public void c20_updateProviderWithNullReturnsNull() {
        // when
        Provider updated = repository.update((Provider) null);

        // then
        assertThat(updated, is(nullValue()));
        assertThat(entityManager.createQuery("select p from Provider p").getResultList().isEmpty(), is(true));
    }

    @Test
    public void c21_updateProviderStoresNewValuesInDatabase() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 15, 14, 0));
        provider.getAppointments().add(appointment1);
        provider.getAppointments().add(appointment2);
        createProvider(provider);
        entityManager.clear();

        // when
        provider.setType(ProviderType.KINDERMEDIZIN);
        provider.setAddress(graz_doc2);
        Provider updated = repository.update(provider);

        // then
        assertThat(updated, is(provider));
        assertThat(updated.getAddress(), is(graz_doc2));
        assertThat(updated.getType(), is(ProviderType.KINDERMEDIZIN));
        assertThat(updated, is(entityManager.find(Provider.class, provider.getId())));
    }

    @Test
    public void c22_updateProviderNotExistingThrowsIllegalArgumentException() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> repository.update(provider));
    }

    @Test
    public void c23_updateProviderAlsoUpdatesValuesOnAppointments() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 15, 14, 0));
        provider.getAppointments().add(appointment1);
        createProvider(provider);
        entityManager.clear();

        // when
        LocalDateTime updatedTime = LocalDateTime.of(2021, 11, 11, 10, 10);
        appointment1.setTime(updatedTime);
        provider.getAppointments().add(appointment2);
        Provider updated = repository.update(provider);

        // then
        assertThat(updated, is(provider));
        assertThat(updated.getAppointments(), containsInAnyOrder(appointment1, appointment2));
        assertThat(entityManager.find(Appointment.class, appointment1.getId()).getTime(), is(updatedTime));
    }

    @Test
    public void c24_updateProviderDeletesAllAppointmentsNoLongerAssigned() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 15, 14, 0));
        Appointment appointment3 = prepareAppointment(LocalDateTime.of(2021, 11, 19, 15, 0));
        provider.getAppointments().add(appointment1);
        provider.getAppointments().add(appointment2);
        provider.getAppointments().add(appointment3);
        createProvider(provider);
        entityManager.clear();

        // when
        provider.getAppointments().remove(appointment1);
        provider.getAppointments().remove(appointment2);
        Provider updated = repository.update(provider);

        // then
        assertThat(updated, is(provider));
        assertThat(updated.getAppointments(), contains(appointment3));
        assertThat(entityManager.find(Appointment.class, appointment1.getId()), is(nullValue()));
        assertThat(entityManager.find(Appointment.class, appointment2.getId()), is(nullValue()));
        assertThat(entityManager.find(Appointment.class, appointment3.getId()), is(appointment3));
    }

    @Test
    public void c25_updateProviderWithSameAppointmentTwiceInListEndsUpHavingThatAppointmentOnlyOnceInList() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        provider.getAppointments().add(appointment1);
        createProvider(provider);
        entityManager.clear();

        // when
        provider.getAppointments().add(appointment1);
        Provider updated = repository.update(provider);

        // then
        assertThat(updated.getAppointments().size(), is(1));
        assertThat(updated.getAppointments(), contains(appointment1));
    }

    @Test
    public void c26_deleteProviderRemovesFromDatabaseAndReturnsTrue() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        createProvider(provider);
        entityManager.clear();

        // when
        boolean success = repository.delete(provider);

        // then
        assertThat(success, is(true));
        Provider fromDb = entityManager.find(Provider.class, provider.getId());
        assertThat(fromDb, is(nullValue()));
    }

    @Test
    public void c27_deleteNotExistingProviderThrowsIllegalArgumentException() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> repository.delete(provider));
    }

    @Test
    public void c28_deleteNullAsProviderReturnsFalse() {
        // when
        boolean result = repository.delete((Provider) null);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void c29_deleteProviderAlsoDeletesAppointmentsFromDatabase() {
        // given
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 15, 14, 0));
        Appointment appointment3 = prepareAppointment(LocalDateTime.of(2021, 11, 19, 15, 0));
        provider.getAppointments().add(appointment1);
        provider.getAppointments().add(appointment2);
        provider.getAppointments().add(appointment3);
        createProvider(provider);
        entityManager.clear();

        // when
        boolean success = repository.delete(provider);

        // then
        assertThat(success, is(true));
        assertThat(entityManager.find(Appointment.class, appointment1.getId()), is(nullValue()));
        assertThat(entityManager.find(Appointment.class, appointment2.getId()), is(nullValue()));
        assertThat(entityManager.find(Appointment.class, appointment3.getId()), is(nullValue()));
    }

    @Test
    public void c30_deleteCustomerAlsoRemovesReferencesFromAppointments() {
        // given
        Customer customer = prepareCustomer(lastname, firstname, email);
        createCustomer(customer);
        Provider provider = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 11, 10, 10));
        provider.getAppointments().add(appointment1);
        appointment1.setCustomer(customer);
        createProvider(provider);
        entityManager.clear();

        // when
        boolean success = repository.delete(customer);

        // then
        assertThat(entityManager.find(Appointment.class, appointment1.getId()).getCustomer(), is(nullValue()));
    }

    @Test
    public void c31_closeClosesEntityManagerButDoesNotCloseFactory() throws IllegalAccessException {
        // given
        EntityManager em = getEntityManagerFromInterface(repository);

        // when
        repository.close();

        // then
        if (em != null) {
            assertThat(em.isOpen(), is(false));
        }
        assertThat(entityManagerFactory.isOpen(), is(true));
    }
}
