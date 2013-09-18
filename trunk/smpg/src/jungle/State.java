package jungle;

/**
 * 
 * @author Chaolun Ni
 * http://en.wikipedia.org/wiki/Jungle_(board_game)
 */

public class State {
	public static int rows = 9;
	public static int cols = 7;
	
	public static boolean inRiver0(int row, int col){
		return (row==1 || row ==2) && (col == 3 || col == 4 || col == 5);
	}
	public static boolean inRiver1(int row, int col){
		return (row==4 || row ==5) && (col == 3 || col == 4 || col == 5);		
	}
	public static boolean inRiver(int row, int col){
		return inRiver0(row, col) || inRiver1(row, col);
	}
	
	private Color turn = Color.RED;
	private Piece[][] board = new Piece[rows][cols];
	
	private boolean[] mouseInRiver = {false, false};
	
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
	public State(Color turn, Piece[][] board, boolean[] mouseInRiver){
		this.turn = turn;
		this.board = board;
		this.mouseInRiver = mouseInRiver;
	}
	
	public Color getTurn(){
		return turn;
	}
	public Piece getPiece(int row, int col){
		return board[row][col];
	}
	public boolean ifMouseInRiver(int riverNum){
		return mouseInRiver[riverNum];
	}
	
	public void changeTurn(){
		turn = turn.getOpposite();
	}
	public void setTurn(Color c){
		turn = c;
	}
	public void setPiece(int row, int col, Piece piece){
		board[row][col] = piece;
	}

}
