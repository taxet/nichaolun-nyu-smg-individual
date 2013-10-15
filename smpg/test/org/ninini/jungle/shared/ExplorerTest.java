package org.ninini.jungle.shared;

import org.junit.Test;

public class ExplorerTest extends AbstractStateExplorerTest {
	
	@Test
	public void test(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 3, new Piece(Color.BLACK, PieceRank.CAT));
		original.setPiece(1, 5, new Piece(Color.RED, PieceRank.RAT));
		for(Position p : stateExplorer.getPossibleStartPositions(original)){
			System.out.println("Possible start position: "+p.toString());
		}
		for(Move move : stateExplorer.getPossibleMoves(original)){
			System.out.println("Possible moves from "+move.getFrom().toString()+" to "+move.getTo().toString());
		}
	}

	@Override
	public StateExplorer getStateExplorer() {
		return new StateExplorerImpl();
	}

}
