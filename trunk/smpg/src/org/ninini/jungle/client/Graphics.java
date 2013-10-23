package org.ninini.jungle.client;

import org.ninini.jungle.client.Presenter.View;
import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.State;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Graphics extends Composite implements View {
	private static GameImages gameImages = GWT.create(GameImages.class);
	private static GameSounds gameSound = GWT.create(GameSounds.class);
	private static GraphicsUiBinder uiBinder = GWT.create(GraphicsUiBinder.class);

	interface GraphicsUiBinder extends UiBinder<Widget, Graphics>{
		
	}

	@UiField GameCss css;
	@UiField Label whoseTurn;
	@UiField Label gameStatus;
	@UiField AbsolutePanel gamePanel;
	@UiField Grid gameGrid;
	@UiField Image logo;
	private Image[][] board = new Image[State.ROWS][State.COLS];
	private Presenter presenter;
	
	private PieceMovingAnimation animation;
	
	public Graphics(){		
		initWidget(uiBinder.createAndBindUi(this));
		logo.setResource(gameImages.logo());
		
		gamePanel.setSize("496px", "636px");
		gameGrid.resize(State.ROWS, State.COLS);
		gameGrid.setCellPadding(0);
		gameGrid.setCellSpacing(0);
		gameGrid.setBorderWidth(0);
		gamePanel.setWidgetPosition(gameGrid, 3, 3);
		
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				final Image img = new Image();
				board[row][col] = img;
				final int rowSelected = row;
				final int colSelected = col;
				
				//add click handler
				img.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event){
						presenter.selectBoard(rowSelected, colSelected);
					}
				});
				img.setSize("70px", "70px");
				gameGrid.setWidget(row, col, img);
				//set drag and drop handler
				/*img.getElement().setDraggable(Element.DRAGGABLE_TRUE);
				img.addDragStartHandler(new DragStartHandler(){
					@Override
					public void onDragStart(DragStartEvent event) {
						event.setData("text", "dragging");
						presenter.getView().setStatus("DragStart at ("+rowSelected+","+colSelected+")");
						presenter.dragStartEvent(rowSelected, colSelected);
					}
				});
				img.addDragOverHandler(new DragOverHandler(){
					@Override
					public void onDragOver(DragOverEvent event) {
						presenter.dragOverEvent(rowSelected, colSelected);							
					}						
				});
				img.addDropHandler(new DropHandler(){
					@Override
					public void onDrop(DropEvent event) {
						presenter.dropEvent(rowSelected, colSelected);							
					}
				});*/
			}
		}
	}
	
	//initialize click handlers and drag&drop handlers of every image in board
	public void initHandlers(){
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				final int rowSelected = row;
				final int colSelected = col;
				//add click handler
				/*board[row][col].addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event){
						presenter.selectBoard(rowSelected, colSelected);
					}
				});*/
				
				//set drag and drop handler
				board[row][col].getElement().setDraggable(Element.DRAGGABLE_TRUE);
				board[row][col].addDragStartHandler(new DragStartHandler(){
					@Override
					public void onDragStart(DragStartEvent event) {
						event.setData("text", "dragging");
						presenter.dragStartEvent(rowSelected, colSelected);
					}
				});
				board[row][col].addDragOverHandler(new DragOverHandler(){
					@Override
					public void onDragOver(DragOverEvent event) {
						presenter.dragOverEvent(rowSelected, colSelected);							
					}						
				});
				board[row][col].addDropHandler(new DropHandler(){
					@Override
					public void onDrop(DropEvent event) {
						presenter.dropEvent(rowSelected, colSelected);							
					}
				});
			}
		}
	}
	
	public Presenter getPresenter(){
		return presenter;
	}
	
	public Image getBoard(int row, int col){
		return board[row][col];
	}
	
	@Override
	public void setPiece(int row, int col, Piece piece) {
		if(piece == null){
			board[row][col].setResource(getBoardWithoutPiece(row, col));
		}else board[row][col].setResource(getPieceImage(piece));
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
			whoseTurn.setStyleName(css.blackTurn());
		}else if(color == Color.RED){
			whoseTurn.setText("Red's Turn");
			whoseTurn.setStyleName(css.redTurn());
		}else {
			whoseTurn.setText("????'s Turn");
		}
	}

	@Override
	public void setGameResult(GameResult gameResult) {
		if(gameResult == null) return;
		if(gameResult.getWinner() == Color.BLACK){
			whoseTurn.setText("Black Win");
			whoseTurn.setStyleName(css.blackTurn());
		}else if(gameResult.getWinner() == Color.RED){
			whoseTurn.setText("Red Win");
			whoseTurn.setStyleName(css.redTurn());
		}else {
			whoseTurn.setText("???? Win");
		}
		
		//play victory sound
		Audio audio;
		if(Audio.isSupported()){
			audio = Audio.createIfSupported();
		} else return;
		audio.addSource(gameSound.victorySound().getSafeUri().asString(), AudioElement.TYPE_OGG);
		audio.play();
	}
	
	//Get a block in board without piece on it
	private ImageResource getBoardWithoutPiece(int row, int col){
		//river
		if (State.inRiver(row, col)){
			return gameImages.riverTile();
		}
		//den
		else if ( col == 3 && row == 0 ){
			return gameImages.redDen();
		}
		else if ( col == 3 && row == 8 ){
			return gameImages.blackDen();
		}
		//trap
		else if(State.inRedTrap(row, col)){
			return gameImages.redTrap();
		}
		else if (State.inBlackTrap(row, col)){
			return gameImages.blackTrap();			
		}
		//else
		else{
			return gameImages.normalTile();
		}
	}
	
	private ImageResource getPieceImage(Piece piece){
		switch(piece.getRank()){
		case RAT :
			if(piece.getColor() == Color.RED){
				return gameImages.redRat();
			}else{
				return gameImages.blackRat();
			}
		case CAT :
			if(piece.getColor() == Color.RED){
				return gameImages.redCat();
			}else{
				return gameImages.blackCat();
			}
		case DOG :
			if(piece.getColor() == Color.RED){
				return gameImages.redDog();
			}else{
				return gameImages.blackDog();
			}
		case WOLF :
			if(piece.getColor() == Color.RED){
				return gameImages.redWolf();
			}else{
				return gameImages.blackWolf();
			}
		case LEOPARD :
			if(piece.getColor() == Color.RED){
				return gameImages.redLeopard();
			}else{
				return gameImages.blackLeopard();
			}
		case TIGER :
			if(piece.getColor() == Color.RED){
				return gameImages.redTiger();
			}else{
				return gameImages.blackTiger();
			}
		case LION :
			if(piece.getColor() == Color.RED){
				return gameImages.redLion();
			}else{
				return gameImages.blackLion();
			}
		case ELEPHANT :
			if(piece.getColor() == Color.RED){
				return gameImages.redElephant();
			}else{
				return gameImages.blackElephant();
			}
		default:
			return null;
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

	@Override
	public void playSoundWhenSelectPiece(Piece piece) {
		Audio audio;
		if(Audio.isSupported()){
			audio = Audio.createIfSupported();
		} else return;
		switch(piece.getRank()){
		case RAT:
			break;
		case CAT: audio.addSource(gameSound.catSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
				audio.play();
			break;
		case DOG: audio.addSource(gameSound.dogSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
				audio.play();
			break;
		case WOLF: audio.addSource(gameSound.wolfSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
				audio.play();
			break;
		case LEOPARD:
			break;
		case LION:
			break;
		case TIGER:
			break;
		case ELEPHANT:
			break;
		default:
			
		}
	}

	@Override
	public void playAnimation(Move move,Piece startPiece, boolean capture) {
		Image startImage = board[move.getFrom().getRow()][move.getFrom().getCol()];
		Image endImage = board[move.getTo().getRow()][move.getTo().getCol()];
		ImageResource pieceImage = getPieceImage(startPiece);
		ImageResource blankImageRes = getBoardWithoutPiece(move.getFrom().getRow(),move.getFrom().getCol());
		Audio audio = null;
		if(Audio.isSupported()){
			audio = Audio.createIfSupported();
			if(capture)
				audio.addSource(gameSound.captureSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
			else if(State.inRiver(move.getTo()))
				audio.addSource(gameSound.ennterWaterSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
			else if(State.inRiver(move.getFrom()) && !State.inRiver(move.getTo()))
				audio.addSource(gameSound.landingSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
			else if(presenter.getState().inOpponentTrap(move.getTo()))
				audio.addSource(gameSound.enterTrapSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
			else 
				audio.addSource(gameSound.moveSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
		}
		animation = new PieceMovingAnimation(startImage, endImage, pieceImage, blankImageRes, audio);
		animation.run(1000);
	}

}
