package javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;
    
    /**
     * The total number of players.
     */
    public static final int COUNT = 4;
    /**
     * The list of all players
     */
    public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));

    /**
     * Gives the team of the player
     * @return a TeamId the team of the player
     */
    public TeamId team() {
        return this.equals(PLAYER_1) || this.equals(PLAYER_3) ? 
                TeamId.TEAM_1 :
                    TeamId.TEAM_2;
    }
    
}
