package Utils;

import Controllers.TimelineController;
import Models.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class EventThumbnail {
    private StackPane view;
    private Event event;
    private double horzPos;
    private int vertPos;
    private TimelineController tc;
    public EventThumbnail(Event event, double horzPos, int vertPos, TimelineController tc) {
        this.horzPos = horzPos;
        this.vertPos = vertPos;
        this.event = event;
        this.tc = tc;
    }

    public void createView(double timeRatio) {
        StackPane thumbnailTemplate = loadEventThumbnailTemplate();
        if(thumbnailTemplate!=null) {
            ((Label) thumbnailTemplate.lookup("#EventLabel")).setText(event.getName());
            ((Label) thumbnailTemplate.lookup("#EventDescription")).setText(Utils.GeneralUtils.generateTimeStr(event.getStartDate(), event.getEndDate()));
            if (event.getImage() != null && !event.getImage().isEmpty()) {
                ImageView imageView = (ImageView) thumbnailTemplate.lookup("#EventImage");
                imageView.setImage(new Image(event.getImage()));
                Utils.GraphicUtils.centerImage(imageView);

            } else {
                //hide image
                thumbnailTemplate.lookup("#EventImage").setVisible(false);
                VBox eventBox = (VBox) thumbnailTemplate.lookup("#EventBox");
                eventBox.setPadding(new Insets(5, 5, 5, 10));
                eventBox.setPrefWidth(175.0);
                ((Label) thumbnailTemplate.lookup("#EventLabel")).setMaxWidth(170);
                ((Label) thumbnailTemplate.lookup("#EventDescription")).setMaxWidth(170);
            }
            double translateY = 0.0;
            double lineStartY = 628.0;
            double lineEndY = 685.0;
            boolean vertMirror = false;
            if (vertPos == 3) {
                translateY = -(58.0 - 12.0);
                thumbnailTemplate.setViewOrder(0);
                //lineHeight
            } else if (vertPos == 2) {
                translateY = -(58.0 * 2 - 16.0) + 29 - 2;
                lineStartY = 628.0;
                lineEndY += 54.0;
                thumbnailTemplate.setViewOrder(1);
            } else if (vertPos == 1) {
                translateY = -(58.0 * 3 - 19.0) + (2*29) - 3.0;
                lineStartY = 628.0;
                lineEndY += (2*58.0) - 8.0;
                thumbnailTemplate.setViewOrder(2);
            } else if (vertPos == 0) {
                translateY = -(58.0 * 4 - 23.0) + (3*29) - 6.0;
                lineStartY = 628.0;
                lineEndY += (3*58.0) - 11.0;
                thumbnailTemplate.setViewOrder(3);
            } else if (vertPos == 4) { //ON THE BOTTOM SIDE
                //=3
                translateY = 58.0 - 12.0;
                vertMirror = true;
                thumbnailTemplate.setViewOrder(0);
            } else if (vertPos == 5) {
                //=2
                translateY = 58.0 * 2 - 16.0 - 29 + 2;;
                vertMirror = true;
                lineStartY -= 54.0;
                thumbnailTemplate.setViewOrder(1);
            } else if (vertPos == 6) {
                //=1
                translateY = 58.0 * 3 - 19.0 - 29 - 29 + 3.0;
                vertMirror = true;
                lineStartY -= (2*58.0) + 8.0;
                thumbnailTemplate.setViewOrder(2);
            } else if (vertPos == 7) {
                //=0
                translateY = 58.0 * 4 - 23.0 - 29 - 29 - 29 + 6.0;
                vertMirror = true;
                lineStartY -= (3*58.0) + 11.0;
                thumbnailTemplate.setViewOrder(3);
            } else {
                System.err.println("UNKNOWN VERTICAL POSITION");
            }

            Rectangle timePoint = (Rectangle) thumbnailTemplate.lookup("#timePoint");
            Line eventLineConnector = (Line) thumbnailTemplate.lookup("#EventLineConnector");
            eventLineConnector.setEndY(lineStartY);
            eventLineConnector.setStartY(lineEndY);
            double timeLenght = 6.0; //duration of the event
            if(event.getEndDate()!=null) {
                timeLenght = (((event.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - event.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())/1000.0)*timeRatio)+6.0;
            }
            timePoint.setWidth(timeLenght);
            if (vertMirror) {
                //invert vertical position of elements
                StackPane eventThumbnailContainer = (StackPane) thumbnailTemplate.lookup("#EventThumbnailContainer");
                eventThumbnailContainer.setAlignment(timePoint, Pos.TOP_LEFT);
                eventThumbnailContainer.setAlignment(thumbnailTemplate.lookup("#EventInnerContainer"), Pos.BOTTOM_LEFT);
            }
            thumbnailTemplate.setTranslateX(horzPos);
            thumbnailTemplate.setTranslateY(translateY);
            Event ev = this.event;
            //create click listener
            thumbnailTemplate.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    openEvent(ev);
                }
            });

            setView(thumbnailTemplate);
            setEvent(event);
            System.out.println("("+vertPos+") "+event.getName());
        }
    }

    private StackPane loadEventThumbnailTemplate() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Views/EventThumbnail.fxml"));
            StackPane thumbnailTemplate = loader.load();
            return thumbnailTemplate;
        } catch (Exception e) {
            System.err.println("Error loading event thumbnail");
            return null;
        }
    }

    private void setView(StackPane view) {
        this.view = view;
    }
    private void setEvent(Event event) {
        this.event = event;
    }
    public StackPane getView() {
        return view;
    }
    public Event getEvent() {
        return event;
    }
    private void openEvent(Event event) {
        tc.openEvent(event);
    }
}
