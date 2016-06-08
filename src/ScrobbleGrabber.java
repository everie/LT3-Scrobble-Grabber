import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hans on 28-05-2016.
 */
public class ScrobbleGrabber
{
    private Settings s = new Settings();
    private JSON json = new JSON();

    private Container container;

    private HBox loadingBar = new HBox();
    private HBox innerLoadingBar = new HBox();

    private HBox buttonBox = new HBox(5);

    private int page = 0;
    private int pages = 0;
    private double pct = 0.0;

    private Text atPage = new Text("Scrobbles: 0 / 0");
    private Text pctText = new Text("0%");
    private Text estimateText = new Text("Estimated Time Left: 0s");

    private boolean isPaused = true;
    private boolean isRipping = false;
    private boolean isDone = false;

    private int pagesRead = 0;
    private long started = 0;

    private TimerTask backgroundLoad;

    private Button pauseButton = new Button("Start");
    private Button doneButton = new Button("Save and Quit");

    public GridPane getPane(Container c) {
        container = c;

        GridPane innerLayout = new GridPane();
        innerLayout.getStylesheets().add("lt3.css");

        innerLayout.setAlignment(Pos.CENTER);
        innerLayout.setHgap(5);
        innerLayout.setVgap(5);

        doneButton.setVisible(false);

        buttonBox.getChildren().addAll(pauseButton, doneButton);

        innerLayout.add(atPage, 0, 0);
        innerLayout.add(loadingBar, 0, 1);
        innerLayout.add(estimateText, 0, 2);
        innerLayout.add(buttonBox, 0, 3);

        loadingBar.setPrefSize(s.getLoadWidth(), s.getLoadHeight());
        loadingBar.setMinSize(s.getLoadWidth(), s.getLoadHeight());
        loadingBar.setAlignment(Pos.CENTER_LEFT);
        loadingBar.getChildren().add(innerLoadingBar);

        innerLoadingBar.setAlignment(Pos.CENTER_RIGHT);
        innerLoadingBar.getChildren().add(pctText);

        loadingBar.getStyleClass().add("outer-load");
        innerLoadingBar.getStyleClass().add("inner-load");

        pauseButton.setOnMouseClicked(e -> {
            if (!isPaused) {
                isPaused = true;
                pauseButton.setText("Start");
            } else {
                isPaused = false;
                pauseButton.setText("Pause");
                started = Instant.now().getEpochSecond();
            }
        });

        doneButton.setOnMouseClicked(e -> {
            Node n = innerLayout.getParent();
            Stage s = (Stage) n.getScene().getWindow();
            s.getOnCloseRequest().handle(null);
            s.close();
        });

        backgroundLoad = new TimerTask() {

            public void run() {
                if (!isPaused)
                {
                    if (!isRipping)
                    {
                        isRipping = true;
                        loadNext();
                    }
                }
            }
        };

        showProgress();
        setDone();
        showProgressTimer();
        reader();

        return innerLayout;
    }

    private void showProgress() {
        page = container.getScrobbles().size();
        pages = container.getTotalTracks();

        int tempRead = pagesRead;

        if (tempRead < 1) {
            tempRead = 1;
        }

        long timeSpent = Instant.now().getEpochSecond() - started;
        double prPage = (double)timeSpent / (double)tempRead;
        long estimate = (long)((container.getTotalPages() - container.getAtPage()) * prPage);

        atPage.setText("Scrobbles: " + page + " / " + pages);
        estimateText.setText("Estimated Time Left: " + json.showDuration(estimate * 1000));

        pct = ((double)page * 100.0 / (double)pages);

        pctText.setText((int)pct + "%");

        innerLoadingBar.setPrefSize(s.getLoadWidth() * (pct / 100.0), s.getLoadHeight());

        setDone();
        //System.out.println(pagesRead);
    }

    private void reader() {
        Layout.timer = new Timer();
        Layout.timer.scheduleAtFixedRate(backgroundLoad, 0, 1000 / s.getReadFrequency());
    }

    private void showProgressTimer() {

        Layout.shower = new Timer();
        Layout.shower.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                Platform.runLater(() -> {
                    if (!isPaused)
                    {
                        showProgress();
                    }
                });
            }
        }, 0, 1000 / 30);

    }

    private void setDone() {
        if (container.getAtPage() >= container.getTotalPages()) {
            pauseButton.setDisable(true);
            doneButton.setVisible(true);
            isPaused = true;
            isDone = true;
        }
    }

    private void loadNext() {

        int atPage = container.getAtPage();
        String user = container.getUser();

        if (!isDone) {

            try {
                List<Scrobble> scrobbles = json.readPage(atPage, user, container);

                if (scrobbles.size() > 0) {
                    atPage++;
                    container.getScrobbles().addAll(scrobbles);
                    container.setAtPage(atPage);
                    pagesRead++;
                }

            } catch (IOException | JSONException e) {
                // something went wrong;
            } finally {
                isRipping = false;
            }
        }
    }
}
