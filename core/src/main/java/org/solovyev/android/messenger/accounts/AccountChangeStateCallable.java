package org.solovyev.android.messenger.accounts;

import org.solovyev.android.messenger.MessengerApplication;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;

/**
 * User: serso
 * Date: 4/13/13
 * Time: 1:12 PM
 */
final class AccountChangeStateCallable implements Callable<Account> {

	@Nonnull
	static final String TASK_NAME = "account-change-state";

	@Nonnull
	private final Account account;

	AccountChangeStateCallable(@Nonnull Account account) {
		this.account = account;
	}

	@Override
	public Account call() throws Exception {
		final AccountService accountService = MessengerApplication.getServiceLocator().getAccountService();
		return accountService.changeAccountState(account, account.isEnabled() ? AccountState.disabled_by_user : AccountState.enabled);
	}
}