package Utils;

import Controllers.ConfirmationMessageController;
import Controllers.PopupController;
import Models.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;



public class GraphicUtils {

    /*private String imageFile;
    private ImageView eventImageView;

    public void fileChooser(Node node){
        FileChooser filePicChooser = new FileChooser();
        filePicChooser.setTitle ("Select  Image");


        filePicChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files", "*.png")
                ,new FileChooser.ExtensionFilter("JPG files", "*.jpg")
        );
        File selectedFile = filePicChooser.showOpenDialog(new Stage());
        this.imageFile = selectedFile.getName();
        this.eventImageView = new ImageView(new Image(getClass().getResourceAsStream(selectedFile.getAbsolutePath())));
    }*/

    /*public static void addConfirmationPopup(Stage parent, String message, String description) {
        parent.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean unfocused, Boolean focused) {
                if (unfocused) {
                    //confirmation popup
                    try {
                        Confirmation.makeText(parent, message, description);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }*/

    public static void makeConfirmation(Stage ownerStage, String TitleText, String DescriptionText, PopupController pc) {
        try {
            Stage confirmationStage = new Stage();
            confirmationStage.initOwner(ownerStage);
            confirmationStage.setResizable(false);
            confirmationStage.initStyle(StageStyle.TRANSPARENT);

            FXMLLoader f = new FXMLLoader(GraphicUtils.class.getResource("/Views/ConfirmationMessage.fxml"));
            Parent root = f.load();
            ConfirmationMessageController cmc = f.getController();

            cmc.setParent(ownerStage);
            cmc.setPC(pc);
            cmc.setText(TitleText, DescriptionText);

            //root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 50px;");
            // root.setOpacity(0);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            confirmationStage.setScene(scene);
            confirmationStage.show();
        }catch (Exception e) {
            System.err.println("Problem creating Confimation popup");
            System.err.println(e);
        }
    }

    public static void generateTimeList(ListView<Timeline> lsv) {
        lsv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lsv.setBorder(Border.EMPTY);
        lsv.setCellFactory(lv -> new ListCell<Timeline>() {
            @Override
            protected void updateItem(Timeline t, boolean empty) {
                super.updateItem(t, empty);
                if (t == null || empty) {
                    setDisable(false);
                    setGraphic(null);
                    setBackground(new Background(new BackgroundFill(Color.web("#FFFFFF"),null,null)));
                } else {
                    setDisable(false);
                    VBox container = new VBox();
                    container.setPadding(new Insets(10,10,10,10));
                    Label title = new Label();
                    if(t.getTitle()!=null) {
                        title.setText(t.getTitle());
                    }
                    title.setTextFill(isSelected() ? Color.WHITE : Color.web("#707070"));

                    Font font = Font.font(23.0);
                    title.setFont(font);
                    Label authorName = new Label();
                    authorName.setTextFill(isSelected() ? Color.WHITE : Color.web("#A2A2A2"));
                    authorName.setStyle("-fx-label-padding: 0 10 0 0");
                    //protect against null pointers
                    authorName.setText("Created by " + t.getCreatedBy().getUsername());
                    title.setMaxWidth(193);
                    //title.setWrappingWidth(100);
                    authorName.setMaxWidth(193);
                    container.getChildren().addAll(title,authorName);
                    //setTextFill(isSelected() ? Color.WHITE : Color.BLACK);
                    setBackground(new Background(new BackgroundFill(isSelected() ? Color.web("#434FE2") : Color.WHITE,null,null)));
                    setGraphic(container);
                }
            }
        });
    }

