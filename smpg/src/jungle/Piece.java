package jungle;

public class Piece {

	private Color color;
	private PieceRank rank;
	public boolean isTrapped;
	
	public Piece(Color color, PieceRank rank){
		this.color = color;
		this.rank = rank;
		this.isTrapped = false;
	}
	public Piece(Color color, PieceRank rank, boolean isTrapped){
		this.color = color;
		this.rank = rank;
		this.isTrapped = isTrapped;
	}
	
	public Color getColor(){
		return color;
	}
	public PieceRank getRank(){
		return rank;
	}
	
	public boolean superiorTo(Piece p2){
		if(p2 == null) return true;
		
		//either is trapped
		if(p2.isTrapped) return true;
		if(this.isTrapped) return false;
		
		//rat is superior to elephant
		if(this.getRank().getRank() == 1 && p2.getRank().getRank() == 8) return true;
		
		//other situation
		return this.getRank().getRank() >= p2.getRank().getRank();
	}
	
}
