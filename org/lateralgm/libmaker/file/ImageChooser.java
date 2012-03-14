/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2007 Clam <clamisgood@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.file;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.components.ErrorDialog;
import org.lateralgm.libmaker.file.FileChooser.CustomFileChooser;
import org.lateralgm.libmaker.file.FileChooser.CustomFileFilter;

public final class ImageChooser
	{
	private static CustomFileChooser imageFc = null;

	public static File chooseImageFile(Component parent)
		{
		if (imageFc == null)
			{
			imageFc = new CustomFileChooser("/org/lateralgm/libmaker","LAST_IMAGE_DIR"); //$NON-NLS-1$ //$NON-NLS-2$
			imageFc.setAccessory(new Accessory(imageFc));
			String[] exts = ImageIO.getReaderFileSuffixes();
			for (int i = 0; i < exts.length; i++)
				exts[i] = "." + exts[i]; //$NON-NLS-1$
			String allSpiImages = Messages.getString("Util.ALL_SPI_IMAGES"); //$NON-NLS-1$
			CustomFileFilter filt = new CustomFileFilter(allSpiImages,exts);
			imageFc.addChoosableFileFilter(filt);
			for (String element : exts)
				{
				imageFc.addChoosableFileFilter(new CustomFileFilter(Messages.format("Util.FILES", //$NON-NLS-1$
						element),element));
				}
			imageFc.setFileFilter(filt);
			}
		if (imageFc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
			return imageFc.getSelectedFile();
		return null;
		}

	public static BufferedImage getValidImage(Frame parent)
		{
		File f = chooseImageFile(parent);
		if (f == null) return null;
		try
			{
			return ImageIO.read(f);
			}
		catch (IOException e)
			{
			new ErrorDialog(parent,Messages.getString("Util.ERROR_TITLE"), //$NON-NLS-1$
					Messages.format("Util.ERROR_LOADING",f),e).setVisible(true); //$NON-NLS-1$
			}
		return null;
		}

	private ImageChooser()
		{
		}

	public static class Accessory extends JLabel implements PropertyChangeListener
		{
		private static final long serialVersionUID = 1L;

		private ImageIcon prev = null;

		private static final int WIDTH = 150;
		private static final int HEIGHT = 150;

		public Accessory(JFileChooser choose)
			{
			choose.addPropertyChangeListener(this);
			setPreferredSize(new Dimension(WIDTH,HEIGHT));
			setHorizontalAlignment(SwingConstants.CENTER);
			}

		public static ImageIcon getScaledIcon(BufferedImage img, int width, int height)
			{
			if (img.getWidth() > width && img.getHeight() > height)
				return new ImageIcon(img.getScaledInstance(img.getWidth() >= img.getHeight() ? width : -1,
						img.getHeight() > img.getWidth() ? height : -1,Image.SCALE_FAST));
			if (img.getWidth() > width || img.getHeight() > height)
				return new ImageIcon(img.getScaledInstance(img.getWidth() > width ? width : -1,
						img.getHeight() > height ? height : -1,Image.SCALE_FAST));
			return new ImageIcon(img);
			}

		public void propertyChange(PropertyChangeEvent e)
			{
			if (e.getPropertyName() != JFileChooser.SELECTED_FILE_CHANGED_PROPERTY || !isShowing())
				return;
			File f = (File) e.getNewValue();
			if (f == null)
				prev = null;
			else
				{
				BufferedImage img = null;
				try
					{
					img = ImageIO.read(f); //can return null
					}
				catch (Throwable t)
					{
					//img = null
					}
				prev = img == null ? null : getScaledIcon(img,WIDTH,HEIGHT);
				}
			setIcon(prev);
			}
		}
	}