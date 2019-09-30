package javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public enum TeamId {
    TEAM_1,
    TEAM_2;
   
    /**
     * The number of teams.
     */
    public static final int COUNT = 2;
    /**
     * The list of all teams.
     */
    public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    
    /**
     * Gives the other team.
     * @return a TeamId the opposing team.
     */
    public TeamId other() {
        return this.equals(TEAM_1) ? TEAM_2 : TEAM_1;
    }
}
