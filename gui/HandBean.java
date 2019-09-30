package ch.epfl.javass.gui;

import java.util.Arrays;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * A bean containing the hand properties.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class HandBean {
    
    private final ObservableList<Card> hand = FXCollections.observableArrayList(Arrays.asList(new Card[Jass.HAND_SIZE]));
    private final ObservableSet<Card> playableCards = FXCollections.observableSet();
    
    /**
     * Gives the observable list of the hand.
     * @return an observable list of the hand.
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }
    
    /**
     * Sets the observable list of the hand to the given hand.
     * @param newHand a CardSet to define the new hand.
     */
    public void setHand(CardSet newHand) {
        for (int i = 0; i < hand.size(); i++)
            if(newHand.size() == Jass.HAND_SIZE)
                hand.set(i, newHand.get(i));
            else if(hand.get(i) != null && !newHand.contains(hand.get(i)))
                hand.set(i, null);
    }
    
    /**
     * Gives the observable set of the playableCards.
     * @return an observable set of the playableCards.
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }
    
    /**
     * Sets the observable list of the playableCards to the given playableCards.
     * @param newPlayableCards a CardSet to define the new playableCards.
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        playableCards.clear();
        
        for (int i = 0; i < newPlayableCards.size(); i++)
            playableCards.add(newPlayableCards.get(i));

    }
}
