package jungle;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class GameResult {

	private Color winner;
	private GameResultReason reason;
	
	public GameResult(Color winner, GameResultReason reason){
		this.winner = winner;
		this.reason = checkNotNull(reason);
	}
	
	public Color getWinner(){
		return winner;
	}
	
	public GameResultReason getReason(){
		return reason;
	}
	
	@Override
	public String toString(){
		return "GameResult [winner=" + winner + ", gameResultReason=" + reason + "]";
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(reason, winner);
	}
	
	@Override
	public boolean equals(Object obj){
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof GameResult)) return false;
		GameResult other = (GameResult) obj;
		return Objects.equal(reason, other.reason)
				&& Objects.equal(winner, other.winner);
	}
}
