import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Math.round;


public class GameScreen {
    private Scene gameScene;
    private double backgroundWidth;
    private double backgroundHeight;
    private String sceneName;
    private Pane containerPane;
    private final Pane birdsPane;
    private final double scale;
    private final Pane backgroundContainer;
    private final ImageView[] backgrounds = {new ImageView(), new ImageView(), new ImageView()};
    private final ImageView[] foregrounds = {new ImageView(), new ImageView(), new ImageView()};

    private final Timeline translationTimelineLeft;
    private final Timeline translationTimelineRight;
    private final double max_distance;
    private int level;
    private int ammo;
    Rectangle leftRectangle;
    Rectangle rightRectangle;
    Label levelText;
    Label ammoText;
    Label birdText;
    VBox levelChangePane;
    ArrayList<Bird> birdsArray;
    Label resultText1;
    Label resultText2;
    MediaPlayer gunshotMediaPlayer;
    MediaPlayer levelCompletedMediaPlayer;
    MediaPlayer gameOverMediaPlayer;
    MediaPlayer gameCompleteMediaPlayer;
    MediaPlayer fallBirdMediaPlayer;
    Timeline resultTextTimeline;
    ImageView cursorImageView;

    public GameScreen(double scale, double volume) {
        this.scale = scale;
        backgroundContainer = new Pane();
        levelChangePane = new VBox();
        birdsPane = new Pane();
        translationTimelineLeft = new Timeline();
        translationTimelineRight = new Timeline();
        max_distance = scale*40;
        leftRectangle = new Rectangle();
        rightRectangle = new Rectangle();
        birdsArray = new ArrayList<>();
        sceneName = "gameScene";
        this.level = 1;
        levelText = new Label("Level " + this.level + "/6");
        ammoText = new Label();
        birdText = new Label();
        resultText1 = new Label();
        resultText2 = new Label();
        gunshotMediaPlayer = new MediaPlayer(new Media(new File("src/assets/effects/Gunshot.mp3")
                .toURI().toString()));
        levelCompletedMediaPlayer = new MediaPlayer(
                new Media(new File("src/assets/effects/LevelCompleted.mp3").toURI().toString()));
        gameOverMediaPlayer = new MediaPlayer(
                new Media(new File("src/assets/effects/GameOver.mp3").toURI().toString()));
        gameCompleteMediaPlayer = new MediaPlayer(
                new Media(new File("src/assets/effects/GameCompleted.mp3").toURI().toString()));
        fallBirdMediaPlayer = new MediaPlayer(new Media(new File("src/assets/effects/DuckFalls.mp3")
                .toURI().toString()));
        for (MediaPlayer mediaPlayer: new MediaPlayer[] {gunshotMediaPlayer, levelCompletedMediaPlayer,
        gameOverMediaPlayer, gameCompleteMediaPlayer, fallBirdMediaPlayer}) {
            mediaPlayer.setVolume(volume);
        }
        resultTextTimeline = new Timeline();
        cursorImageView = new ImageView();
        configureScene();
    }

    public void configureScene()    {
        containerPane = new Pane();

        HBox backgroundImagesHBox = new HBox();
        for (ImageView background: backgrounds) {
            background.setPreserveRatio(true);
            backgroundImagesHBox.getChildren().add(background);
        }

        HBox foregroundImageHbox = new HBox();

        for (ImageView foreground: foregrounds) {
            foreground.setPreserveRatio(true);
            foregroundImageHbox.getChildren().add(foreground);
        }

        backgroundContainer.getChildren().addAll(backgroundImagesHBox, birdsPane,foregroundImageHbox);


        leftRectangle.setFill(Color.TRANSPARENT);
        rightRectangle.setFill(Color.TRANSPARENT);
        levelChangePane.getChildren().addAll(resultText1, resultText2);

        containerPane.getChildren().addAll(backgroundContainer, birdText,
                levelText, ammoText, cursorImageView, leftRectangle, rightRectangle);

        gameScene = new Scene(containerPane);
    }

    public Scene getGameScene()    {
        return gameScene;
    }

    public Rectangle getLeftRectangle() {
        return leftRectangle;
    }

    public Rectangle getRightRectangle() {
        return rightRectangle;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSizes(int backgroundNumber, int crossHairNumber)   {
        level = 1;

        Image backgroundImage = new Image(String.format("assets/background/%s.png", backgroundNumber));
        backgroundWidth = backgroundImage.getWidth() * scale;
        backgroundHeight = backgroundImage.getHeight() * scale;
        for (ImageView background: backgrounds) {
            background.setImage(backgroundImage);
            background.setFitWidth(backgroundWidth);
        }

        for (Label text: new Label[] {levelText, ammoText, birdText})   {
            text.setTextAlignment(TextAlignment.CENTER);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 8*scale));
            text.setTextFill(Color.ORANGE);
        }

