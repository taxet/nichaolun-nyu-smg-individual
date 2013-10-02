package org.ninini.jungle.client.games;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class ChessEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		final Graphics graphics = new Graphics();
		RootPanel.get().add(graphics);
	}

}
