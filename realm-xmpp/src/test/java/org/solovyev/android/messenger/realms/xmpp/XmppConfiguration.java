package org.solovyev.android.messenger.realms.xmpp;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 3/4/13
 * Time: 8:40 PM
 */
public final class XmppConfiguration {

	public static final String USER_LOGIN = "messengerplusplus@gmail.com";
	public static final String USER_LOGIN2 = "messengerplusplus2@gmail.com";

	@Nonnull
	private final static XmppAccountConfiguration instance = new XmppAccountConfiguration("talk.google.com", USER_LOGIN, "Qwerty!@");

	@Nonnull
	private final static XmppAccountConfiguration instance2 = new XmppAccountConfiguration("talk.google.com", USER_LOGIN2, "Qwerty!@");

	private XmppConfiguration() {
	}

	@Nonnull
	public static XmppAccountConfiguration getInstance() {
		return instance;
	}

	@Nonnull
	public static XmppAccountConfiguration getInstance2() {
		return instance2;
	}
}