package at.campus02.dbp2.assignment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository {

    /**
     * Erzeugt den übergebenen Customer in der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es einen Customer mit derselben Email in der Datenbank passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird der Customer in der Datenbank persistiert und <code>true</code> zurückgegeben.
     * </ul>
     * @param customer der zu persistierende Customer
     * @return <code>true</code> falls der Customer durch diese Aktion in der DB erzeugt wurde, ansonsten <code>false</code>.
     */
    boolean create(Customer customer);

    /**
     * Liest den Customer mit der übergebenen Email aus der Datenbank.
     * Bei Übergabe von <code>null</code> wird <code>null</code> zurückgegeben.
     * @param email die Email des gewünschten Customers.
     * @return der gefundene Customer oder <code>null</code>, falls der Customer mit dieser Email nicht existiert.
     */
    Customer read(String email);

    /**
     * Überträgt den aktuellen Zustand eines Customers in die Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit dieser Email wird eine <code>IllegalArgumentException</code> geworfen.
     *   <li> Ansonsten wird der Customer in der Datenbank übernommen und zurückgegeben.
     * </ul>
     * @param customer der zu speichernde Customer.
     * @return der aktuelle Zustand dieses Customers oder <code>null</code>, falls der Customer nicht übertragen wurde.
     * @throws IllegalArgumentException falls der Customer nicht in der DB vorhanden ist.
     */
    Customer update(Customer customer);

    /**
     * Löscht den angegebenen Customer aus der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit derselben Email in der Datenbank wird eine <code>IllegalArgumentException</code> geworfen.
     *   <li> Ansonsten wird der Customer aus der Datenbank gelöscht und <code>true</code> zurückgegeben.
     * </ul>
     * @param customer der zu löschende Customer
     * @return <code>true</code>, falls der Customer durch diese Aktion gelöscht wurde, ansonsten <code>false</code>.
     * @throws IllegalArgumentException falls der Customer nicht in der DB vorhanden ist.
     */
    boolean delete(Customer customer);

    /**
     * Erzeugt den übergebenen Provider in der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es einen Provider mit derselben ID in der Datenbank passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird der Provider in der Datenbank persistiert und <code>true</code> zurückgegeben.
     *   <li> Nach erfolgreichem Persistieren sind auch alle noch nicht vorhandenen Appointments in der DB persistiert.
     * </ul>
     * @param provider der zu persistierende Provider
     * @return <code>true</code> falls der Provider durch diese Aktion in der DB erzeugt wurde, ansonsten <code>false</code>.
     */
    boolean create(Provider provider);

    /**
     * Liest den Provider mit der angegebenen ID aus der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Übergabe von <code>null</code> wird <code>null</code> zurückgegeben. </li>
     * </ul>
     * @param id die ID des gewünschten Providers.
     * @return den gefundenen Provider oder <code>null</code>, falls der Provider mit dieser ID nicht existiert.
     */
    Provider read(Integer id);

    /**
     * Überträgt den aktuellen Zustand eines Providers in die Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>null</code> wird zurückgegeben.
     *   <li> Hat der übergebene Provider keine ID oder gibt es keinen Provider mit dieser ID in der Datenbank
     *        wird eine <code>IllegalArgumentException</code> geworfen.
     *   <li> Ansonsten wird der Provider in die Datenbank übernommen und zurückgegeben.
     *   <li> Appointments, die nicht mehr zu diesem Provider gehören, werden in der Datenbank ebenfalls gelöscht.
     *   <li> Appointments, die noch nicht in der Datenbank existieren, werden persistiert.
     * </ul>
     * @param provider der zu speichernde Provider.
     * @return der aktuelle Zustand dieses Providers oder <code>null</code>, falls der Provider nicht übertragen wurde.
     */
    Provider update(Provider provider);

    /**
     * Löscht den angegebenen Provider aus der Datenbank.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Provider keine ID oder gibt es keinen Provider mit dieser ID in der Datenbank
     *        wird eine <code>IllegalArgumentException</code> geworfen.
     *   <li> Ansonsten wird der Provider aus der Datenbank gelöscht und <code>true</code> zurückgegeben.
     *   <li> Appointments, die zu diesem Provider gehörten, werden in der Datenbank ebenfalls gelöscht.
     * </ul>
     * @param provider der zu löschende Provider.
     * @return <code>true</code>, falls der Provider durch diese Aktion gelöscht wurde, ansonsten <code>false</code>.
     */
    boolean delete(Provider provider);

    /**
     * Findet alle Customer mit angegebenem Vor- bzw. Nachnamen.
     * <p>Bedingungen: <ul>
     *   <li> Wird als <code>lastname</code> <code>null</code> übergeben, wird eine <code>IllegalArgumentException</code> geworfen.
     *   <li> Wird als <code>firstname</code> <code>null</code> übergeben, werden alle Customer mit dem angegeben Nachnamen
     *     zurückgegeben und der Vorname ignoriert.
     *   <li> Wird sowohl für <code>lastname</code>, als auch <code>firstname</code> <code>null</code> übergeben, werden alle
     *     Customer aus der Datenbank zurückgegeben.
     *   <li> Die Suche soll auch case-insensitive funktionieren, braucht aber nur exakte Matches finden.
     * </ul>
     * @param lastname Nachname des zu suchenden Customer (required).
     * @param firstname Vorname des zu suchenden Customer (oder <code>null</code>, falls nicht nach Vorname gesucht werden soll).
     * @return Liste der gefundenen Customers.
     */
    List<Customer> findCustomersBy(String lastname, String firstname);

    /**
     * Findet alle Provider mit angegebenem ProviderType und einer Adresse, die dem angegebenen addressPart entspricht.
     * <p>Bedingungen: <ul>
     *   <li> Wird als <code>type</code> <code>null</code> übergeben, wird eine leere Liste zurückgegeben.
     *   <li> Wird als <code>location</code> <code>null</code> übergeben, wird eine leere Liste zurückgegeben.
     *   <li> Wird sowohl für <code>type</code>, als auch <code>location</code> <code>null</code> übergeben, wird eine leere Liste zurückgegeben.
     *   <li> Die Suche nach addressPart soll eine "LIKE"-Suche sein und case-insensitive funktionieren.</li>
     * </ul>
     * @param type ProviderType des zu suchenden Providers.
     * @param addressPart Teil der Adresse des zu suchenden Providers.
     * @return Liste der gefundenen Provider.
     */
    List<Provider> findProvidersBy(ProviderType type, String addressPart);

    /**
     * Findet alle noch verfügbaren Appointments bei Providern, deren Adresse den "addressPart" enthält.
     * <p>Bedingungen: <ul>
     *   <li> Wird als addressPart <code>null</code> übergeben, wird eine leere Liste zurückgegeben. </li>
     *   <li> Die Suche nach addressPart soll eine "LIKE"-Suche sein und case-insensitive funktionieren.</li>
     *   <li> Es werden immer nur Appointments gefunden, die noch nicht reserviert wurden.</li>
     * </ul>
     * @param addressPart Teil der Adresse des Providers, bei dem der Termin verfügbar ist.
     * @return Liste der gefundenen buchbaren Appointments.
     */
    List<Appointment> findAppointmentsAt(String addressPart);

    /**
     * Findet alle noch verfügbaren Appointments bei Providern, deren Adresse den "addressPart" enthält.
     * <p>Bedingungen: <ul>
     *   <li> Wird als <code>from</code> <code>null</code> übergeben, wird als untere Schranke der 1.1.2000 verwendet. </li>
     *   <li> Wird als <code>to</code> <code>null</code> übergeben, wird als obere Schranke der 1.1.3000 verwendet. </li>
     *   <li> Sind beide Parameter <code>null</code>, sollen alle freien Appointments zwischen 1.1.2000 und 1.1.3000 gefunden werden.</li>
     *   <li> Es werden immer nur Appointments gefunden, die noch nicht reserviert wurden.</li>
     * </ul>
     * @param from Zeitpunkt, ab dem Appointments gesucht werden sollen (oder <code>null</code>, falls keine untere
     *             Schranke gewünscht ist).
     * @param to Zeitpunkt, bis zu dem Appointments gesucht werden sollen (oder <code>null</code>, falls keine obere
     *             Schranke gewünscht ist).
     * @return Liste der gefundenen buchbaren Appointments.
     */
    List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to);

    /**
     * Findet alle Appointments (von allen Providern), die von einem bestimmten Customer gebucht wurden.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> wird eine leere Liste zurückgegeben.
     *   <li> Bei Übergabe eines unbekannten Customers wird eine leere Liste zurückgegeben.
     * </ul>
     * @param customer der Customer, dessen gebuchte Appointments gefunden werden sollen.
     * @return Liste der gebuchten Appointments des Customers
     */
    List<Appointment> getAppointmentsFor(Customer customer);

    /**
     * Reserviert ein freies Appointment für einen Customer.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> für den Customer passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit dieser Email in der Datenbank, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Bei Parameter <code>null</code> für das Appointment passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Existiert das übergebene Appointment nicht, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ist das Appointment bereits reserviert, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird das Appointment dem Customer in der Datenbank zugewiesen.
     * </ul>
     * @param appointment das zu reservierende Appointment
     * @param customer der Customer, für den das Appointment reserviert werden soll.
     * @return <code>true</code>, falls die Reservierung erfolgreich war, ansonsten <code>false</code>
     */
    boolean reserve(Appointment appointment, Customer customer);

    /**
     * Storniert eine Reservierung für einen Customer.
     * <p>Bedingungen: <ul>
     *   <li> Bei Parameter <code>null</code> für den Customer passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Hat der übergebene Customer keine Email, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Gibt es keinen Customer mit dieser Email in der Datenbank, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Bei Parameter <code>null</code> für das Appointment, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Existiert das übergebene Appointment nicht, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ist das Appointment bereits reserviert, passiert nichts und <code>false</code> wird zurückgegeben.
     *   <li> Ansonsten wird die Buchung des Appointment in der Datenbank aufgehoben.
     * </ul>
     * @param appointment das zu stornierende Appointment
     * @param customer der Customer, für den das Appointment storniert werden soll.
     * @return <code>true</code>, falls die Stornierung erfolgreich war, ansonsten <code>false</code>
     */
    boolean cancel(Appointment appointment, Customer customer);

    /**
     * Sollte alle Ressourcen schließen, die intern erzeugt und verwendet wurden - normalerweise den EntityManager.
     */
    void close();
}
