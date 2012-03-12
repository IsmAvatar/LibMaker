/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.lateralgm.libmaker.backend.Library;
import org.lateralgm.libmaker.file.FileChooser;

public class LibMaker extends JFrame
	{
	private static final long serialVersionUID = 1L;

	protected FileChooser fc;
	protected Library currentLib;
	protected boolean modified;

	public LibMaker()
		{
		super(); //Title gets set when library is set
		fc = new FileChooser(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setSize(350,350); //XXX: Delete once components are laid out.
		setLocationRelativeTo(null);
		setMinimumSize(getSize());

		setLibrary(new Library()); //Now that we have our needed size, setup with our initial library
		}

	public void setLibrary(Library lib)
		{
		currentLib = lib;
		modified = false;
		setTitleFile(lib.sourceFile);
		}

	public void setTitleFile(File f)
		{
		String name = f == null ? Messages.getString("LibMaker.NEWLIB") : f.getName(); //$NON-NLS-1$
		setTitle(Messages.format("LibMaker.TITLE",name)); //$NON-NLS-1$
		}

	public static void main(String[] args)
		{
		//Annoyingly, Metal bolds almost all components by default. This unbolds them.
		UIManager.put("swing.boldMetal",Boolean.FALSE); //$NON-NLS-1$

		new LibMaker().setVisible(true);
		}
	}