    public static void centerImage(ImageView imageView) {
        Image img = imageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;
            System.out.println("x: "+((imageView.getFitWidth() - w) / 2));
            System.out.println("y: "+((imageView.getFitHeight() - h) / 2));
            imageView.setX((imageView.getFitWidth() - w) / 2);
            //imageView.setY((imageView.getFitHeight() - h) / 2);
            imageView.setY(10);
        }
    }

    public static boolean isHorizontalImage(Image image) {
        return image.getWidth() > image.getHeight();
    }

    public static void makeToast(Stage ownerStage, String toastMsg, ToastType toastType) {
        makeToast(ownerStage, toastMsg, toastType, 3000, 200, 200);
    }

    //Code inspiration: https://stackoverflow.com/questions/26792812/android-toast-equivalent-in-javafx
    //Design inspiration: https://dribbble.com/shots/6852000-Engagio-app-toast-UI-component
    public static void makeToast(Stage ownerStage, String toastMsg, ToastType toastType, int toastDelay, int fadeInDelay, int fadeOutDelay)
    {
        Stage toastStage=new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setY(ownerStage.getY()+50);
        toastStage.setWidth(280);
        toastStage.setHeight(180);
        toastStage.setX(ownerStage.getX()+(ownerStage.getWidth()/2)-140);
        int heightMultiplier = toastMsg.length() / 33;

        Text text = new Text(toastMsg);
        text.setFont(Font.font("Verdana", 12));
        text.setFill(Color.web("#2c2522"));
        text.maxWidth(250);
        text.setWrappingWidth(200);

        StackPane container = new StackPane();
        container.setAlignment(Pos.TOP_LEFT);

        System.out.println("NEW HEIGHT: "+heightMultiplier);
        container.setMaxSize(250,55+(heightMultiplier*17.0));
        container.setPrefSize(250,55+(heightMultiplier*17.0));
        container.setMinSize(250,55+(heightMultiplier*17.0));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(1.0);
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setSpread(0.01);
        dropShadow.setRadius(2);
        dropShadow.setColor(Color.rgb(100,100,100,0.8));
        container.setEffect(dropShadow);

        Rectangle rectLeft = new Rectangle();
        rectLeft.setWidth(3);
        rectLeft.setHeight(54+(heightMultiplier*17.0));

        final Rectangle outputClip = new Rectangle();
        outputClip.setArcWidth(6.0);
        outputClip.setArcHeight(6.0);
        outputClip.setHeight(55+(heightMultiplier*17.0));
        outputClip.setWidth(12);
        rectLeft.setClip(outputClip);
        String iconPath = "/Images/icon_error.png";
        String title_txt = "";
        if(toastType == ToastType.ERROR) {
            container.setStyle("-fx-background-radius: 5; -fx-background-color: #fbe8e3; -fx-padding: 16px;");
            rectLeft.setFill(Color.web("#ee8f6c"));
            iconPath = "/Images/icon_error.png";
            title_txt ="Error";
        } else if(toastType == ToastType.SUCCESS) {
            container.setStyle("-fx-background-radius: 5; -fx-background-color: #e6f4e7; -fx-padding: 16px;");
            rectLeft.setFill(Color.web("#82c785"));
            iconPath = "/Images/icon_success.png";
            title_txt ="Success!";
        } else if(toastType == ToastType.WARNING) {
            container.setStyle("-fx-background-radius: 5; -fx-background-color: #fff1db; -fx-padding: 16px;");
            rectLeft.setFill(Color.web("#ffb74d"));
            iconPath = "/Images/icon_warning.png";
            title_txt ="Warning";
        } else if(toastType == ToastType.INFO) {
            container.setStyle("-fx-background-radius: 5; -fx-background-color: #e0eefb; -fx-padding: 16px;");
            rectLeft.setFill(Color.web("#62aced"));
            iconPath = "/Images/icon_info.png";
            title_txt ="Info";
        }

        Text title = new Text(title_txt);
        title.setFont(Font.font("Verdana", 14));
        title.setFill(Color.web("#2c2522"));

        ImageView icon = null;
        try {
            icon = new ImageView(new Image(GraphicUtils.class.getResource(iconPath).toURI().toString()));
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            icon.setPreserveRatio(true);
        }catch (Exception e) {
            System.err.println(e);
        }

        container.getChildren().addAll(rectLeft,title, text, icon);
        container.setAlignment(rectLeft,Pos.TOP_LEFT);
        rectLeft.setTranslateX(-16);
        title.setTranslateY(-6);
        title.setTranslateX(30);
        text.setTranslateY(12);
        text.setTranslateX(30);
        rectLeft.setTranslateY(-16);
        icon.setTranslateY(-5);

        StackPane root = new StackPane();
        root.getChildren().add(container);
        root.setMargin(container,new Insets(15,15,15,15));

        root.setBackground(Background.EMPTY);
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
        toastStage.show();

        javafx.animation.Timeline fadeInTimeline = new javafx.animation.Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished((ae) ->
        {
            new Thread(() -> {
                try
                {
                    Thread.sleep(toastDelay);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                javafx.animation.Timeline fadeOutTimeline = new javafx.animation.Timeline();
                KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
                fadeOutTimeline.setOnFinished((aeb) -> toastStage.close());
                fadeOutTimeline.play();
            }).start();
        });
        fadeInTimeline.play();
    }

    public static void setAvatarImage(Image image, Circle avatar) {
        ImagePattern userImagePattern;
        double ratio = image.getWidth()/image.getHeight();
        double ratio_h = image.getHeight()/image.getWidth();
        double width, height;

        if(ratio>=1) {
            //width > height
            height = 40;
            width = height * ratio;
            double bigger = height > width ? height : width;
            userImagePattern = new ImagePattern(image,bigger/2,bigger/2, width, width,false);
            System.out.println("height: "+height+"; width: "+width +"; x: "+bigger/2+"; y: "+bigger/2);
        } else {
            //height > width
            width = 40;
            height = (40*ratio_h);
            double bigger = height > width ? height : width;
            double smaller = height < width ? height : width;
            userImagePattern = new ImagePattern(image,height/2,height/2, height, height,false);
            System.out.println("height: "+height+"; width: "+width +"; x: "+bigger/2+"; y: "+smaller/2);
        }
        avatar.setFill(userImagePattern);
    }
}

