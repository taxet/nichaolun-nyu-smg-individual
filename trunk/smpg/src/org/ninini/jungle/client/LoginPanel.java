package org.ninini.jungle.client;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginPanel extends PopupPanel {
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(Graphics.gameMessage.loginPanelMessage());
	private Label noteLabel = new Label(Graphics.gameMessage.note());
	private Anchor signInLink = new Anchor(Graphics.gameMessage.signining());
	
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
