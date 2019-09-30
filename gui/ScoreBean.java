package javass.gui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javass.jass.TeamId;
import javafx.beans.property.*;

/**
 * A bean containing the score properties.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class ScoreBean {
    
    private final SimpleIntegerProperty turnPointsTeam1Property = new SimpleIntegerProperty();
    private final SimpleIntegerProperty turnPointsTeam2Property = new SimpleIntegerProperty();
    private final SimpleIntegerProperty gamePointsTeam1Property = new SimpleIntegerProperty();
    private final SimpleIntegerProperty gamePointsTeam2Property = new SimpleIntegerProperty();
    private final SimpleIntegerProperty totalPointsTeam1Property = new SimpleIntegerProperty();
    private final SimpleIntegerProperty totalPointsTeam2Property = new SimpleIntegerProperty();
    private final SimpleObjectProperty<TeamId> winningTeamProperty = new SimpleObjectProperty<TeamId>();
    private final Map<TeamId, SimpleIntegerProperty> turnPointsPropertyMap;
    {
        Map<TeamId, SimpleIntegerProperty> temp = new HashMap<TeamId, SimpleIntegerProperty>();
        temp.put(TeamId.TEAM_1, turnPointsTeam1Property);
        temp.put(TeamId.TEAM_2, turnPointsTeam2Property);
        turnPointsPropertyMap = Collections.unmodifiableMap(temp);
    }   
    private final Map<TeamId, SimpleIntegerProperty> gamePointsPropertyMap;
    {
        Map<TeamId, SimpleIntegerProperty> temp = new HashMap<TeamId, SimpleIntegerProperty>();
        temp.put(TeamId.TEAM_1, gamePointsTeam1Property);
        temp.put(TeamId.TEAM_2, gamePointsTeam2Property);
        gamePointsPropertyMap = Collections.unmodifiableMap(temp);
    }
    private final Map<TeamId, SimpleIntegerProperty> totalPointsPropertyMap;
    {
        Map<TeamId, SimpleIntegerProperty> temp = new HashMap<TeamId, SimpleIntegerProperty>();
        temp.put(TeamId.TEAM_1, totalPointsTeam1Property);
        temp.put(TeamId.TEAM_2, totalPointsTeam2Property);
        totalPointsPropertyMap = Collections.unmodifiableMap(temp);
    }  
    
    /**
     * Gives the property of the turn points of a given team.
     * @param team the TeamId of the team to get the turn points from.
     * @return a ReadOnlyIntegerProperty the turn points property of the given team.
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return turnPointsPropertyMap.get(team);
    }

    /**
     * Sets the turn points property of a given team to a given number.
     * @param team a teamId the team to change the turn points. 
     * @param newTurnPoints an int the bew number of points of the team.
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        turnPointsPropertyMap.get(team).set(newTurnPoints);
    }
    
    /**
     * Gives the property of the game points of a given team.
     * @param team the TeamId of the team to get the game points from.
     * @return a ReadOnlyIntegerProperty the game points property of the given team.
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return gamePointsPropertyMap.get(team);
    }
    
    /**
     * Sets the game points property of a given team to a given number.
     * @param team a teamId the team to change the game points. 
     * @param newGamePoints an int the new number of points of the team.
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        gamePointsPropertyMap.get(team).set(newGamePoints);
    }
    
    /**
     * Gives the property of the total points of a given team.
     * @param team the TeamId of the team to get the total points from.
     * @return a ReadOnlyIntegerProperty the total points property of the given team.
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return totalPointsPropertyMap.get(team);
    }
    
    /**
     * Sets the total points property of a given team to a given number.
     * @param team a teamId the team to change the total points. 
     * @param newTotalPoints an int the new number of points of the team.
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        totalPointsPropertyMap.get(team).set(newTotalPoints);
    }
    
    /**
     * Gives the property of the winning team.
     * @return a ReadOnlyObjectProperty the winning team property of the game.
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() { 
        return winningTeamProperty;
    }
    
    /**
     * Sets the winning team property to the given team.
     * @param winningTeam the team that won the game.
     */
    public void setWinningTeam(TeamId winningTeam) { 
        winningTeamProperty.set(winningTeam);
    }

}
