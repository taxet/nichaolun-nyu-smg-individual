package org.ninini.jungle.shared;

import java.util.Set;
import java.util.HashSet;

public class StateExplorerImpl implements StateExplorer {

	@Override
	public Set<Move> getPossibleMoves(State state) {
		Set<Move> moveSet = new HashSet<Move>();
		if(state.getGameResult() != null) return moveSet;
		for(Position p : getPossibleStartPositions(state)){
			moveSet.addAll(getPossibleMovesFromPosition(state, p));
		}
		return moveSet;
	}

	@Override
	public Set<Move> getPossibleMovesFromPosition(State state, Position start) {
		Set<Move> moves = new HashSet<Move>();
		if (state.getGameResult() != null)
			return moves;
		
		if (!getPossibleStartPositions(state).contains(start))
			return moves;
		
		Piece movingPiece = state.getPiece(start);
		//rat
		if(movingPiece.getRank() == PieceRank.RAT){
			//possible moving position: adjacent positions
			Set<Position> possibleMovePosition = getAdjacentPositions(start);
			for(Position p : possibleMovePosition){
				//cannot enter own den
				if(p.getRow() == state.getDenofTurnColor().getRow() &&
						p.getCol() == state.getDenofTurnColor().getCol()) continue;
				Piece targetPiece = state.getPiece(p);
				if(targetPiece != null){
					//Can not move to own piece
					if(targetPiece.getColor() != movingPiece.getColor()) continue;
					//Can not capture the piece whose rank is superior to it
					if(!movingPiece.superiorTo(targetPiece)) continue;
					//Can not capture the piece in water on land 
					//or capture the piece on land in water
					if(State.inRiver(p) != State.inRiver(start)) continue;
				}
				
				moves.add(new Move(start,p));
			}
		}else 
		
		//tiger and lion
		if(movingPiece.getRank() == PieceRank.TIGER || movingPiece.getRank() == PieceRank.LION){
			//possible moving position: adjacent positions and positions across the river
			Set<Position> possibleMovePosition = getAdjacentPositions(start);
			//Position across river0
			{
				Position positionAcrossRiver = getPositionCrossRiver0(start);
				if(positionAcrossRiver != null && !state.ifRatInRiver(0))
					possibleMovePosition.add(positionAcrossRiver);
			}
			//Position across river1
			{
				Position positionAcrossRiver = getPositionCrossRiver1(start);
				if(positionAcrossRiver != null && !state.ifRatInRiver(1))
					possibleMovePosition.add(positionAcrossRiver);
			}
			for(Position p : possibleMovePosition){
				//cannot enter own den
				if(p.getRow() == state.getDenofTurnColor().getRow() &&
						p.getCol() == state.getDenofTurnColor().getCol()) continue;
				//cannot move into river
				if(State.inRiver(p)) continue;
				Piece targetPiece = state.getPiece(p);
				if(targetPiece != null){
					//Can not move to own piece
					if(targetPiece.getColor() != movingPiece.getColor()) continue;
					//Can not capture the piece whose rank is superior to it
					if(!movingPiece.superiorTo(targetPiece)) continue;
				}
				
				moves.add(new Move(start,p));
			}		
			
		}else 
		
		//else piece
		{
			//possible moving position: adjacent positions
			Set<Position> possibleMovePosition = getAdjacentPositions(start);
			for(Position p : possibleMovePosition){
				//cannot enter own den
				if(p.getRow() == state.getDenofTurnColor().getRow() &&
						p.getCol() == state.getDenofTurnColor().getCol()) continue;
				//cannot move into river
				if(State.inRiver(p)) continue;
				Piece targetPiece = state.getPiece(p);
				if(targetPiece != null){
					//Can not move to own piece
					if(targetPiece.getColor() != movingPiece.getColor()) continue;
					//Can not capture the piece whose rank is superior to it
					if(!movingPiece.superiorTo(targetPiece)) continue;
				}
				
				moves.add(new Move(start,p));
			}
			
		}
		return moves;
	}

