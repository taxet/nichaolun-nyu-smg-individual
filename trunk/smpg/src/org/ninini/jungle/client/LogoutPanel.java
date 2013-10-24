package org.ninini.jungle.client;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LogoutPanel extends PopupPanel  {
	private VerticalPanel logoutPanel = new VerticalPanel();
	private Label logoutLabel = new Label("Are you sure to log out?.");
	private Label noteLabel = new Label("Note: Click outside of this popup to close it.");
	private Anchor signOutLink = new Anchor("Yes");
	
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
