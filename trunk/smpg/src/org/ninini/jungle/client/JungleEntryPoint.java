package org.ninini.jungle.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class JungleEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		final Graphics graphics = new Graphics();		
		Presenter presenter = new Presenter();		
		presenter.setView(graphics);		
		presenter.initializeHistory();		
		RootPanel.get().add(graphics);
	}

}
