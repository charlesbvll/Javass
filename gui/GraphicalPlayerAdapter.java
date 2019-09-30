package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;

/**
 * A {@link Player} that has a graphical interface.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class GraphicalPlayerAdapter implements Player {

    private final ScoreBean scoreBean;
    private final HandBean handBean;
    private final TrickBean trickBean;
    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<Card> queue;

    /**
     * Constructor of a {@link Player} that has a graphical interface.
     */
    public GraphicalPlayerAdapter() {
        scoreBean = new ScoreBean();
        handBean = new HandBean();
        trickBean = new TrickBean();

        queue = new ArrayBlockingQueue<Card>(1);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        Platform.runLater(() -> {
            handBean.setPlayableCards(state.trick().playableCards(hand));
        });

        Card c;
        try {
            c = queue.take();
            Platform.runLater(() -> {
                handBean.setPlayableCards(CardSet.EMPTY);
            });
            return c;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean,
                trickBean, handBean, queue);
        Platform.runLater(() -> {
            graphicalPlayer.createStage().show();
        });
    }

    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> {
            trickBean.setTrump(trump);
        });
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {
            scoreBean.setWinningTeam(winningTeam);
        });
    }

    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> {
            handBean.setHand(newHand);
        });
    }

    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            for (TeamId id : TeamId.ALL) {
                scoreBean.setTurnPoints(id, score.turnPoints(id));
                scoreBean.setGamePoints(id, score.gamePoints(id));
                scoreBean.setTotalPoints(id, score.totalPoints(id));
            }
        });
    }

    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {
            trickBean.setTrick(newTrick);
        });
    }
}
