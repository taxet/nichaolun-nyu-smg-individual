package jungle;

public enum Color {

	RED,
	BLACK,
	;
	
	public boolean isBlack(){
		return this == BLACK;
	}
	
	public Color getOpposite(){
		return this == RED ? BLACK : RED;
	}
	
	@Override
	public String toString(){
		return isBlack()?"B":"R";
	}
}
