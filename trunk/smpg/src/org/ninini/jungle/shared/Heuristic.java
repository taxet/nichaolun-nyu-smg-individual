package org.ninini.jungle.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Heuristic {
	private StateExplorer stateExplorer = new StateExplorerImpl();
	//private StateChanger stateChanger = new StateChangerImpl();
	
	public int getStateValue(State state){
		int result = 0;
		if(state.getGameResult() == null){
			for(int row = 0; row < State.ROWS; row++){
				for(int col = 0; col < State.COLS; col++){
					result += pieceValue(new Position(row, col), state.getPiece(row, col));
				}
			}
		}else{
			GameResult gameResult = state.getGameResult();
			if(gameResult.getWinner() == Color.RED){
				return Integer.MAX_VALUE;
			}else{
				return Integer.MIN_VALUE;
			}
		}
		return result;
	}
	
	Iterable<Move> getOrderedMoves(final State state){
		ArrayList<Move> result = new ArrayList<Move>();
		result.addAll(stateExplorer.getPossibleMoves(state));
		Collections.sort(result, new Comparator<Move>(){

			@Override
			public int compare(Move arg0, Move arg1) {
				Random rnd = new Random();
				return rnd.nextInt() - rnd.nextInt();
			}
			
		});
		Collections.sort(result, new Comparator<Move>(){

			@Override
			public int compare(Move arg0, Move arg1) {
				return captureValue(state, arg1) - captureValue(state, arg0);
			}
			
		});
		return result;
	}
	
	private int captureValue(State state, Move move){
		int currentValue = pieceValue(move.getFrom(), state.getPiece(move.getFrom()))+
				pieceValue(move.getTo(), state.getPiece(move.getTo()));
		int afterMoveValue = pieceValue(move.getTo(), state.getPiece(move.getFrom()));
		return afterMoveValue - currentValue;
	}
	
	private int pieceValue(Position p, Piece piece){
		int value = 0;
		if(piece == null) return 0;
		//Red get positive value and black get negative value
		if (piece.getColor() == Color.RED){
			//the nearest to its opponent's den, the more value the piece is
			int distanceToDen = Math.abs(p.getCol() - 8) + Math.abs(p.getRow() - 3);
			int positionValue = 10 * (int)Math.pow(2, 11-distanceToDen);
			//piece value:
			int pieceValue = piece.getRank().getRank();
			value = positionValue+pieceValue;
		}else{
			//the nearest to its opponent's den, the more value the piece is
			int distanceToDen = Math.abs(p.getCol() - 0) + Math.abs(p.getRow() - 3);
			int positionValue = 10 * (int)Math.pow(2, 11-distanceToDen);
			//piece value:
			int pieceValue = piece.getRank().getRank();
			value = positionValue+pieceValue;
			value = -value;
		}
		return value;
	}
}
