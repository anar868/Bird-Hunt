import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;

import static java.lang.Math.round;

public class Bird {
    private final double scale;
    private final double backgroundWidth;
    private final double backgroundHeight;
    private final double birdSpeed;
    private final int timelineDuration;
    private final int dyingDuration;
    private final String birdName;
    private final String birdType;
    private final ImageView bird;
    private int frameIndexBird;
    private double birdXDirection;
    private double birdYDirection;
    Timeline translateTimeline;
    Timeline translateTimelineY;
    Timeline fallTimeline;
    Timeline flyTimeline;

    public Bird(double scale, double backgroundWidth, double backgroundHeight, String birdName, String birdType)   {

        this.scale = scale;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.birdSpeed = 38;

        this.timelineDuration = 300;
        this.dyingDuration = 400;
        this.birdName = birdName;
        this.birdType = birdType;
        this.bird = new ImageView();
        this.translateTimelineY = new Timeline();
        birdWingAnimation();
        setBirdDirection();
        birdFlyAnimation();
    }

    public void birdWingAnimation()  {
        int birdImageNumber = 1;

        if (birdType.substring(0, birdType.length() - 1).equals("horizontal")) {
            birdImageNumber = 4;
        }

        Image[] birdFrames = new Image[3];
        double[] birdWidths = new double[3];
        double[] birdHeights = new double[3];
        for (int i = 0; i < 3; i++) {
            birdFrames[i] = new Image("assets/"+ birdName +"/" + (i + birdImageNumber) + ".png");
            birdWidths[i] = birdFrames[i].getWidth() * scale;
            birdHeights[i] = birdFrames[i].getHeight() * scale;
        }

        flyTimeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), event -> {
            frameIndexBird = (frameIndexBird + 1) % 3;
            bird.setFitWidth(birdWidths[frameIndexBird]);
            bird.setFitHeight(birdHeights[frameIndexBird]);
            bird.setImage(birdFrames[frameIndexBird]);
        }));
        flyTimeline.setCycleCount(Animation.INDEFINITE);
        flyTimeline.play();
    }

    public void setBirdDirection() {
        if (birdType.endsWith("1")) {
            this.birdXDirection = 1;
            bird.setTranslateX((backgroundWidth * 3 - bird.getFitWidth()) *  ((double) 1 / 3 - 1 / birdSpeed));

            if (birdType.startsWith("cross"))   {
                this.birdYDirection = -1;
            } else {
                bird.setTranslateY((backgroundHeight - bird.getFitWidth())/9);
            }
        } else if (birdType.endsWith("2"))  {
            this.birdXDirection = -1;
            bird.setTranslateX((backgroundWidth * 3 - bird.getFitWidth()) / 1.5);
            if (birdType.startsWith("cross"))   {
                this.birdYDirection = -1;
            } else {
                bird.setTranslateY((backgroundHeight - bird.getFitWidth())/9 * 2);
            }
        } else if (birdType.endsWith("3")) {
            this.birdXDirection = 1;
            bird.setTranslateX((backgroundWidth * 3 - bird.getFitWidth()) *  ((double) 1 / 3 - 1 / birdSpeed));
            bird.setTranslateY(backgroundHeight - bird.getFitWidth());
            this.birdYDirection = 1;
        } else if (birdType.endsWith("4"))  {
            this.birdXDirection = -1;
            bird.setTranslateX((backgroundWidth * 3 - bird.getFitWidth()) / 1.5);
            bird.setTranslateY(backgroundHeight - bird.getFitWidth());
            this.birdYDirection = 1;
        }
        bird.setScaleX(birdXDirection);
    }

    public void birdFlyAnimation()  {
        translateTimeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), event -> {
            if (round(bird.getTranslateX()) + bird.getFitWidth() >= backgroundWidth * 3)   {
                birdXDirection = -1;
                bird.setScaleX(-1);
            }
            if (round(bird.getTranslateX())  <= 0)  {
                birdXDirection = 1;
                bird.setScaleX(1);
            }
            bird.setTranslateX(bird.getTranslateX() + birdXDirection * (backgroundWidth * 3 -
                    bird.getFitWidth()) / birdSpeed);
        }));

        if (birdType.substring(0, birdType.length() - 1).equals("cross")) {
            translateTimelineY.getKeyFrames().add(new KeyFrame(Duration.millis(timelineDuration), event -> {
                if (round(bird.getTranslateY()) + bird.getFitHeight() >= backgroundHeight - 2 * scale)   {
                    birdYDirection = -1;
                    bird.setScaleY(1);
                }
                if (round(bird.getTranslateY()) <= 0)  {
                    birdYDirection = 1;
                    bird.setScaleY(-1);
                }
                bird.setTranslateY(bird.getTranslateY() + birdYDirection * (backgroundHeight - bird.getFitWidth()) / 9);
            }));
            translateTimelineY.setCycleCount(Animation.INDEFINITE);
            translateTimelineY.play();
        }
        translateTimeline.setCycleCount(Animation.INDEFINITE);
        translateTimeline.play();
    }

    public void hitBird()   {
        translateTimeline.stop();
        flyTimeline.stop();
        translateTimelineY.stop();
        Timeline dieImageTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    Image birdImage = new Image("assets/"+ birdName +"/" + 7 + ".png");
                    double birdWidth = birdImage.getWidth()*scale;
                    double birdHeight = birdImage.getHeight()*scale;
                    bird.setFitWidth(birdWidth);
                    bird.setFitHeight(birdHeight);
                    bird.setScaleY(1);
                    bird.setImage(birdImage);
                }),
                new KeyFrame(Duration.millis(dyingDuration), event -> {
                    Image birdImage = new Image("assets/"+ birdName +"/" + 8 + ".png");
                    double birdWidth = birdImage.getWidth()*scale;
                    double birdHeight = birdImage.getHeight()*scale;
                    bird.setFitWidth(birdWidth);
                    bird.setFitHeight(birdHeight);
                    bird.setImage(birdImage);
                    startFallAnimation();
                })
        );
        dieImageTimeline.play();
    }

    public void startFallAnimation()  {
        fallTimeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), event -> {
            if (bird.getTranslateY() > backgroundHeight)    {
                fallTimeline.stop();
            }
            bird.setTranslateY(bird.getTranslateY() +  (backgroundWidth * 3 -
                    bird.getFitWidth()) / birdSpeed);
        }));
        fallTimeline.setCycleCount(Animation.INDEFINITE);
        fallTimeline.play();
    }

    public ImageView getBird() {
        return bird;
    }

}
