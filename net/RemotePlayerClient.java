package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;
import static ch.epfl.javass.net.StringSerializer.*;

/**
 * A player that plays on a remote server.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class RemotePlayerClient implements Player, AutoCloseable{
    
    private final static int PORT_NUMBER = 5108;
    
    private final Socket s;
    private final BufferedReader r;
    private final BufferedWriter w;
    
    /**
     * Creates a player that plays on a given host server.
     * @param host a string the adress of the host server.
     * @throws UnknownHostException if the host ip is wrong.
     * @throws IOException if the signal has been interrupted.
     */
    public RemotePlayerClient(String host) throws UnknownHostException, IOException {
        
        this.s = new Socket(host, PORT_NUMBER);
         this.r =
          new BufferedReader(
            new InputStreamReader(s.getInputStream(),
                    StandardCharsets.UTF_8));
        this.w =
          new BufferedWriter(
            new OutputStreamWriter(s.getOutputStream(),
                    StandardCharsets.UTF_8));
    }
    
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String cmd = JassCommand.PLRS.toString(); 
        String id = serializeInt(ownId.ordinal());
        String[] names = new String[PlayerId.COUNT];
        
        for (int i = 0; i < names.length; i++)
            names[i] = serializeString(playerNames.get(PlayerId.ALL.get(i)));
        
        String combinedNames = combine(',', names);
        String[] msg = new String[]{cmd, id, combinedNames};
        
        sendMsg(combineMsg(msg));
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        String cmd = JassCommand.WINR.toString();
        String winningTeamIndex = serializeInt(winningTeam.ordinal());
        String[] msg = new String[]{cmd, winningTeamIndex};
        
        sendMsg(combineMsg(msg));
    }
    
    @Override
    public void setTrump(Color trump) {
        String cmd = JassCommand.TRMP.toString();
        String trumpIndex = serializeInt(trump.ordinal());
        String[] msg = new String[]{cmd, trumpIndex};
        
        sendMsg(combineMsg(msg));
    }

    @Override
    public void updateHand(CardSet newHand) {
        String cmd = JassCommand.HAND.toString();
        String hand = serializeLong(newHand.packed());
        String[] msg = new String[]{cmd, hand};
        
        sendMsg(combineMsg(msg));
    }
    
    @Override
    public void updateScore(Score score) {
        String cmd = JassCommand.SCOR.toString();
        String s = serializeLong(score.packed());
        String[] msg = new String[]{cmd, s};
        
        sendMsg(combineMsg(msg));
    }
    
    @Override
    public void updateTrick(Trick newTrick) {
        String cmd = JassCommand.TRCK.toString();
        String trick = serializeInt(newTrick.packed());
        String[] msg = new String[]{cmd, trick};
        
        sendMsg(combineMsg(msg));
    }
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        String cmd = JassCommand.CARD.toString();
        String s = combine(',', new String[]{serializeLong(state.packedScore()), serializeLong(state.packedUnplayedCards()), serializeInt(state.packedTrick())});
        String h = serializeLong(hand.packed());
        String[] msg = new String[]{cmd, s, h};       
        String c = "0";
        
        sendMsg(combineMsg(msg));    
        
        try {
            c = r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Card.ofPacked(deserializeInt(c));       
    }
    
    @Override
    public void close() throws Exception {
        w.close();
        r.close();
        s.close();
    }
    
    private String combineMsg(String[] strings) {
        return combine(' ', strings);
    }
    
    private void sendMsg(String msg) {
        try {
            w.write(msg);
            w.newLine();
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
