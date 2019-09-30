package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Represents the graphic interface of a human player
 * @author Célia Houssiaux
 * @author Charles Beauville
 *
 */
public final class GraphicalPlayer {

    private static final double TRUMP_H_AND_W = 101;
    private static final double CARD_HEIGHT = 180;
    private static final double CARD_WIDTH = 120;
    private static final int COLUMN_TEAM = 0;
    private static final int COLUMN_TURNPOINTS = 1;
    private static final int COLUMN_TRICKPOINTS = 2;
    private static final int COLUMN_TOTAL_LABEL = 3;
    private static final int COLUMN_GAMEPOINTS = 4;
    private static final int CARD_ROW_SPAN = 3;
    private static final int CARD_COL_SPAN = 1;
    private static final int CARD_ROW_INDEX = 0;
    private static final int OWN_CARD_ROW_INDEX = 2;
    private static final int LEFT_CARD_COL_INDEX = 0;
    private static final int RIGHT_CARD_COL_INDEX = 2;
    private static final int MIDDLE_CARD_COL_INDEX = 1;
    private static final int TRUMP_ROW_INDEX = 1;
    private static final int TRUMP_COL_INDEX = 1;
    private static final double FULL_OPACITY = 1;
    private static final double NOT_PLAYABLE_OPACITY = 0.2;
    
    private final PlayerId ownId;
    private final Map<PlayerId, String> playerNames;
    private final ScoreBean sb;
    private final TrickBean tb;
    private final HandBean hb;
    private final StackPane victory;
    private final BlockingQueue<Card> queue;
    private final ObservableMap<Card, Image> cardMap = FXCollections
            .observableHashMap();
    private final ObservableMap<Card.Color, Image> trumpMap = FXCollections
            .observableHashMap();

    /**
     * The constructor for the graphic interface of a given player.
     * @param ownId the id of the player playing on the interface.
     * @param playerNames the map between the IDs of the players and their names. 
     * @param sb the score bean of the game.
     * @param tb the trick bean of the game.
     * @param hb the hand bean of the game.
     * @param queue a queue where the card t obe played by the player is put. 
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames,
            ScoreBean sb, TrickBean tb, HandBean hb,
            BlockingQueue<Card> queue) {
        
        this.ownId = ownId;
        this.playerNames = playerNames;
        this.sb = sb;
        this.tb = tb;
        this.hb = hb;
        this.queue = queue;

        //Creates the complete pane for the game
        BorderPane game = new BorderPane(createTrickPane(), createScorePane(),
                null, createHandPane(), null);
        this.victory = new StackPane(game, createVictoryPanes(TeamId.TEAM_1),
                createVictoryPanes(TeamId.TEAM_2));

    }

    /**
     * Creates the stage of the game.
     * @return a Stage for the game.
     */
    public Stage createStage() {
        Stage stage = new Stage();
        stage.setTitle("Javass - " + playerNames.get(ownId));
        stage.setScene(new Scene(victory));
        return stage;
    }

