package jungle;

public class Move {
	
	private Position from;
	private Position to;
	
	private PieceRank promoteToPiece;
	
	public Move(Position from, Position to, PieceRank promoteToPiece){
		this.from = from;
		this.to = to;
		this.promoteToPiece = promoteToPiece;
	}
	
	public Position getFrom(){
		return from;
	}
	
	public Position getTo(){
		return to;
	}
	
	public PieceRank getPromoteToPiece(){
		return promoteToPiece;
	}

}
