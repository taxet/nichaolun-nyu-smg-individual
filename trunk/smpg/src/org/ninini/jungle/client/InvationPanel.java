package org.ninini.jungle.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtfb.sdk.FBCore;

public class InvationPanel extends PopupPanel  {
	private VerticalPanel invationPanel = new VerticalPanel();
	private Label invationLabel = new Label(Graphics.gameMessage.invationPanelMessage());
	private Label noteLabel = new Label(Graphics.gameMessage.note());
	
	private final FBCore fbCore = GWT.create(FBCore.class);
	
	public InvationPanel(final String oppoId){
		super(true);
		invationPanel.add(invationLabel);
		invationPanel.add(noteLabel);
		HorizontalPanel buttons = new HorizontalPanel();
		Button invite = new Button();
		invite.setText(Graphics.gameMessage.invite());
		invite.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				//set request
				FbRequest request = (FbRequest)JavaScriptObject.createObject().cast();
				request.setMethod("apprequests");
				request.setTitle("Jungle Game");
				request.setMessage("I want to play jungle game with you.");
				request.setTo(oppoId);
				fbCore.ui(request, new AsyncCallback<JavaScriptObject>(){

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Invite failed");
					}

					@Override
					public void onSuccess(JavaScriptObject result) {
						Window.alert("Invite Success");
		                InvationPanel.this.hide();
					}
					
				});
			}
			
		});
		Button cancle = new Button();
		cancle.setText(Graphics.gameMessage.cancle());
		cancle.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
                InvationPanel.this.hide();
			}
			
		});
		buttons.add(invite);
		buttons.add(cancle);
		invationPanel.add(buttons);
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.setWidget(invationPanel);
	}
}
