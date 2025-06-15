package restaurantmanagementsys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

/**
 * Clean JavaFX launcher.
 * <p>
 * Fixes:
 * <ul>
 *     <li>Ensures FXML resource is found (uses <code>Objects.requireNonNull</code>).</li>
 *     <li>No duplicate Scene creation.</li>
 *     <li>Transparent, draggable window remains.</li>
 * </ul>
 */
public class RestaurantManagementSys extends Application {
    private double x, y;

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Seed in-memory data (idempotent)
        DataInitializer.seed();

        // 2. Load initial FXML (login). If login succeeds, controller will swap to dashboard.
        //    Adjust the path if your resources are in another folder.
        Parent root = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("FXMLDocument.fxml"))); // <- no leading slash
        Scene scene = new Scene(root);

        // 3. Optional: transparent undecorated window
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);

        // 4. Enable window dragging
        root.setOnMousePressed((MouseEvent e) -> {
            x = e.getSceneX();
            y = e.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent e) -> {
            stage.setX(e.getScreenX() - x);
            stage.setY(e.getScreenY() - y);
            stage.setOpacity(0.8);
        });
        root.setOnMouseReleased(e -> stage.setOpacity(1));

        // 5. Show the stage
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
