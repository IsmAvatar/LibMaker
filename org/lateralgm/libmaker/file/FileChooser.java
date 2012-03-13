/*
 * Copyright (C) 2007-2012 IsmAvatar <IsmAvatar@gmail.com>
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

package org.lateralgm.libmaker.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.Library;
import org.lateralgm.libmaker.backend.Library.Format;
import org.lateralgm.libmaker.backend.Library.LglFormat;
import org.lateralgm.libmaker.backend.Library.LibFormat;
import org.lateralgm.libmaker.components.ErrorDialog;
import org.lateralgm.libmaker.components.NumberField;
import org.lateralgm.libmaker.file.LibReader.LibFormatException;

public class FileChooser
	{
	protected JFrame parent;
	protected CustomFileChooser fc = new CustomFileChooser("/org/lateralgm/libmaker","LAST_FILE_DIR"); //$NON-NLS-1$ //$NON-NLS-2$
	protected SelectionAccessory accessory = new SelectionAccessory();

	public FileChooser(JFrame parent)
		{
		this.parent = parent;
		String[] exts = { LglFormat.EXTENSION,LibFormat.EXTENSION };
		String msg = Messages.format("FileChooser.FILE_FILTER",implode(", ",exts)); //$NON-NLS-1$ //$NON-NLS-2$
		FileFilter normalFilter = new CustomFileFilter(msg,exts);
		fc.addChoosableFileFilter(normalFilter);
		fc.setFileFilter(normalFilter);
		}

	private static String implode(String delim, String...args)
		{
		if (args.length == 0) return new String();
		StringBuilder sb = new StringBuilder(args[0]);
		for (int i = 1; i < args.length; i++)
			sb.append(delim).append(args[i]);
		return sb.toString();
		}

	public Library openNewFile()
		{
		fc.setAccessory(null);
		if (fc.showOpenDialog(parent) != CustomFileChooser.APPROVE_OPTION) return null;
		File f = fc.getSelectedFile();
		if (f == null) return null;
		return open(f);
		}

	public Library open(File f)
		{
		if (f == null) return null;
		try
			{
			return LibReader.loadFile(f);
			}
		catch (LibFormatException ex)
			{
			new ErrorDialog(parent,Messages.getString("FileChooser.ERROR_OPEN_TITLE"), //$NON-NLS-1$
					Messages.getString("FileChooser.ERROR_OPEN"),ex).setVisible(true); //$NON-NLS-1$
			}
		return null;
		}

	public boolean saveNewFile(Library lib)
		{
		fc.setAccessory(accessory);
		fc.setSelectedFile(lib.sourceFile);
		File file;
		do //repeatedly display dialog until a valid response is given
			{
			if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return false;
			file = fc.getSelectedFile();
			//Force the extension, since LGM and GM only read files with the extension.
			String ext = accessory.getSelectedFormat().getExtension();
			if (!file.getName().endsWith(ext)) file = new File(file.getPath() + ext);
			if (file.exists())
				{
				int result = JOptionPane.showConfirmDialog(
						parent,
						Messages.format("FileChooser.CONFIRM_REPLACE",file.getPath()), //$NON-NLS-1$
						Messages.getString("FileChooser.CONFIRM_REPLACE_TITLE"),JOptionPane.YES_NO_CANCEL_OPTION, //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.NO_OPTION) file = null;
				if (result == JOptionPane.CANCEL_OPTION) return false;
				}
			}
		while (file == null);
		return save(lib,file,accessory.getSelectedFormat());
		}

	public boolean save(Library lib, File f, Format fmt)
		{
		if (lib == null) return false;
		lib.sourceFile = f;
		lib.format = fmt;

		try
			{
			GmStreamEncoder out = new GmStreamEncoder(f);
			if (fmt instanceof LibFormat)
				LibWriter.saveLib(lib,out,((LibFormat) lib.format).getVersion());
			else if (lib.format instanceof LglFormat)
				LibWriter.saveLgl(lib,out,((LglFormat) lib.format).getIconColumns());
			out.close();
			return true;
			}
		catch (IOException ex)
			{
			new ErrorDialog(parent,Messages.getString("FileChooser.ERROR_SAVE_TITLE"), //$NON-NLS-1$
					Messages.getString("FileChooser.ERROR_SAVE"),ex).setVisible(true); //$NON-NLS-1$
			}

		return false;
		}

	//Helper Classes
	public class SelectionAccessory extends JPanel implements ActionListener
		{
		private static final long serialVersionUID = 1L;

		protected JRadioButton lgl, lib;
		protected NumberField iconColumns;
		protected JRadioButton lib520, lib500;

		public SelectionAccessory()
			{
			super();

			initKeyComponents();

			GroupLayout layout = new GroupLayout(this);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			setLayout(layout);

			layoutComponents(layout);
			}

		protected void initKeyComponents()
			{
			iconColumns = new NumberField(1,50,5);
			lgl = new JRadioButton(Messages.getString("Accessory.LGL"),true); //$NON-NLS-1$
			lib = new JRadioButton(Messages.getString("Accessory.LIB")); //$NON-NLS-1$
			ButtonGroup bg = new ButtonGroup();
			bg.add(lgl);
			bg.add(lib);
			lgl.addActionListener(this);
			lib.addActionListener(this);
			lib520 = new JRadioButton(Messages.getString("Accessory.LIB520"),true); //$NON-NLS-1$
			lib500 = new JRadioButton(Messages.getString("Accessory.LIB500"),true); //$NON-NLS-1$
			bg = new ButtonGroup();
			bg.add(lib520);
			bg.add(lib500);
			}

		protected void layoutComponents(GroupLayout layout)
			{
			JLabel lFormat = new JLabel(Messages.getString("Accessory.FORMAT")); //$NON-NLS-1$
			JLabel lIconColumns = new JLabel(Messages.getString("Accessory.COLUMNS")); //$NON-NLS-1$
			JLabel lVersion = new JLabel(Messages.getString("Accessory.VERSION")); //$NON-NLS-1$

			int PREF = GroupLayout.PREFERRED_SIZE, DEF = GroupLayout.DEFAULT_SIZE;

			layout.setHorizontalGroup(layout.createParallelGroup()
			/**/.addComponent(lFormat)
			/**/.addComponent(lgl)
			/**/.addGroup(layout.createSequentialGroup()
			/*	*/.addGap(15,15,15 /*indent*/)
			/*	*/.addGroup(layout.createParallelGroup()
			/*		*/.addComponent(lIconColumns)
			/*		*/.addComponent(iconColumns)))
			/**/.addComponent(lib)
			/**/.addGroup(layout.createSequentialGroup()
			/*	*/.addGap(15,15,15 /*indent*/)
			/*	*/.addGroup(layout.createParallelGroup()
			/*		*/.addComponent(lVersion)
			//			/*		*/.addGroup(layout.createSequentialGroup())
			/*		*/.addComponent(lib520)
			/*		*/.addComponent(lib500))));
			layout.setVerticalGroup(layout.createSequentialGroup()
			/**/.addComponent(lFormat)
			/**/.addComponent(lgl)
			/**/.addGroup(layout.createParallelGroup()
			/*	*/.addGroup(layout.createSequentialGroup()
			/*		*/.addComponent(lIconColumns)
			/*		*/.addComponent(iconColumns,PREF,DEF,PREF)))
			/**/.addComponent(lib)
			/*		*/.addComponent(lVersion)
			//			/*		*/.addGroup(layout.createSequentialGroup())
			/*		*/.addComponent(lib520)
			/*		*/.addComponent(lib500));
			}

		public void setSelectedFormat(Format fmt)
			{
			if (fmt instanceof LglFormat)
				{
				lgl.setSelected(true);
				iconColumns.setValue(((LglFormat) fmt).getIconColumns());
				}
			else if (fmt instanceof LibFormat)
				{
				lib.setSelected(true);
				(((LibFormat) fmt).getVersion() == 500 ? lib500 : lib520).setSelected(true);
				}
			}

		public Format getSelectedFormat()
			{
			if (lgl.isSelected()) return new LglFormat(iconColumns.getIntValue());
			if (lib.isSelected()) return lib500.isSelected() ? LibFormat.LIB500 : LibFormat.LIB520;
			return null; //something messed with our panel...
			}

		public int getIconColumns()
			{
			return iconColumns.getIntValue();
			}

		@Override
		public void actionPerformed(ActionEvent e)
			{
			iconColumns.setEnabled(e.getSource() == lgl);
			lib500.setEnabled(e.getSource() == lib);
			lib520.setEnabled(e.getSource() == lib);
			}
		}

	public static class CustomFileChooser extends JFileChooser
		{
		private static final long serialVersionUID = 1L;
		private Preferences prefs;
		private String propertyName;

		public CustomFileChooser(String node, String propertyName)
			{
			this.propertyName = propertyName;
			prefs = Preferences.userRoot().node(node);
			setCurrentDirectory(new File(prefs.get(propertyName,getCurrentDirectory().getAbsolutePath())));
			}

		public void approveSelection()
			{
			super.approveSelection();
			saveDir();
			}

		public void cancelSelection()
			{
			super.cancelSelection();
			saveDir();
			}

		private void saveDir()
			{
			prefs.put(propertyName,getCurrentDirectory().getAbsolutePath());
			}
		}

	public static class CustomFileFilter extends FileFilter implements FilenameFilter
		{
		private ArrayList<String> ext = new ArrayList<String>();
		private String desc;

		/**
		 * Gets the extension part of the given filename, including the period
		 * @param filename
		 * @return the extension, including period
		 */
		public static String getExtension(String filename)
			{
			int p = filename.lastIndexOf('.');
			if (p == -1) return null;
			return filename.substring(p).toLowerCase(Locale.ENGLISH);
			}

		public CustomFileFilter(String desc, String...ext)
			{
			this.desc = desc;
			for (String element : ext)
				this.ext.add(element);
			}

		public boolean accept(File f)
			{
			if (f.isDirectory()) return true;
			return accept(f,f.getPath());
			}

		public boolean accept(File dir, String name)
			{
			if (ext.size() == 0) return true;
			//if (f.isDirectory()) return true;
			String s = getExtension(name);
			if (s == null) return false;
			return ext.contains(s);
			}

		public String getDescription()
			{
			return desc;
			}

		public String[] getExtensions()
			{
			return ext.toArray(new String[0]);
			}
		}
	}
