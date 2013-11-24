package org.ninini.jungle.client;

import org.ninini.jungle.client.Presenter.View;
import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.FbInfo;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.State;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtfb.client.JSOModel;
import com.gwtfb.sdk.FBCore;
import com.gwtfb.sdk.FBXfbml;

public class Graphics extends Composite implements View {
	private static GameImages gameImages = GWT.create(GameImages.class);
	private static GameSounds gameSound = GWT.create(GameSounds.class);
	public static GameMessage gameMessage = GWT.create(GameMessage.class);
	private static GraphicsUiBinder uiBinder = GWT.create(GraphicsUiBinder.class);

	interface GraphicsUiBinder extends UiBinder<Widget, Graphics>{
		
	}

	@UiField GameCss css;
	@UiField Label title;
	@UiField Label whoseTurn;
	@UiField Label gameStatus;
	StringBuffer logs = new StringBuffer();
	@UiField Image avatar;
	@UiField Label gameDate;
	@UiField Label loginMessage;
	@UiField Label newGameMessage;
	@UiField Label yourRank;
	@UiField AbsolutePanel gamePanel;
	@UiField Grid gameGrid;
	@UiField Image logo;
	@UiField HorizontalPanel loginout;
	//@UiField Button quickStart;
	//@UiField Button findOpponent;
	@UiField Label oppoMessage;
	@UiField TextBox oppoEmail;
	/*@UiField ListBox playersOnline;
	@UiField ListBox matchesList;
	@UiField Button loadGameButton;
	@UiField Label playersListTitle;
	@UiField Label matchListTitle;*/
	@UiField Label friend;
	@UiField VerticalPanel friendList;
	@UiField Button playWithAi;
	@UiField HorizontalPanel likeButton;
	@UiField Button invite;
	private Image[][] board = new Image[State.ROWS][State.COLS];
	private Presenter presenter;
	
	private PieceMovingAnimation animation;
	
	//private HandlerRegistration handlerRegistration;
	
	
	public Graphics(){		
		initWidget(uiBinder.createAndBindUi(this));
		logo.setResource(gameImages.logo());
		
		gamePanel.setSize("496px", "636px");
		gameGrid.resize(State.ROWS, State.COLS);
		gameGrid.setCellPadding(0);
		gameGrid.setCellSpacing(0);
		gameGrid.setBorderWidth(0);
		gamePanel.setWidgetPosition(gameGrid, 3, 3);
		
		//initial text
		title.setText(Graphics.gameMessage.gameName());
		whoseTurn.setText(Graphics.gameMessage.whoseTurn());
		loginMessage.setText(Graphics.gameMessage.login());
		oppoMessage.setText(Graphics.gameMessage.findGame());
		//quickStart.setText(Graphics.gameMessage.quickStart());
		//findOpponent.setText(Graphics.gameMessage.matchWith());
		//loadGameButton.setText(Graphics.gameMessage.loadGame());
		//playersListTitle.setText(Graphics.gameMessage.onlinePlayers());
		//matchListTitle.setText(Graphics.gameMessage.yourMatches());
		friend.setText(Graphics.gameMessage.yourFriends());
		yourRank.setText(Graphics.gameMessage.yourRank(0,0));
		playWithAi.setText(Graphics.gameMessage.withAi());
		playWithAi.setTitle(Graphics.gameMessage.withAiNote());
		avatar.setPixelSize(50, 50);
		invite.setText(Graphics.gameMessage.invite());
		
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
		/*playersOnline.addChangeHandler(new ChangeHandler(){
			//when a email address selected, the oppoEmail will set text to this email
			@Override
			public void onChange(ChangeEvent event) {
				int selected = playersOnline.getSelectedIndex();
				if(selected >= 0)
					oppoEmail.setText(playersOnline.getValue(selected));
			}
			
		});*/
		
		//add facebook like button
		HTML fbLike = new HTML("<div class=\"fb-like\" data-href=\"http://10.socialgamescourse.appspot.com\" data-layout=\"standard\" data-action=\"like\" data-show-faces=\"true\" data-share=\"true\"></div>");
		likeButton.add(fbLike);
	}
	
	//set Button Click Handler
	//quick start button
	/*@UiHandler("quickStart")
	void quickStartClickHandler(ClickEvent e){
		if(!presenter.ifLogin()){
			Window.alert(Graphics.gameMessage.loginAlert());
		}else{
			presenter.findOpponend();
		}
	}
	
	//find opponent button
	@UiHandler("findOpponent")
	void findOpponentClickHandler(ClickEvent e){
		if(!presenter.ifLogin()){
			Window.alert(Graphics.gameMessage.loginAlert());
		}else{
			presenter.findOpponentWith(oppoEmail.getText());
		}
	}
	
	//load game button
	@UiHandler("loadGameButton")
	void loadGameClickHander(ClickEvent e){
		if(!presenter.ifLogin()){
			Window.alert(Graphics.gameMessage.loginAlert());
		}else{
			int selected = matchesList.getSelectedIndex();
			if(selected == -1){//not selected
			}else{//select a game
				Long matchId = Long.parseLong(matchesList.getValue(selected));
				setStatus(""+matchId);
				presenter.loadGame(matchId);
				if(presenter.getCurrentMatch() != null && presenter.getCurrentMatch().ifFinished())
					Window.alert(Graphics.gameMessage.gameFinishAlert());
			}
		}		
	}*/
	
	//play with AI button
	@UiHandler("playWithAi")
	void playWithAiClickHander(ClickEvent e){
		presenter.playWithAi();
	}
	
