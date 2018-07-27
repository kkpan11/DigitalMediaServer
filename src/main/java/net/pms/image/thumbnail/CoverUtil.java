/*
 * Digital Media Server, for streaming digital media to UPnP AV or DLNA
 * compatible devices based on PS3 Media Server and Universal Media Server.
 * Copyright (C) 2016 Digital Media Server developers.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */
package net.pms.image.thumbnail;

import javax.annotation.Nullable;
import net.pms.PMS;
import org.jaudiotagger.tag.Tag;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class is the superclass of all cover utility implementations.
 * Cover utilities are responsible for getting media covers based
 * on information given by the caller.
 *
 * @author Nadahar
 */
public abstract class CoverUtil {

	private static Object instanceLock = new Object();
	private static CoverArtArchiveUtil coverArtArchiveInstance;

	/**
	 * Do not instantiate this class, use {@link #get()}.
	 */
	protected CoverUtil() {
	}

	/**
	 * Returns the static instance of the configured {@link CoverSupplier} type,
	 * or {@code null} if no {@link CoverSupplier} is configured.
	 *
	 * @return The {@link CoverUtil} or {@code null}.
	 */
	@Nullable
	public static CoverUtil get() {
		return get(PMS.getConfiguration().getAudioThumbnailMethod());
	}

	/**
	 * Returns the static instance of the specified {@link CoverSupplier} type,
	 * or {@code null} if no valid {@link CoverSupplier} is specified.
	 *
	 * @param supplier the {@link CoverSupplier} whose instance to get.
	 * @return The {@link CoverUtil} or {@code null}.
	 */
	@Nullable
	public static CoverUtil get(CoverSupplier supplier) {
		synchronized (instanceLock) {
			switch (supplier) {
				case COVER_ART_ARCHIVE:
					if (coverArtArchiveInstance == null) {
						coverArtArchiveInstance = new CoverArtArchiveUtil();
					}
					return coverArtArchiveInstance;
				default:
					return null;
			}
		}
	}

	/**
	 * Convenience method to find the first child {@link Element} of the given
	 * name.
	 *
	 * @param element the {@link Element} to search
	 * @param name the name of the child {@link Element}
	 * @return The found {@link Element} or null if not found
	 */
	protected Element getChildElement(Element element, String name) {
		NodeList list = element.getElementsByTagName(name);
		int listLength = list.getLength();
		for (int i = 0; i < listLength; i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(name) && node instanceof Element) {
				return (Element) node;
			}
		}
		return null;
	}

	/**
	 * Gets a thumbnail from the configured cover utility based on the specified
	 * {@link Tag}.
	 *
	 * @param tag the {@link tag} to use while searching for a cover.
	 * @return The thumbnail or {@code null} if none was found.
	 */
	public final byte[] getThumbnail(Tag tag) {
		boolean externalNetwork = PMS.getConfiguration().getExternalNetwork();
		return doGetThumbnail(tag, externalNetwork);
	}

	/**
	 * Gets a thumbnail from the configured cover utility based on the specified
	 * {@link Tag}.
	 *
	 * @param tag the {@link tag} to use while searching for a cover.
	 * @param externalNetwork {@code true} if the use of external networks
	 *            (Internet) is allowed, {@code false} otherwise.
	 * @return The thumbnail or {@code null} if none was found.
	 */
	protected abstract byte[] doGetThumbnail(Tag tag, boolean externalNetwork);
}