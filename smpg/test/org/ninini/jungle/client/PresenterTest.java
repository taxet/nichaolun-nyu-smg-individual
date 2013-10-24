package org.ninini.jungle.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeast;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.PieceRank;
import org.ninini.jungle.shared.Position;
import org.ninini.jungle.shared.State;
import org.ninini.jungle.shared.StateChanger;
import org.ninini.jungle.shared.StateChangerImpl;
import org.ninini.jungle.shared.StateExplorer;
import org.ninini.jungle.shared.StateExplorerImpl;


public class PresenterTest {
	Presenter presenter;
	Presenter.View view;
	
	@Before
	public void setup(){
		presenter = new Presenter();
		view = Mockito.mock(Presenter.View.class);
		presenter.setView(view);
	}
	
	@Test
	public void testSerialize(){
		State state = new State();
		String expected = "r00" 
				+ "2066"//rat
				+ "1571"//cat
				+ "1175"//dog
				+ "2462"//wolf
				+ "2264"//leopard
				+ "0680"//tiger
				+ "0086"//lion
				+ "2660"//elephant
				;
		String actual = presenter.serializeState(state);
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void testUnserializeState(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		board[3][1] = new Piece(Color.RED, PieceRank.RAT,false);
		board[1][3] = new Piece(Color.BLACK, PieceRank.CAT,true);
		State expected = new State(Color.RED,board,null);
		String stateString = "r00"
				+ "3199"//rat
				+ "9913"//cat
				+ "9999"//dog
				+ "9999"//wolf
				+ "9999"//leopard
				+ "9999"//tiger
				+ "9999"//lion
				+ "9999"//elephant
				;				
		State actual = presenter.unserializeState(stateString);
		assertEquals(expected, actual);		
	}
	
	@Test
	public void testReturnDefaultStateOnUnparsableSerializedState() {
		State expected = new State();
		String stateString = "hahaha";
		State actual = presenter.unserializeState(stateString);
		assertEquals(expected, actual);		
	}
	
	@Test
	public void testSelectPiece(){
		State state = new State();
		presenter.selectBoard(0, 0);
		verify(view).setSelected(0, 0, true);
		StateExplorer stateExplorer = new StateExplorerImpl();
		Set<Move> moves = stateExplorer.getPossibleMovesFromPosition(state, new Position(0,0));
		for(Move m : moves){
			verify(view).setHighlighted(m.getTo().getRow(), m.getTo().getCol(), true);
		}
	}
	
	@Test
	public void testSelectNewPiece(){
		State state = new State();
		presenter.selectBoard(0, 0);
		presenter.selectBoard(2, 2);
		verify(view).setSelected(0, 0, false);
		verify(view).setSelected(2, 2, true);
		StateExplorer stateExplorer = new StateExplorerImpl();
		Set<Move> moves = stateExplorer.getPossibleMovesFromPosition(state, new Position(0,0));
		for(Move m : moves){
			verify(view).setHighlighted(m.getTo().getRow(), m.getTo().getCol(), false);
		}
		moves = stateExplorer.getPossibleMovesFromPosition(state, new Position(2,2));
		for(Move m : moves){
			verify(view).setHighlighted(m.getTo().getRow(), m.getTo().getCol(), true);
		}
		
	}

	@Test
	public void testMove(){
		State state = new State();
		presenter.selectBoard(0, 0);
		presenter.selectBoard(1, 0);
		StateChanger stateChanger = new StateChangerImpl();
		stateChanger.makeMove(state, new Move(new Position(0,0), new Position(1,0)));
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				verify(view).setPiece(row, col, state.getPiece(row, col));
			}
		}
		verify(view).setWhoseTurn(Color.BLACK,false);
		verify(view).setGameResult(null);
	}
	
	@Test
	public void testGameOverWithEnterDen(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		board[8][2] = new Piece(Color.RED, PieceRank.CAT, true);
		board[1][1] = new Piece(Color.BLACK, PieceRank.CAT, false);
		State state1 = new State(Color.RED, board, null);
		State state2 = new State(Color.RED, board, null);
		presenter.setState(state1);
		presenter.selectBoard(8, 2);
		presenter.selectBoard(8, 3);
		StateChanger stateChanger = new StateChangerImpl();
		stateChanger.makeMove(state2, new Move(new Position(8,2), new Position(8,3)));
		verify(view, atLeast(2)).setGameResult(state2.getGameResult());
	}
	
	@Test
	public void testGameOverWithCaptureAllPieces(){
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		board[8][2] = new Piece(Color.RED, PieceRank.CAT, true);
		board[8][1] = new Piece(Color.BLACK, PieceRank.CAT, false);
		State state1 = new State(Color.RED, board, null);
		State state2 = new State(Color.RED, board, null);
		presenter.setState(state1);
		presenter.selectBoard(8, 2);
		presenter.selectBoard(8, 1);
		StateChanger stateChanger = new StateChangerImpl();
		stateChanger.makeMove(state2, new Move(new Position(8,2), new Position(8,1)));
		verify(view, atLeast(2)).setGameResult(state2.getGameResult());		
	}
}
