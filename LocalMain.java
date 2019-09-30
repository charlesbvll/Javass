package ch.epfl.javass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * An application to launch a Jass Game locally.
 * @author Célia Houssiaux
 * @author Charles Beauville
 *
 */
public final class LocalMain extends Application {

    private static final double MIN_TIME = 1.0;
    private static final int WAIT_TIME = 1000;
    private static final int TYPE_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int ADDRESS_INDEX = 2;
    private static final int ITER_NBR_INDEX = 2;

    /**
     * 
     * @param args
     *            parametres donnés a l'application sous cette forme :
     * 
     *            <pre>
     * {j1}…{j4} [{graine}] où :
     *       {jn} spécifie le joueur n, ainsi:
     *                   h:{nom}  un joueur humain nommé {nom}"
     *       
     *                   s:{nom}:{n}  un joueur simulé nommé {nom} qui itère l'algorithme MCTS {n} fois"
     *       
     *                   r:{nom}:{adresse}  un joueur distant nommé {nom} et sur le serveur d'adresse : {adresse}
     *       
     *               Les agruments {nom}, {n} et {adresses} sont optionnelles et ont pour valeurs par défaut : 
     *       
     *                   Aline, Bastien, Colette et David attribués dans l'ordre, à défaut de {nom}
     *       
     *                   10 000 iterations de MCTS, à défaut de {n}
     *           et localhost, à défaut de {adresse}.
     *            </pre>
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> args = getParameters().getRaw();
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        // Setting up the default values.
        String[] defaultNames = { "Aline", "Bastien", "Colette", "David" };
        int iterNbr = 10000;
        Random rnd = new Random();
        String adress = "localhost";

        // Explaining the syntax if too much arguments are detected.
        if (!(args.size() == 4 || args.size() == 5)) {
            System.err.println(
                    "Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>] où :");
            System.err.println("<jn> spécifie le joueur n, ainsi:");
            System.err.println("    h:<nom>  un joueur humain nommé <nom>");
            System.err.println(
                    "    s:<nom>:<n>  un joueur simulé nommé <nom> qui itère l'algorithme MCTS <n> fois");
            System.err.println(
                    "    r:<nom>:<adresse>  un joueur distant nommé <nom> et sur le serveur d'adresse : <adresse>");
            System.err.println("");
            System.err.println(
                    "Les agruments <nom>, <n> et <adresses> sont optionnelles et ont pour valeurs par défaut : ");
            System.err.println(
                    "    Aline, Bastien, Colette et David attribués dans l'ordre, à défaut de <nom>,");
            System.err
                    .println("    10 000 iterations de MCTS, à défaut de <n>,");
            System.err.println("    et localhost, à défaut de <adresse>,");

            System.exit(1);
        }

        // Checking if the given seed is correct.
        if (args.size() == 5) {
            try {
                rnd.setSeed(Long.parseLong(args.get(4)));
            } catch (Exception NumberFormatException) {
                System.err.println(
                        "Erreur : seed de génération aléatoire incorrect");
                System.exit(1);
            }
        }

        int gameSeed = rnd.nextInt();

        for (int i = 0; i < PlayerId.COUNT; i++) {
            String[] playerInfo = args.get(i).split(":", -1);

            String playerType = playerInfo[TYPE_INDEX];

            if (!(playerType.equals("h") || playerType.equals("s")
                    || playerType.equals("r"))) {
                System.err
                        .println("Erreur : spécification de joueur invalide : "
                                + args.get(i));
                System.exit(1);
            }

            if (playerInfo.length > 3) {
                System.err
                        .println("Erreur : trop de composantes sur le joueur : "
                                + args.get(i));
                System.exit(1);
            }

            // Handles the case where the type argument is "h", creates a
            // GraphicalPlayerAdapter.
            if (playerType.equals("h")) {

                // Check if their aren't too many components.
                if (playerInfo.length > 2) {
                    System.err.println(
                            "Erreur : trop de composantes sur le joueur : "
                                    + args.get(i));
                    System.exit(1);
                }

                // Creates the GraphicalPlayerAdapter.
                players.put(PlayerId.ALL.get(i), new GraphicalPlayerAdapter());

                // Assigns the given name to the player if it is correct.
                if (playerInfo.length > 1)
                    if (!playerInfo[NAME_INDEX].isEmpty())
                        playerNames.put(PlayerId.ALL.get(i), playerInfo[NAME_INDEX]);
                    else
                        playerNames.put(PlayerId.ALL.get(i), defaultNames[i]);
                else
                    playerNames.put(PlayerId.ALL.get(i), defaultNames[i]);
            }

            // Handles the case where the type argument is "r", creates a
            // RemotePlayerClient.
            if (playerType.equals("r")) {

                // Sets the adress to the given argument if it isn't null.
                if (playerInfo.length > 1)
                    if (!playerInfo[ADDRESS_INDEX].isEmpty())
                        adress = playerInfo[ADDRESS_INDEX];

                try {
                    // Creates the RemotePlayerClient, quits if it can't
                    // connect.
                    players.put(PlayerId.ALL.get(i),
                            new RemotePlayerClient(adress));
                } catch (Exception IOError) {
                    System.err.println(
                            "Erreur : Connexion impossible au joueur simulé : "
                                    + args.get(i));
                    System.exit(1);
                }

                // Assigns the given name to the player if it is correct.
                if (playerInfo.length > 1)
                    if (!playerInfo[NAME_INDEX].isEmpty())
                        playerNames.put(PlayerId.ALL.get(i), playerInfo[NAME_INDEX]);
                    else
                        playerNames.put(PlayerId.ALL.get(i), defaultNames[i]);
                else
                    playerNames.put(PlayerId.ALL.get(i), defaultNames[i]);
            }

            // Handles the case where the type argument is "s", creates a
            // MCTSPlayer.
            if (playerType.equals("s")) {

                // Sets the number of iteration to the given argument if it is
                // correct.
                if (playerInfo.length > 1)
                    if (!playerInfo[ITER_NBR_INDEX].isEmpty()) {
                        try {
                            iterNbr = Integer
                                    .parseInt(playerInfo[ITER_NBR_INDEX]);
                        } catch (Exception NumberFormatException) {
                            System.err.println(
                                    "Erreur : Nombre d'iterations du MCTS non valide pour le joueur simulé : "
                                            + args.get(i));
                            System.exit(1);
                        }
                        if (iterNbr < 10) {
                            System.err.println(
                                    "Erreur : Nombre d'iterations du MCTS non valide pour le joueur simulé : "
                                            + args.get(i));
                            System.exit(1);
                        }
                    }

                // Creates the simulated player.
                players.put(PlayerId.ALL.get(i),
                        new PacedPlayer(new MctsPlayer(PlayerId.ALL.get(i),
                                rnd.nextLong(), iterNbr), MIN_TIME));

                // Assigns the given name to the player if it is correct.
                if (playerInfo.length > 1)
                    if (!playerInfo[NAME_INDEX].isEmpty())
                        playerNames.put(PlayerId.ALL.get(i), playerInfo[NAME_INDEX]);
                    else
                        playerNames.put(PlayerId.ALL.get(i), defaultNames[i]);
                else
                    playerNames.put(PlayerId.ALL.get(i), defaultNames[i]);

            }
        }

        // Launches the game with the givem arguments
        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(gameSeed, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (Exception e) {
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

}
