package Models;

import Utils.DatabaseController;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Timeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id is generated by the DB (do not set it)

    @Generated(GenerationTime.INSERT)
    private Date creationDate; //creationDate is generated by the DB (do not set it)

    private String title, description, timeUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_userid")
    private User createdBy;

    private Date startDate, endDate;
    //for custom time unit
    private int startInt, endInt, stepInt;

    //listOfEvents: it's a good way to show classes dependency, however the events maybe loaded automatically from the DB to the timeline GUI
    //and only load the events that are visible for the user and reuse the GUI objects while scrolling
    @Transient //used only in the data model (not mirrored in th DB
    private ArrayList<Event> eventlist;

    public Timeline() {}

    public Timeline(String title, String description, String timeUnit, User createdBy, Date startDate, Date endDate) {
        this.title = title;
        this.description = description;
        this.timeUnit = timeUnit;
        this.createdBy = createdBy;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void editTimeline(String title, String description, String timeUnit, User createdBy, Date startDate, Date endDate) {
        this.title = title;
        this.description = description;
        this.timeUnit = timeUnit;
        this.createdBy = createdBy;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String reference) throws RuntimeException{
        if(reference.equals("") || reference==null)
            throw new RuntimeException("Timeline name field can not be empty");
        this.title = reference;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date date) throws RuntimeException{
        if(date==null)
            throw new RuntimeException("Start date field can not be empty");
        this.startDate = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date end) throws RuntimeException {
        if(end == null )
            throw new RuntimeException("End date can not be empty");
        this.endDate = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String unit) throws RuntimeException{
        if(unit==null || unit.equals(""))
            throw new RuntimeException("Time unit can not be empty");
        this.timeUnit = unit;
    }

    public ArrayList<Event> getListOfEvents() {
        return eventlist;
    }

    public void addEvent(Event event) {
        eventlist.add(event);
    }

    public void setEvents(ArrayList<Event> events) {
        eventlist = events;
    }

    public void deleteEvent(Event toDelete) {
        eventlist.remove(toDelete);
    }

    public String toString() {
        return this.getTitle() + "," + this.getStartDate() + "," + this.getEndDate() + "," + this.getDescription();
    }

    public int getStartInt() {
        return startInt;
    }

    public void setStartInt(int startInt) {
        this.startInt = startInt;
    }

    public int getEndInt() {
        return endInt;
    }

    public void setEndInt(int endInt) {
        this.endInt = endInt;
    }

    public int getStepInt() {
        return stepInt;
    }

    public void setStepInt(int stepInt) {
        this.stepInt = stepInt;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp createdAt) {
        this.creationDate = createdAt;
    }

    /**public int getNumberOfEvents() {
     return listOfEvents.size();
     }

     public int size() {
     return listOfEvents.size();
     }

     public boolean isEmpty() {
     return getTitle().equals("") || getStartDate().equals("") || getEndDate().equals("");
     }

     private String addEvents() {
     String events = "";
     for (int i = 0; i < listOfEvents.size(); i++)
     events += i + "," + listOfEvents.get(i).toString() + ",";
     return events;
     } **/

    //DB CRUD
    public void save() {
        DatabaseController.createRecord(this);
    }

    public void update() {
        DatabaseController.updateRecord(this);
    }

    public void delete() {
        DatabaseController.deleteRecord(this.id, Timeline.class);
    }

    static public Timeline load(int id) {
        return (Timeline) DatabaseController.getRecord(id, Timeline.class);
    }

    static public List<Timeline> loadAll() {
        return (List<Timeline>)(List<?>) DatabaseController.getAll(Timeline.class);
    }

    public List<Event> getEventsBetween(LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        if(id > 0) {
            return DatabaseController.getEventsBetween(id, startDate, endDate);
        } else {
            throw new Exception("Timeline ID not defined");
        }
    }

    public List<Event> getEventsBetween(int startInt, int endInt) throws Exception {
        if(id > 0) {
            return DatabaseController.getEventsBetween(id, startInt, endInt);
        } else {
            throw new Exception("Timeline ID not defined");
        }
    }

    public Long getTotalNumberOfEvents() throws Exception {
        if(id > 0) {
            return DatabaseController.getNumberOfEvents(id);
        } else {
            throw new Exception("Timeline ID not defined");
        }
    }
}