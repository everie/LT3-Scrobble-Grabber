import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

/**
 * Created by Hans on 28-05-2016.
 */
public class Layout extends Application
{
    private Settings s = new Settings();
    private JSON json = new JSON();
    private BorderPane layout = new BorderPane();
    private Crypt crypt = new Crypt();
    private ScrobbleGrabber sg = new ScrobbleGrabber();

    public static Timer timer;
    public static Timer shower;

    private Container container = null;

    private TextField userField = new TextField();
    private Label userLabel = new Label("Last.fm username");
    private Text errorText = new Text();

    private File scrobbleFile = new File(s.getFileName());

    public static Stage stage;

    public static void main(String[] args)
    {
        launch(args);
    }

    public void start(Stage mainStage) {

        stage = mainStage;

        if (scrobbleFile.exists()) {
            container = crypt.decrypt();
        }

        if (container == null)
        {
            layout.setCenter(frontPane());
        } else {
            //System.out.println(container.getUser());
            //System.out.println(container.getTotalTracks());
            //System.out.println(container.getTotalPages());
            scrobblePane();
        }

        Scene scene = new Scene(layout, s.getWidth(), s.getHeight());
        stage.setScene(scene);
        stage.setTitle("LessThan3.xyz ScrobbleGrabber");
        stage.setMinWidth(s.getWidth());
        stage.setMinHeight(s.getHeight());
        stage.show();

        stage.setOnCloseRequest(e -> {
            if (container != null) {
                crypt.encrypt(container);

                timer.cancel();
                shower.cancel();
            }
        });

    }

    private void scrobblePane() {
        layout.setCenter(sg.getPane(container));
    }

    private GridPane frontPane() {
        GridPane innerLayout = new GridPane();
        innerLayout.setHgap(5);
        innerLayout.setVgap(5);
        innerLayout.setAlignment(Pos.CENTER);

        Button userButton = new Button("Go");

        innerLayout.add(userLabel, 0, 0);
        innerLayout.add(userField, 0, 1);
        innerLayout.add(userButton, 1, 1);
        innerLayout.add(errorText, 0, 2, 2, 1);

        userButton.setOnMouseClicked(e -> {
            verifyUser(userField);
        });

        return innerLayout;
    }

    private void verifyUser(TextField uField) {
        if (uField.getText().length() > 0)
        {
            try
            {
                String input = json.apiToString("http://ws.audioscrobbler.com/2.0/?method=user.getinfo&user=" + uField.getText() + "&api_key=" + s.getApi() + "&format=json");
                JSONObject obj = new JSONObject(input);

                JSONObject user = obj.getJSONObject("user");

                String name = user.getString("realname");
                String country = user.getString("country");
                String image = user.getJSONArray("image").getJSONObject(2).getString("#text");
                String regDate = json.getDateFromTS(user.getJSONObject("registered").getLong("unixtime"));
                int playcount = user.getInt("playcount");
                int pages = (int)Math.ceil((double)playcount / (double)s.getLimit());

                GridPane innerLayout = new GridPane();
                Button verifyButton = new Button("This is me");
                Button cancelButton = new Button("Go back");

                HBox buttonBox = new HBox(5);
                buttonBox.getChildren().addAll(verifyButton, cancelButton);
                buttonBox.setAlignment(Pos.BOTTOM_LEFT);

                Image i = new Image(image);
                ImageView iv = new ImageView(i);

                innerLayout.setVgap(5);
                innerLayout.setHgap(5);
                innerLayout.setAlignment(Pos.CENTER);

                innerLayout.add(iv, 0, 0, 1, 4);
                innerLayout.add(new Text("Name: " + name), 1, 0);
                innerLayout.add(new Text("Country: " + country), 1, 1);
                innerLayout.add(new Text("Scrobbles: " + playcount + ", Registered: " + regDate), 1, 2);
                innerLayout.add(buttonBox, 1, 3);

                layout.setCenter(innerLayout);

                cancelButton.setOnMouseClicked(e -> {
                    layout.setCenter(frontPane());
                });

                verifyButton.setOnMouseClicked(e -> {
                    container = new Container(uField.getText(), playcount, pages);
                    scrobblePane();
                });

            } catch (IOException | JSONException e)
            {
                errorText.setText("Something went wrong. " + e.getMessage());
            }
        } else {
            errorText.setText("Incorrect name");
        }
    }


}
