/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Library
	{
	public static interface Format
		{
		String getExtension();
		}

	public static class LglFormat implements Format
		{
		public static final String EXTENSION = ".lgl"; //$NON-NLS-1$
		protected int iconColumns;

		public LglFormat(int iconColumns)
			{
			this.iconColumns = iconColumns;
			}

		public int getIconColumns()
			{
			return iconColumns;
			}

		@Override
		public String getExtension()
			{
			return EXTENSION;
			}
		}

	public static class LibFormat implements Format
		{
		public static final LibFormat LIB520 = new LibFormat(520);
		public static final LibFormat LIB500 = new LibFormat(500);
		public static final String EXTENSION = ".lib"; //$NON-NLS-1$

		protected int version;

		public LibFormat(int version)
			{
			this.version = version;
			}

		public int getVersion()
			{
			return version;
			}

		@Override
		public String getExtension()
			{
			return EXTENSION;
			}
		}

	public static int randomId()
		{
		return new Random().nextInt(999000) + 1000;
		}

	public File sourceFile;
	public Format format;
	public String caption;
	public int id = randomId();
	//Info
	public String author;
	public int version;
	public Date changed;
	public String info;
	//Etc
	public String initCode;
	public boolean advanced;
	public List<Action> actions = new ArrayList<Action>();
	}
