package org.ninini.jungle.shared;

import com.google.common.base.Objects;

public class Position {
	private int row;
	private int col;


	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}


	public int getRow() {
		return row;
	}


	public int getCol() {
		return col;
	}
	
	@Override
	public String toString(){
		return "(" + row + "," + col + ")";
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(row, col);
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof Position)) return false;
		Position other = (Position) obj;
		return Objects.equal(row, other.col)
				&& Objects.equal(row, other.col);
	}
}
