package org.briarproject.briar.handshakekeyexchange;

import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;
import org.briarproject.bramble.api.sync.validation.ValidationManager;
import org.briarproject.bramble.api.versioning.ClientVersioningManager;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.handshakekeyexchange.HandshakeKeyExchangeManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HandshakeKeyExchangeModule {
	public static class EagerSingletons {
		@Inject
		HandshakeKeyExchangeManager handshakeKeyExchangeManager;
	}

	@Provides
	@Singleton
	HandshakeKeyExchangeManager handshakeKeyExchangeManager(
			LifecycleManager lifecycleManager,
			ValidationManager validationManager,
			ContactManager contactManager,
			ClientVersioningManager clientVersioningManager,
			ConversationManager conversationManager,
			HandshakeKeyExchangeManagerImpl handshakeKeyExchangeManager) {

		lifecycleManager.registerOpenDatabaseHook(handshakeKeyExchangeManager);
		validationManager
				.registerIncomingMessageHook(HandshakeKeyExchangeManager.CLIENT_ID,
						HandshakeKeyExchangeManager.MAJOR_VERSION, handshakeKeyExchangeManager);

		contactManager.registerContactHook(handshakeKeyExchangeManager);
		clientVersioningManager.registerClient(HandshakeKeyExchangeManager.CLIENT_ID, HandshakeKeyExchangeManager.MAJOR_VERSION,
				HandshakeKeyExchangeManager.MINOR_VERSION, handshakeKeyExchangeManager);
		conversationManager.registerConversationClient(handshakeKeyExchangeManager);
		return handshakeKeyExchangeManager;
	}
}