	@Override
	public Set<Position> getPossibleStartPositions(State state) {
		Set<Position> positionSet = new HashSet<Position>();
		if(state.getGameResult() != null) return positionSet;
		for(int row = 0; row < State.ROWS; row++){
			for(int col = 0; col < State.COLS; col++){
				Position p = new Position(row, col);
				if((state.getPiece(p) != null) && (state.getPiece(p).getColor() == state.getTurn()))
					positionSet.add(p);
			}
		}
		return positionSet;
	}
	
	//check if a position is out of border
	private boolean outOfBorder(Position p){
		if (p.getRow() < 0 || p.getRow() >= State.ROWS
                || p.getCol() < 0 || p.getCol() >= State.COLS)
		return true;
		return false;		
	}
	
	//get the adjacent position of the given position
	private Set<Position> getAdjacentPositions(Position p){
		Set<Position> positions = new HashSet<Position>();
		//top
		Position top = new Position(p.getRow()-1,p.getCol());
		if(!outOfBorder(top))positions.add(top);
		//bot
		Position bot = new Position(p.getRow()+1,p.getCol());
		if(!outOfBorder(bot))positions.add(bot);
		//left
		Position left = new Position(p.getRow(),p.getCol()-1);
		if(!outOfBorder(left))positions.add(left);
		//right
		Position right = new Position(p.getRow(),p.getCol()-1);
		if(!outOfBorder(right))positions.add(right);
		return positions;
	}
	//return the position that across the river from p
	/*
	private Set<Position> getPositionsAcrossRiver(Position p){
		Set<Position> positions = new HashSet<Position>();
		if(getPositionCrossRiver0(p) != null) positions.add(getPositionCrossRiver0(p));
		if(getPositionCrossRiver1(p) != null) positions.add(getPositionCrossRiver1(p));
		return positions;		
	}*/
	private Position getPositionCrossRiver0(Position p){
		//cross river0
		//horizon
		if(p.getRow() == 3 && p.getCol() == 0)
			return new Position(3,3);
		if(p.getRow() == 3 && p.getCol() == 3)
			return new Position(3,0);
		if(p.getRow() == 4 && p.getCol() == 0)
			return new Position(4,3);
		if(p.getRow() == 4 && p.getCol() == 3)
			return new Position(4,0);
		if(p.getRow() == 5 && p.getCol() == 0)
			return new Position(5,3);
		if(p.getRow() == 5 && p.getCol() == 3)
			return new Position(5,0);
		//vertical
		if(p.getCol() == 1 && p.getRow() == 2)
			return new Position(1,6);
		if(p.getCol() == 1 && p.getRow() == 6)
			return new Position(1,2);
		if(p.getCol() == 2 && p.getRow() == 2)
			return new Position(2,6);
		if(p.getCol() == 2 && p.getRow() == 6)
			return new Position(2,2);
		
		return null;
	}
	private Position getPositionCrossRiver1(Position p){
		//cross river1
		//horizon
		if(p.getRow() == 3 && p.getCol() == 6)
			return new Position(3,3);
		if(p.getRow() == 3 && p.getCol() == 3)
			return new Position(3,6);
		if(p.getRow() == 4 && p.getCol() == 6)
			return new Position(4,3);
		if(p.getRow() == 4 && p.getCol() == 3)
			return new Position(4,6);
		if(p.getRow() == 5 && p.getCol() == 6)
			return new Position(5,3);
		if(p.getRow() == 5 && p.getCol() == 3)
			return new Position(5,6);
		//vertical
		if(p.getCol() == 4 && p.getRow() == 2)
			return new Position(4,6);
		if(p.getCol() == 4 && p.getRow() == 6)
			return new Position(4,2);
		if(p.getCol() == 5 && p.getRow() == 2)
			return new Position(5,6);
		if(p.getCol() == 5 && p.getRow() == 6)
			return new Position(5,2);
		
		
		return null;
	}
}
