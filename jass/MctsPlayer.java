package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.SplittableRandom;

/**
 * A simulated player that plays cards selected by a Monte Carlo search tree Algorithm.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class MctsPlayer implements Player {

	private final static int EXPLORATION_CONST = 40;

	private final SplittableRandom rng;
	private final PlayerId id;
	private final int iterations;

	/**
	 * A simulated player that plays cards selected by a Monte Carlo search tree Algorithm.
	 * @param ownId the playerId of the simulated player.
	 * @param rngSeed a long the seed for the pseudo-random number generator used to simulate random turns. 
	 * @param iterations the number of iteration of the algorithm, must be superior to the size of a hand of Jass.
	 * @throws IllegalArgumentException if the number of iterations is inferior to 9.
	 */
	public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
		checkArgument(iterations >= Jass.HAND_SIZE);

		this.rng = new SplittableRandom(rngSeed);
		this.id = ownId;
		this.iterations = iterations;
	}

	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {
		Node root = new Node(state, playableCards(state, hand.packed(), id), id);
		ArrayList<Node> path;

		for (int i = 0; i < iterations; ++i) {
		    //Add nodes to the path.
			path = root.addNode(hand.packed());
			//Simulate the last node of the path.
			long score = simulate(path.get(path.size()-1), hand.packed());
			//Propagate the score through all the nodes.
			backPropagateScore(score, path);
		}
		return Card.ofPacked(PackedCardSet.get(playableCards(state, hand.packed(), id), root.highestV(0)));
	}

	/*
	 * Simulate the possible score of a random turn given a certain state and hand.
	 * Returns the final score of a finished random turn.
	 */
	private long simulate(Node node, long hand) {
		TurnState s = node.state;
		//Plays a random turn from the state of the given node.
		if(s.isTerminal())
		    return s.packedScore();
		else
    		while (!s.isTerminal()) {
    			long pC = playableCards(s, hand, id);
    			Card randomCard = Card.ofPacked(PackedCardSet.get(pC, rng.nextInt(PackedCardSet.size(pC))));
    			s = s.withNewCardPlayedAndTrickCollected(randomCard);
    		}
		return s.packedScore();
	}

	/*
	 * Determines the playable cards for a given state taking account of the non-played cards and the hand of the payer.
	 * Returns the playable cards for state.
	 */
	private static long playableCards(TurnState state, long hand, PlayerId id) {
		if (state.isTerminal())
			return PackedCardSet.EMPTY;

		return state.nextPlayer().equals(id) ? 
		        PackedTrick.playableCards(state.packedTrick(), PackedCardSet.intersection(hand, state.packedUnplayedCards())) :
		            PackedTrick.playableCards(state.packedTrick(), PackedCardSet.difference(state.packedUnplayedCards(), hand));
	}

	/*
	 * A method that propagate the updated score along the path of nodes by updating the score of every nodes. 
	 */
	private void backPropagateScore(long score, ArrayList<Node> path) {
		path.get(0).updateScore(score, id.team());
		
		for (int i = 1; i < path.size(); ++i)
			path.get(i).updateScore(score,
					path.get(i - 1)
					.state.nextPlayer()
					.team());
	}
/**
 * A node of the Monte Carlo Tree Search algorithm. 
 * @author Charles Beauville
 * @author Célia Houssiaux
 *
 */
	private static final class Node {
		private final TurnState state;
		private long potentialCards;
		private final Node[] children;
		private int n;
		private int s;
		private PlayerId id;

		private Node(TurnState state, long playableCards, PlayerId id) {
			this.state = state;
			this.potentialCards = playableCards;
			this.s = 0;
			this.n = 0;
			this.children = new Node[PackedCardSet.size(potentialCards)];
			this.id = id;
		}

		/*
		 * A method that gives the index of the best child of a Node.
		 * That means the one who got the highest value of V.
		 * c is the exploration constant.
		 * Returns the index of the best child in children[].
		 */
		private int highestV(int c) {
			int bestVIndex = 0;
			double v;
			double bestV = 0;
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null)
					return i;

				//Computes the function V.
				if (children[i].n > 0)
					v = (double) (children[i].s) / (double)(children[i].n)
					+ c * Math.sqrt((Math.log(n+1) / (double)children[i].n));
				else
					return i;

				//Computes if the node is the new best node.
				if (v > bestV) {
					bestV = v;
					bestVIndex = i;
				}
			}
			return bestVIndex;
		}

		/*
		 * A method that adds if possible a new node at the right place of the tree.
		 * Returns a List that contains the path from the root to the new node freshly created.
		 */
		private ArrayList<Node> addNode(long hand) {
			ArrayList<Node> path = new ArrayList<>();
			boolean newNodeAdded = false;
			path.add(this);

			Node lastNode = this;
			//Goes through all the best children of the last node and adds them to the path until a node does not have all its children.
			while (lastNode.hasAllChildren()) {
				lastNode = lastNode.children[lastNode.highestV(EXPLORATION_CONST)];
				path.add(lastNode);
				if (lastNode.state.isTerminal())
					return path;
			}

			//Creates a new node as a child of last node
			if (!PackedCardSet.isEmpty(lastNode.potentialCards)) {
			    //Computes a new turn state with the first card of the potential cards played and removes the played card form the potential cards. 
				int card = PackedCardSet.get(lastNode.potentialCards, 0);
				TurnState newState = lastNode.state
						.withNewCardPlayedAndTrickCollected(Card.ofPacked(card));
				lastNode.potentialCards = PackedCardSet.remove(lastNode.potentialCards, card);

				long newPotentialCards = playableCards(newState, hand, id);

				if (PackedCardSet.isEmpty(newPotentialCards))
					return path;

				Node node = new Node(newState, newPotentialCards, id);
				
				//Adds the new computed node to the children of this node where there is space.
				for (int i = 0; i < lastNode.children.length; i++) {
					if (newNodeAdded)
						break;

					if (lastNode.children[i] == null) {
						lastNode.children[i] = node;
						newNodeAdded = true;
					}
				}
			}
			return path;
		}

		/*
		 * This boolean indicates if a children[]is full.
		 * Returns true, if children[] is full.
		 */
		private boolean hasAllChildren() {
			return children[children.length-1] != null;
		}

		/*
		 * This method updates the score of a given team. 
		 */
		private void updateScore(long score, TeamId t) {
			s += PackedScore.totalPoints(score, t);
			n++;
		}

	}
}
