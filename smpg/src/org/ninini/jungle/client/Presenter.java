package org.ninini.jungle.client;

import java.util.HashSet;
import java.util.Set;

import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.GameResultReason;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.PieceRank;
import org.ninini.jungle.shared.Position;
import org.ninini.jungle.shared.State;
import org.ninini.jungle.shared.StateChanger;
import org.ninini.jungle.shared.StateChangerImpl;
import org.ninini.jungle.shared.StateExplorer;
import org.ninini.jungle.shared.StateExplorerImpl;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;


public class Presenter {
	public interface View{
		//Render the piece at a cell
		void setPiece(int row, int col, Piece piece);
		//Turns the highlighting on or off at this shell
		void setHighlighted(int row, int col, boolean highlighted);
		//Indicate whose turn
		void setWhoseTurn(Color color);
		//Indicate the game result
		void setGameResult(GameResult gameResult);
	}

	private View view;
	private State state;
	private StateChanger stateChanger;
	private StateExplorer stateExplorer;
	private Set<Position> highlightedPositions;
	private Set<Move> possibleMoves;
	private Position selected;
	
	public Presenter(){
		state = new State();
		highlightedPositions = new HashSet<Position>();
		possibleMoves = new HashSet<Move>();
		stateChanger = new StateChangerImpl();
		stateExplorer = new StateExplorerImpl();
	}
	
	public State getState(){
		return state;
	}
	public View getView(){
		return view;
	}
	
