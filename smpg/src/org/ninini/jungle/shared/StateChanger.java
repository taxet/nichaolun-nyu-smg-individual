package org.ninini.jungle.shared;


public interface StateChanger {
	/**
	 * Make a piece move and change state to reflect the new game state.
	 * If the move is illegal, the method throws IllegalMove.
	 * 
	 * http://en.wikipedia.org/wiki/Jungle_(board_game)
	*/
	public void makeMove(State state, Move move) throws IllegalMove;
}