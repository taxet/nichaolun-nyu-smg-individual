package org.ninini.jungle.client.games;

import org.ninini.jungle.Color;
import org.ninini.jungle.GameResult;
import org.ninini.jungle.Piece;
import org.ninini.jungle.State;
import org.ninini.jungle.client.games.Presenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Graphics extends Composite implements View {
	private static GameImages gameImages = GWT.create(GameImages.class);
	private static GraphicsUiBinder uiBinder = GWT.create(GraphicsUiBinder.class);

	interface GraphicsUiBinder extends UiBinder<Widget, Graphics>{
		
	}

	@UiField GameCss css;
	@UiField Label gameStatus;
	@UiField Grid gameGrid;
	private Image[][] board = new Image[State.ROWS][State.COLS];
	private Presenter presenter;
	
	public Graphics(){
		initWidget(uiBinder.createAndBindUi(this));
		gameGrid.resize(State.ROWS, State.COLS);
		gameGrid.setCellPadding(0);
		gameGrid.setCellSpacing(0);
		gameGrid.setBorderWidth(0);
		
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				/*final Image image = new Image();
				image.setWidth("100%");
				//river
				if (State.inRiver(row, col)){
					image.setResource(gameImages.riverTile());
				}
				//den
				else if ( col == 3 && (row == 0 || row == 8)){
					image.setResource(gameImages.denTile());
				}
				//trap
				else if(State.inBlackTrap(row, col) || State.inRedTrap(row, col)){
					image.setResource(gameImages.trapTile());
				}
				//else
				else{
					image.setResource(gameImages.normalTile());
				}
				gameGrid.setWidget(row, col, image);*/
			}
		}
		
	}
	
	public Presenter getPresenter(){
		return presenter;
	}
	
	@Override
	public void setPiece(int row, int col, Piece piece) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHighlighted(int row, int col, boolean highlighted) {
		Element element = board[row][col].getElement();
		if(highlighted){
			element.setClassName(css.highlighted());
		}else{
			element.removeClassName(css.highlighted());
		}
	}

	@Override
	public void setWhoseTurn(Color color) {
		if(color == Color.BLACK){
			gameStatus.setText("Black's Turn");
		}else if(color == Color.RED){
			gameStatus.setText("Red's Turn");
		}else {
			gameStatus.setText("????'s Turn");
		}
	}

	@Override
	public void setGameResult(GameResult gameResult) {
		if(gameResult.getWinner() == Color.BLACK){
			gameStatus.setText("Black Win");
		}else if(gameResult.getWinner() == Color.RED){
			gameStatus.setText("Red Win");
		}else {
			gameStatus.setText("???? Win");
		}
	}

}
