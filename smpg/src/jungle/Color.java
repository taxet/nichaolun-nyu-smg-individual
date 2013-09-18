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
	
}
