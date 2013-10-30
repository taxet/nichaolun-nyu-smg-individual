package org.ninini.jungle.shared;

public class StateChangerImpl implements StateChanger {

	@Override
	public void makeMove(State state, Move move) throws IllegalMove {
		if (state.getGameResult() != null)
			throw new IllegalMove();
    
		//out of border
		if (move.getFrom().getRow() < 0 || move.getFrom().getRow() >= State.ROWS
                    || move.getFrom().getCol() < 0 || move.getFrom().getCol() >= State.COLS)
			throw new IllegalMove();
		if (move.getTo().getRow() < 0 || move.getTo().getRow() >= State.ROWS
                    || move.getTo().getCol() < 0 || move.getTo().getCol() >= State.COLS)
			throw new IllegalMove();
		Piece movingPiece = state.getPiece(move.getFrom());
		//no piece in From position
		if (movingPiece == null)
			throw new IllegalMove();
		//moving piece is opponent color's
		if (movingPiece.getColor() != state.getTurn())
			throw new IllegalMove();
		//moving into own den
		if(move.getTo().getRow() == state.getDenofTurnColor().getRow() &&
				move.getTo().getCol() == state.getDenofTurnColor().getCol())
			throw new IllegalMove();
		
		Piece attackedPiece = state.getPiece(move.getTo());
		if(movingPiece.getRank() == PieceRank.LION || movingPiece.getRank() == PieceRank.TIGER){
			if (State.inRiver(move.getTo())) throw new IllegalMove();//to water
			if(!isAdjacent(move.getFrom(), move.getTo()) && 
					crossRiver(move.getFrom(), move.getTo()) == -1) throw new IllegalMove();//jump over river
			if(crossRiver(move.getFrom(), move.getTo()) == 0 && state.ifRatInRiver(0))
				throw new IllegalMove();
			if(crossRiver(move.getFrom(), move.getTo()) == 1 && state.ifRatInRiver(1))
				throw new IllegalMove();
		}else if(movingPiece.getRank() == PieceRank.RAT){
			if(!isAdjacent(move.getFrom(), move.getTo())) throw new IllegalMove();
		}else {
			if (State.inRiver(move.getTo())) throw new IllegalMove();//to water
			if(!isAdjacent(move.getFrom(), move.getTo())) throw new IllegalMove();				
		}
		
		if (attackedPiece != null && attackedPiece.getColor() == state.getTurn())
			throw new IllegalMove();
		else if (attackedPiece != null){//capture
			if(!movingPiece.superiorTo(attackedPiece)) throw new IllegalMove();
			//rat in river cannot capture animals on land
			//rat on land cannot capture animals in river
			if((movingPiece.getRank() == PieceRank.RAT) && 
					( State.inRiver(move.getFrom()) != State.inRiver(move.getTo()) )  )
				throw new IllegalMove();
			state.setPiece(move.getTo(), null);
			state.captureEnemyPiece();
		}
		
		if(state.inOpponentTrap(move.getTo())) 
			state.setPiece(move.getTo(), new Piece(movingPiece.getColor(),movingPiece.getRank(),true));
		else
			state.setPiece(move.getTo(), new Piece(movingPiece.getColor(),movingPiece.getRank()));
		state.setPiece(move.getFrom(), null);
		//rat into river
		if(State.inRiver0(move.getTo())) state.ratIntoRiver(0);
		if(State.inRiver1(move.getTo())) state.ratIntoRiver(1);
		//rat out of river
		if(State.inRiver0(move.getFrom()) && !State.inRiver0(move.getTo())) state.ratOutofRiver(0);
		if(State.inRiver1(move.getFrom()) && !State.inRiver1(move.getTo())) state.ratOutofRiver(1);
		
		//determine whether game is over
		if(state.inOpponentDen(move.getTo())) {
			Color winner = state.getTurn();
			GameResultReason reason = GameResultReason.ENTER_DEN;
			GameResult gameResult = new GameResult(winner, reason);
			state.setGameResult(gameResult);			
		}
		if(state.getRemainingPieces(state.getTurn().getOpposite()) <= 0){
			Color winner = state.getTurn();
			GameResultReason reason = GameResultReason.CAPTURE_ALL_PIECES;
			GameResult gameResult = new GameResult(winner, reason);
			state.setGameResult(gameResult);
		}
		
		//change turns
		state.changeTurn();
	}
	
