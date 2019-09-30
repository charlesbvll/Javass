package javass.jass;

import java.util.Map;

import javass.jass.Card.Color;
import static javass.Preconditions.checkArgument;

/**
 * A player that slows down the game
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class PacedPlayer implements Player {
    private final Player underlyingPlayer;
    private final long minTime;

    /**
     * A player that slows down the game
     * @param underlyingPlayer the {@link Player} associated with this {@link PacedPlayer}.
     * @param minTime a double the minimum time in seconds to wait for the {@link Player}.
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        checkArgument(minTime >= 0);
        
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = (long) minTime * 1000;

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long start = System.currentTimeMillis();
        Card c = underlyingPlayer.cardToPlay(state, hand);
        long end = System.currentTimeMillis();
        
        long timeElapsed = (end - start);
        
        if (timeElapsed >= minTime)
            return c;
        else
            try {
                Thread.sleep((minTime - timeElapsed));
            } catch (InterruptedException e) {
                /* ignore */ }
        return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);

    }

    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);

    }

    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
}
