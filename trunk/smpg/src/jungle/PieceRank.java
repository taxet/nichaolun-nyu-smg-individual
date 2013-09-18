package jungle;

public enum PieceRank {

	RAT(1),
	CAT(2),
	DOG(3),
	WOLF(4),
	LEOPARD(5),
	TIGER(6),
	LION(7),
	ELEPHANT(8),
	;
	
	private int rank;
	PieceRank(int rank){
		this.rank = rank;
	}
	
	public int getRank(){
		return rank;
	}
}
