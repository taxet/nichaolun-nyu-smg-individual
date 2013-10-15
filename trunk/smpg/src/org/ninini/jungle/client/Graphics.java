package org.ninini.jungle.client;

import org.ninini.jungle.client.Presenter.View;
import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.State;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	@UiField Label whoseTurn;
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
				final Image img = new Image();
				board[row][col] = img;
				final int rowSelected = row;
				final int colSelected = col;
				img.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event){
						presenter.selectBoard(rowSelected, colSelected);
					}
				});
				img.setWidth("100%");
				gameGrid.setWidget(row, col, img);
			}
		}
	}
	
	public Presenter getPresenter(){
		return presenter;
	}
	
	@Override
	public void setPiece(int row, int col, Piece piece) {
		if(piece == null){
			setBoardWithoutPiece(row, col);
		}else switch(piece.getRank()){
		case RAT :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redRat());
			}else{
				board[row][col].setResource(gameImages.blackRat());
			}
			break;
		case CAT :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redCat());
			}else{
				board[row][col].setResource(gameImages.blackCat());
			}
			break;
		case DOG :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redDog());
			}else{
				board[row][col].setResource(gameImages.blackDog());
			}
			break;
		case WOLF :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redWolf());
			}else{
				board[row][col].setResource(gameImages.blackWolf());
			}
			break;
		case LEOPARD :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redLeopard());
			}else{
				board[row][col].setResource(gameImages.blackLeopard());
			}
			break;
		case TIGER :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redTiger());
			}else{
				board[row][col].setResource(gameImages.blackTiger());
			}
			break;
		case LION :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redLion());
			}else{
				board[row][col].setResource(gameImages.blackLion());
			}
			break;
		case ELEPHANT :
			if(piece.getColor() == Color.RED){
				board[row][col].setResource(gameImages.redElephant());
			}else{
				board[row][col].setResource(gameImages.blackElephant());
			}
			break;
		default:
			break;
		}
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
			whoseTurn.setText("Black's Turn");
		}else if(color == Color.RED){
			whoseTurn.setText("Red's Turn");
		}else {
			whoseTurn.setText("????'s Turn");
		}
	}

	@Override
	public void setGameResult(GameResult gameResult) {
		if(gameResult == null) return;
		if(gameResult.getWinner() == Color.BLACK){
			whoseTurn.setText("Black Win");
		}else if(gameResult.getWinner() == Color.RED){
			whoseTurn.setText("Red Win");
		}else {
			whoseTurn.setText("???? Win");
		}
	}
	
	//Set a block in board without piece on it
	private void setBoardWithoutPiece(int row, int col){
		//river
		if (State.inRiver(row, col)){
			board[row][col].setResource(gameImages.riverTile());
		}
		//den
		else if ( col == 3 && row == 0 ){
			board[row][col].setResource(gameImages.redDen());
		}
		else if ( col == 3 && row == 8 ){
			board[row][col].setResource(gameImages.blackDen());
		}
		//trap
		else if(State.inRedTrap(row, col)){
			board[row][col].setResource(gameImages.redTrap());
		}
		else if (State.inBlackTrap(row, col)){
			board[row][col].setResource(gameImages.blackTrap());			
		}
		//else
		else{
			board[row][col].setResource(gameImages.normalTile());
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setStatus(String string) {
		gameStatus.setText(string);
	}
	
	@Override
	public void setSelected(int row, int col, boolean selected){
		Element element = board[row][col].getElement();
		if(selected){
			element.setClassName(css.selected());
		}else{
			element.removeClassName(css.selected());
		}
	}

}
