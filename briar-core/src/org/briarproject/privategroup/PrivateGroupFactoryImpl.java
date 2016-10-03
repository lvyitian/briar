package org.briarproject.privategroup;

import org.briarproject.api.FormatException;
import org.briarproject.api.clients.ClientHelper;
import org.briarproject.api.data.BdfList;
import org.briarproject.api.identity.Author;
import org.briarproject.api.privategroup.PrivateGroup;
import org.briarproject.api.privategroup.PrivateGroupFactory;
import org.briarproject.api.sync.Group;
import org.briarproject.api.sync.GroupFactory;
import org.briarproject.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

import javax.inject.Inject;

import static org.briarproject.api.privategroup.PrivateGroupConstants.GROUP_SALT_LENGTH;
import static org.briarproject.api.privategroup.PrivateGroupConstants.MAX_GROUP_NAME_LENGTH;

class PrivateGroupFactoryImpl implements PrivateGroupFactory {

	private final GroupFactory groupFactory;
	private final ClientHelper clientHelper;
	private final SecureRandom random;

	@Inject
	PrivateGroupFactoryImpl(GroupFactory groupFactory,
			ClientHelper clientHelper, SecureRandom random) {

		this.groupFactory = groupFactory;
		this.clientHelper = clientHelper;
		this.random = random;
	}

	@NotNull
	@Override
	public PrivateGroup createPrivateGroup(String name, Author author) {
		int length = StringUtils.toUtf8(name).length;
		if (length == 0) throw new IllegalArgumentException("Group name empty");
		if (length > MAX_GROUP_NAME_LENGTH)
			throw new IllegalArgumentException(
					"Group name exceeds maximum length");
		byte[] salt = new byte[GROUP_SALT_LENGTH];
		random.nextBytes(salt);
		return createPrivateGroup(name, author, salt);
	}

	@NotNull
	@Override
	public PrivateGroup createPrivateGroup(String name, Author author,
			byte[] salt) {
		try {
			BdfList group = BdfList.of(
					name,
					author.getName(),
					author.getPublicKey(),
					salt
			);
			byte[] descriptor = clientHelper.toByteArray(group);
			Group g = groupFactory
					.createGroup(PrivateGroupManagerImpl.CLIENT_ID, descriptor);
			return new PrivateGroup(g, name, author, salt);
		} catch (FormatException e) {
			throw new RuntimeException(e);
		}
	}

}
