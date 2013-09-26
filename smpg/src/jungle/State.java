package jungle;

/**
 * 
 * @author Chaolun Ni
 * http://en.wikipedia.org/wiki/Jungle_(board_game)
 */

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.google.common.base.Objects;

public class State {
	public static int rows = 9;
	public static int cols = 7;
	
	public static boolean inRiver0(int row, int col){
		return (row==1 || row ==2) && (col == 3 || col == 4 || col == 5);
	}
	public static boolean inRiver0(Position p){
		return inRiver0(p.getRow(), p.getCol());
	}
	public static boolean inRiver1(int row, int col){
		return (row==4 || row ==5) && (col == 3 || col == 4 || col == 5);		
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
	
	private Color turn = Color.RED;
	private Piece[][] board = new Piece[rows][cols];
	
	private boolean[] mouseInRiver = {false, false};
	
	private GameResult gameResult;
	
	public State(){
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
		board[8][6] = new Piece(Color.RED, PieceRank.LION);
		board[8][0] = new Piece(Color.RED, PieceRank.TIGER);
		board[7][5] = new Piece(Color.RED, PieceRank.DOG);
		board[7][1] = new Piece(Color.RED, PieceRank.CAT);
		board[6][6] = new Piece(Color.RED, PieceRank.RAT);
		board[6][4] = new Piece(Color.RED, PieceRank.LEOPARD);
		board[6][2] = new Piece(Color.RED, PieceRank.WOLF);
		board[6][0] = new Piece(Color.RED, PieceRank.ELEPHANT);
		
	}
	public State(Color turn, Piece[][] board, boolean[] mouseInRiver, GameResult gameResult){
		this.turn = checkNotNull(turn);
		for(int r = 0 ;r < rows; r++){
			for(int c = 0; c < cols; c++)
				this.board[r][c] = board[r][c];
		}
		this.mouseInRiver[0] = mouseInRiver[0];
		this.mouseInRiver[1] = mouseInRiver[1];
		this.gameResult = checkNotNull(gameResult);
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
	public boolean ifMouseInRiver(int riverNum){
		return mouseInRiver[riverNum];
	}
	public GameResult getGameResult(){
		return gameResult;
	}
	public Position getDenofTurnColor(){
		if (turn == Color.RED) return new Position(0,3);
		if (turn == Color.BLACK) return new Position(8,3);
		return null;
	}
	public Position getDenofOpposit(){
		if (turn == Color.RED) return new Position(8,3);
		if (turn == Color.BLACK) return new Position(0,3);
		return null;
	}
	public boolean inRedTrap(int row, int col){
		if (row == 0 && col == 2) return true;
		if (row == 1 && col == 3) return true;
		if (row == 0 && col == 4) return true;
		else return false;
	}
	public boolean inRedTrap(Position p){
		return inRedTrap(p.getRow(), p.getCol());
	}
	public boolean inBlackTrap(int row, int col){
		if (row == 8 && col == 2) return true;
		if (row == 7 && col == 3) return true;
		if (row == 8 && col == 4) return true;
		else return false;		
	}
	public boolean inBlackTrap(Position p){
		return inBlackTrap(p.getRow(), p.getCol());
	}
	public boolean inOpponentTrap(Position p){
		return (turn == Color.RED)?inBlackTrap(p):inRedTrap(p);
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
	public void mouseIntoRiver(int num){
		mouseInRiver[num] = true;
	}
	public void mouseOutofRiver(int num){
		mouseInRiver[num] = false;
	}
	@Override
	public String toString(){
		return "State [" 
				+ "turn=" + turn + ", " 
				+ "board=" + Arrays.deepToString(board)
				+ ", mouseInRiver=" + Arrays.toString(mouseInRiver) 
				+ (gameResult != null ? "gameResult=" + gameResult + ", " : "")
				+ "]";
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(turn, Arrays.deepHashCode(board), 
				Arrays.hashCode(mouseInRiver), gameResult);
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof State)) return false;
		State other = (State) obj;
		return Objects.equal(turn, other.turn)
				&& Objects.equal(board, other.board)
				&& Objects.equal(mouseInRiver, other.mouseInRiver)
				&& Objects.equal(gameResult, other.gameResult);
	}
}
