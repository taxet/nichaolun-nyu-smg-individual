package org.ninini.jungle.client;

import java.util.Set;

import org.ninini.jungle.shared.Player;

import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends XsrfProtectedService {
	/*public LoginInfo login(String requestUrl);
	public Set<Player> onlineList(String myEmail);*/
	public String fbLogin(LoginInfo fbInfo);
}
