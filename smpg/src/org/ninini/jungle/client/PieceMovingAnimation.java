package org.ninini.jungle.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Grid;

public class PieceMovingAnimation extends Animation {
	
	AbsolutePanel panel;
	Image start, end, moving;
	ImageResource piece;
	int startX, startY, endX, endY;
	int startWidth, startHeight;
	Audio audio;
	
	public PieceMovingAnimation(Image start, Image end,
								ImageResource piece, ImageResource blankRes, 
								Audio audio){
		this.start = start;
		this.end = end;
		this.piece = piece;
		panel = (AbsolutePanel) (((Grid) this.start.getParent()).getParent());
		startX = this.start.getAbsoluteLeft()-panel.getAbsoluteLeft();
		startY = this.start.getAbsoluteTop()-panel.getAbsoluteTop();
		endX = this.end.getAbsoluteLeft()-panel.getAbsoluteLeft();
		endY = this.end.getAbsoluteTop()-panel.getAbsoluteTop();
		startWidth = this.piece.getWidth();
		startHeight = this.piece.getHeight();
		this.audio = audio;
		
		this.start.setResource(blankRes);
		moving = new Image(piece);
		moving.setPixelSize(startWidth, startHeight);
		panel.add(moving, startX, startY);
		
	}

	@Override
	protected void onUpdate(double progress) {
		int x = (int) (startX + (endX - startX) * progress);
		int y = (int) (startY + (endY - startY) * progress);
		double scale = 1 + 0.5 * Math.sin(progress * Math.PI);
		int width = (int) (startWidth * scale);
		int height = (int) (startHeight * scale);
		moving.setPixelSize(width, height);
		x -= (width - startWidth) / 2;
		y -= (height - startHeight) / 2;
		
		panel.remove(moving);
		moving = new Image(piece.getSafeUri());
		moving.setPixelSize(width, height);
		panel.add(moving, x, y);
	}

	@Override
	protected void onComplete(){
		if(audio != null) audio.play();
		panel.remove(moving);
	}
}
