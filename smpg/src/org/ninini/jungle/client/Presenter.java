package org.ninini.jungle.client;

import java.util.HashSet;
import java.util.Set;

import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.GameResultReason;
import org.ninini.jungle.shared.IllegalMove;
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
		//Indicate the presenter
		void setPresenter(Presenter presenter);
		//Set the status
		void setStatus(String string);
		//Turns the selected on or off at this shell
		void setSelected(int row, int col, boolean selected);
		//Play sound when selecting a piece
		void playSoundWhenSelectPiece(Piece piece);
		//play animation
		void playAnimation(Move move,Piece startPiece, boolean capture);
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
		selected = null;
	}
	
	public State getState(){
		return state;
	}
	public View getView(){
		return view;
	}
	
	public void setView(View view){
		this.view = view;
		view.setPresenter(this);
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
		if(state.getGameResult() != null) return;
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
			if(targetPiece != null && targetPiece.getColor() == state.getTurn()){
				newPieceSelected(moveTo);
			}else{
				try{
					Piece startPiece = state.getPiece(selected);
					boolean capture = state.getPiece(moveTo) != null;
					Move move = new Move(selected, moveTo);
					stateChanger.makeMove(state, move);
					//play animation
					view.playAnimation(move, startPiece, capture);
					//make changes on graphics
					clearSets();
					view.setSelected(selected.getRow(), selected.getCol(), false);
					selected = null;
					showState();
				}catch (IllegalMove imove){
				}
			}
		}
	}
	//Select a new piece to move
	private void newPieceSelected(Position p){
		clearSets();
		if(selected != null) view.setSelected(selected.getRow(), selected.getCol(), false);
		selected = p;
		view.setSelected(selected.getRow(), selected.getCol(), true);
		possibleMoves.addAll(stateExplorer.getPossibleMovesFromPosition(state, selected));
		for(Move move : possibleMoves){
			highlightedPositions.add(move.getTo());
		}
		for(Position highlightedp : highlightedPositions){
			view.setHighlighted(highlightedp.getRow(), highlightedp.getCol(), true);
		}
		view.playSoundWhenSelectPiece(state.getPiece(p));
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
				view.setSelected(row, col, false);
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
		char[] stringBuffer = new char[35];
		//[0] -- who's turn
		switch(state.getTurn()){
		case RED: stringBuffer[0] = 'r';
			break;
		case BLACK: stringBuffer[0] = 'b';
			break;
		default: stringBuffer[0] = 'n';
			break;
		}
		
		//[1:2] gameResult
		if(state.getGameResult() != null){
			switch (state.getGameResult().getWinner()){
			case RED: stringBuffer[1] = 'r';
				break;
			case BLACK: stringBuffer[1] = 'b';
				break;
			default :
				stringBuffer[1] = 'n';
				break;
			}
			switch (state.getGameResult().getReason()){
			case ENTER_DEN : stringBuffer[2] = '1';
				break;
			case CAPTURE_ALL_PIECES : stringBuffer[2] = '2';
				break;
			default: stringBuffer[2] = 'n';
				break;
			}
		}else{
			stringBuffer[1] = '0';
			stringBuffer[2] = '0';
		}
		
		//[3:34] piece position
		//every two char record the position of the piece
		//(9,9) means the piece is removed from the board
		//initialize 
		for(int i = 5; i < 35; i++)
			stringBuffer[i] = '9';
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				Piece piece = state.getPiece(row, col);
				if (piece == null) continue;
				switch(piece.getRank()){
				case RAT:
					if(piece.getColor() == Color.RED){
						stringBuffer[3] = Character.forDigit(row, 10);
						stringBuffer[4] = Character.forDigit(col, 10);
					}else {
						stringBuffer[5] = Character.forDigit(row, 10);
						stringBuffer[6] = Character.forDigit(col, 10);						
					}
					break;
				case CAT:
					if(piece.getColor() == Color.RED){
						stringBuffer[7] = Character.forDigit(row, 10);
						stringBuffer[8] = Character.forDigit(col, 10);
					}else {
						stringBuffer[9] = Character.forDigit(row, 10);
						stringBuffer[10] = Character.forDigit(col, 10);						
					}
					break;
				case DOG:
					if(piece.getColor() == Color.RED){
						stringBuffer[11] = Character.forDigit(row, 10);
						stringBuffer[12] = Character.forDigit(col, 10);
					}else {
						stringBuffer[13] = Character.forDigit(row, 10);
						stringBuffer[14] = Character.forDigit(col, 10);						
					}
					break;
				case WOLF:
					if(piece.getColor() == Color.RED){
						stringBuffer[15] = Character.forDigit(row, 10);
						stringBuffer[16] = Character.forDigit(col, 10);
					}else {
						stringBuffer[17] = Character.forDigit(row, 10);
						stringBuffer[18] = Character.forDigit(col, 10);						
					}
					break;
				case LEOPARD:
					if(piece.getColor() == Color.RED){
						stringBuffer[19] = Character.forDigit(row, 10);
						stringBuffer[20] = Character.forDigit(col, 10);
					}else {
						stringBuffer[21] = Character.forDigit(row, 10);
						stringBuffer[22] = Character.forDigit(col, 10);						
					}
					break;
				case TIGER:
					if(piece.getColor() == Color.RED){
						stringBuffer[23] = Character.forDigit(row, 10);
						stringBuffer[24] = Character.forDigit(col, 10);
					}else {
						stringBuffer[25] = Character.forDigit(row, 10);
						stringBuffer[26] = Character.forDigit(col, 10);						
					}
					break;
				case LION:
					if(piece.getColor() == Color.RED){
						stringBuffer[27] = Character.forDigit(row, 10);
						stringBuffer[28] = Character.forDigit(col, 10);
					}else {
						stringBuffer[29] = Character.forDigit(row, 10);
						stringBuffer[30] = Character.forDigit(col, 10);						
					}
					break;
				case ELEPHANT:
					if(piece.getColor() == Color.RED){
						stringBuffer[31] = Character.forDigit(row, 10);
						stringBuffer[32] = Character.forDigit(col, 10);
					}else {
						stringBuffer[33] = Character.forDigit(row, 10);
						stringBuffer[34] = Character.forDigit(col, 10);						
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
		if (serialized == null) return new State();
		if(serialized.length() != 35) return new State();
		Color turn = null;
		switch(serialized.charAt(0)){
		case 'r': turn = Color.RED;
			break;
		case 'b': turn = Color.BLACK;
			break;
		default:
			return new State();
		}
		GameResult gameResult = null;
		if(serialized.charAt(1) != '0' || serialized.charAt(2) != '0'){
			Color winner = null;
			GameResultReason reason= null;
			switch(serialized.charAt(1)){
			case 'r':
				winner = Color.RED;
				break;
			case 'b':
				winner = Color.BLACK;
				break;
			default:
				return new State();
			}
			switch(serialized.charAt(2)){
			case '1':
				reason = GameResultReason.ENTER_DEN;
				break;
			case '2':
				reason = GameResultReason.CAPTURE_ALL_PIECES;
				break;
			default:
				return new State();
			}
			gameResult = new GameResult(winner, reason);
		}
		
		Piece[][] board = new Piece[State.ROWS][State.COLS];
		//red rat
		{
			int row = charToInt(serialized.charAt(3));
			int col = charToInt(serialized.charAt(4));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.RAT, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.RAT, false);
			}
		}
		//black rat
		{
			int row = charToInt(serialized.charAt(5));
			int col = charToInt(serialized.charAt(6));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.RAT, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.RAT, false);
			}
		}
		//red cat
		{
			int row = charToInt(serialized.charAt(7));
			int col = charToInt(serialized.charAt(8));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.CAT, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.CAT, false);
			}
		}
		//black cat
		{
			int row = charToInt(serialized.charAt(9));
			int col = charToInt(serialized.charAt(10));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.CAT, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.CAT, false);
			}
		}
		//red dog
		{
			int row = charToInt(serialized.charAt(11));
			int col = charToInt(serialized.charAt(12));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.DOG, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.DOG, false);
			}
		}
		//black dog
		{
			int row = charToInt(serialized.charAt(13));
			int col = charToInt(serialized.charAt(14));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.DOG, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.DOG, false);
			}
		}
		//red wolf
		{
			int row = charToInt(serialized.charAt(15));
			int col = charToInt(serialized.charAt(16));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.WOLF, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.WOLF, false);
			}
		}
		//black wolf
		{
			int row = charToInt(serialized.charAt(17));
			int col = charToInt(serialized.charAt(18));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.WOLF, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.WOLF, false);
			}
		}
		//red leopard
		{
			int row = charToInt(serialized.charAt(19));
			int col = charToInt(serialized.charAt(20));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.LEOPARD, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.LEOPARD, false);
			}
		}
		//black leopard
		{
			int row = charToInt(serialized.charAt(21));
			int col = charToInt(serialized.charAt(22));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.LEOPARD, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.LEOPARD, false);
			}
		}
		//red tiger
		{
			int row = charToInt(serialized.charAt(23));
			int col = charToInt(serialized.charAt(24));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.TIGER, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.TIGER, false);
			}
		}
		//black tiger
		{
			int row = charToInt(serialized.charAt(25));
			int col = charToInt(serialized.charAt(26));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.TIGER, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.TIGER, false);
			}
		}
		//red lion
		{
			int row = charToInt(serialized.charAt(27));
			int col = charToInt(serialized.charAt(28));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.LION, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.LION, false);
			}
		}
		//black lion
		{
			int row = charToInt(serialized.charAt(29));
			int col = charToInt(serialized.charAt(30));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.LION, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.LION, false);
			}
		}
		//red elephant
		{
			int row = charToInt(serialized.charAt(31));
			int col = charToInt(serialized.charAt(32));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inBlackTrap(row, col))
					board[row][col] = new Piece(Color.RED, PieceRank.ELEPHANT, true);
				else 
					board[row][col] = new Piece(Color.RED, PieceRank.ELEPHANT, false);
			}
		}
		//black elephant
		{
			int row = charToInt(serialized.charAt(33));
			int col = charToInt(serialized.charAt(34));
			if(row == -1 || col == -1) return new State();
			if(row < State.ROWS && col < State.COLS){
				if(State.inRedTrap(row, col))
					board[row][col] = new Piece(Color.BLACK, PieceRank.ELEPHANT, true);
				else 
					board[row][col] = new Piece(Color.BLACK, PieceRank.ELEPHANT, false);
			}
		}
		
		return new State(turn, board, gameResult);
	}
	
	//char to int
	private int charToInt(char c){
		switch(c){
		case '0': return 0;
		case '1': return 1;
		case '2': return 2;
		case '3': return 3;
		case '4': return 4;
		case '5': return 5;
		case '6': return 6;
		case '7': return 7;
		case '8': return 8;
		case '9': return 9;
		default: break;
		}
		return -1;
	}
}