    private Pane createScorePane() {

        //Initializes the pane
        GridPane scorePane = new GridPane();
        scorePane.setStyle(
                "-fx-font: 16 Optima; -fx-background-color: lightgray;-fx-padding: 5px; -fx-alignment: center;");

        //Write the player names for each team in the score pane
        Node team1 = new Text(playerNames.get(PlayerId.PLAYER_1) + " et "
                + playerNames.get(PlayerId.PLAYER_3) + " : ");
        Node team2 = new Text(playerNames.get(PlayerId.PLAYER_2) + " et "
                + playerNames.get(PlayerId.PLAYER_4) + " : ");

        scorePane.add(team1, COLUMN_TEAM, TeamId.TEAM_1.ordinal());
        scorePane.add(team2, COLUMN_TEAM, TeamId.TEAM_2.ordinal());

        
        //Sets the scores next to each team everytime they change.
        for (TeamId teamId : TeamId.ALL) {
            Label turnPoints = new Label();
            Label trickPoints = new Label();
            Label scTotal = new Label(" /Total : ");
            Label totalPoints = new Label();

            StringProperty trickPointsProperty = new SimpleStringProperty();
            
            sb.turnPointsProperty(teamId).addListener((o, oV, nV) -> {
                trickPointsProperty
                        .setValue((nV.intValue() - oV.intValue()) < 0 ? ""
                                : "(+" + (nV.intValue() - oV.intValue()) + ")");
                trickPoints.setText(trickPointsProperty.getValue());
            });

            trickPoints.setTextAlignment(TextAlignment.LEFT);
            turnPoints.textProperty()
                    .bind(Bindings.convert(sb.turnPointsProperty(teamId)));
            totalPoints.textProperty()
                    .bind(Bindings.convert(sb.totalPointsProperty(teamId)));

            //Add the points texts to the pane.
            scorePane.add(turnPoints, COLUMN_TURNPOINTS, teamId.ordinal());
            scorePane.add(trickPoints, COLUMN_TRICKPOINTS, teamId.ordinal());
            scorePane.add(scTotal, COLUMN_TOTAL_LABEL, teamId.ordinal());
            scorePane.add(totalPoints, COLUMN_GAMEPOINTS, teamId.ordinal());
        }
        return scorePane;
    }

    private Pane createTrickPane() {

        //Initializes the pane
        GridPane trickPane = new GridPane();
        trickPane.setStyle(
                "-fx-background-color: whitesmoke; fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray;-fx-alignment: center;");

        //Associates a trump image with a trump color in the trump map, if it is empty.
        if(trumpMap.isEmpty())
            for (Card.Color color : Color.ALL) {
                trumpMap.put(color, new Image("/trump_" + color.ordinal() + ".png"));
            }

        //Sets up the image of the trump color.
        ImageView trumpIm = new ImageView();
        trumpIm.imageProperty()
                .bind(Bindings.valueAt(trumpMap, tb.trumpProperty()));
        trumpIm.setFitWidth(TRUMP_H_AND_W);
        trumpIm.setFitHeight(TRUMP_H_AND_W);
        //Adds the trump image to the pane.
        trickPane.add(trumpIm, TRUMP_COL_INDEX, TRUMP_ROW_INDEX);
        GridPane.setHalignment(trumpIm, HPos.CENTER);

        //Adds the played card of each player next to their name each trick.
        for (PlayerId pId : PlayerId.ALL) {
            //Sets up the halo effect on the current winning card of the trick.
            Rectangle halo = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
            halo.setStyle(
                    "-fx-arc-width: 20; -fx-arc-height: 20;-fx-fill: transparent;-fx-stroke: lightpink;-fx-stroke-width: 5;-fx-opacity: 0.5;");
            halo.setEffect(new GaussianBlur(4));
            halo.visibleProperty()
                    .bind(tb.winningPlayerProperty().isEqualTo(pId).and(Bindings.createBooleanBinding(() -> !(tb.trick().values().isEmpty()), tb.trick())));

            //Gets the name of the player
            Text name = new Text(playerNames.get(pId));
            name.setStyle("-fx-font: 14 Optima;");

            //Creates a pane with the card image and the halo effect.
            ImageView img = getImage(pId);
            StackPane hAndC = new StackPane(img, halo);

            if (pId == ownId) {
                //Creates a vertical box with the card pane and the name at the bottom of it.
                VBox ownCard = new VBox(hAndC, name);
                ownCard.setStyle("-fx-padding: 5px;-fx-alignment: center;");
                trickPane.add(ownCard, MIDDLE_CARD_COL_INDEX, OWN_CARD_ROW_INDEX);
            } else {
                //Creates a vertical box with the card pane and the name on top of it.                
                VBox card = new VBox(name, hAndC);
                card.setStyle("-fx-padding: 5px;-fx-alignment: center;");
                if (pId.ordinal() == (ownId.ordinal() + 1) % PlayerId.COUNT) {
                    trickPane.add(card, RIGHT_CARD_COL_INDEX, CARD_ROW_INDEX, CARD_COL_SPAN, CARD_ROW_SPAN);
                    card.setAlignment(Pos.CENTER);
                }
                if (pId.ordinal() == (ownId.ordinal() + 2) % PlayerId.COUNT) 
                    trickPane.add(card, MIDDLE_CARD_COL_INDEX, CARD_ROW_INDEX);
                
                if (pId.ordinal() == (ownId.ordinal() + 3) % PlayerId.COUNT) {
                    trickPane.add(card, LEFT_CARD_COL_INDEX, CARD_ROW_INDEX, CARD_COL_SPAN, CARD_ROW_SPAN);
                    card.setAlignment(Pos.CENTER);
                }
            }
        }
        return trickPane;
    }

