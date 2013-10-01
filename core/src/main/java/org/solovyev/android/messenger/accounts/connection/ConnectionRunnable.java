package org.solovyev.android.messenger.accounts.connection;

import android.util.Log;
import org.solovyev.android.messenger.accounts.AccountConnectionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.messenger.App.getAccountService;
import static org.solovyev.android.messenger.App.getExceptionHandler;
import static org.solovyev.android.messenger.accounts.AccountState.disabled_by_app;
import static org.solovyev.android.messenger.accounts.connection.DefaultAccountConnections.TAG;

class ConnectionRunnable implements Runnable {

	private static final int RETRY_CONNECTION_ATTEMPT_COUNT = 5;
	public static final long DEFAULT_RECOVERY_SLEEP_MILLIS = 5000L;

	@Nonnull
	private final AccountConnection connection;

	private final long recoverySleepMillis;

	public ConnectionRunnable(@Nonnull AccountConnection connection) {
		this(connection, DEFAULT_RECOVERY_SLEEP_MILLIS);
	}

	ConnectionRunnable(@Nonnull AccountConnection connection, long recoverySleepMillis) {
		this.connection = connection;
		this.recoverySleepMillis = recoverySleepMillis;
	}

	@Override
	public void run() {
		startConnection(0, null);
	}

	private void startConnection(int attempt, @Nullable AccountConnectionException lastError) {
		Log.d(TAG, "Account start requested, attempt: " + attempt);

		if (attempt > RETRY_CONNECTION_ATTEMPT_COUNT) {
			onMaxAttemptsReached(lastError);
		} else {
			if (connection.isStopped()) {
				try {
					if (connection.getAccount().isEnabled()) {
						Log.d(TAG, "Account is enabled => starting connection...");
						connection.start();
						Log.d(TAG, "Connection is successfully established => no more work is needed on background thread. Terminating...");
					}
				} catch (AccountConnectionException e) {
					onConnectionException(attempt, e);
				} catch (Throwable e) {
					onConnectionException(attempt, e);
				}
			}
		}
	}

	private void onConnectionException(int attempt, @Nonnull Throwable e) {
		onConnectionException(attempt, new AccountConnectionException(connection.getAccount().getId(), e));
	}

	private void onConnectionException(int attempt, AccountConnectionException e) {
		Log.w(TAG, "Account connection error occurred, connection attempt: " + attempt, e);

		if (!connection.isStopped()) {
			connection.stop();
		}

		startConnectionDelayed(attempt, e);
	}

	private void onMaxAttemptsReached(@Nullable AccountConnectionException lastError) {
		Log.d(TAG, "Max retry count reached => stopping...");

		if (!connection.isStopped()) {
			connection.stop();
		}

		if (lastError != null) {
			getExceptionHandler().handleException(lastError);
		}

		getAccountService().changeAccountState(connection.getAccount(), disabled_by_app);
	}

	private void startConnectionDelayed(int attempt, @Nullable AccountConnectionException lastException) {
		try {
			// let's wait a little bit - may be the exception was caused by connectivity problem
			Thread.sleep(recoverySleepMillis);
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			startConnection(attempt + 1, lastException);
		}
	}
}