        containerPane.setPrefWidth(backgroundWidth);
        containerPane.setPrefHeight(backgroundHeight);

        backgroundContainer.setTranslateX(-backgroundWidth);

        leftRectangle.setWidth(max_distance);
        leftRectangle.setHeight(backgroundHeight);
        rightRectangle.setWidth(max_distance);
        rightRectangle.setHeight(backgroundHeight);
        rightRectangle.setX(backgroundWidth - max_distance);
        setTranslationTimeline(backgroundWidth);

        Image foregroundImage = new Image(String.format("assets/foreground/%s.png", backgroundNumber));

        for (ImageView foreground: foregrounds) {
            foreground.setImage(foregroundImage);
            foreground.setFitWidth(backgroundWidth);
        }

        Image cursorImage = new Image(String.format("assets/crosshair/%s.png", crossHairNumber));
        double cursorWidth = cursorImage.getWidth() * scale;
        double cursorHeight = cursorImage.getHeight() * scale;
        cursorImageView.setImage(cursorImage);
        cursorImageView.setFitWidth(cursorWidth);
        cursorImageView.setFitHeight(cursorHeight);
        cursorImageView.setX(backgroundWidth - (cursorWidth / 2));
        cursorImageView.setY(backgroundHeight - (cursorHeight / 2));

