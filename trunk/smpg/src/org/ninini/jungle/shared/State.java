package org.ninini.jungle.shared;

/**
 * 
 * @author Chaolun Ni
 * http://en.wikipedia.org/wiki/Jungle_(board_game)
 */

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.google.common.base.Objects;

public class State {
	public static final int ROWS = 9;
	public static final int COLS = 7;
	
	private Color turn = Color.RED;
	private Piece[][] board = new Piece[ROWS][COLS];
	
	private boolean[] ratInRiver = {false, false};
	private int[] remainingPieces = new int[2]; //0 for red and 1 for black
	private GameResult gameResult;
	
	//river0: left river
	public static boolean inRiver0(int row, int col){
		return (col == 1 || col == 2) && (row == 3 || row == 4 || row == 5);
	}
	public static boolean inRiver0(Position p){
		return inRiver0(p.getRow(), p.getCol());
	}
	//river1: right river
	public static boolean inRiver1(int row, int col){
		return (col == 4 || col == 5) && (row == 3 || row == 4 || row == 5);		
	}
	public static boolean inRiver1(Position p){
		return inRiver1(p.getRow(), p.getCol());
	}
	public static boolean inRiver(int row, int col){
		return inRiver0(row, col) || inRiver1(row, col);
	}
	public static boolean inRiver(Position p){
		return inRiver(p.getRow(), p.getCol());
	}
	
	public State(){
		//set who's turn
		turn = Color.RED;
		
		//set rat in river bools
		ratInRiver[0] = false;
		ratInRiver[1] = false;
		
		//set red pieces
		board[0][0] = new Piece(Color.RED, PieceRank.LION);
		board[0][6] = new Piece(Color.RED, PieceRank.TIGER);
		board[1][1] = new Piece(Color.RED, PieceRank.DOG);
		board[1][5] = new Piece(Color.RED, PieceRank.CAT);
		board[2][0] = new Piece(Color.RED, PieceRank.RAT);
		board[2][2] = new Piece(Color.RED, PieceRank.LEOPARD);
		board[2][4] = new Piece(Color.RED, PieceRank.WOLF);
		board[2][6] = new Piece(Color.RED, PieceRank.ELEPHANT);
		
		//set black pieces
		board[8][6] = new Piece(Color.BLACK, PieceRank.LION);
		board[8][0] = new Piece(Color.BLACK, PieceRank.TIGER);
		board[7][5] = new Piece(Color.BLACK, PieceRank.DOG);
		board[7][1] = new Piece(Color.BLACK, PieceRank.CAT);
		board[6][6] = new Piece(Color.BLACK, PieceRank.RAT);
		board[6][4] = new Piece(Color.BLACK, PieceRank.LEOPARD);
		board[6][2] = new Piece(Color.BLACK, PieceRank.WOLF);
		board[6][0] = new Piece(Color.BLACK, PieceRank.ELEPHANT);
		
		remainingPieces[0] = 8;
		remainingPieces[1] = 8;		
	}
	public State(Color turn, Piece[][] board, GameResult gameResult){
		remainingPieces[0] = 0;
		remainingPieces[1] = 0;		
		this.turn = checkNotNull(turn);
		for(int r = 0 ;r < ROWS; r++){
			for(int c = 0; c < COLS; c++){
				this.board[r][c] = board[r][c];
				if(board[r][c] != null){
					//inRiver
					if(inRiver0(r,c)) ratInRiver[0] = true;
					if(inRiver1(r,c)) ratInRiver[1] = true;
					//remaining
					if(board[r][c].getColor() == Color.RED) remainingPieces[0]++;
					if(board[r][c].getColor() == Color.BLACK) remainingPieces[1]++; 
				}
			}
		}
		this.gameResult = gameResult;
	}
	
