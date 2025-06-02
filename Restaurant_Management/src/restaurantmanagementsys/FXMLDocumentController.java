package restaurantmanagementsys;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXMLDocumentController implements Initializable {

    @FXML private AnchorPane main_form;
    @FXML private TextField username;       // wire it up in FXML
    @FXML private PasswordField password;   // wire it up in FXML
    @FXML private Button loginBtn;
    @FXML private Button close;

    private double x = 0, y = 0;

    /**
     * Very simple “login” method. 
     * Checks username/password against a hardcoded pair (admin/admin).
     * You can modify it to “always succeed” by dropping the check entirely.
     */
    public void login(ActionEvent event) {
        String user = username.getText().trim();
        String pass = password.getText().trim();

        Alert alert;
        if (user.isEmpty() || pass.isEmpty()) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        // HARD‐CODED credentials: admin / admin
        if (user.equals("admin") && pass.equals("admin")) {
            data.username = user;  // store into static data
            alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Message");
            alert.setHeaderText(null);
            alert.setContentText("Successfully Login!");
            alert.showAndWait();

            // Hide login form
            loginBtn.getScene().getWindow().hide();

            try {
                Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                // enable dragging
                root.setOnMousePressed((MouseEvent e) -> {
                    x = e.getSceneX();
                    y = e.getSceneY();
                });

                root.setOnMouseDragged((MouseEvent e) -> {
                    stage.setX(e.getScreenX() - x);
                    stage.setY(e.getScreenY() - y);
                });

                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Wrong Username/Password (use admin/admin)!");
            alert.showAndWait();
        }
    }

    public void close(ActionEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // nothing special here
    }
}