	public void setView(View view){
		this.view = view;
	}
	public void setState(State state){
		this.state = state;
		view.setGameResult(state.getGameResult());
		view.setWhoseTurn(state.getTurn());
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				view.setPiece(row, col, state.getPiece(row, col));
			}
		}
	}
	
	public void selectBoard(int row, int col){
		if(state.getGameResult() == null) return;
		if(selected == null){//no piece is selected
			Position temp = new Position(row,col);
			if(state.getPiece(temp) != null){
				Piece piece = state.getPiece(temp);
				if(piece.getColor() != state.getTurn()) return;
				newPieceSelected(temp);
			}else return;
		}else {//a piece is selected
			Position moveTo = new Position(row, col);
			Piece targetPiece = state.getPiece(moveTo);
			if(targetPiece.getColor() == state.getTurn()){
				newPieceSelected(moveTo);
			}else{
				stateChanger.makeMove(state, new Move(selected, moveTo));
				clearSets();
			}
		}
	}
	//Select a new piece to move
	private void newPieceSelected(Position p){
		selected = p;
		clearSets();
		highlightedPositions.add(selected);
		possibleMoves.addAll(stateExplorer.getPossibleMovesFromPosition(state, selected));
		for(Move move : possibleMoves){
			highlightedPositions.add(move.getTo());
		}
		for(Position highlightedp : highlightedPositions){
			view.setHighlighted(highlightedp.getRow(), highlightedp.getCol(), true);
		}		
	}
	//Clear all hightlighted positions and possible moves
	private void clearSets(){
		//clear all highlighted in the view
		for(Position p : highlightedPositions){
			view.setHighlighted(p.getRow(), p.getCol(), false);
		}
		highlightedPositions.clear();
		possibleMoves.clear();
	}
	
	//Renders the state
	public void showState(){
		view.setWhoseTurn(state.getTurn());
		view.setGameResult(state.getGameResult());
		//Render the cells
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				view.setHighlighted(row, col, false);
				view.setPiece(row, col, state.getPiece(row, col));
			}
		}
		
		//If game is over
		if(state.getGameResult() != null){
			view.setGameResult(state.getGameResult());
		}
	}
	
	//Create a valueChangeHnader responsible for record browser history
	public void initializeHistory(){
		History.addValueChangeHandler(new ValueChangeHandler<String>(){
			@Override
			public void onValueChange(ValueChangeEvent<String> event){
				String historyToken = event.getValue();
				setState(unserializeState(historyToken));
			}
		});
		String startState = History.getToken();
		setState(unserializeState(startState));
	}
	
	//Creates a string representing all the information in a state.
	public String serializeState(State state) {
		char[] stringBuffer = new char[37];
		//[0] -- who's turn
		switch(state.getTurn()){
		case RED: stringBuffer[0] = 'r';
			break;
		case BLACK: stringBuffer[0] = 'b';
			break;
		default: stringBuffer[0] = 'n';
			break;
		}
		
		//[1:2] ratInRiver flag
		if(state.ifRatInRiver(0)) stringBuffer[1] = '1';
		else stringBuffer[1] = '0';
		if(state.ifRatInRiver(1)) stringBuffer[2] = '1';
		else stringBuffer[2] = '0';
		
		//[3:4] gameResult
		if(state.getGameResult() != null){
			switch (state.getGameResult().getWinner()){
			case RED: stringBuffer[3] = 'r';
				break;
			case BLACK: stringBuffer[3] = 'b';
				break;
			default :
				stringBuffer[3] = 'n';
				break;
			}
			switch (state.getGameResult().getReason()){
			case ENTER_DEN : stringBuffer[4] = '1';
				break;
			case CAPTURE_ALL_PIECES : stringBuffer[4] = '2';
				break;
			default: stringBuffer[4] = 'n';
				break;
			}
		}else{
			stringBuffer[3] = '0';
			stringBuffer[4] = '0';
		}
		
		//[5:36] piece position
		//every two char record the position of the piece
		//(9,9) means the piece is removed from the board
		//initialize 
		for(int i = 5; i < 37; i++)
			stringBuffer[i] = '9';
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				Piece piece = state.getPiece(row, col);
				switch(piece.getRank()){
				case RAT:
					if(piece.getColor() == Color.RED){
						stringBuffer[5] = Character.forDigit(row, 10);
						stringBuffer[6] = Character.forDigit(col, 10);
					}else {
						stringBuffer[7] = Character.forDigit(row, 10);
						stringBuffer[8] = Character.forDigit(col, 10);						
					}
					break;
				case CAT:
					if(piece.getColor() == Color.RED){
						stringBuffer[9] = Character.forDigit(row, 10);
						stringBuffer[10] = Character.forDigit(col, 10);
					}else {
						stringBuffer[11] = Character.forDigit(row, 10);
						stringBuffer[12] = Character.forDigit(col, 10);						
					}
					break;
				case DOG:
					if(piece.getColor() == Color.RED){
						stringBuffer[13] = Character.forDigit(row, 10);
						stringBuffer[14] = Character.forDigit(col, 10);
					}else {
						stringBuffer[15] = Character.forDigit(row, 10);
						stringBuffer[16] = Character.forDigit(col, 10);						
					}
					break;
				case WOLF:
					if(piece.getColor() == Color.RED){
						stringBuffer[17] = Character.forDigit(row, 10);
						stringBuffer[18] = Character.forDigit(col, 10);
					}else {
						stringBuffer[19] = Character.forDigit(row, 10);
						stringBuffer[20] = Character.forDigit(col, 10);						
					}
					break;
				case LEOPARD:
					if(piece.getColor() == Color.RED){
						stringBuffer[21] = Character.forDigit(row, 10);
						stringBuffer[22] = Character.forDigit(col, 10);
					}else {
						stringBuffer[23] = Character.forDigit(row, 10);
						stringBuffer[24] = Character.forDigit(col, 10);						
					}
					break;
				case TIGER:
					if(piece.getColor() == Color.RED){
						stringBuffer[25] = Character.forDigit(row, 10);
						stringBuffer[26] = Character.forDigit(col, 10);
					}else {
						stringBuffer[27] = Character.forDigit(row, 10);
						stringBuffer[28] = Character.forDigit(col, 10);						
					}
					break;
				case LION:
					if(piece.getColor() == Color.RED){
						stringBuffer[29] = Character.forDigit(row, 10);
						stringBuffer[30] = Character.forDigit(col, 10);
					}else {
						stringBuffer[31] = Character.forDigit(row, 10);
						stringBuffer[32] = Character.forDigit(col, 10);						
					}
					break;
				case ELEPHANT:
					if(piece.getColor() == Color.RED){
						stringBuffer[33] = Character.forDigit(row, 10);
						stringBuffer[34] = Character.forDigit(col, 10);
					}else {
						stringBuffer[35] = Character.forDigit(row, 10);
						stringBuffer[36] = Character.forDigit(col, 10);						
					}
					break;
				default:
					break;
				}
			}
		}
		
		return new String(stringBuffer);
	}
	
	//Decodes a State encoded by serializeString.
	public State unserializeState(String serialized){
		Color turn = null;
		switch(serialized.charAt(0)){
		case 'r': turn = Color.RED;
			break;
		case 'b': turn = Color.BLACK;
			break;
		default:
			break;
		}
		boolean[] ratInRiver = {false, false};
		if(serialized.charAt(1) == '1') ratInRiver[0] = true;
		if(serialized.charAt(2) == '1') ratInRiver[1] = true;
		GameResult gameResult = null;
		if(serialized.charAt(3) != '0' || serialized.charAt(4) != '0'){
			Color winner = null;
			GameResultReason reason= null;
			switch(serialized.charAt(3)){
			case 'r':
				winner = Color.RED;
				break;
			case 'b':
				winner = Color.BLACK;
				break;
			default:
				break;
			}
			switch(serialized.charAt(4)){
			case '1':
				reason = GameResultReason.ENTER_DEN;
				break;
			case '2':
				reason = GameResultReason.CAPTURE_ALL_PIECES;
				break;
			default:
				break;
			}
			gameResult = new GameResult(winner, reason);
		}
		
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		//red rat
		{
			int row = Character.getNumericValue(serialized.charAt(5));
			int col = Character.getNumericValue(serialized.charAt(6));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.RAT, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.RAT, false);
			}
		}
		//black rat
		{
			int row = Character.getNumericValue(serialized.charAt(7));
			int col = Character.getNumericValue(serialized.charAt(8));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.RAT, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.RAT, false);
			}
		}
		//red cat
		{
			int row = Character.getNumericValue(serialized.charAt(9));
			int col = Character.getNumericValue(serialized.charAt(10));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.CAT, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.CAT, false);
			}
		}
		//black cat
		{
			int row = Character.getNumericValue(serialized.charAt(11));
			int col = Character.getNumericValue(serialized.charAt(12));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.CAT, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.CAT, false);
			}
		}
		//red dog
		{
			int row = Character.getNumericValue(serialized.charAt(13));
			int col = Character.getNumericValue(serialized.charAt(14));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.DOG, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.DOG, false);
			}
		}
		//black dog
		{
			int row = Character.getNumericValue(serialized.charAt(15));
			int col = Character.getNumericValue(serialized.charAt(16));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.DOG, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.DOG, false);
			}
		}
		//red wolf
		{
			int row = Character.getNumericValue(serialized.charAt(17));
			int col = Character.getNumericValue(serialized.charAt(18));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.WOLF, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.WOLF, false);
			}
		}
		//black wolf
		{
			int row = Character.getNumericValue(serialized.charAt(19));
			int col = Character.getNumericValue(serialized.charAt(20));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.WOLF, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.WOLF, false);
			}
		}
		//red leopard
		{
			int row = Character.getNumericValue(serialized.charAt(21));
			int col = Character.getNumericValue(serialized.charAt(22));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.LEOPARD, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.LEOPARD, false);
			}
		}
		//black leopard
		{
			int row = Character.getNumericValue(serialized.charAt(23));
			int col = Character.getNumericValue(serialized.charAt(24));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.LEOPARD, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.LEOPARD, false);
			}
		}
		//red tiger
		{
			int row = Character.getNumericValue(serialized.charAt(25));
			int col = Character.getNumericValue(serialized.charAt(26));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.TIGER, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.TIGER, false);
			}
		}
		//black tiger
		{
			int row = Character.getNumericValue(serialized.charAt(27));
			int col = Character.getNumericValue(serialized.charAt(28));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.TIGER, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.TIGER, false);
			}
		}
		//red lion
		{
			int row = Character.getNumericValue(serialized.charAt(29));
			int col = Character.getNumericValue(serialized.charAt(30));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.LION, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.LION, false);
			}
		}
		//black lion
		{
			int row = Character.getNumericValue(serialized.charAt(31));
			int col = Character.getNumericValue(serialized.charAt(32));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.LION, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.LION, false);
			}
		}
		//red elephant
		{
			int row = Character.getNumericValue(serialized.charAt(33));
			int col = Character.getNumericValue(serialized.charAt(34));
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.ELEPHANT, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.ELEPHANT, false);
			}
		}
		//black elephant
		{
			int row = Character.getNumericValue(serialized.charAt(35));
			int col = Character.getNumericValue(serialized.charAt(36));
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.ELEPHANT, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.ELEPHANT, false);
			}
		}
		
		return new State(turn, board, ratInRiver, gameResult);
	}
}
