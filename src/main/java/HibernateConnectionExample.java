import entity.Actor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConnectionExample {

    public static void main(String[] args) {
        SessionFactory sessionFactory = null;
        Session session = null;

        try {
            // 1. Create a Configuration object and load hibernate.cfg.xml
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

            // 2. Build the SessionFactory
            sessionFactory = configuration.buildSessionFactory();

            // 3. Open a Session
            session = sessionFactory.openSession();

            System.out.println("Successfully connected to the database with Hibernate.");

            // 4. Perform a simple query to get an actor
            session.beginTransaction();
            Actor actor = session.get(Actor.class, (short) 1);
            session.getTransaction().commit();

            // 5. Print the result
            if (actor != null) {
                System.out.println("Successfully retrieved Actor with ID 1:");
                System.out.println(actor);
            } else {
                System.out.println("Could not find Actor with ID 1.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
            }
        }
    }
}