	//refresh playersOnline list
	/*public void refreshPlayersOnline(Set<Player> players){
		playersOnline.clear();
		int itemNo = 0;
		for(Player p : players){
			if(!p.getUID().equals(presenter.getUserId())){//not the player
				playersOnline.addItem(p.getUID());
				Element.as(playersOnline.getElement().getChild(itemNo)).setTitle(Graphics.gameMessage.rank(p.getRank()));
			}
		}
	}*/
	//refresh matches list
	/*@Override
	public void refresheMatches(ArrayList<Match> matches){
		matchesList.clear();
		for(Match m : matches){
			String msg = ""+m.getMatchId();
			matchesList.addItem(msg);
		}
	}*/
	@UiHandler("invite")
	void inviteClickHandler(ClickEvent e){
		final FBCore fbCore = GWT.create(FBCore.class);
		FbRequest request = (FbRequest)JavaScriptObject.createObject().cast();
		request.setMethod("apprequests");
		request.setTitle("Jungle Game");
		request.setMessage("I want to play jungle game with you.");
		fbCore.ui(request, new AsyncCallback<JavaScriptObject>(){

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Invite failed");
			}

			@Override
			public void onSuccess(JavaScriptObject result) {
				JSOModel jsm = result.cast();
				Window.alert("Invite Success");
			}
			
		});
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
	/*public void setLogButton(){
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
			loginout.setText(Graphics.gameMessage.signOut());
		}else{//log in
			handlerRegistration = loginout.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					// pop up log in panel
					LoginPanel loginPanel = new LoginPanel(url);
					loginPanel.center();
				}
				
			});			
			loginout.setText(Graphics.gameMessage.signIn());
		}
	}*/
	public void setLoginButton(){
		if(!presenter.ifLogin()){//if logged in, do nothing, else loging in
			loginout.clear();
			HTML fbLoginButton = new HTML("<fb:login-button width=\"200\" onlogin=\"window.location.reload()\"></fb:login-button>");
			loginout.add(fbLoginButton);
			FBXfbml.parse(loginout);
		}
	}
	public void setLogoutButton(Widget w){
		if(presenter.ifLogin()){//if not logged in, do nothing
			loginout.clear();
			loginout.add(w);
		}		
	}

	
	public Presenter getPresenter(){
		return presenter;
	}
	
	public Image getBoard(int row, int col){
		return board[row][col];
	}
	
	public void setAvatar(String avatarUrl){
		avatar.setUrl(avatarUrl);
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
				whoseTurn.setText(Graphics.gameMessage.yourTurn());
			else
				whoseTurn.setText(Graphics.gameMessage.blackTurn());
			whoseTurn.setStyleName(css.blackTurn());
		}else if(color == Color.RED){
			if(myTurn)
				whoseTurn.setText(Graphics.gameMessage.yourTurn());
			else
				whoseTurn.setText(Graphics.gameMessage.redTurn());
			whoseTurn.setStyleName(css.redTurn());
		}else {
			whoseTurn.setText("????'s Turn");
		}
	}

	@Override
	public void setGameResult(GameResult gameResult) {
		if(gameResult == null) return;
		if(gameResult.getWinner() == Color.BLACK){
			whoseTurn.setText(Graphics.gameMessage.blackWin());
			whoseTurn.setStyleName(css.blackTurn());
		}else if(gameResult.getWinner() == Color.RED){
			whoseTurn.setText(Graphics.gameMessage.redWin());
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
		//quickStart.setEnabled(true);
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
		logs.append(string+" \n");
		gameStatus.setText(logs.toString());
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
	
	@Override
	public void setOppoMessage(String msg){
		oppoMessage.setText(msg);
	}

	@Override
	public void setMatchDate(String str) {
		gameDate.setText(str);
	}
	
	@Override
	public void setRank(int rank, int rd){
		this.yourRank.setText(Graphics.gameMessage.yourRank(rank-2*rd, rank+2*rd));
	}

	@Override
	public void clearFriendList() {
		friendList.clear();
	}

	@Override
	public void addFriend(final FbInfo friend) {
		// TODO Auto-generated method stub
		HorizontalPanel friendItem = new HorizontalPanel();
		friendItem.setSize("350px", "50px");
		Image avatar = new Image();
		avatar.setSize("50px", "50px");
		avatar.setUrl(friend.getAvatarUrl());
		friendItem.add(avatar);
		VerticalPanel info = new VerticalPanel();
		info.setSize("300px", "50px");
		Label name = new Label();
		name.setText(friend.getName());
		Label rank = new Label();
		rank.setText(gameMessage.rank(friend.getRank()-2*friend.getRd(), friend.getRank()+2*friend.getRd()));
		info.add(name);
		info.add(rank);
		friendItem.add(info);
		final FocusPanel wrapper = new FocusPanel();
		wrapper.setSize("350px", "50px");
		wrapper.add(friendItem);
		wrapper.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				presenter.playWith(friend.getFbId());
			}			
		});
		wrapper.addMouseOverHandler(new MouseOverHandler(){

			@Override
			public void onMouseOver(MouseOverEvent event) {
				wrapper.setStyleName(css.highlighted());
			}
			
		});
		wrapper.addMouseOutHandler(new MouseOutHandler(){

			@Override
			public void onMouseOut(MouseOutEvent event) {
				wrapper.removeStyleName(css.highlighted());
			}
			
		});
		friendList.add(wrapper);
	}

}