	//check whether p1 and p2 is adjacent
	private boolean isAdjacent(Position p1, Position p2){
		if (p1.getRow() == p2.getRow()){
			if(Math.abs(p1.getCol() - p2.getCol()) == 1) 
				return true;
		}
		if (p1.getCol() == p2.getCol()){
			if(Math.abs(p1.getRow() - p2.getRow()) == 1) 
				return true;		
		}
		return false;		
	}
	
	//check whether p1 and p2 is on the opposite side of an river
	private int crossRiver(Position p1, Position p2){
		//cross river0
		//horizon
		if(p1.getRow() == 3 && p2.getRow() == 3 && p1.getCol() == 0 && p2.getCol() == 3)
			return 0;
		if(p1.getRow() == 3 && p2.getRow() == 3 && p1.getCol() == 3 && p2.getCol() == 0)
			return 0;
		if(p1.getRow() == 4 && p2.getRow() == 4 && p1.getCol() == 0 && p2.getCol() == 3)
			return 0;
		if(p1.getRow() == 4 && p2.getRow() == 4 && p1.getCol() == 3 && p2.getCol() == 0)
			return 0;
		if(p1.getRow() == 5 && p2.getRow() == 5 && p1.getCol() == 0 && p2.getCol() == 3)
			return 0;
		if(p1.getRow() == 5 && p2.getRow() == 5 && p1.getCol() == 3 && p2.getCol() == 0)
			return 0;
		//vertical
		if(p1.getCol() == 1 && p2.getCol() == 1 && p1.getRow() == 2 && p2.getRow() == 6)
			return 0;
		if(p1.getCol() == 1 && p2.getCol() == 1 && p1.getRow() == 6 && p2.getRow() == 2)
			return 0;
		if(p1.getCol() == 2 && p2.getCol() == 2 && p1.getRow() == 2 && p2.getRow() == 6)
			return 0;
		if(p1.getCol() == 2 && p2.getCol() == 2 && p1.getRow() == 6 && p2.getRow() == 2)
			return 0;
		

		//cross river1
		//horizon
		if(p1.getRow() == 3 && p2.getRow() == 3 && p1.getCol() == 6 && p2.getCol() == 3)
			return 1;
		if(p1.getRow() == 3 && p2.getRow() == 3 && p1.getCol() == 3 && p2.getCol() == 6)
			return 1;
		if(p1.getRow() == 4 && p2.getRow() == 4 && p1.getCol() == 6 && p2.getCol() == 3)
			return 1;
		if(p1.getRow() == 4 && p2.getRow() == 4 && p1.getCol() == 3 && p2.getCol() == 6)
			return 1;
		if(p1.getRow() == 5 && p2.getRow() == 5 && p1.getCol() == 6 && p2.getCol() == 3)
			return 1;
		if(p1.getRow() == 5 && p2.getRow() == 5 && p1.getCol() == 3 && p2.getCol() == 0)
			return 1;
		//vertical
		if(p1.getCol() == 4 && p2.getCol() == 4 && p1.getRow() == 2 && p2.getRow() == 6)
			return 1;
		if(p1.getCol() == 4 && p2.getCol() == 4 && p1.getRow() == 6 && p2.getRow() == 2)
			return 1;
		if(p1.getCol() == 5 && p2.getCol() == 5 && p1.getRow() == 2 && p2.getRow() == 6)
			return 1;
		if(p1.getCol() == 5 && p2.getCol() == 5 && p1.getRow() == 6 && p2.getRow() == 2)
			return 1;
		
		
		return -1;
	}

}