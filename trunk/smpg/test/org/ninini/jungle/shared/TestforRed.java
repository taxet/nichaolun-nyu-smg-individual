package org.ninini.jungle.shared;

import org.junit.Test;
import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.IllegalMove;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.PieceRank;
import org.ninini.jungle.shared.Position;
import org.ninini.jungle.shared.State;
import org.ninini.jungle.shared.StateChanger;
import org.ninini.jungle.shared.StateChangerImpl;

public class TestforRed extends AbstractStateChangerTest {
	
	@Test
	public void testRegularMove(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 3, new Piece(Color.BLACK, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(3, 3),new Position(4, 3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatIntoRiver(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 3, new Piece(Color.BLACK, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(3,3),new Position(3,4));
		stateChanger.makeMove(original, move);		
	}
	
	@Test
	public void testLionAcrossRiver(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(4, 3, new Piece(Color.RED, PieceRank.LION));
		Move move = new Move(new Position(4, 3),new Position(4, 0));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testTigerAcrossRiver(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 1, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(2, 2, new Piece(Color.RED, PieceRank.TIGER));
		Move move = new Move(new Position(2, 2),new Position(6, 2));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatOutOfRiver(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(4, 2, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(4, 2),new Position(4, 3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testCaptureMove(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.CAT));
		Move move = new Move(new Position(2,3),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatCaptureElephant(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.RAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		Move move = new Move(new Position(2,3),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatCaptureRatInRiver(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 2, new Piece(Color.RED, PieceRank.RAT));
		original.setPiece(4, 2, new Piece(Color.BLACK, PieceRank.RAT));
		Move move = new Move(new Position(3,2),new Position(4,2));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testLionAcrossRiverCapture(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(4, 3, new Piece(Color.RED, PieceRank.LION));
		original.setPiece(4, 0, new Piece(Color.BLACK, PieceRank.RAT));
		Move move = new Move(new Position(4, 3),new Position(4, 0));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testTigerAcrossRiverCapture(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 1, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(2, 2, new Piece(Color.RED, PieceRank.TIGER));
		Move move = new Move(new Position(2, 2),new Position(6, 2));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testCaptureEnimyInTrap(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(1, 2, new Piece(Color.RED, PieceRank.DOG));
		original.setPiece(0, 2, new Piece(Color.BLACK, PieceRank.ELEPHANT, true));
		Move move = new Move(new Position(1, 2),new Position(0, 2));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testIntoOppositeDen(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(8, 2, new Piece(Color.RED, PieceRank.DOG));
		original.setPiece(7, 1, new Piece(Color.BLACK, PieceRank.DOG));
		Move move = new Move(new Position(8, 2),new Position(8, 3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testMoveOutOfTrap(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(8, 2, new Piece(Color.RED, PieceRank.DOG, true));
		original.setPiece(7, 1, new Piece(Color.BLACK, PieceRank.DOG));
		Move move = new Move(new Position(8, 2),new Position(7, 2));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testCaptureHigerPiece(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.LION));
		Move move = new Move(new Position(2,3),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testIlligalMove(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.CAT));
		Move move = new Move(new Position(2,3),new Position(3,4));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testMoveIntoOwnDen(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(0, 2, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.CAT));
		Move move = new Move(new Position(0,2),new Position(0,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testRatInLandCaptureRatInRiver(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 0, new Piece(Color.RED, PieceRank.RAT));
		original.setPiece(3, 1, new Piece(Color.BLACK, PieceRank.RAT));
		Move move = new Move(new Position(3, 0),new Position(3,1));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testRatInRiverCaptureAnimalInLand(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 0, new Piece(Color.BLACK, PieceRank.RAT));
		original.setPiece(3, 1, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(3, 1),new Position(3,0));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testLionAcrossRiverWithRat(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 1, new Piece(Color.BLACK, PieceRank.RAT));
		original.ratIntoRiver(0);
		original.setPiece(3, 0, new Piece(Color.RED, PieceRank.LION));
		Move move = new Move(new Position(3, 0),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testTigerAcrossRiverWithRat(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.ratIntoRiver(0);
		original.setPiece(3, 1, new Piece(Color.BLACK, PieceRank.RAT));
		original.setPiece(2, 1, new Piece(Color.RED, PieceRank.TIGER));
		Move move = new Move(new Position(2, 1),new Position(6,1));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testAnimalMoveIntoRiver(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		State original = new State(Color.RED, board, null);
		original.setPiece(3, 0, new Piece(Color.BLACK, PieceRank.RAT));
		original.setPiece(2, 1, new Piece(Color.RED, PieceRank.CAT));
		Move move = new Move(new Position(2, 1),new Position(3,1));
		stateChanger.makeMove(original, move);
	}

	@Override
	public StateChanger getStateChanger() {
		return new StateChangerImpl();
	}

}