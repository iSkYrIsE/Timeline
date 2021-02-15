package Utils;

import Models.Event;
import Models.Timeline;
import Models.User;
import Utils.Persistence;
import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class DatabaseController {
    //START OF DATABASE CONFIGURATION
    //If you want to use and external DB just change persistence property to Persistance.EXTERNAL_DB
    //and add the program needs to connect to that DB
    final static Persistence persistence = Persistence.DB; //=> can be Persistence.DB, Persistence.NONE, Persistence.DB
    final static int dbPort = 26439;
    final static String dbFolder = "DB";
    final static String dbUser = "root";
    final static String dbPassword = "root";
    final static String dbName = "mainDB";
    final static String dbDriver = "org.mariadb.jdbc.Driver";
    final static String dbUrl = "jdbc:mysql://localhost:"+ dbPort +"/"+dbName;
    final static String dbDialect = "org.hibernate.dialect.MariaDBDialect";
    //END OF DATABASE CONFIGURATION

    private static DB db;
    private static final SessionFactory sf = getSessionFactory(); //buildSessionFactory();

    public static void startDB() throws ManagedProcessException {
        if(persistence == Persistence.DB) {
            DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
            config.setPort(dbPort); // 0 => autom. detect free port
            config.setDataDir(dbFolder);
            db = DB.newEmbeddedDB(config.build());
            db.start();
            db.createDB(dbName);
            System.out.println("=== Database running on: " + config.getURL(dbName) + " ===");
        }else if(persistence == Persistence.EXTERNAL_DB) {
            System.out.println("=== External database: " + dbUrl + " ===");
        } else {
            System.out.println("=== Database off ===");
        }
    }

    public static void terminateDB() throws ManagedProcessException {
        db.stop();
    }

    public static Object getRecord(int id, Class entityClass) {
        Session s = getSessionFactory().openSession();
        s.beginTransaction();
        Object result = s.load(entityClass, id);
        Hibernate.initialize(result);
        //s.flush();
        //s.close();
        return result;
    }

    public static void createRecord(Object e) {
        Session s = getSessionFactory().openSession();
        s.beginTransaction();
        s.save(e);
        s.getTransaction().commit();
        //s.flush();
        //s.close();
    }

    public static void updateRecord(Object e) {
        Session s = getSessionFactory().openSession();
        s.beginTransaction();
        s.merge(e);
        s.getTransaction().commit();
    }

    public static void deleteRecord(int id, Class entityClass) {
        Session s = getSessionFactory().openSession();
        s.beginTransaction();
        Object object = s.load(entityClass, id);
        s.delete(object);
        s.getTransaction().commit();
        //s.flush();
       //s.close();
    }

    public static List<Object> getAll(Class objectClass) {
        Session s = getSessionFactory().openSession();
        System.out.println("DB: getting list of "+objectClass.getName());
        //CriteriaBuilder builder = s.getCriteriaBuilder();
        //List<Object> list = s.createQuery(builder.createQuery(objectClass)).getResultList();
        List<Object> list = s.createCriteria(objectClass).list();
        System.out.println("DB: got list of "+objectClass.getName());
        return list;
    }
    
    public static List<User> getAllUsers () {
    	Session s = getSessionFactory().openSession();
    	List<User> allUsernames = s.createCriteria(User.class).list();
    	
    	return allUsernames;
    }

    //SESSION CREATION

    public static SessionFactory getSessionFactory() {
        if(sf!=null) {
            return sf;
        }else {
            SessionFactory sessionFactory = null;
            try {
                Configuration configuration = new Configuration();
                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, dbDriver);
                settings.put(Environment.URL, dbUrl);
                if(dbUser.length()>0) {
                    settings.put(Environment.USER, dbUser);
                }
                if(dbPassword.length()>0) {
                    settings.put(Environment.PASS, dbPassword);
                }
                settings.put(Environment.DIALECT, dbDialect);
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                //settings.put(Environment.HBM2DDL_AUTO, "create-drop");
                settings.put(Environment.HBM2DDL_AUTO, "update");

                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Event.class);
                configuration.addAnnotatedClass(Timeline.class);
                configuration.addAnnotatedClass(User.class);
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                System.err.println("Error in Session Factory");
                System.err.println(e.getMessage());
                //e.printStackTrace();
            }
            return sessionFactory;
        }
    }

    //SPECIFIC METHODS => maybe we can move them to the specific Object model

    public static boolean usernameTaken(String username) {
        Session s = getSessionFactory().openSession();
        Criteria criteria = s.createCriteria(User.class).add(Restrictions.eq("username", username));
        List<User> allUsernames = (List<User>)(List<?>) criteria.list();

        for (int i = 0; i < allUsernames.size(); i++) {
            if (allUsernames.get(i).getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static User getUserByUsernamePassword(String username, String password) {
        Session s = getSessionFactory().openSession();
        Criteria criteria = s.createCriteria(User.class);
        criteria.add(Restrictions.eq("username", username));
        criteria.add(Restrictions.eq("password", password));
        User user =(User) criteria.uniqueResult();
        //s.flush();
        //s.close();
        return user;
    }

    public static List<Event> getEventsBetween(int timelineID, LocalDateTime startDate, LocalDateTime endDate) {
        Session session = getSessionFactory().openSession();
        Query query = session.createQuery("from Event where startDate >= :beginDate and startDate < :endDate and timeline_id = :timelineID ORDER BY startDate ASC");
        query.setParameter("beginDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("timelineID", timelineID);
        System.out.println("From: "+startDate.toString());
        System.out.println("To: "+endDate.toString());
        System.out.println("SQL QUERY: "+query.toString());
        List<Event> listEvents = query.getResultList();
        return listEvents;
    }

    public static List<Event> getEventsBetween(int timelineID, int startInt, int endInt) {
        Session session = getSessionFactory().openSession();
        Query query = session.createQuery("from Event where startInt >= :startInt and startInt < :endInt and timeline_id = :timelineID ORDER BY startInt ASC");
        query.setParameter("startInt", startInt);
        query.setParameter("endInt", endInt);
        query.setParameter("timelineID", timelineID);
        System.out.println("From: "+startInt);
        System.out.println("To: "+endInt);
        System.out.println("SQL QUERY: "+query.toString());
        List<Event> listEvents = query.getResultList();
        return listEvents;
    }

    public static Long getNumberOfEvents(int timelineID) {
        Session session = getSessionFactory().openSession();
        Query query = session.createQuery("SELECT COUNT(*) FROM Event WHERE timeline_id = :timelineID");
        query.setParameter("timelineID", timelineID);
        Long count = (Long) query.uniqueResult();
        return count;
    }
    public static String getUserHint(String username) {
        Session s = getSessionFactory().openSession();
        Criteria criteria = s.createCriteria(User.class);
        criteria.add(Restrictions.eq("username", username));
        User user =(User) criteria.uniqueResult();
        if(user==null) {
            return null;
        }
       else{
            return user.getPasswordHint();
        }




        //s.flush();
        //s.close();
        ///return user.getPasswordHint();
    }
}
