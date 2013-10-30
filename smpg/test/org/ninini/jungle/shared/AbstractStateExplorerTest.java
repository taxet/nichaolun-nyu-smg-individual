package org.ninini.jungle.shared;

import java.util.Set;

import org.junit.Before;

public abstract class AbstractStateExplorerTest {

	protected State start;
	protected StateExplorer stateExplorer;
	
	public abstract StateExplorer getStateExplorer();
	
	@Before
	public void setUp(){
		start = new State();
		final StateExplorer impl = getStateExplorer();
		stateExplorer = new StateExplorer(){
			@Override
			public Set<Move> getPossibleMoves(State state) throws IllegalMove{
				assertStatePossible(state);
				return impl.getPossibleMoves(state);
			}
			
			@Override
			public Set<Move> getPossibleMovesFromPosition(State state, Position start) throws IllegalMove{
				assertStatePossible(state);
				return impl.getPossibleMovesFromPosition(state, start);
			}

			@Override
			public Set<Position> getPossibleStartPositions(State state) {
				assertStatePossible(state);
				return impl.getPossibleStartPositions(state);
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
				check(piecesCount[c.ordinal()][p.ordinal()] <= 1, "Only one Piece "+p+" should be in the board for one color.");
			}
		}
	}
	
	public static void check(boolean condition, String message){
		if(!condition){
			throw new RuntimeException(message);
		}
	}
}
