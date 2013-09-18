package jungle;

public class GameResult {

	private Color winner;
	private GameResultReason reason;
	
	public GameResult(Color winner, GameResultReason reason){
		this.winner = winner;
		this.reason = reason;
	}
	
	public Color getWinner(){
		return winner;
	}
	
	public GameResultReason getReason(){
		return reason;
	}
}