    private Pane createHandPane() {

        //Initialize the horizontal box for the hand of the player.
        HBox handPane = new HBox();
        handPane.setStyle(
                "-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px;");
        
        //For each card in the hand draws it if it has not been played.
        for (int i = 0; i < Jass.HAND_SIZE; i++) {
            int index = i;
            
            //Sets up the image of the card
            ImageView img = new ImageView();
            img.imageProperty().bind(
                    Bindings.valueAt(cardMap, Bindings.valueAt(hb.hand(), i)));
            img.setFitWidth(CARD_WIDTH / 2);
            img.setFitHeight(CARD_HEIGHT / 2);
            //Plays the card when it is clicked
            img.setOnMouseClicked(e -> {
                try {
                    queue.put(hb.hand().get(index));
                } catch (InterruptedException e1) {
                    throw new Error(e1);
                }
                ;
            });
            
            //Decrease the opacity of unplayable cards
            BooleanBinding isPlayable = Bindings.createBooleanBinding(
                    () -> hb.playableCards().contains(hb.hand().get(index)),
                    hb.playableCards(), hb.hand());
            img.opacityProperty()
                    .bind(Bindings.when(isPlayable).then(FULL_OPACITY).otherwise(NOT_PLAYABLE_OPACITY));
            img.disableProperty().bind(isPlayable.not());

            //Adds the image of the card to the pane
            handPane.getChildren().add(img);
        }

        return handPane;
    }

    private Pane createVictoryPanes(TeamId team) {

        //Creates the victory message with the winning team.
        Text message = new Text();
        message.textProperty()
                .bind(Bindings.format(
                        "%s win the game with a total of %d to %d",
                        team.toString(), sb.totalPointsProperty(team),
                        sb.totalPointsProperty(team.other())));

        //Sets up the pane with victory message to display.
        BorderPane victoryPane = new BorderPane(message);
        victoryPane
                .setStyle("-fx-font: 16 Optima;-fx-background-color: white;");
        victoryPane.visibleProperty()
                .bind(sb.winningTeamProperty().isEqualTo(team));
        
        return victoryPane;
    }

    private ImageView getImage(PlayerId pId) {
        ImageView img = new ImageView();

        //Associates a card image with a card in the card map, if it is empty.
        if (cardMap.isEmpty()) {
            for (Rank rank : Card.Rank.ALL) {
                for (Color color : Card.Color.ALL) {

                    Card c = Card.of(color, rank);
                    cardMap.put(c, new Image("/card_" + color.ordinal() + "_"
                            + rank.ordinal() + "_240.png"));
                }
            }
        }
        //Finds the image at the given pId.
        img.imageProperty().bind(
                Bindings.valueAt(cardMap, Bindings.valueAt(tb.trick(), pId)));
        img.setFitWidth(CARD_WIDTH);
        img.setFitHeight(CARD_HEIGHT);

        return img;
    }
}