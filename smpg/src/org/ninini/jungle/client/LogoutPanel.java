package org.ninini.jungle.client;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LogoutPanel extends PopupPanel  {
	private VerticalPanel logoutPanel = new VerticalPanel();
	private Label logoutLabel = new Label(Graphics.gameMessage.logoutPanelMessage());
	private Label noteLabel = new Label(Graphics.gameMessage.note());
	private Anchor signOutLink = new Anchor(Graphics.gameMessage.yes());
	
	public LogoutPanel(String url){
		super(true);
		signOutLink.setHref(url);
		logoutPanel.add(logoutLabel);
		logoutPanel.add(noteLabel);
		logoutPanel.add(signOutLink);
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.setWidget(logoutPanel);
	}
}
