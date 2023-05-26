import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class DuckHunt extends Application {
    double scale = 4;
    double volume = 0.025;
    boolean allowButtons = true;


    public static void main(String args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("HUBBM Duck Hunt");
        primaryStage.getIcons().add(new Image("assets/favicon/1.png"));
// backgroundchangescreen cursor scale dont forget
//        check sources on music and images
//
        TitleScreen titleScreen = new TitleScreen(scale, volume);
        BackgroundChangeScreen backgroundChangeScreen = new BackgroundChangeScreen(scale);
        GameScreen gameScreen = new GameScreen(scale, volume);

        MediaPlayer introSound = new MediaPlayer(new Media(new File("src/assets/effects/Intro.mp3")
                .toURI().toString()));
        introSound.setVolume(volume);
//

        primaryStage.setScene(titleScreen.getScene());

        titleScreen.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                primaryStage.setScene(backgroundChangeScreen.getScene());
                backgroundChangeScreen.centerText();
                backgroundChangeScreen.centerCursor();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                primaryStage.close();
            }
        });

        backgroundChangeScreen.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && allowButtons) {
                allowButtons = false;
                titleScreen.stopMusic();
                introSound.setOnEndOfMedia(() -> {
                    Cursor cursor = Cursor.cursor("NONE");
                    gameScreen.getGameScene().setCursor(cursor);
                    gameScreen.setSizes(backgroundChangeScreen.getBackgroundNumber(),
                            backgroundChangeScreen.getCrossHairNumber());
                    primaryStage.setScene(gameScreen.getGameScene());
                    gameScreen.centerText();
                    gameScreen.alignAmmoText();
                    allowButtons = true;
                    introSound.stop();
                });
                introSound.play();
            } else if (e.getCode() == KeyCode.ESCAPE && allowButtons) {
                primaryStage.setScene(titleScreen.getScene());
            } else if (e.getCode() == KeyCode.RIGHT && allowButtons)    {
                backgroundChangeScreen.changeImage("right");
            } else if (e.getCode() == KeyCode.LEFT && allowButtons)    {
                backgroundChangeScreen.changeImage("left");
            } else if (e.getCode() == KeyCode.UP && allowButtons)    {
                backgroundChangeScreen.changeCursor("up");
            } else if (e.getCode() == KeyCode.DOWN && allowButtons)    {
                backgroundChangeScreen.changeCursor("down");
            }
        });

//        check left click or both
        gameScreen.getGameScene().setOnMouseClicked(e -> {
            if (gameScreen.getSceneName().equals("gameScene"))  {
                gameScreen.hit(e.getX(), e.getY());
            }
        });


        gameScreen.getGameScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                switch (gameScreen.getSceneName()) {
                    case "won":
                        gameScreen.setBirdsPane();
                        break;
                    case "failed":
                    case "complete":
                        gameScreen.setLevel(1);
                        gameScreen.setBirdsPane();
                        break;
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                if (gameScreen.getSceneName().equals("failed") || gameScreen.getSceneName().equals("complete"))  {
                    primaryStage.setScene(titleScreen.getScene());
                    gameScreen.stopAllMusic();
                    titleScreen.playMusic();
                    primaryStage.getScene().setCursor(Cursor.DEFAULT);
                    System.out.println(primaryStage.getScene().getCursor());
                }
            }
        });

        gameScreen.getLeftRectangle().setOnMouseEntered(event -> {
            if (gameScreen.getSceneName().equals("gameScene"))  {
                gameScreen.startTranslationToRight();
            }
        });
        gameScreen.getLeftRectangle().setOnMouseExited(event -> {
            gameScreen.stopTranslationToRight();
        });

        gameScreen.getRightRectangle().setOnMouseEntered(event -> {
            if (gameScreen.getSceneName().equals("gameScene"))  {
                gameScreen.startTranslationToLeft();
            }
        });
        gameScreen.getRightRectangle().setOnMouseExited(event -> {
            gameScreen.stopTranslationToLeft();
        });

        gameScreen.getGameScene().setOnMouseMoved(e -> {
            gameScreen.moveMouse(e.getX(), e.getY());
        });



        primaryStage.show();
        primaryStage.setResizable(false);

        titleScreen.centerText();
    }

}

