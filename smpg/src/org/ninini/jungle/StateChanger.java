package org.ninini.jungle;

public class StateChanger {

	public void makeMove(State state, Move move) throws IllegalMove {
		if (state.getGameResult() != null)
			throw new IllegalMove();
    
		if (move.getFrom().getRow() < 0 || move.getFrom().getRow() >= State.ROWS
                    || move.getFrom().getCol() < 0 || move.getFrom().getCol() >= State.COLS)
			throw new IllegalMove();
		if (move.getTo().getRow() < 0 || move.getTo().getRow() >= State.ROWS
                    || move.getTo().getCol() < 0 || move.getTo().getCol() >= State.COLS)
			throw new IllegalMove();
		Piece movingPiece = state.getPiece(move.getFrom());
		if (movingPiece == null)
			throw new IllegalMove();
		if (movingPiece.getColor() != state.getTurn())
			throw new IllegalMove();
		if(move.getTo().getRow() == state.getDenofTurnColor().getRow() &&
				move.getTo().getCol() == state.getDenofTurnColor().getCol())
			throw new IllegalMove();
		
		Piece attackedPiece = state.getPiece(move.getTo());
		if(movingPiece.getRank() == PieceRank.LION || movingPiece.getRank() == PieceRank.TIGER){
			if (State.inRiver(move.getTo())) throw new IllegalMove();//to water
			if(!isAdjacent(move.getFrom(), move.getTo()) && 
					crossRiver(move.getFrom(), move.getTo()) == -1) throw new IllegalMove();//jump over river
			if(crossRiver(move.getFrom(), move.getTo()) == 0 && state.ifMouseInRiver(0))
				throw new IllegalMove();
			if(crossRiver(move.getFrom(), move.getTo()) == 1 && state.ifMouseInRiver(1))
				throw new IllegalMove();
		}else if(movingPiece.getRank() == PieceRank.RAT){
			if(!isAdjacent(move.getFrom(), move.getTo())) throw new IllegalMove();
		}else {
			if (State.inRiver(move.getTo())) throw new IllegalMove();//to water
			if(!isAdjacent(move.getFrom(), move.getTo())) throw new IllegalMove();				
		}
		
		if (attackedPiece != null && attackedPiece.getColor() == state.getTurn())
			throw new IllegalMove();
		else{//capture
			if(!movingPiece.superiorTo(attackedPiece)) throw new IllegalMove();
			state.setPiece(move.getTo(), null);
		}
		
		if(state.inOpponentTrap(move.getTo())) 
			state.setPiece(move.getTo(), new Piece(movingPiece.getColor(),movingPiece.getRank(),true));
		state.setPiece(move.getTo(), new Piece(movingPiece.getColor(),movingPiece.getRank()));
		state.setPiece(move.getFrom(), null);
		if(State.inRiver0(move.getTo())) state.mouseIntoRiver(0);
		if(State.inRiver1(move.getTo())) state.mouseIntoRiver(1);
		if(State.inRiver0(move.getFrom())) state.mouseOutofRiver(0);
		if(State.inRiver1(move.getFrom())) state.mouseOutofRiver(1);
	}
	
	private boolean isAdjacent(Position p1, Position p2){
		//from position and to position must be adjacent
		if (p1.getRow() == p2.getRow()){
			if(Math.abs(p1.getCol() - p2.getCol()) == 1) 
				return true;
		}
		if (p1.getCol() == p2.getCol()){
			if(Math.abs(p1.getRow() - p2.getRow()) != 1) 
				throw new IllegalMove();			
		}
		return false;		
	}
	
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
