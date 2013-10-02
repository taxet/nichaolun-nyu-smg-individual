package org.ninini.jungle.client.games;

import org.ninini.jungle.Color;
import org.ninini.jungle.Piece;
import org.ninini.jungle.GameResult;
import org.ninini.jungle.State;


public class Presenter {
	public interface View{
		void setPiece(int row, int col, Piece piece);
		
		void setHighlighted(int row, int col, boolean highlighted);
		
		void setWhoseTurn(Color color);
		
		void setGameResult(GameResult gameResult);
	}
	
	private State state;
	private View view;
	
	public Presenter(View graphics){
		this.view = graphics;
		state = new State();
	}
	
	public State getState(){
		return state;
	}
	public View getView(){
		return view;
	}
}
