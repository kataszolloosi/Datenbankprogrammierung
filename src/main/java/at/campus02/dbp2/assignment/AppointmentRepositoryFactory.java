package at.campus02.dbp2.assignment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class AppointmentRepositoryFactory {

    // Für Klassen, die nur statische Methoden anbieten, verwendet man in Java häufig einen
    // privaten Konstruktor, damit von "außen" keine Instanz erzeugt werden kann.
    private AppointmentRepositoryFactory() {
    }

    /**
     * Factory Methode, mit deren Hilfe eine Instanz der Implementierung von <code>{@link AppointmentRepository}</code>
     * erzeugt wird.
     * @param factory EntityManagerFactory zur Erzeugung des EntityManagers, den die Implementierung für den
     *                Zugriff auf die DB verwenden soll.
     * @return eine Implementierung von <code>{@link AppointmentRepository}</code>.
     */
    public static AppointmentRepository get(EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();
        return new AppointmentRepositoryImpl(manager);
    }
}
