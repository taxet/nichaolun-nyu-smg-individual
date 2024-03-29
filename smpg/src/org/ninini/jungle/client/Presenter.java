package org.ninini.jungle.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.ninini.jungle.shared.AlphaBetaPruning;
import org.ninini.jungle.shared.Color;
import org.ninini.jungle.shared.FbInfo;
import org.ninini.jungle.shared.GameResult;
import org.ninini.jungle.shared.GameResultReason;
import org.ninini.jungle.shared.Heuristic;
import org.ninini.jungle.shared.IllegalMove;
import org.ninini.jungle.shared.Match;
import org.ninini.jungle.shared.Move;
import org.ninini.jungle.shared.Piece;
import org.ninini.jungle.shared.PieceRank;
import org.ninini.jungle.shared.Position;
import org.ninini.jungle.shared.State;
import org.ninini.jungle.shared.StateChanger;
import org.ninini.jungle.shared.StateChangerImpl;
import org.ninini.jungle.shared.StateExplorer;
import org.ninini.jungle.shared.StateExplorerImpl;
import org.ninini.jungle.shared.Timer;

import com.google.gwt.appengine.channel.client.ChannelError;
import com.google.gwt.appengine.channel.client.ChannelFactoryImpl;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
//import com.google.gwt.event.logical.shared.ValueChangeEvent;
//import com.google.gwt.event.logical.shared.ValueChangeHandler;
//import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.HasRpcToken;


public class Presenter {
	public interface View{
		//Render the piece at a cell
		void setPiece(int row, int col, Piece piece);
		//Turns the highlighting on or off at this shell
		void setHighlighted(int row, int col, boolean highlighted);
		//Indicate whose turn
		void setWhoseTurn(Color color, boolean myTurn);
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
		//set Login message
		void setLoginMessage(String msg);
		//new game message
		void newGameMessage(String message);
		//refresh matches list
		//void refresheMatches(ArrayList<Match> matches);
		//set opponent message
		void setOppoMessage(String msg);
		//set match date message
		void setMatchDate(String str);
		//set rank
		void setRank(int rank, int id);
		//clear friendList
		void clearFriendList();
		//add an item to FriendList
		void addFriend(FbInfo friend);
	}
	

	private View view;
	private State state;
	private StateChanger stateChanger;
	private StateExplorer stateExplorer;
	private Set<Position> highlightedPositions;
	private Set<Move> possibleMoves;
	private Position selected;
	private String userId = "";
	private Color myColor;
	private boolean login = false;
	private Match currentMatch;
	private boolean actionFlag = false;
	private boolean withAi = false;
	private AlphaBetaPruning abp = new AlphaBetaPruning(new Heuristic());
	
	private JungleServiceAsync jungleServiceAsync;

	public Presenter(){
		state = new State();
		highlightedPositions = new HashSet<Position>();
		possibleMoves = new HashSet<Move>();
		stateChanger = new StateChangerImpl();
		stateExplorer = new StateExplorerImpl();
		selected = null;
		//initializeHistory();
	}
	public Presenter(View view){
		state = new State();
		highlightedPositions = new HashSet<Position>();
		possibleMoves = new HashSet<Move>();
		stateChanger = new StateChangerImpl();
		stateExplorer = new StateExplorerImpl();
		selected = null;
		setView(view);
		//initializeHistory();
	}
	
	public State getState(){
		return state;
	}
	public View getView(){
		return view;
	}
	public String getUserId(){
		return userId;
	}
	public boolean ifLogin(){
		return login;
	}
	public Match getCurrentMatch(){
		return currentMatch;
	}
	public String getOpponentId(){
		if(currentMatch == null) return "";
		if(userId.equals(currentMatch.getBlackPlayer()))
			return currentMatch.getRedPlayer();
		if(userId.equals(currentMatch.getRedPlayer()))
			return currentMatch.getBlackPlayer();
		return "";
	}
	
