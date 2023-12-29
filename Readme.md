## Aufgabenstellung

Implementieren Sie die Geschäftslogik für ein einfaches Terminbuchungssystem für Gesundheitseinrichtungen

Über dieses System können Gesundheitseinrichtungen (Allgemeinmedizin, Physiotherapie, Kinderarzt) für 
ein bestimmtes Datum zur Verfügung stehende Behandlungstermine anbieten.

Diese Behandlungstermine stehen zu einem bestimmten Datum bei einer bestimmten Gesundheitseinrichtung
zur Verfügung und können von Kunden reserviert (bzw. storniert) werden.

Das System soll dabei die Entities ``Provider`` (Allgemeinmedizin, Physiotherapie, Kinderarzt), 
``Appointment`` (Behanldungstermin) und ``Customer`` in einer Datenbank persistieren und Operationen 
zur Verwaltung zur Verfügung stellen. 

```java
public class Customer {
    // constructor
    public Customer(String email) {...}
    // getter/setter
    public String getEmail() {...}
    public String getLastname() {...}
    public void setLastname(String lastname) {...}
    public String getFirstname() {...}
    public void setFirstname(String firstname) {...}
}

public class Provider {
    public Integer getId() {...}
    public ProviderType getType() {...}
    public void setType(ProviderType type) {...}
    public String getAddress() {...}
    public void setAddress(String address) {...}
    public List<Appointment> getAppointments() {...}
}

public class Appointment {
    public Integer getId() {...}
    public Customer getCustomer() {...}
    public void setCustomer(Customer customer) {...}
    public Provider getProvider() {...}
    public void setProvider(Provider provider) {...}
    public LocalDateTime getTime() {...}
    public void setTime(LocalDateTime time) {...}
}
```

Die Enum-Klasse ``ProviderType`` ist wie folgt definiert:

```java
public enum ProviderType {
    ALLGEMEINMEDIZIN, PHYSIOTHERAPIE, KINDERMEDIZIN
}
```

Die zu implementierende Business-Logik wird über das Interface ``AppointmentRepository``
spezifiziert:

```java
public interface AppointmentRepository {

    boolean create(Customer customer);
    Customer read(String email);
    Customer update(Customer customer);
    boolean delete(Customer customer);

    boolean create(Provider provider);
    Provider read(Integer id);
    Provider update(Provider provider);
    boolean delete(Provider provider);

    List<Customer> findCustomersBy(String lastname, String firstname);
    List<Provider> findProvidersBy(ProviderType type, String addressPart);
    List<Appointment> findAppointmentsAt(String addressPart);
    List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to);
    List<Appointment> getAppointmentsFor(Customer customer);

    boolean reserve(Appointment appointment, Customer customer);
    boolean cancel(Appointment appointment, Customer customer);

    void close();
}
```
Die Implementierung dieser Business-Logik soll in einer eigenen Klasse von Ihnen erstellt werden.  

Die Spezifikation (und Überprüfung) der Funktionalität erfolgt über vorgegebene Test-Klassen.
Zusätzlich sind die Rahmenbedingungen der einzelnen Methoden direkt am Interface ``AppointmentRepository`` als
javadoc-Kommentare beschrieben. 

Damit die Testklassen auf eine Instanz Ihrer Implementierung zugreifen kann, gibt es die Klasse ``AppointmentRepositoryFactory``.
In der Methode ``get(EntityManagerFactory factory)`` instanzieren Sie dazu Ihre Klasse, sorgen dafür, dass sie einen 
``EntityManager`` von der übergebenen ``EntityManagerFactory`` verwendet und geben Ihre Instanz zurück. 

### Aufgaben

* Verwenden Sie dieses gradle-Projekt für die Implementierung der Aufgabe.
    * Öffnen Sie das Projekt in IntelliJ, indem Sie das ``build.gradle`` File als Projekt öffnen.
    * Konfigurieren Sie die nötige _Persistence Unit_ für das Pojekt. Der Name der Persistence-Unit lautet dabei **"assignment"**.
* Die Rümpfe der Entitäten finden Sie im Package ``at.campus02.dbp2.assignment``.
    * Stellen Sie die Entity-Klassen fertig und verwenden Sie dabei die nötigen JPA Mappings.
    * **Achtung:** Die Methoden ``hashCode`` und ``equals`` sind bereits definiert und so implementiert, wie es für die Tests gebraucht wird. Ändern Sie diese Methoden nicht!
* Das Interface ``AppointmentRepository`` finden Sie ebenfalls im Package ``at.campus02.dbp2.assignment``.
    * Programmieren Sie eine Implementierung des Interfaces.
    * Stellen Sie die ``AppointmentRepositoryFactory`` so fertig, dass die Factory-Methode eine Instanz Ihrer Implementierung zurückgibt.
* Die Testklassen finden Sie im Source-Folder ``src/main/test`` im Package ``at.campus02.dbp2.assignment``.
    * Implementieren Sie die Funktionalität so, dass möglichst viele Unit-Tests  erfolgreich durchlaufen.
    * Lassen Sie dann auch den ``IntegrationTest`` laufen und vergleichen Sie dann die Datenbank mit dem am Ende von ``IntegrationTest`` als Kommentar beschriebenen Datenstand.
    * Die vorgegebenen Testklassen sollen nicht geändert werden. 
    * Zum Ausführen aller Tests auf einmal können Sie den gradle-Task ``test`` verwenden. 
* Verwenden Sie zumindest einmal auch eine ``NamedQuery``!

### Abgabe

Zippen Sie das fertiggestellte Projekt (den  gesamten "assignment" Folder) und geben Sie dem zip-File den Namen "IHRNAME.zip" (beispielsweise "mair.zip").
Das zip-File über Moodle abgegeben (im Ordner "Abgabe Assignment").

### Tipps zur Umsetzung

* Reihenfolge
  1) Projekt öffnen und alle Tests laufen lassen. Zunächst wird klarerweise kein Test funktionieren, aber wenn die Tests prinzipiell gestartet werden, wissen Sie, dass die Libraries vorhanden und das Projekt kompiliert werden kann. 
  2) ``persistence.xml`` einrichten (analog zu unseren bisherigen Beispielen, ``drop-and-create-tables`` verwenden).
  3) Entitäten fertigstellen, JPA Annotationen. 
  4) Implementierung von ``AppointmentRepository`` in eigener Klasse (nur Rümpfe)
  5) ``AppointmentRepositoryFactory`` fertigstellen, sodass eine Instanz Ihrer Implementierung verwendet werden kann. 
  6) Tests laufen lassen, eventuell sind dann schon einige "erfolgreich" (nicht wirklich, aber zufällig)
  7) Tatsächliche Implementierung Schritt für Schritt, beginnend mit den CRUD-Operationen (und ``CrudSpecification`` zur Überprüfung).
  8) Danach die Such- und Reservierungslogik implementieren (``BusinessLogicSpecification`` testet diese).
  9) Am Ende auch ``IntegrationTest`` laufen lassen und in der Datenbank kontrollieren. 
* JPQL unterstützt auch Funktionalitäten wie "like", "lower", "upper", ...
* Bei Queries mit optionalen Parametern können Sie zB. eine eigene Query für jeden möglichen Fall erstellen, 
  oder aber den Query-String erweitern, falls der Parameter vorhanden ist (und auch ``setParameter`` dafür nur 
  aufrufen, falls der Parameter vorhanden ist).
* Überlegen Sie sich, wann bestimmte ``EntityManager`` Methoden (``merge``, ``refresh``, ...) hilfreich sind - oder welcher ``CascadeType`` bei Relations-Annotationen.