package ch.epfl.javass;

import java.io.IOException;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * An application to launch a Jass Game remotely.
 * @author Célia Houssiaux
 * @author Charles Beauville
 *
 */
public final class RemoteMain extends Application{

    /**
     * Starts a Remote game once the client connects.
     * @param args no arguments are needed to start a remote game
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread gameThread = new Thread(() -> {
            try {
                new RemotePlayerServer(new GraphicalPlayerAdapter()).run();
            } catch (IOException e) {
                throw new Error(e);
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
        
        System.out.println("La partie commencera à la connexion du client…");
    }
    
}