	public void setView(View view){
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setState(State state){
		this.state = state;
		view.setGameResult(state.getGameResult());
		view.setWhoseTurn(state.getTurn(), myColor == state.getTurn());
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				view.setPiece(row, col, state.getPiece(row, col));
			}
		}
	}
	
	public void setUserId(String userId){
		this.userId = userId;
	}
	public void logIn(String userId){
		login = true;
		setUserId(userId);
	}
	public void logOut(){
		login = false;
		userId = "";
	}
	public void setCurrentMatch(Match match){
		currentMatch = match;
		String oppoId = "";
		if(userId.equals(currentMatch.getBlackPlayer())){
			myColor = Color.BLACK;
			oppoId = currentMatch.getRedPlayer();
		}
		if(userId.equals(currentMatch.getRedPlayer())){
			myColor = Color.RED;
			oppoId = currentMatch.getBlackPlayer();
		}
		setState(unserializeState(currentMatch.getState()));
		view.setOppoMessage(Graphics.gameMessage.oppoMsg(oppoId, currentMatch.getMatchId()));
		String dateMsg = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG).format(currentMatch.getStartDate());
		view.setMatchDate(Graphics.gameMessage.matchBeginTime(dateMsg));
	}
	
	public void selectBoard(int row, int col){
		//if(currentMatch == null) return;
		if(state.getGameResult() != null) return;
		if(myColor == null || !(myColor == state.getTurn())) return;
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

	//start drag event
	public void dragStartEvent(int row, int col){
		//if(currentMatch == null) return;
		if(state.getGameResult() != null) return;
		if(myColor == null || !(myColor == state.getTurn())) return;
		Position thisPosition = new Position(row, col);
		if(!stateExplorer.getPossibleStartPositions(state).contains(thisPosition)) return;//not a possible start position
		newPieceSelected(thisPosition);
	}
	//drag over event
	public void dragOverEvent(int row, int col){
		//if(currentMatch == null) return;
		if(state.getGameResult() != null) return;
		if(myColor == null || !(myColor == state.getTurn())) return;
		Position thisPosition = new Position(row, col);
		//make a possible moves position highlighted but not selected
		for(Move m : possibleMoves){
			view.setSelected(m.getTo().getRow(), m.getTo().getCol(), false);
			view.setHighlighted(m.getTo().getRow(), m.getTo().getCol(), true);
			//make target position selected if it is in possible moves
			if(m.getTo().equals(thisPosition))
				view.setSelected(m.getTo().getRow(), m.getTo().getCol(), true);
		}
	}
	//drop event
	public void dropEvent(int row, int col){
		//if(currentMatch == null) return;
		if(state.getGameResult() != null) return;
		if(myColor == null || !(myColor == state.getTurn())) return;
		Position thisPosition = new Position(row, col);
		Move move = new Move(selected, thisPosition);
		if(possibleMoves.contains(move)){//possible move, try move
			try{
				view.setSelected(row, col, false);
				stateChanger.makeMove(state, move);
				//make changes on graphics
				view.setSelected(selected.getRow(), selected.getCol(), false);
				selected = null;
				showState();
			}catch(IllegalMove e){
				
			}
		}
		//else do nothing.
	}
	
	//Renders the state
	public void showState(){
		view.setWhoseTurn(state.getTurn(), myColor == state.getTurn());
		view.setGameResult(state.getGameResult());
		//Render the cells
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				view.setHighlighted(row, col, false);
				view.setSelected(row, col, false);
				view.setPiece(row, col, state.getPiece(row, col));
			}
		}
		//clear hightlighted
		clearSets();
		
		//send to server
		if(currentMatch != null){
			actionFlag = true;
			jungleServiceAsync.updateState(serializeState(state), userId, currentMatch.getMatchId(),
					new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					view.setStatus(Graphics.gameMessage.callbackFail());
				}

				@Override
				public void onSuccess(Void result) {
					//nothing
				}
			});
		
			//refresh match
			currentMatch.setState(serializeState(state));
		}
		
		//ai move
		if(withAi && !state.getTurn().equals(myColor))
			aiMove();
			
		//If game is over
		if(state.getGameResult() != null){
			currentMatch = null;
			withAi = false;
			//socket.close();
		}		
		
	}
	
	//initialize channel to Server
	public void initMuiltiPlayer(final LoginInfo loginInfo, XsrfToken xsrfToken){
		jungleServiceAsync = GWT.create(JungleService.class);
		((HasRpcToken) jungleServiceAsync).setRpcToken(xsrfToken);
		Socket socket = new ChannelFactoryImpl().createChannel(loginInfo.getToken()).open(new SocketListener(){

			@Override
			public void onOpen() {
			}

			@Override
			public void onMessage(String message) {
				//TODO debug
				if(message.toCharArray()[0]=='`'){
					view.setStatus("message Receive: "+message);
					return;
				}
				/*if(!gameStart){//looking for another player
					String oppoName = message.substring(0, message.length() - 1);
					char color = message.toCharArray()[message.length() - 1];
					view.setLoginMessage("Finding your opponent: "+oppoName);
					myColor = color=='R'?Color.RED:Color.BLACK;
					gameStart = true;
					setState(new State());
				}else{//in gaming, refresh state
					setState(unserializeState(message));
					showState();
				}*/
				Match gotMatch = Match.unserializeMatch(message);
				if(currentMatch == null && (!withAi)){
					//new game to start
					String oppoId = "";
					if(userId.equals(gotMatch.getRedPlayer())) oppoId = gotMatch.getBlackPlayer();
					if(userId.equals(gotMatch.getBlackPlayer())) oppoId = gotMatch.getRedPlayer();
					String newGameMessage = "";
					//new game
					if(gotMatch.getState().equals(newStateString))
						newGameMessage = Graphics.gameMessage.newGameWith(oppoId, gotMatch.getMatchId());
					else newGameMessage = Graphics.gameMessage.updateState(oppoId,gotMatch.getMatchId());
					if(!actionFlag) view.newGameMessage(newGameMessage);
					setCurrentMatch(gotMatch);
					getMatchesOfUser();
					
				}else if(currentMatch == null || !currentMatch.getMatchId().equals(gotMatch.getMatchId())){
					//notify
					String oppoId = "";
					if(userId.equals(gotMatch.getRedPlayer())) oppoId = gotMatch.getBlackPlayer();
					if(userId.equals(gotMatch.getBlackPlayer())) oppoId = gotMatch.getRedPlayer();
					String newGameMessage = "";
					//new game
					if(gotMatch.getState().equals(newStateString)) newGameMessage = Graphics.gameMessage.newGameApply(oppoId, gotMatch.getMatchId());
					else newGameMessage = Graphics.gameMessage.updateState(oppoId,gotMatch.getMatchId());
					if(!actionFlag) view.newGameMessage(newGameMessage);
					getMatchesOfUser();
				}else{
					//refresh current game
					setCurrentMatch(gotMatch);
				}
				actionFlag = false;
			}
			
			@Override
			public void onError(ChannelError error) {		
			}

			@Override
			public void onClose() {
			}
			
		});
		//rank
		jungleServiceAsync.getRank(userId, new AsyncCallback<Integer[]>(){

			@Override
			public void onFailure(Throwable caught) {
				view.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Integer[] result) {
				view.setRank(result[0], result[1]);
			}
			
		});
	}
	
	//ready to find an opponent randomly
	public void findOpponend(){
		if(!ifLogin()){//not login
			Window.alert(Graphics.gameMessage.loginAlert());
			return;
		}
		actionFlag = true;
		jungleServiceAsync.findingGame(userId, new AsyncCallback<Match>(){

			@Override
			public void onFailure(Throwable caught) {
				view.setStatus(caught.getMessage());				
			}

			@Override
			public void onSuccess(Match result) {
				//setCurrentMatch(result);
			}
			
		});
		view.setOppoMessage(Graphics.gameMessage.lookForAnotherPlayer());
	}
	
	//ready to find a game with opponent ID
	public void findOpponentWith(String opponentId){
		if(!ifLogin()){//not login
			Window.alert(Graphics.gameMessage.loginAlert());
			return;
		}
		actionFlag = true;
		jungleServiceAsync.findingGameWith(userId, opponentId, new AsyncCallback<Match>(){

			@Override
			public void onFailure(Throwable caught) {
				view.setStatus(caught.getMessage());	
			}

			@Override
			public void onSuccess(Match result) {
				//setCurrentMatch(result);
			}
			
		});
	}
	
	//load game
	public void loadGame(Long matchId){
		if(!ifLogin()){//not login
			Window.alert(Graphics.gameMessage.loginAlert());
			return;
		}
		actionFlag = true;
		if(currentMatch != null) currentMatch = null;
		jungleServiceAsync.loadMatch(userId, matchId, new AsyncCallback<Match>(){

			@Override
			public void onFailure(Throwable caught) {
				view.setStatus(caught.getMessage());	
			}

			@Override
			public void onSuccess(Match result) {
				//setCurrentMatch(result);
			}
			
		});
	}
	
	//get all matches of userId
	public void getMatchesOfUser(){
		if(!ifLogin()){//not login
			//Window.alert("Please Login First.");
			return;
		}
		jungleServiceAsync.getMatches(userId, new AsyncCallback<Set<Match>>(){

			@Override
			public void onFailure(Throwable caught) {
				view.setStatus(caught.getMessage());	
			}

			@Override
			public void onSuccess(Set<Match> result) {
				ArrayList<Match> ongoingMatches = new ArrayList<Match>();
				ArrayList<Match> finishedMatches = new ArrayList<Match>();
				ArrayList<Match> allMatches = new ArrayList<Match>();
				for(Match m : result){
					if(m.ifFinished()) finishedMatches.add(m);
					else ongoingMatches.add(m);
				}
				allMatches.addAll(ongoingMatches);
				allMatches.addAll(finishedMatches);
				//view.refresheMatches(allMatches);
			}
			
		});
	}
	
	public void playWithAi() {
		withAi = true;
		//initialize game
		if(new Random().nextBoolean())
			myColor = Color.RED;
		else myColor = Color.BLACK;
		this.setState(new State());
		
		if(!state.getTurn().equals(myColor))
			aiMove();
	}
	
	private void aiMove(){
		Timer timer = new Timer(1000);
		Move bestMove = abp.findBestMove(state, 2, timer);
		stateChanger.makeMove(state, bestMove);
		this.showState();
	}
	
	//set friend list to graphics
	public void setFriendList(Set<FbInfo> friendList){
		view.clearFriendList();
		jungleServiceAsync.getRanks(friendList, new AsyncCallback<Set<FbInfo>>(){
			
			@Override
			public void onFailure(Throwable caught) {
				view.setStatus(caught.getMessage());
			}
			@Override
			public void onSuccess(Set<FbInfo> result) {
				for(FbInfo friendsFbInfo : result){
					view.addFriend(friendsFbInfo);
				}
			}
				
		});

	}
	
	//play new game or load game with facebook friends
	public void playWith(final String oppoFbId){
		currentMatch = null;
		jungleServiceAsync.fbPlay(userId, oppoFbId, new AsyncCallback<Match>(){

			@Override
			public void onFailure(Throwable caught) {
				view.setStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(Match result) {
				if(result == null){
					//TODO add invitation
					InvationPanel invationPanel = new InvationPanel(oppoFbId);
					invationPanel.center();
				}
			}
			
		});
	}
	
	//Create a valueChangeHnader responsible for record browser history
	/*public void initializeHistory(){
		History.addValueChangeHandler(new ValueChangeHandler<String>(){
			@Override
			public void onValueChange(ValueChangeEvent<String> event){
				String historyToken = event.getValue();
				setState(unserializeState(historyToken));
			}
		});
		String startState = History.getToken();
		setState(unserializeState(startState));
	}*/
	
	//Creates a string representing all the information in a state.
	public static String serializeState(State state) {
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
	public static State unserializeState(String serialized){
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
	private static int charToInt(char c){
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
	
	public static String newStateString = "r0020661571117524622264068000862660";

}
