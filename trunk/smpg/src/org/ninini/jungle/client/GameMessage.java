package org.ninini.jungle.client;


import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.Messages;

@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
@DefaultLocale("en_US")
public interface GameMessage extends Messages {
	@DefaultMessage("Jungle Game")
	String gameName();
	@DefaultMessage("Whose trun")
	String whoseTurn();
	@DefaultMessage("Please Login.")
	String login();
	@DefaultMessage("Please find a game.")
	String findGame();
	@DefaultMessage("Quick Start")
	String quickStart();
	@DefaultMessage("Match with")
	String matchWith();
	@DefaultMessage("Online Players")
	String onlinePlayers();
	@DefaultMessage("Your matches")
	String yourMatches();
	@DefaultMessage("Rank: {0}")
	String rank(int rank);
	@DefaultMessage("Load Game")
	String loadGame();
	@DefaultMessage("Please login first.")
	String loginAlert();
	@DefaultMessage("Log in")
	String signIn();
	@DefaultMessage("Sign out")
	String signOut();
	@DefaultMessage("Your Turn")
	String yourTurn();
	@DefaultMessage("Black''s Turn")
	String blackTurn();
	@DefaultMessage("Red''s Turn")
	String redTurn();
	@DefaultMessage("Black Win")
	String blackWin();
	@DefaultMessage("Red Win")
	String redWin();
	@DefaultMessage("This game is finished.")
	String gameFinishAlert();
	@DefaultMessage("You are playing with {0} in match {1}")
	String oppoMsg(String oppoId, Long matchId);
	@DefaultMessage("Get Callback Fail")
	String callbackFail();
	@DefaultMessage("You will have a new game with {0}. Match ID: {1}")
	String newGameWith(String oppoId, Long matchId);
	@DefaultMessage("{0} has unpdate the state in match {1}")
	String updateState(String oppoId, Long matchId);
	@DefaultMessage("{0} wants to have a new game with you. Match ID: {1}")
	String newGameApply(String oppoId, Long matchId);
	@DefaultMessage("Looking for another player...")
	String lookForAnotherPlayer();
	@DefaultMessage("Welcome, {0}")
	String welcomMsg(String nickname);
	@DefaultMessage("This match beginned in {0}")
	String matchBeginTime(String dateMsg);
	@DefaultMessage("Please sign in to your Google Account to access the Jungle Game Application.")
	String loginPanelMessage();
	@DefaultMessage("Sign in")
	String signining();
	@DefaultMessage("Note: Click outside of this popup to close it.")
	String note();
	@DefaultMessage("Are you sure to log out?.")
	String logoutPanelMessage();
	@DefaultMessage("Yes")
	String yes();
	@DefaultMessage("Your Rank: {0}")
	String yourRank(int rank);
	@DefaultMessage("With Ai")
	String withAi();
	@DefaultMessage("Note: playing with AI does not update the state to server.")
	String withAiNote();
}
