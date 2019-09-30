package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * A bean containing the trick properties.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class TrickBean {

    private final SimpleObjectProperty<Color> trumpProperty = new SimpleObjectProperty<Color>();
    private final SimpleObjectProperty<PlayerId> winningPlayerProperty = new SimpleObjectProperty<PlayerId>();
    private final ObservableMap<PlayerId, Card> trickMap = FXCollections.observableHashMap();
    
    /**
     * Gives the trump property of the trick.
     * @return a ReadOnlyObjectProperty of a the trump color.
     */
    public ReadOnlyObjectProperty<Color> trumpProperty(){ 
        return trumpProperty;
    }
    
    /**
     * Sets the trump property to the given color.
     * @param trump a color the trump color to be set.
     */
    public void setTrump(Color trump){ 
        trumpProperty.set(trump);
    }
    
    /**
     * Gives an observable map of cards associated by player IDs.
     * @return an observable map of cards associated by player IDs.
     */
    public ObservableMap<PlayerId, Card> trick(){
        return FXCollections.unmodifiableObservableMap(trickMap);
    }
    
    /**
     * Set the trick property to the given trick.
     * @param newTrick a trick to change the trick property.
     */
    public void setTrick(Trick newTrick) {
        trickMap.clear();
        
        for (int i = 0; i < newTrick.size(); i++)
            trickMap.put(newTrick.player(i), newTrick.card(i));
        
        winningPlayerProperty.set(newTrick.winningPlayer());
    }
    
    /**
     * Gives the property of the winning player of the trick.
     * @return ReadOnlyObjectProperty the winning player property.
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty(){
        return winningPlayerProperty;
    }
}
