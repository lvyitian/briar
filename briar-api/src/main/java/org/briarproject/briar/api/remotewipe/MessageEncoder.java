package org.briarproject.briar.api.remotewipe;

public interface MessageEncoder {
	byte[] encodeSetupMessage();

	byte[] encodeWipeMessage();
}