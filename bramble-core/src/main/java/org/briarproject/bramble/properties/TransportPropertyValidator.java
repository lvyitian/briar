package org.briarproject.bramble.properties;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.BdfMessageContext;
import org.briarproject.bramble.api.client.BdfMessageValidator;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.data.MetadataEncoder;
import org.briarproject.bramble.api.sync.Group;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.system.Clock;
import org.briarproject.nullsafety.NotNullByDefault;

import javax.annotation.concurrent.Immutable;

import static org.briarproject.bramble.api.plugin.TransportId.MAX_TRANSPORT_ID_LENGTH;
import static org.briarproject.bramble.api.properties.TransportPropertyConstants.MSG_KEY_LOCAL;
import static org.briarproject.bramble.api.properties.TransportPropertyConstants.MSG_KEY_TRANSPORT_ID;
import static org.briarproject.bramble.api.properties.TransportPropertyConstants.MSG_KEY_VERSION;
import static org.briarproject.bramble.util.ValidationUtils.checkLength;
import static org.briarproject.bramble.util.ValidationUtils.checkSize;

@Immutable
@NotNullByDefault
class TransportPropertyValidator extends BdfMessageValidator {

	TransportPropertyValidator(ClientHelper clientHelper,
			MetadataEncoder metadataEncoder, Clock clock) {
		// Accept transport properties in non-canonical form
		// TODO: Remove this after a reasonable migration period
		//  (added 2023-02-17)
		super(clientHelper, metadataEncoder, clock, false);
	}

	@Override
	protected BdfMessageContext validateMessage(Message m, Group g,
			BdfList body) throws FormatException {
		// Transport ID, version, properties
		checkSize(body, 3);
		// Transport ID
		String transportId = body.getString(0);
		checkLength(transportId, 1, MAX_TRANSPORT_ID_LENGTH);
		// Version
		long version = body.getLong(1);
		if (version < 0) throw new FormatException();
		// Properties
		BdfDictionary dictionary = body.getDictionary(2);
		clientHelper.parseAndValidateTransportProperties(dictionary);
		// Return the metadata
		BdfDictionary meta = new BdfDictionary();
		meta.put(MSG_KEY_TRANSPORT_ID, transportId);
		meta.put(MSG_KEY_VERSION, version);
		meta.put(MSG_KEY_LOCAL, false);
		return new BdfMessageContext(meta);
	}
}
