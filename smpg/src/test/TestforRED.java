package test;

import jungle.Color;
import jungle.IllegalMove;
import jungle.Move;
import jungle.Piece;
import jungle.PieceRank;
import jungle.Position;
import jungle.State;

import org.junit.Test;

public abstract class TestforRED extends AbstractStateChangerTest {
	
	@Test
	public void testRegularMove(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 3, new Piece(Color.BLACK, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(3, 3),new Position(4, 3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatIntoRiver(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 3, new Piece(Color.BLACK, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(3,3),new Position(3,4));
		stateChanger.makeMove(original, move);		
	}
	
	@Test
	public void testLionAcrossRiver(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(4, 3, new Piece(Color.RED, PieceRank.LION));
		Move move = new Move(new Position(4, 3),new Position(4, 0));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testTigerAcrossRiver(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 1, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(2, 2, new Piece(Color.RED, PieceRank.TIGER));
		Move move = new Move(new Position(2, 2),new Position(6, 2));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatOutOfRiver(){
		boolean[] ratInRiver = {true, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(4, 2, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(4, 2),new Position(4, 3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testCaptureMove(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.CAT));
		Move move = new Move(new Position(2,3),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatCaptureElephant(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.RAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		Move move = new Move(new Position(2,3),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testRatCaptureRatInRiver(){
		boolean[] ratInRiver = {true, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 2, new Piece(Color.RED, PieceRank.RAT));
		original.setPiece(4, 2, new Piece(Color.BLACK, PieceRank.RAT));
		Move move = new Move(new Position(3,2),new Position(4,2));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testLionAcrossRiverCapture(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(4, 3, new Piece(Color.RED, PieceRank.LION));
		original.setPiece(4, 0, new Piece(Color.BLACK, PieceRank.RAT));
		Move move = new Move(new Position(4, 3),new Position(4, 0));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testTigerAcrossRiverCapture(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 1, new Piece(Color.BLACK, PieceRank.ELEPHANT));
		original.setPiece(2, 2, new Piece(Color.RED, PieceRank.TIGER));
		original.setPiece(2, 2, new Piece(Color.BLACK, PieceRank.RAT));
		Move move = new Move(new Position(2, 2),new Position(6, 2));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testCaptureEnimyInTrap(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(1, 2, new Piece(Color.RED, PieceRank.DOG));
		original.setPiece(0, 2, new Piece(Color.BLACK, PieceRank.ELEPHANT, true));
		Move move = new Move(new Position(1, 2),new Position(0, 3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testIntoOppositeDen(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(8, 2, new Piece(Color.RED, PieceRank.DOG));
		original.setPiece(7, 1, new Piece(Color.BLACK, PieceRank.DOG));
		Move move = new Move(new Position(8, 2),new Position(8, 3));
		stateChanger.makeMove(original, move);
	}
	
	@Test
	public void testMoveOutOfTrap(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(8, 2, new Piece(Color.RED, PieceRank.DOG, true));
		original.setPiece(7, 1, new Piece(Color.BLACK, PieceRank.DOG));
		Move move = new Move(new Position(8, 2),new Position(7, 2));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testCaptureHigerPiece(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.LION));
		Move move = new Move(new Position(2,3),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testIlligalMove(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(2, 3, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.CAT));
		Move move = new Move(new Position(2,3),new Position(3,4));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testMoveIntoOwnDen(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(0, 2, new Piece(Color.RED, PieceRank.CAT));
		original.setPiece(3, 3, new Piece(Color.BLACK, PieceRank.CAT));
		Move move = new Move(new Position(0,2),new Position(0,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testRatInLandCaptureRatInRiver(){
		boolean[] ratInRiver = {true, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 0, new Piece(Color.RED, PieceRank.RAT));
		original.setPiece(3, 1, new Piece(Color.BLACK, PieceRank.RAT));
		Move move = new Move(new Position(3, 0),new Position(3,1));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testRatInRiverCaptureAnimalInLand(){
		boolean[] ratInRiver = {true, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 0, new Piece(Color.BLACK, PieceRank.RAT));
		original.setPiece(3, 1, new Piece(Color.RED, PieceRank.RAT));
		Move move = new Move(new Position(3, 1),new Position(3,0));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testLionAcrossRiverWithRat(){
		boolean[] ratInRiver = {true, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 1, new Piece(Color.BLACK, PieceRank.RAT));
		original.setPiece(3, 0, new Piece(Color.RED, PieceRank.LION));
		Move move = new Move(new Position(3, 0),new Position(3,3));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testTigerAcrossRiverWithRat(){
		boolean[] ratInRiver = {true, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 1, new Piece(Color.BLACK, PieceRank.RAT));
		original.setPiece(2, 1, new Piece(Color.RED, PieceRank.TIGER));
		Move move = new Move(new Position(2, 1),new Position(6,1));
		stateChanger.makeMove(original, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testAnimalMoveIntoRiver(){
		boolean[] ratInRiver = {false, false};
		Piece[][] board = new Piece[State.rows][State.cols];
		State original = new State(Color.RED, board, ratInRiver, null);
		original.setPiece(3, 0, new Piece(Color.BLACK, PieceRank.RAT));
		original.setPiece(2, 1, new Piece(Color.RED, PieceRank.CAT));
		Move move = new Move(new Position(2, 1),new Position(3,1));
		stateChanger.makeMove(original, move);
	}

}
