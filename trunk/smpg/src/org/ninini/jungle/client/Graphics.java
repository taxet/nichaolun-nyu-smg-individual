package org.ninini.jungle.client;

import java.util.ArrayList;
import java.util.Set;

import org.ninini.jungle.client.Presenter.View;
import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.Match;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.Player;
import org.ninini.jungle.shared.State;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class Graphics extends Composite implements View {
	private static GameImages gameImages = GWT.create(GameImages.class);
	private static GameSounds gameSound = GWT.create(GameSounds.class);
	private static GraphicsUiBinder uiBinder = GWT.create(GraphicsUiBinder.class);

	interface GraphicsUiBinder extends UiBinder<Widget, Graphics>{
		
	}

	@UiField GameCss css;
	@UiField Label whoseTurn;
	@UiField Label gameStatus;
	@UiField Label loginMessage;
	@UiField Label newGameMessage;
	@UiField AbsolutePanel gamePanel;
	@UiField Grid gameGrid;
	@UiField Image logo;
	@UiField Button loginout;
	@UiField Button quickStart;
	@UiField Button findOpponent;
	@UiField ListBox playersOnline;
	@UiField Label oppoMessage;
	@UiField TextBox oppoEmail;
	@UiField ListBox matchesList;
	@UiField Button loadGameButton;
	private Image[][] board = new Image[State.ROWS][State.COLS];
	private Presenter presenter;
	
	private PieceMovingAnimation animation;
	
	private HandlerRegistration handlerRegistration;
	
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
		
		//initialize other handlers
		//initialize playersOnline changeHandler
		playersOnline.addChangeHandler(new ChangeHandler(){
			//when a email address selected, the oppoEmail will set text to this email
			@Override
			public void onChange(ChangeEvent event) {
				int selected = playersOnline.getSelectedIndex();
				if(selected >= 0)
					oppoEmail.setText(playersOnline.getValue(selected));
			}
			
		});
		
	}
	
	//set Button Click Handler
	//quick start button
	@UiHandler("quickStart")
	void quickStartClickHandler(ClickEvent e){
		if(!presenter.ifLogin()){
			Window.alert("Please login first.");
		}else{
			presenter.findOpponend();
		}
	}
	
	//find opponent button
	@UiHandler("findOpponent")
	void findOpponentClickHandler(ClickEvent e){
		if(!presenter.ifLogin()){
			Window.alert("Please login first.");
		}else{
			presenter.findOpponentWith(oppoEmail.getText());
		}
	}
	
	//load game button
	@UiHandler("loadGameButton")
	void loadGameClickHander(ClickEvent e){
		if(!presenter.ifLogin()){
			Window.alert("Please login first.");
		}else{
			int selected = matchesList.getSelectedIndex();
			if(selected == -1){//not selected
			}else{//select a game
				Match selectedMatch = presenter.getMatches().get(selected);
				presenter.loadGame(selectedMatch.getMatchId());
				if(selectedMatch.ifFinished())
					Window.alert("This game is finished.");
			}
		}		
	}
	
	//refresh playersOnline list
	public void refreshPlayersOnline(Set<Player> players){
		playersOnline.clear();
		for(Player p : players){
			if(!p.getEmail().equals(presenter.getUserId())){//not the player
				playersOnline.addItem(p.getEmail());
			}
		}
	}
	//refresh matches list
	@Override
	public void refresheMatches(ArrayList<Match> matches){
		matches.clear();
		for(Match m : matches){
			String msg = ""+m.getMatchId();
			matchesList.addItem(msg);
		}
	}
	
	//initialize drag&drop handlers of every image in board
	public void initDndHandlers(){
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
	
	//add click handler to loginout Button
	public void setLogButton(final String url){
		if(handlerRegistration != null) handlerRegistration.removeHandler();
		if(presenter.ifLogin()){//Sign out
			handlerRegistration = loginout.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					// pop up log out panel
					LogoutPanel logoutPanel = new LogoutPanel(url);
					logoutPanel.center();
					
				}
				
			});
			loginout.setText("Sign Out");
		}else{//log in
			handlerRegistration = loginout.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					// pop up log in panel
					LoginPanel loginPanel = new LoginPanel(url);
					loginPanel.center();
				}
				
			});			
			loginout.setText("Log In");
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
	public void setWhoseTurn(Color color, boolean myTurn) {
		if(color == Color.BLACK){
			if(myTurn)
				whoseTurn.setText("Your Turn");
			else
				whoseTurn.setText("Black's Turn");
			whoseTurn.setStyleName(css.blackTurn());
		}else if(color == Color.RED){
			if(myTurn)
				whoseTurn.setText("Your Turn");
			else
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
		
		//enable findopponent button
		quickStart.setEnabled(true);
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
			else if(presenter.getState().inPlayerTrap(move.getTo()))
				audio.addSource(gameSound.enterTrapSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
			else 
				audio.addSource(gameSound.moveSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
		}
		animation = new PieceMovingAnimation(startImage, endImage, pieceImage, blankImageRes, audio);
		animation.run(1000);
	}
	
	@Override
	public void setLoginMessage(String msg){
		loginMessage.setText(msg);
	}

	@Override
	public void newGameMessage(String message) {
		newGameMessage.setText(message);
		Audio audio = null;
		if(Audio.isSupported()){
			audio = Audio.createIfSupported();
			audio.addSource(gameSound.bellSound().getSafeUri().asString(), AudioElement.TYPE_OGG);
			audio.play();
		}
	}

}
