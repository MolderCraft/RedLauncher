package main;

import RedLauncher.Browser;
import static RedLauncher.Config.Messages.launcher;
import RedLauncher.Launcher;
import static RedLauncher.Launcher.postUpdate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.w3c.dom.Node;

/**
 * Основной класс приложения
 *
 * @author RedCreepster
 */
public class RedLauncher extends Application {

    private static final String EXECUTING_ARGUMENTS = "Executing arguments: ";
    private static final String LAUNCHER_STARTING = "Launcher starting";

    private static final Logger LOG = Logger.getLogger(RedLauncher.class.getName());
    private Scene scene;

    /**
     *
     * @param args Параметры командной строки
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            LOG.log(Level.INFO, EXECUTING_ARGUMENTS + "{0}", Arrays.toString(args));
            postUpdate(args[0]);
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LOG.info(LAUNCHER_STARTING);
        Node size = launcher("size");
        int width = Integer.valueOf(size.getChildNodes().item(1).getTextContent());
        int height = Integer.valueOf(size.getChildNodes().item(3).getTextContent());

        scene = new Scene(new Browser(new Launcher(), width + 10, height + 10), width, height);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle(launcher("name").getTextContent());
        primaryStage.getIcons().add(new Image("/res/img/favicon.png"));

        primaryStage.show();
    }
}
