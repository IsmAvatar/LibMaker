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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.PAction;
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.backend.Argument.PArgument;
import org.lateralgm.libmaker.backend.Library;
import org.lateralgm.libmaker.backend.Library.PLibrary;
import org.lateralgm.libmaker.backend.PropertyMap;
import org.lateralgm.libmaker.backend.PropertyMap.PropertyListener;
import org.lateralgm.libmaker.backend.PropertyMap.PropertyUpdateEvent;
import org.lateralgm.libmaker.components.AboutBox;
import org.lateralgm.libmaker.components.GmMenu;
import org.lateralgm.libmaker.components.ObservableList.ListUpdateEvent;
import org.lateralgm.libmaker.components.ObservableList.ListUpdateListener;
import org.lateralgm.libmaker.file.FileChooser;
import org.lateralgm.libmaker.mockui.MockUI;

public class LibMaker extends JFrame implements ActionListener
	{
	private static final long serialVersionUID = 1L;

	protected FileChooser fc;
	protected MockUI ui;
	protected Library currentLib;
	protected ChangeListener cl;
	protected boolean modified;

	public LibMaker()
		{
		super(); //Title gets set when library is set
		setIconImage(Messages.getIconForKey("LibMaker.FRAME_ICON").getImage()); //$NON-NLS-1$
		ui = new MockUI();
		fc = new FileChooser(this);

		setJMenuBar(createMenu());
		add(createTool(),BorderLayout.NORTH);

		add(ui,BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
					{
					if (!saveConfirm()) return;
					System.exit(0);
					return;
					}
			});
		pack();
		setLocationRelativeTo(null);
		setMinimumSize(getSize());

		setLibrary(new Library()); //Now that we have our needed size, setup with our initial library
		}

	protected JMenuBar createMenu()
		{
		JMenuBar mb = new JMenuBar();
		GmMenu menu;

		mb.add(menu = new GmMenu(Messages.getString("Menu.FILE"),this)); //$NON-NLS-1$
		menu.addItem("Menu.NEW",KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK); //$NON-NLS-1$
		menu.addItem("Menu.OPEN",KeyEvent.VK_O,InputEvent.CTRL_DOWN_MASK); //$NON-NLS-1$
		menu.addSeparator();
		menu.addItem("Menu.SAVE",KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK); //$NON-NLS-1$
		menu.addItem("Menu.SAVEAS"); //$NON-NLS-1$
		menu.addSeparator();
		menu.addItem("Menu.MERGE",KeyEvent.VK_M,InputEvent.CTRL_DOWN_MASK); //$NON-NLS-1$
		menu.addSeparator();
		menu.addItem("Menu.EXIT",KeyEvent.VK_F4,InputEvent.ALT_DOWN_MASK); //$NON-NLS-1$

		mb.add(menu = new GmMenu(Messages.getString("Menu.HELP"),this)); //$NON-NLS-1$
		menu.addItem("Menu.MANUAL",KeyEvent.VK_F1,0); //$NON-NLS-1$
		menu.addSeparator();
		menu.addItem("Menu.ABOUT"); //$NON-NLS-1$

		return mb;
		}

	protected void addMenuItem(JMenu mb, String key)
		{
		JMenuItem mi = new JMenuItem(key);
		mb.add(mi);
		mi.setActionCommand(key);
		mi.addActionListener(this);
		}

	protected JToolBar createTool()
		{
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);

		addToolItem(tb,"Tool.NEW"); //$NON-NLS-1$
		addToolItem(tb,"Tool.OPEN"); //$NON-NLS-1$
		addToolItem(tb,"Tool.SAVE"); //$NON-NLS-1$
		addToolItem(tb,"Tool.SAVEAS"); //$NON-NLS-1$
		tb.addSeparator();
		addToolItem(tb,"Tool.MANUAL"); //$NON-NLS-1$

		return tb;
		}

	protected void addToolItem(JToolBar tb, String key)
		{
		ImageIcon icon = Messages.getIconForKey(key);
		String tip = Messages.getString(key);
		JButton b = new JButton(icon);
		if (icon == null) b.setText(key);
		b.setToolTipText(tip);
		b.setActionCommand(key);
		b.addActionListener(this);
		tb.add(b);
		}

	public void save(boolean as)
		{
		boolean b;
		if (as || currentLib.sourceFile == null)
			b = fc.saveNewFile(currentLib);
		else
			b = fc.save(currentLib,currentLib.sourceFile,currentLib.format);
		if (b) setModified(false);
		}

	public void setLibrary(Library lib)
		{
		if (cl != null) cl.dispose();
		currentLib = lib;
		modified = false;
		ui.setLibrary(lib);
		setTitleFile(lib.sourceFile);
		cl = new ChangeListener(lib);
		}

	class ChangeListener implements ListUpdateListener
		{
		Library lib;
		List<MyPropList<?>> lists = new LinkedList<MyPropList<?>>();

		public ChangeListener(Library lib)
			{
			this.lib = lib;
			lib.actions.addListUpdateListener(this);
			lists.add(new MyPropList<PLibrary>(lib.properties));
			for (Action a : lib.actions)
				{
				lists.add(new MyPropList<PAction>(a.properties));
				for (Argument arg : a.arguments)
					lists.add(new MyPropList<PArgument>(arg.properties));
				}
			}

		public void dispose()
			{
			lib.actions.removeListUpdateListener(this);
			for (MyPropList<?> mpl : lists)
				mpl.dispose();
			}

		public void fire()
			{
			setModified(true);
			}

		class MyPropList<K extends Enum<K>> implements PropertyListener<K>
			{
			protected PropertyMap<K> map;

			public MyPropList(PropertyMap<K> map)
				{
				this.map = map;
				map.addPropertyListener(this);
				}

			public void dispose()
				{
				map.removePropertyListener(this);
				}

			@Override
			public void propertyUpdate(PropertyUpdateEvent<K> evt)
				{
				fire();
				}
			}

		@Override
		public void listUpdate(ListUpdateEvent evt)
			{
			fire();
			}
		}

	public void setModified(boolean mod)
		{
		if (mod == modified) return;
		modified = mod;
		setModifiedTitleIndicator(mod);
		}

	public boolean saveConfirm()
		{
		if (modified)
			{
			int r = JOptionPane.showConfirmDialog(this,Messages.getString("LibMaker.SAVE_CHANGES")); //$NON-NLS-1$
			if (r == JOptionPane.CANCEL_OPTION) return false;
			if (r == JOptionPane.YES_OPTION) save(false);
			}
		return true;
		}

	/**
	 * Set whether to display an indicator in the title
	 * that indicates whether the file has been modified or not.
	 * Currently the indicator is implemented as an appended asterisk (*).
	 * <p>
	 * Setting the indicator to the same value twice has no effect.
	 * <p>
	 * Note that any method that sets the title (like setTitleFile)
	 * will clear the indicator. This is usually convenient since
	 * you usually only set the title when a file is created or loaded,
	 * in which case the file will already be unmodified.
	 * @param mod
	 */
	public void setModifiedTitleIndicator(boolean mod)
		{
		final String ind = "*";
		String title = getTitle();
		if (!mod ^ title.endsWith(ind)) return;
		setTitle(mod ? title + ind : title.substring(0,title.length() - 1));
		}

	public void setTitleFile(File f)
		{
		String name = f == null ? Messages.getString("LibMaker.NEWLIB") : f.getName(); //$NON-NLS-1$
		setTitle(Messages.format("LibMaker.TITLE",name)); //$NON-NLS-1$
		}

	@Override
	public void actionPerformed(ActionEvent e)
		{
		String cmd = e.getActionCommand();
		if (cmd.endsWith(".NEW")) //$NON-NLS-1$
			{
			if (!saveConfirm()) return;
			setLibrary(new Library());
			return;
			}
		if (cmd.endsWith(".OPEN")) //$NON-NLS-1$
			{
			if (!saveConfirm()) return;
			Library lib = fc.openNewFile();
			if (lib != null) setLibrary(lib);
			return;
			}
		if (cmd.endsWith(".SAVE")) //$NON-NLS-1$
			{
			save(false);
			return;
			}
		if (cmd.endsWith(".SAVEAS")) //$NON-NLS-1$
			{
			save(true);
			return;
			}
		if (cmd.endsWith(".MERGE")) //$NON-NLS-1$
			{
			Library lib = fc.openNewFile();
			if (lib != null) currentLib.actions.addAll(lib.actions);
			return;
			}
		if (cmd.endsWith(".EXIT")) //$NON-NLS-1$
			{
			if (!saveConfirm()) return;
			System.exit(0);
			return;
			}
		if (cmd.endsWith(".ABOUT")) //$NON-NLS-1$
			{
			new AboutBox(this).setVisible(true);
			return;
			}
		if (cmd.endsWith(".HELP")) //$NON-NLS-1$
			{
			//TODO: Help
			return;
			}
		}

	public static void main(String[] args)
		{
		//Annoyingly, Metal bolds almost all components by default. This unbolds them.
		UIManager.put("swing.boldMetal",Boolean.FALSE); //$NON-NLS-1$

		new LibMaker().setVisible(true);
		}
	}
