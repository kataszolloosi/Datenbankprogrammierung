package at.campus02.dbp2.assignment;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BusinessLogicSpecification extends BaseSpecification {

    private Customer customer1;
    private Customer customer2;
    private Customer customer3;
    private Customer customer4;

    private Provider provider1;
    private Provider provider2;
    private Provider provider3;
    private Provider provider4;

    private Appointment appointment1;
    private Appointment appointment2;
    private Appointment appointment3;
    private Appointment appointment4;
    private Appointment appointment5;
    private Appointment appointment6;
    private Appointment appointment7;
    private Appointment appointment8;

    private void setupTestData() {

        customer1 = createCustomer(prepareCustomer("Dornacher", "Dorothea", "dorli@mail.com"));
        customer2 = createCustomer(prepareCustomer("Dornacher", "Reinhard", "reini@mail.com"));
        customer3 = createCustomer(prepareCustomer("Hornbacher", "Bernhard", "berni@mail.com"));
        customer4 = createCustomer(prepareCustomer("Hornbacher", "Bernhard", "another.berni@mail.com"));

        provider1 = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc1);
        provider2 = prepareProvider(ProviderType.ALLGMEINMEDIZIN, graz_doc2);
        provider3 = prepareProvider(ProviderType.PHYSIOTHERAPIE, graz_test);
        provider4 = prepareProvider(ProviderType.PHYSIOTHERAPIE, leibnitz_test);

        appointment1 = prepareAppointment(LocalDateTime.of(2021, 11, 20, 10, 0));
        appointment2 = prepareAppointment(LocalDateTime.of(2021, 11, 20, 14, 0));
        appointment3 = prepareAppointment(LocalDateTime.of(2021, 11, 22, 10, 0));
        appointment4 = prepareAppointment(LocalDateTime.of(2021, 11, 23, 10, 0));
        appointment5 = prepareAppointment(LocalDateTime.of(2021, 11, 24, 10, 0));
        appointment6 = prepareAppointment(LocalDateTime.of(2021, 11, 24, 18, 0));
        appointment7 = prepareAppointment(LocalDateTime.of(2021, 12, 2, 10, 0));
        appointment8 = prepareAppointment(LocalDateTime.of(2021, 12, 3, 10, 0));

        provider1.getAppointments().addAll(Arrays.asList(appointment1, appointment2));
        provider2.getAppointments().addAll(Arrays.asList(appointment3, appointment4, appointment5));
        provider3.getAppointments().add(appointment6);
        provider4.getAppointments().addAll(Arrays.asList(appointment7, appointment8));

        createProvider(provider1);
        createProvider(provider2);
        createProvider(provider3);
        createProvider(provider4);
    }

    private void setupRegistrations() {
        appointment1.setCustomer(customer1);
        appointment4.setCustomer(customer3);
        appointment8.setCustomer(customer1);

        entityManager.getTransaction().begin();
        entityManager.merge(appointment1);
        entityManager.merge(appointment4);
        entityManager.merge(appointment8);
        entityManager.getTransaction().commit();
    }

    @Test
    public void bl01_findCustomersByWithoutLastnameThrowsIllegalArgumentException() {
        // when / then
        assertThrows(IllegalArgumentException.class, () -> repository.findCustomersBy(null, "Dorothea"));
    }

    @Test
    public void bl02_findCustomersByLastnameOnlyReturnsMatchingCustomers() {
        // given
        setupTestData();

        // when
        List<Customer> customers = repository.findCustomersBy("Dornacher", null);

        // then
        assertThat(customers, containsInAnyOrder(customer1, customer2));

        // and when
        customers = repository.findCustomersBy("asdf", null);

        // then
        assertThat(customers, is(empty()));

    }

    @Test
    public void bl02_findCustomersByLastnameAndFirstnameReturnsMatchingCustomers() {
        // given
        setupTestData();

        // when
        List<Customer> customers = repository.findCustomersBy("Dornacher", "Dorothea");

        // then
        assertThat(customers, containsInAnyOrder(customer1));

        // and when
        customers = repository.findCustomersBy("Dornacher", "Hansi");

        // then
        assertThat(customers, is(empty()));
    }

    @Test
    public void bl03_findCustomersBySearchesCaseInsensitive() {
        // given
        setupTestData();

        // when
        List<Customer> customers = repository.findCustomersBy("dornacher", "dorothea");

        // then
        assertThat(customers, containsInAnyOrder(customer1));
    }

    @Test
    public void bl04_findProvidersByTypeOnlyReturnsEmptyList() {
        // given
        setupTestData();

        // when
        List<Provider> providers = repository.findProvidersBy(ProviderType.ALLGMEINMEDIZIN, null);

        // then
        assertThat(providers.size(), is(0));

        // and when
        providers = repository.findProvidersBy(ProviderType.KINDERMEDIZIN, null);
        assertThat(providers.size(), is(0));
    }

    @Test
    public void bl05_findProvidersByAddressOnlyReturnsEmptyList() {
        // given
        setupTestData();

        // when
        List<Provider> providers = repository.findProvidersBy(null, "graz");

        // then
        assertThat(providers.size(), is(0));
    }

    @Test
    public void bl06_findProvidersByNeitherTypeNorAddressReturnsEmptyList() {
        // given
        setupTestData();

        // when
        List<Provider> providers = repository.findProvidersBy(null, null);

        // then
        assertThat(providers.size(), is(0));
    }

    @Test
    public void bl07_findProvidersByTypeAndAddressFindsMatching() {
        // given
        setupTestData();

        // when
        List<Provider> providers = repository.findProvidersBy(ProviderType.ALLGMEINMEDIZIN, graz_doc1);

        // then
        assertThat(providers.size(), is(1));
        assertThat(providers, contains(provider1));

        // and when
        providers = repository.findProvidersBy(ProviderType.PHYSIOTHERAPIE, leibnitz_test);
        assertThat(providers.size(), is(1));
        assertThat(providers, contains(provider4));
    }

    @Test
    public void bl08_findProvidersByTypeAndAddressPartFindsMatching() {
        // given
        setupTestData();

        // when
        List<Provider> providers = repository.findProvidersBy(ProviderType.ALLGMEINMEDIZIN, "Graz");

        // then
        assertThat(providers.size(), is(2));
        assertThat(providers, containsInAnyOrder(provider1, provider2));

        // and when
        providers = repository.findProvidersBy(ProviderType.PHYSIOTHERAPIE, "Leibnitz");
        assertThat(providers.size(), is(1));
        assertThat(providers, contains(provider4));
    }

    @Test
    public void bl09_findProvidersByTypeAndAddressPartFindsMatchingCaseInsensitively() {
        // given
        setupTestData();

        // when
        List<Provider> providers = repository.findProvidersBy(ProviderType.ALLGMEINMEDIZIN, "graz");

        // then
        assertThat(providers.size(), is(2));
        assertThat(providers, containsInAnyOrder(provider1, provider2));

        // and when
        providers = repository.findProvidersBy(ProviderType.PHYSIOTHERAPIE, "leibnitz");
        assertThat(providers.size(), is(1));
        assertThat(providers, contains(provider4));
    }

    @Test
    public void bl10_findAppointmentsAtNullReturnsEmptyList() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointmentsAt(null);

        // then
        assertThat(appointments, is(empty()));
    }

    @Test
    public void bl11_findAppointmentsAtUnknownAddressReturnsEmptyList() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointmentsAt("leoben");

        // then
        assertThat(appointments, is(empty()));
    }

    @Test
    public void bl12_findAppointmentsAtReturnsMatching() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointmentsAt(graz_doc1);

        // then
        assertThat(appointments, containsInAnyOrder(appointment1, appointment2));
    }

    @Test
    public void bl13_findAppointmentsAtAddressPartReturnsMatching() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointmentsAt("Graz");

        // then
        assertThat(appointments, containsInAnyOrder(appointment1, appointment2, appointment3, appointment4, appointment5, appointment6));
    }

    @Test
    public void bl14_findAppointmentsAtAddressPartReturnsMatchingCaseInsensitively() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointmentsAt("graz");

        // then
        assertThat(appointments, containsInAnyOrder(appointment1, appointment2, appointment3, appointment4, appointment5, appointment6));
    }

    @Test
    public void bl15_findAppointmentsAtReturnsMatchingAvailableOnly() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        List<Appointment> appointments = repository.findAppointmentsAt("graz");

        // then
        assertThat(appointments, containsInAnyOrder(appointment2, appointment3, appointment5, appointment6));
    }

    @Test
    public void bl16_findAppointmentsFromToFindsMatching() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointments(
                LocalDateTime.of(2021, 11, 22, 11, 0),
                LocalDateTime.of(2021, 11, 25, 11, 0)
        );

        // then
        assertThat(appointments, containsInAnyOrder(appointment4, appointment5, appointment6));
    }

    @Test
    public void bl17_findAppointmentsFromNullFindsMatchingUntilTo() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointments(
                null,
                LocalDateTime.of(2021, 11, 25, 11, 0)
        );

        // then
        assertThat(appointments, containsInAnyOrder(appointment1, appointment2, appointment3, appointment4, appointment5, appointment6));
    }

    @Test
    public void bl18_findAppointmentsToNullFindsMatchingStartingFrom() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointments(
                LocalDateTime.of(2021, 11, 22, 11, 0),
                null
        );

        // then
        assertThat(appointments, containsInAnyOrder(appointment4, appointment5, appointment6, appointment7, appointment8));
    }

    @Test
    public void bl19_findAppointmentsFromNullToNullFindsAll() {
        // given
        setupTestData();

        // when
        List<Appointment> appointments = repository.findAppointments(
                null,
                null
        );

        // then
        assertThat(appointments, containsInAnyOrder(appointment1, appointment2, appointment3, appointment4, appointment5, appointment6, appointment7, appointment8));
    }

    @Test
    public void bl20_findAppointmentsFindsMatchingAvailiableOnly() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        List<Appointment> appointments = repository.findAppointments(
                LocalDateTime.of(2021, 11, 22, 11, 0),
                LocalDateTime.of(2021, 11, 25, 11, 0)
        );

        // then
        assertThat(appointments, containsInAnyOrder(appointment5, appointment6));
    }

    @Test
    public void bl21_getAppointmentsForNullReturnsEmptyList() {
        // given
        setupTestData();

        // when / then
        assertThat(repository.getAppointmentsFor(null), is(empty()));
    }

    @Test
    public void bl22_getAppointmentsForUnknownCustomerReturnsEmptyList() {
        // given
        setupTestData();

        // when / then
        assertThat(repository.getAppointmentsFor(prepareCustomer("last", "first", "email")), is(empty()));
    }

    @Test
    public void bl23_getAppointmentsForCustomerReturnsMatching() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        List<Appointment> appointments = repository.getAppointmentsFor(customer1);

        // then
        assertThat(appointments, containsInAnyOrder(appointment1, appointment8));

        // and when
        appointments = repository.getAppointmentsFor(customer2);

        // then
        assertThat(appointments, is(empty()));

        // and when
        appointments = repository.getAppointmentsFor(customer3);

        // then
        assertThat(appointments, contains(appointment4));
    }

    @Test
    public void bl24_reserveNullAppointmentForNullCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.reserve(null, null);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl25_reserveNullAppointmentForCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.reserve(null, customer3);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl26_reserveAppointmentForNullReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.reserve(appointment3, null);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl27_reserveAppointmentForUnknownCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.reserve(appointment3, prepareCustomer("last", "first", "email"));

        // then
        assertThat(success, is(false));
        assertThat(appointment3.getCustomer(), is(nullValue()));
    }

    @Test
    public void bl28_reserveUnknownAppointmentForCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.reserve(prepareAppointment(LocalDateTime.of(2021, 11, 26, 10, 0)), customer3);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl29_reserveAppointmentForCustomerReturnsTrueAndUpdatesDatabase() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.reserve(appointment3, customer3);

        // then
        assertThat(success, is(true));
        assertThat(appointment3.getCustomer(), is(customer3));
        entityManager.clear();
        Appointment fromDb = entityManager.find(Appointment.class, appointment3.getId());
        entityManager.refresh(fromDb);
        assertThat(fromDb.getCustomer(), is(customer3));
    }

    @Test
    public void bl30_reserveAlreadyAssignedAppointmentReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.reserve(appointment1, customer3);

        // then
        assertThat(success, is(false));
        assertThat(appointment1.getCustomer(), is(customer1));
        entityManager.clear();
        Appointment fromDb = entityManager.find(Appointment.class, appointment1.getId());
        entityManager.refresh(fromDb);
        assertThat(fromDb.getCustomer(), is(customer1));
    }

    @Test
    public void bl31_cancelNullAppointmentForNullCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.cancel(null, null);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl32_cancelNullAppointmentForCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.cancel(null, customer3);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl33_cancelAppointmentForNullReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.cancel(appointment4, null);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl34_cancelAppointmentForUnknownCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.cancel(appointment4, prepareCustomer("last", "first", "email"));

        // then
        assertThat(success, is(false));
        assertThat(appointment4.getCustomer(), is(customer3));
    }

    @Test
    public void bl35_cancelUnknownAppointmentForCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.cancel(prepareAppointment(LocalDateTime.of(2021, 11, 26, 10, 0)), customer3);

        // then
        assertThat(success, is(false));
    }

    @Test
    public void bl36_cancelAppointmentForCustomerReturnsTrueAndUpdatesDatabase() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.cancel(appointment1, customer1);

        // then
        assertThat(success, is(true));
        assertThat(appointment1.getCustomer(), is(nullValue()));
        entityManager.clear();
        Appointment fromDb = entityManager.find(Appointment.class, appointment1.getId());
        entityManager.refresh(fromDb);
        assertThat(fromDb.getCustomer(), is(nullValue()));
    }

    @Test
    public void bl37_cancelAppointmentAssignedToDifferentCustomerReturnsFalse() {
        // given
        setupTestData();
        setupRegistrations();

        // when
        boolean success = repository.cancel(appointment1, customer3);

        // then
        assertThat(success, is(false));
        assertThat(appointment1.getCustomer(), is(customer1));
        entityManager.clear();
        Appointment fromDb = entityManager.find(Appointment.class, appointment1.getId());
        entityManager.refresh(fromDb);
        assertThat(fromDb.getCustomer(), is(customer1));
    }

}
