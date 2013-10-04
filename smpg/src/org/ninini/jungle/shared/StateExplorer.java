package org.ninini.jungle.shared;

import java.util.Set;

/**
 * 
 * @author nini
 * Explorer the state graph
 */

public interface StateExplorer {
	  /**
	   * Returns all the possible moves from the given state.
	   */
	  Set<Move> getPossibleMoves(State state);


	  /**
	   * Returns the possible moves from the given state that begin at start.
	   */
	  Set<Move> getPossibleMovesFromPosition(State state, Position start);
	  
	  /**
	   * Returns the list of start positions of all possible moves.
	   */
	  Set<Position> getPossibleStartPositions(State state);
}
