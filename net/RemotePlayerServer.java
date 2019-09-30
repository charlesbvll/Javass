package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import static ch.epfl.javass.net.StringSerializer.*;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Connects to a remote client and executes functions according to the data
 * sent.
 * 
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class RemotePlayerServer {

    private static final int PORT_NUMBER = 5108;
    private static final int COMMAND_INDEX = 0;
    private static final int ARG_0_INDEX = 0;
    private static final int ARG_1_INDEX = 1;
    private static final int ARG_2_INDEX = 2;
    
    private Player localPlayer;

    /**
     * Connects to a remote client and executes functions of the given player
     * according to the data sent.
     * 
     * @param p
     *            the underlying player.
     */
    public RemotePlayerServer(Player p) {
        this.localPlayer = p;
    }

    /**
     * Runs the server on the port number.
     * @throws IOException
     */
    public void run() throws IOException {
        @SuppressWarnings("resource")
        //Listens for a connection on the port number.
        ServerSocket s0 = new ServerSocket(PORT_NUMBER);
        Socket s = s0.accept();

        //Sets up the buffers to read and to write on the socket.
        BufferedReader r = new BufferedReader(new InputStreamReader(
                s.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                s.getOutputStream(), StandardCharsets.UTF_8));
        
        
        String command;

        //Reads the commands wrote on the socket line per line
        while ((command = r.readLine()) != null) {
            String[] msg = split(' ', command);

            switch (JassCommand.valueOf(msg[COMMAND_INDEX])) {

            case PLRS:
                
                //Creates the players map with the arguments and calls the setPlayers function of the underlyingPlayer.
                Map<PlayerId, String> players = new HashMap<>();
                for (int i = 0; i < PlayerId.COUNT; i++) {
                    players.put(PlayerId.ALL.get(i),
                            deserializeString(split(',', msg[ARG_2_INDEX])[i]));
                }
                localPlayer.setPlayers(PlayerId.ALL.get(deserializeInt(msg[ARG_1_INDEX])),
                        players);
                break;

            case CARD:
                
                //Calls the cardToPlay function of the underlying function with the arguments of the message.
                String[] st = split(',', msg[ARG_1_INDEX]);
                TurnState state = TurnState.ofPackedComponents(
                        deserializeLong(st[ARG_0_INDEX]), deserializeLong(st[ARG_1_INDEX]),
                        deserializeInt(st[ARG_2_INDEX]));
                Card c = localPlayer.cardToPlay(state,
                        CardSet.ofPacked(deserializeLong(msg[ARG_2_INDEX])));
                w.write(serializeInt(c.packed()));
                w.newLine();
                w.flush();
                break;

            case HAND:         
                localPlayer
                        .updateHand(CardSet.ofPacked(deserializeLong(msg[ARG_1_INDEX])));
                break;

            case TRMP:
                localPlayer.setTrump(Color.ALL.get(deserializeInt(msg[ARG_1_INDEX])));
                break;

            case SCOR:
                localPlayer
                        .updateScore(Score.ofPacked(deserializeLong(msg[ARG_1_INDEX])));
                break;

            case TRCK:
                localPlayer.updateTrick(Trick.ofPacked(deserializeInt(msg[ARG_1_INDEX])));
                break;

            case WINR:
                localPlayer
                        .setWinningTeam(TeamId.ALL.get(deserializeInt(msg[ARG_1_INDEX])));
                break;

            default:
            }
        }
    }
}
