package org.ninini.jungle.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface GameImages extends ClientBundle {
	@Source("normal_tile.png")
	ImageResource normalTile();	
	@Source("river_tile.png")
	ImageResource riverTile();	
	
	@Source("black_trap.png")
	ImageResource blackTrap();
	@Source("black_den.png")
	ImageResource blackDen();
	
	@Source("red_trap.png")
	ImageResource redTrap();
	@Source("red_den.png")
	ImageResource redDen();
	
	@Source("black_rat.png")
	ImageResource blackRat();
	@Source("black_cat.png")
	ImageResource blackCat();
	@Source("black_dog.png")
	ImageResource blackDog();
	@Source("black_wolf.png")
	ImageResource blackWolf();
	@Source("black_leopard.png")
	ImageResource blackLeopard();
	@Source("black_tiger.png")
	ImageResource blackTiger();
	@Source("black_lion.png")
	ImageResource blackLion();
	@Source("black_elephant.png")
	ImageResource blackElephant();

	@Source("red_rat.png")
	ImageResource redRat();
	@Source("red_cat.png")
	ImageResource redCat();
	@Source("red_dog.png")
	ImageResource redDog();
	@Source("red_wolf.png")
	ImageResource redWolf();
	@Source("red_leopard.png")
	ImageResource redLeopard();
	@Source("red_tiger.png")
	ImageResource redTiger();
	@Source("red_lion.png")
	ImageResource redLion();
	@Source("red_elephant.png")
	ImageResource redElephant();
}
