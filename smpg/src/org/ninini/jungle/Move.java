package org.ninini.jungle;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class Move {
	
	private Position from;
	private Position to;
	
	public Move(Position from, Position to){
		this.from = checkNotNull(from);
		this.to = checkNotNull(to);
	}
	
	public Position getFrom(){
		return from;
	}
	
	public Position getTo(){
		return to;
	}
	
	@Override
	public String toString(){
		return from + "->" + to ;
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(from, to);
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof Move)) return false;
		Move other = (Move) obj;
		return Objects.equal(from, other.from)
				&& Objects.equal(to, other.to);
	}
}
