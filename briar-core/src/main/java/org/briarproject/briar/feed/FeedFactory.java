package org.briarproject.briar.feed;

import com.rometools.rome.feed.synd.SyndFeed;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.briar.api.feed.Feed;

import javax.annotation.Nullable;

interface FeedFactory {

	/**
	 * Create a new feed based on the feed url
	 * and the metadata of an existing {@link SyndFeed}.
	 */
	Feed createFeed(@Nullable String url, SyndFeed sf);

	/**
	 * Creates a new updated feed, based on the given existing feed,
	 * new metadata from the given {@link SyndFeed}
	 * and the time of the last feed entry.
	 */
	Feed updateFeed(Feed feed, SyndFeed sf, long lastEntryTime);

	/**
	 * De-serializes a {@link BdfDictionary} into a {@link Feed}.
	 */
	Feed createFeed(BdfDictionary d) throws FormatException;

	/**
	 * Serializes a {@link Feed} into a {@link BdfDictionary}.
	 */
	BdfDictionary feedToBdfDictionary(Feed feed);

}
