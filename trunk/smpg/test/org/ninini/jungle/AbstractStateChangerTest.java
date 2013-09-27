package org.ninini.jungle;

import org.junit.Before;

import org.ninini.jungle.Color;
import org.ninini.jungle.PieceRank;
import org.ninini.jungle.State;
import org.ninini.jungle.StateChanger;
import org.ninini.jungle.Move;
import org.ninini.jungle.IllegalMove;

public abstract class AbstractStateChangerTest {

	protected State start;
	protected StateChanger stateChanger;
	
	public abstract StateChanger getStateChanger();
	
	@Before
	public void setUp(){
		start = new State();
		final StateChanger impl = getStateChanger();
		stateChanger = new StateChanger(){
			@Override
			public void makeMove(State state, Move move) throws IllegalMove{
				assertStatePossible(state);
				impl.makeMove(state, move);
			}
		};
	}
	
	public static void assertStatePossible(State state){
		int[][] piecesCount = new int[2][PieceRank.values().length];
		for(int r = 0; r < State.ROWS; r++){
			for(int c = 0; c < State.COLS; c++){
				if(state.getPiece(r, c) == null) continue;
				++ piecesCount[state.getPiece(r, c).getColor().ordinal()][state.getPiece(r, c).getRank().ordinal()] ;
			}
		}
		for (Color c: Color.values()){
			for(PieceRank p: PieceRank.values()){
				check(piecesCount[c.ordinal()][p.ordinal()] == 1, "Only one Piece "+p+" should be in the board for one color.");
			}
		}
	}
	
	public static void check(boolean condition, String message){
		if(!condition){
			throw new RuntimeException(message);
		}
	}
}