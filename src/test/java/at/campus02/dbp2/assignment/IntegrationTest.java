package at.campus02.dbp2.assignment;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static at.campus02.dbp2.assignment.ProviderType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class IntegrationTest extends BaseSpecification {

    @Test
    public void integrationTest() {

        // given
        Customer customer1 = prepareCustomer("last1", "first1", "mail1");
        Customer customer2 = prepareCustomer("last2", "first2", "mail2");
        Customer customer3 = prepareCustomer("last3", "first3", "mail3");
        Customer customer4 = prepareCustomer("last4", "first4", "mail4");
        Customer customer5 = prepareCustomer("last5", "first5", "mail5");

        Provider provider1 = prepareProvider(ALLGMEINMEDIZIN, "irgendeine straße, graz");
        Provider provider2 = prepareProvider(KINDERMEDIZIN, "eine kleine gasse 2, graz");
        Provider provider3 = prepareProvider(PHYSIOTHERAPIE, "irgendwo 3, leibnitz");

        Appointment appointment1 = prepareAppointment(LocalDateTime.of(2021, 12, 1, 10, 0));
        Appointment appointment2 = prepareAppointment(LocalDateTime.of(2021, 12, 2, 10, 0));
        Appointment appointment3 = prepareAppointment(LocalDateTime.of(2021, 12, 3, 10, 0));
        Appointment appointment4 = prepareAppointment(LocalDateTime.of(2021, 12, 4, 10, 0));
        Appointment appointment5 = prepareAppointment(LocalDateTime.of(2021, 12, 5, 10, 0));
        Appointment appointment6 = prepareAppointment(LocalDateTime.of(2021, 12, 6, 10, 0));
        Appointment appointment7 = prepareAppointment(LocalDateTime.of(2021, 12, 7, 10, 0));

        // assign appointments to cars
        provider1.getAppointments().add(appointment1);
        provider1.getAppointments().add(appointment2);
        provider2.getAppointments().add(appointment3);
        provider2.getAppointments().add(appointment4);
        provider2.getAppointments().add(appointment5);
        provider3.getAppointments().add(appointment6);
        provider3.getAppointments().add(appointment7);

        // when
        repository.create(customer1);
        repository.create(customer2);
        repository.create(customer3);
        repository.create(customer4);
        repository.create(customer5);

        repository.create(provider1);
        repository.create(provider2);
        repository.create(provider3);

        repository.reserve(appointment1, customer1);
        repository.reserve(appointment2, customer2);
        repository.reserve(appointment4, customer3);
        repository.reserve(appointment5, customer3);

        // then
        assertThat(repository.findAppointmentsAt("leibnitz"), containsInAnyOrder(appointment6, appointment7));
        assertThat(repository.findAppointmentsAt("graz"), containsInAnyOrder(appointment3));
        assertThat(
                repository.findAppointments(
                        LocalDateTime.of(2021, 12, 3, 8, 0),
                        LocalDateTime.of(2021, 12, 5, 14, 0)
                ),
                containsInAnyOrder(appointment3));
        assertThat(repository.findProvidersBy(ALLGMEINMEDIZIN, "graz"), containsInAnyOrder(provider1));
        assertThat(repository.findProvidersBy(KINDERMEDIZIN, "graz"), containsInAnyOrder(provider2));
        assertThat(repository.findProvidersBy(PHYSIOTHERAPIE, "leibnitz"), containsInAnyOrder(provider3));
        assertThat(repository.getAppointmentsFor(customer2), containsInAnyOrder(appointment2));
        assertThat(repository.getAppointmentsFor(customer3), containsInAnyOrder(appointment4, appointment5));

        // and when ... data is removed
        repository.delete(provider2);
        repository.delete(customer4);

        // then
        assertThat(repository.findAppointmentsAt("leibnitz"), containsInAnyOrder(appointment6, appointment7));
        assertThat(repository.findAppointmentsAt("graz"), is(empty()));
        assertThat(
                repository.findAppointments(
                        LocalDateTime.of(2021, 12, 3, 8, 0),
                        LocalDateTime.of(2021, 12, 5, 14, 0)
                ),
                is(empty()));
        assertThat(repository.findProvidersBy(ALLGMEINMEDIZIN, "graz"), containsInAnyOrder(provider1));
        assertThat(repository.findProvidersBy(KINDERMEDIZIN, "graz"), is(empty()));
        assertThat(repository.findProvidersBy(PHYSIOTHERAPIE, "leibnitz"), containsInAnyOrder(provider3));
        assertThat(repository.getAppointmentsFor(customer2), containsInAnyOrder(appointment2));
        assertThat(repository.getAppointmentsFor(customer3), is(empty()));
        assertThat(repository.getAppointmentsFor(customer4), is(empty()));
        assertThat(repository.findCustomersBy("last4", "first4"), is(empty()));

        // Datenbankstand am Ende:
        //
        // Providers: 2 (1 DOCTOR in Graz, 1 TEST_CENTER in Leibnitz)
        //
        // Customers: 4
        //
        // Appointments: 4 (2 verfügbar, 2 reserviert)
    }
}
