/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * 
 * LibMaker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LibMaker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License (COPYING) for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.lateralgm.libmaker;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class Messages
	{
	private static final String MESSAGES_NAME = "org.lateralgm.libmaker.messages"; //$NON-NLS-1$
	private static final ResourceBundle MESSAGES_BUNDLE = ResourceBundle.getBundle(MESSAGES_NAME);

	private static final String ICONS_PATH = "org/lateralgm/libmaker/"; //$NON-NLS-1$
	private static final Properties ICONS_PROPS = new Properties();

	static
		{
		try
			{
			ICONS_PROPS.load(Messages.class.getResourceAsStream("icons.properties")); //$NON-NLS-1$
			}
		catch (IOException e)
			{
			System.err.println("Unable to read icons.properties"); //$NON-NLS-1$
			}
		}

	private Messages()
		{
		}

	public static String getString(String key)
		{
		try
			{
			return MESSAGES_BUNDLE.getString(key);
			}
		catch (MissingResourceException e)
			{
			return '!' + key + '!';
			}
		}

	public static String format(String key, Object...arguments)
		{
		try
			{
			String p = MESSAGES_BUNDLE.getString(key);
			return MessageFormat.format(p,arguments);
			}
		catch (MissingResourceException e)
			{
			return '!' + key + '!';
			}
		}

	protected static ImageIcon findIcon(String filename)
		{
		String location = ICONS_PATH + filename;
		ImageIcon ico = new ImageIcon(location);
		if (ico.getIconWidth() == -1)
			{
			URL url = Messages.class.getClassLoader().getResource(location);
			if (url != null) ico = new ImageIcon(url);
			}
		return ico;
		}

	public static ImageIcon getIconForKey(String key)
		{
		String filename = ICONS_PROPS.getProperty(key);
		if (filename == null || filename.isEmpty()) return null;
		return findIcon(filename);
		}

	public static BufferedImage getImageForKey(String key)
		{
		String filename = ICONS_PROPS.getProperty(key);
		if (filename == null || filename.isEmpty()) return null;
		URL url = Messages.class.getClassLoader().getResource(ICONS_PATH + filename);
		if (url == null) return null;
		try
			{
			return ImageIO.read(url);
			}
		catch (IOException e)
			{
			return null;
			}
		}
	}
