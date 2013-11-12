package org.ninini.jungle.shared;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;


public class AlphaBetaPruning {
	StateChanger stateChanger = new StateChangerImpl();
	
	static class TimeoutException extends RuntimeException{
		private static final long serialVersionUID = 1L;		
	}
	
	static class MoveScore implements Comparable<MoveScore>{
		Move move;
		int score;
		@Override
		public int compareTo(MoveScore ms) {
			return ms.score - this.score;
		}
	}
	private Heuristic heuristic;
	
	public AlphaBetaPruning(Heuristic heuristic){
		this.heuristic = heuristic;
	}
	
	public Move findBestMove(State state, int depth, Timer timer){
		boolean isBlack = state.getTurn().isBlack();
		
		// Do iterative deepening (A*), and slow get better heuristic values for the states.
		List<MoveScore> scores = Lists.newArrayList();
		{
			Iterable<Move> possibleMoves = heuristic.getOrderedMoves(state);
			for(Move move : possibleMoves){
				MoveScore score = new MoveScore();
				score.move = move;
				score.score = Integer.MIN_VALUE;
				scores.add(score);
			}
		}
		try{
			for(int i = 0; i < depth; i++){
				for(MoveScore moveScore : scores){
					Move move = moveScore.move;
					State temp = new State(state);
					stateChanger.makeMove(temp, move);
					int score = findMoveScore(temp, i, Integer.MIN_VALUE, Integer.MAX_VALUE, timer);
					if(isBlack){
						// the scores are from the point of view of the white, so for black we need to switch.
	                    score = -score;
					}
					moveScore.score = score;
				}
				Collections.sort(scores);
			}
		} catch(TimeoutException e){
			
		}
		Collections.sort(scores);
		Move result = scores.get(0).move;
		return result;
	}
	
	/**
     * If we get a timeout, then the score is invalid.
     */
	private int findMoveScore(State state, int depth, int alpha, int beta, Timer timer)
			throws TimeoutException{
		if(timer.didTimeout()){
			throw new TimeoutException();
		}
		GameResult gameResult = state.getGameResult();
		if(depth == 0 || gameResult != null){
			return heuristic.getStateValue(state);
		}
		Color color = state.getTurn();
		int scoreSum = 0;
		int count = 0;
		Iterable<Move> possibleMoves = heuristic.getOrderedMoves(state);
		for(Move move : possibleMoves){
			count++;
			State tmp = new State(state);
			stateChanger.makeMove(tmp, move);
			int childScore = findMoveScore(tmp, depth - 1, alpha, beta, timer);
			if(color == null){
				scoreSum += childScore;
			}else if(color.isBlack()){
				beta = Math.min(beta, childScore);
				if(beta <= alpha) break;
			}else {
				alpha = Math.max(alpha, childScore);
				if(beta <= alpha) break;
			}
		}
		return color == null ? scoreSum / count : color.isBlack() ? beta : alpha;
	}
}
