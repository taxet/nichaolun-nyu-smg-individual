package org.ninini.jungle.client;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginPanel extends PopupPanel {
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label("Please sign in to your Google Account to access the Chess Application.");
	private Label noteLabel = new Label("Note: Click outside of this popup to close it.");
	private Anchor signInLink = new Anchor("Sign In");
	
	public LoginPanel(String url){
		super(true);
		signInLink.setHref(url);
		loginPanel.add(loginLabel);
		loginPanel.add(noteLabel);
		loginPanel.add(signInLink);
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.setWidget(loginPanel);
	}
}
