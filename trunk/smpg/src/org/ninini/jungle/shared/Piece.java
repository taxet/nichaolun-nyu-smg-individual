package org.ninini.jungle.shared;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class Piece {

	private Color color;
	private PieceRank rank;
	public boolean isTrapped;
	
	public Piece(Color color, PieceRank rank){
		this.color = checkNotNull(color);
		this.rank = checkNotNull(rank);
		this.isTrapped = false;
	}
	public Piece(Color color, PieceRank rank, boolean isTrapped){
		this.color = checkNotNull(color);
		this.rank = checkNotNull(rank);
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
		//if(this.isTrapped) return false;
		
		//rat is superior to elephant
		if(this.getRank().getRank() == 1 && p2.getRank().getRank() == 8) return true;
		if(this.getRank().getRank() == 8 && p2.getRank().getRank() == 1) return false;
		
		//other situation
		return this.getRank().getRank() >= p2.getRank().getRank();
	}
	
	@Override
	public String toString(){
		if(isTrapped)
			return "[" + color + "'s " + rank +"]" + "(trapped)";
		else return "[" + color + "'s " + rank +"]";
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(color, rank, isTrapped);
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof Piece)) return false;
		Piece other = (Piece) obj;
		return Objects.equal(color, other.color)
				&& Objects.equal(rank, other.rank)
				&& Objects.equal(isTrapped, isTrapped);
	}
}
