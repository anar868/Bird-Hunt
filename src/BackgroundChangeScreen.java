import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


public class BackgroundChangeScreen {
    private Scene scene;
    private Pane containerPane;
    private Label text;
    private double scale;
    ImageView background;
    ImageView cursorImageView;
    private int backgroundNumber;
    private int crossHairNumber;

    public BackgroundChangeScreen(double scale) {
        this.backgroundNumber = 1;
        this.crossHairNumber = 1;
        this.scale = scale;
        this.background = new ImageView();
        this.cursorImageView = new ImageView();
        configureScene();
    }

    public void configureScene()    {
        containerPane = new Pane();

        background.setPreserveRatio(true);
        cursorImageView.setPreserveRatio(true);

        text = new Label("USE ARROW KEYS TO NAVIGATE\nPRESS ENTER TO START\nPRESS ESC TO EXIT");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 8*scale));
        text.setTextFill(Color.ORANGE);

        containerPane.getChildren().addAll(background, cursorImageView, text);

        scene = new Scene(containerPane);
    }

    public Scene getScene()    {
        changeImage("");
        changeCursor("");
        return scene;
    }

    public void centerText() {
        text.setLayoutX((containerPane.getWidth() - text.getWidth()) / 2);
        text.setLayoutY(containerPane.getWidth() > 10 * scale + text.getHeight() ?
                10*scale : containerPane.getWidth() / 10);
    }

    public void centerCursor()  {
        cursorImageView.setLayoutX((containerPane.getWidth() - cursorImageView.getFitWidth()) / 2);
        cursorImageView.setLayoutY((containerPane.getHeight() - cursorImageView.getFitHeight()) / 2);
    }

    public void changeImage(String direction)   {
        if (direction.equals("right"))  {
            backgroundNumber = backgroundNumber == 6 ? 1 : backgroundNumber + 1;
        } else if (direction.equals("left"))  {
            backgroundNumber = backgroundNumber == 1 ? 6 : backgroundNumber - 1;
        } else {
            backgroundNumber = 1;
        }
        Image backgroundImage = new Image(String.format("assets/background/%s.png", backgroundNumber));
        double originalBackgroundWidth = backgroundImage.getWidth();
        double scaledBackgroundWidth = originalBackgroundWidth * scale;
        background.setImage(backgroundImage);
        background.setFitWidth(scaledBackgroundWidth);
    }

    public void changeCursor(String direction)  {
        if (direction.equals("up"))  {
            crossHairNumber = crossHairNumber == 7 ? 1 : crossHairNumber + 1;
        } else if (direction.equals("down"))  {
            crossHairNumber = crossHairNumber == 1 ? 7 : crossHairNumber - 1;
        } else {
            crossHairNumber = 1;
        }
        Image cursorImage = new Image(String.format("assets/crosshair/%s.png", crossHairNumber));
        double originalCursorWidth = cursorImage.getWidth();
        double scaledCursorWidth = originalCursorWidth * scale;
        cursorImageView.setImage(cursorImage);
        cursorImageView.setFitWidth(scaledCursorWidth);
    }

    public int getBackgroundNumber() {
        return backgroundNumber;
    }

    public int getCrossHairNumber() {
        return crossHairNumber;
    }
}