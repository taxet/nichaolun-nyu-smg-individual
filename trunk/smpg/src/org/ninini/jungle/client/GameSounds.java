package org.ninini.jungle.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface GameSounds extends ClientBundle {
	@Source("sound/Bite.ogg")
	DataResource captureSound();
	
	@Source("sound/Cat.ogg")
	DataResource catSound();
	
	@Source("sound/Dive.ogg")
	DataResource ennterWaterSound();
	
	@Source("sound/Dog.ogg")
	DataResource dogSound();
	
	@Source("sound/InTrap.ogg")
	DataResource enterTrapSound();
	
	@Source("sound/Move.ogg")
	DataResource moveSound();
	
	@Source("sound/Landing.ogg")
	DataResource landingSound();

	@Source("sound/Victory.ogg")
	DataResource victorySound();
	
	@Source("sound/Wolf.ogg")
	DataResource wolfSound();
}