        setBirdsPane();
    }

    public void hit(double cursorX, double cursorY)   {
        gunshotMediaPlayer.stop();
        gunshotMediaPlayer.play();
        gameScene.setCursor(Cursor.CROSSHAIR);
        ArrayList<Bird> birdsToRemove = new ArrayList<>();
        for (Bird bird: birdsArray) {
            Bounds birdCoordinates = bird.getBird().localToScene(bird.getBird().getBoundsInLocal());
            double x1 = birdCoordinates.getMinX();
            double x2 = birdCoordinates.getMaxX();
            double y1 = birdCoordinates.getMinY();
            double y2 = birdCoordinates.getMaxY();

            if (cursorX >= x1 && cursorX <= x2 && cursorY >= y1 && cursorY <= y2) {
                bird.hitBird();
                birdsToRemove.add(bird);
            }
        }

        if (birdsToRemove.size() != 0)  {
            fallBirdMediaPlayer.stop();
            fallBirdMediaPlayer.play();
        }
        for (Bird bird: birdsToRemove)  {
            birdsArray.remove(bird);
        }

        ammo--;
        birdText.setText("Birds Left: " + birdsArray.size());
        ammoText.setText("Ammo Left: " + ammo);

        if (birdsArray.size() == 0) {
            level++;
            changeLevel("won");
        } else {
            if (ammo == 0)  {
                changeLevel("fail");
            }
        }
    }

    public void changeLevel(String gameStatus)   {
        levelChangePane.setPrefWidth(backgroundWidth);
        levelChangePane.setPrefHeight(backgroundHeight);
        levelChangePane.setAlignment(Pos.CENTER);

        for (Label text: new Label[]{resultText1, resultText2}) {
            text.setTextAlignment(TextAlignment.CENTER);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 14*scale));
            text.setTextFill(Color.ORANGE);
        }
        containerPane.getChildren().add(levelChangePane);
        if (level != 7) {

            if (gameStatus.equals("won"))   {
                levelCompletedMediaPlayer.play();
                sceneName = "won";
                resultText1.setText("YOU WIN!");
                resultText2.setText("Press ENTER to play next level");

                resultTextTimeline.getKeyFrames().clear();
                resultTextTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(800), event -> {
                    if (!resultText2.getText().equals(" "))   {
                        resultText2.setText(" ");
                    } else {
                        resultText2.setText("Press ENTER to play next level");
                    }
                }));
                resultTextTimeline.setCycleCount(Animation.INDEFINITE);
                resultTextTimeline.play();
            } else {
                gameOverMediaPlayer.stop();
                gameOverMediaPlayer.play();
                sceneName = "failed";
                resultText1.setText("GAME OVER!");
                playAgainScreen();
            }

        } else {
            gameCompleteMediaPlayer.stop();
            gameCompleteMediaPlayer.play();
            sceneName = "complete";
            resultText1.setText("You have completed the game!");
            playAgainScreen();
        }
    }

    private void playAgainScreen() {
        resultText2.setText("Press ENTER to play again\nPress ESC to exit");

        resultTextTimeline.getKeyFrames().clear();
        resultTextTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(800), event -> {
            if (!resultText2.getText().equals("\n\n"))   {
                resultText2.setText("\n\n");
            } else {
                resultText2.setText("Press ENTER to play again\nPress ESC to exit");
            }
        }));
        resultTextTimeline.setCycleCount(Animation.INDEFINITE);
        resultTextTimeline.play();
    }

    public void setBirdsPane()  {
        backgroundContainer.setTranslateX(-backgroundWidth);
        stopAllMusic();
        containerPane.getChildren().remove(levelChangePane);
        resultTextTimeline.stop();
        sceneName = "gameScene";
        birdsArray.clear();
        birdsPane.getChildren().clear();
        switch (level) {
            case 1: {
                Bird bird1 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_black", "horizontal1");
                birdsArray.add(bird1);
                break;
            }
            case 2: {
                Bird bird1 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_blue", "cross4");
                birdsArray.add(bird1);
                break;
            }
            case 3: {
                Bird bird1 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_black", "horizontal1");
                Bird bird2 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_red", "horizontal2");
                birdsArray.add(bird1);
                birdsArray.add(bird2);
                break;
            }
            case 4: {
                Bird bird1 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_red", "cross3");
                Bird bird2 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_blue", "cross4");
                birdsArray.add(bird1);
                birdsArray.add(bird2);
                break;
            }
            case 5: {
                Bird bird1 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_black", "horizontal1");
                Bird bird2 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_blue", "horizontal2");
                Bird bird3 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_red", "cross1");
                birdsArray.add(bird1);
                birdsArray.add(bird2);
                birdsArray.add(bird3);
                break;
            }
            case 6: {
                Bird bird1 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_black", "cross1");
                Bird bird2 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_blue", "cross2");
                Bird bird3 = new Bird(scale, backgroundWidth, backgroundHeight,
                        "duck_red", "cross3");
                birdsArray.add(bird1);
                birdsArray.add(bird2);
                birdsArray.add(bird3);
                break;
            }
        }
        levelText.setText("Level " + level + "/6");
        ammo = 0;
        for (Bird bird: birdsArray)    {
            birdsPane.getChildren().add(bird.getBird());
            ammo += 3;
        }
        ammoText.setText("Ammo Left: " + ammo);
        birdText.setText("Birds Left: " + birdsArray.size());
    }

    public void setTranslationTimeline(double backgroundWidth)    {
        translationTimelineLeft.getKeyFrames().clear();
        translationTimelineLeft.getKeyFrames().add(new KeyFrame(Duration.millis(80), event -> {
            if (backgroundContainer.getTranslateX() >= -backgroundWidth * 2 + scale * 5) {
                backgroundContainer.setTranslateX(backgroundContainer.getTranslateX() - scale * 5);
            }
        }));
        translationTimelineRight.getKeyFrames().clear();
        translationTimelineRight.getKeyFrames().add(new KeyFrame(Duration.millis(80), event -> {
            if (backgroundContainer.getTranslateX() <= -scale * 5) {
                backgroundContainer.setTranslateX(backgroundContainer.getTranslateX() + scale * 5);
            }
        }));
    }

    public void startTranslationToLeft() {
        translationTimelineLeft.setCycleCount(Animation.INDEFINITE);
        translationTimelineLeft.play();
    }

    public void stopTranslationToLeft() {
        translationTimelineLeft.stop();
    }

    public void startTranslationToRight() {
        translationTimelineRight.setCycleCount(Animation.INDEFINITE);
        translationTimelineRight.play();
    }

    public void stopTranslationToRight() {
        translationTimelineRight.stop();
    }

    public void centerText() {
        levelText.setLayoutX((backgroundWidth - levelText.getWidth()) / 2);
    }

    public void alignAmmoText() {
        ammoText.setLayoutX(backgroundWidth - ammoText.getWidth() - 5);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void stopAllMusic()  {
        gameOverMediaPlayer.stop();
        gameCompleteMediaPlayer.stop();
        gunshotMediaPlayer.stop();
        levelCompletedMediaPlayer.stop();
        fallBirdMediaPlayer.stop();
    }

    public void moveMouse(double x, double y)   {
        cursorImageView.setX(x - cursorImageView.getFitWidth() / 2);
        cursorImageView.setY(y - cursorImageView.getFitHeight() / 2);
    }

}