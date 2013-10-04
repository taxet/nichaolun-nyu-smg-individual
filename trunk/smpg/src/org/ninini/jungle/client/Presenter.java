package org.ninini.jungle.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.Position;
import org.ninini.jungle.shared.State;

import com.google.common.collect.Sets;


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
	private Set<Position> highlightedPositions;
	private List<Move> possibleMoves;
	
	public Presenter(){
		state = new State();
		highlightedPositions = Sets.newHashSet();
		possibleMoves = new LinkedList<Move>();
	}
	
	public State getState(){
		return state;
	}
	public View getView(){
		return view;
	}
	
	public void setView(View view){
		
	}
	public void setState(State state){
		
	}
	public void selectBoard(int row, int col){
		
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
}