	public Color getTurn(){
		return turn;
	}
	public Piece getPiece(int row, int col){
		return board[row][col];
	}
	public Piece getPiece(Position p){
		return getPiece(p.getRow(), p.getCol());
	}
	//check whether there is a rat in river[riverNum]
	//riverNum = 1 or 0
	public boolean ifRatInRiver(int riverNum){
		return ratInRiver[riverNum];
	}
	public GameResult getGameResult(){
		return gameResult;
	}
	public int getRemainingPieces(Color c){
		if(c == Color.RED) return remainingPieces[0];
		if(c == Color.BLACK) return remainingPieces[1];
		return 0;
	}
	//get the den's position of the Color in turn
	public Position getDenofTurnColor(){
		if (turn == Color.RED) return new Position(0,3);
		if (turn == Color.BLACK) return new Position(8,3);
		return null;
	}
	//get the den's position of the opponent's Color
	public Position getDenofOpponentColor(){
		if (turn == Color.RED) return new Position(8,3);
		if (turn == Color.BLACK) return new Position(0,3);
		return null;
	}
	//check a position whether is in red's trap
	public static boolean inRedTrap(int row, int col){
		if (row == 0 && col == 2) return true;
		if (row == 1 && col == 3) return true;
		if (row == 0 && col == 4) return true;
		else return false;
	}
	public static boolean inRedTrap(Position p){
		return inRedTrap(p.getRow(), p.getCol());
	}
	//check a position whether is in black's trap
	public static boolean inBlackTrap(int row, int col){
		if (row == 8 && col == 2) return true;
		if (row == 7 && col == 3) return true;
		if (row == 8 && col == 4) return true;
		else return false;		
	}
	//check a position whether is in red's trap
	public static boolean inBlackTrap(Position p){
		return inBlackTrap(p.getRow(), p.getCol());
	}
	public static boolean inRedDen(int row, int col){
		return (row == 0) && (col == 3);
	}
	//check a position whether is in black's den
	public static boolean inRedDen(Position p){
		return inRedDen(p.getRow(), p.getCol());
	}
	public static boolean inBlackDen(int row, int col){
		return (row == 8) && (col == 3);
	}
	public static boolean inBlackDen(Position p){
		return inBlackDen(p.getRow(), p.getCol());
	}
	//check a position whether is in opponent's trap
	public boolean inOpponentTrap(Position p){
		return (turn == Color.RED)?inBlackTrap(p):inRedTrap(p);
	}
	//check a position whether is in opponent's den
	public boolean inOpponentDen(Position p){
		return (turn == Color.RED)?inBlackDen(p):inRedDen(p);
	}
	//check a position whether is in player's den
	public boolean inPLayerDen(Position p){
		return (turn == Color.RED)?inRedDen(p):inBlackDen(p);
	}
	
	public void changeTurn(){
		turn = turn.getOpposite();
	}
	public void setTurn(Color c){
		turn = checkNotNull(c);
	}
	public void setPiece(int row, int col, Piece piece){
		board[row][col] = piece;
	}
	public void setPiece(Position p, Piece piece){
		setPiece(p.getRow(), p.getCol(), piece);
	}
	public void setGameResult(GameResult gameResult){
		this.gameResult = gameResult;
	}
	//when capture an enemy's piece
	public void captureEnemyPiece(){
		if(turn == Color.RED) remainingPieces[1]--;
		if(turn == Color.BLACK) remainingPieces[0]--;
	}
	//rat move into a river, num = 1 or 0
	public void ratIntoRiver(int num){
		ratInRiver[num] = true;
	}
	//rat move out of a river, num = 1 or 0
	public void ratOutofRiver(int num){
		ratInRiver[num] = false;
	}
	
	
	@Override
	public String toString(){
		return "State [" 
				+ "turn=" + turn + ", " 
				+ "board=" + Arrays.deepToString(board)
				+ ", ratInRiver=" + Arrays.toString(ratInRiver) 
				+ (gameResult != null ? "gameResult=" + gameResult + ", " : "")
				+ "]";
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(turn, Arrays.deepHashCode(board), 
				Arrays.hashCode(ratInRiver), gameResult);
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof State)) return false;
		State other = (State) obj;
		return Objects.equal(turn, other.turn)
				&& Arrays.deepEquals(board, other.board)
				&& Arrays.equals(ratInRiver, other.ratInRiver)
				&& Objects.equal(gameResult, other.gameResult);
	}
}
