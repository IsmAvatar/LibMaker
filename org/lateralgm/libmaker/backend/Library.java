/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.backend;

import java.io.File;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Random;

import org.lateralgm.libmaker.components.ObservableList;
import org.lateralgm.libmaker.components.ObservableList.ActiveArrayList;

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
	public ObservableList<Action> actions = new ActiveArrayList<Action>();

	/*package*/static int lastActionId; //only accessible to Action

	public enum PLibrary
		{
		CAPTION,ID,AUTHOR,VERSION,CHANGED,INFO,INIT_CODE,ADVANCED
		}

	private static final EnumMap<PLibrary,Object> DEFS = PropertyMap.makeDefaultMap(PLibrary.class,
			null,null/*set at init*/,null,100,null/*set at init*/,null,null,false);

	public final PropertyMap<PLibrary> properties = new PropertyMap<PLibrary>(PLibrary.class,DEFS);

	public Library()
		{
		lastActionId = 1;
		put(PLibrary.ID,randomId());
		put(PLibrary.CHANGED,longTimeToGmTime(System.currentTimeMillis()));
		}

	public void put(PLibrary key, Object value)
		{
		properties.put(key,value);
		}

	public <V>V get(PLibrary key)
		{
		return properties.get(key);
		}

	public static Calendar gmBaseTime()
		{
		Calendar res = Calendar.getInstance();
		res.set(1899,11,29,23,59,59);
		return res;
		}

	public static double longTimeToGmTime(long time)
		{
		return (time - gmBaseTime().getTimeInMillis()) / 86400000d;
		}
	}
