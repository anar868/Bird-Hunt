import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;


public class TitleScreen    {
    private Scene scene;
    private Pane containerPane;
    private Label text;
    MediaPlayer titleMediaPlayer;

    public TitleScreen(double scale, double volume) {
        titleMediaPlayer = new MediaPlayer(new Media(new File("src/assets/effects/Title.mp3")
                .toURI().toString()));
        titleMediaPlayer.setVolume(volume);
        configureScene(scale);
        playMusic();
    }

    public void configureScene(double scale)    {
        containerPane = new Pane();

        Image backgroundImage = new Image("assets/welcome/1.png");
        double originalBackgroundWidth = backgroundImage.getWidth();
        double scaledBackgroundWidth = originalBackgroundWidth * scale;
        ImageView background = new ImageView(backgroundImage);
        background.setPreserveRatio(true);
        background.setFitWidth(scaledBackgroundWidth);


        text = new Label("PRESS ENTER TO PLAY\nPRESS ESC TO EXIT");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 14*scale));
        text.setTextFill(Color.ORANGE);

        EventHandler<ActionEvent> eventHandler = event -> {
            if (text.getText().length() != 0)   {
                text.setText("");
            } else {
                text.setText("PRESS ENTER TO PLAY\nPRESS ESC TO EXIT");
            }
        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(800), eventHandler));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

        containerPane.getChildren().addAll(background, text);

        scene = new Scene(containerPane);
    }

    public Scene getScene()    {
        return scene;
    }

    public void centerText() {
        text.setLayoutX((containerPane.getWidth() - text.getWidth()) / 2);
        text.setLayoutY(containerPane.getHeight() / 1.5);
    }

    public void playMusic() {
        titleMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        titleMediaPlayer.play();
    }

    public void stopMusic() {
        titleMediaPlayer.stop();
    }
}