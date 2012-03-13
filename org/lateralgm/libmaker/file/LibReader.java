/*
 * Copyright (C) 2007, 2008, 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2006, 2007 Clam <clamisgood@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.file;

import static org.lateralgm.libmaker.file.GmStreamDecoder.mask;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.Execution;
import org.lateralgm.libmaker.backend.Action.InterfaceKind;
import org.lateralgm.libmaker.backend.Action.PAction;
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.backend.Argument.PArgument;
import org.lateralgm.libmaker.backend.Library;
import org.lateralgm.libmaker.backend.Library.PLibrary;
import org.lateralgm.libmaker.backend.PropertyMap;

public class LibReader
	{
	public static class LibFormatException extends Exception
		{
		private static final long serialVersionUID = 1L;

		public LibFormatException(String message)
			{
			super(message);
			}

		public String stackAsString()
			{
			StackTraceElement[] els = getStackTrace();
			String res = ""; //$NON-NLS-1$
			for (int i = 0; i < els.length; i++)
				{
				res += els[i].toString();
				if (i != els.length - 1) res += "\n"; //$NON-NLS-1$
				}
			return res;
			}
		}

	/*package*/static final Action.Kind ACT_KINDS[] = { Action.Kind.NORMAL,Action.Kind.BEGIN_GROUP,
			Action.Kind.END_GROUP,Action.Kind.ELSE,Action.Kind.EXIT,Action.Kind.REPEAT,
			Action.Kind.VARIABLE,Action.Kind.CODE,Action.Kind.PLACEHOLDER,Action.Kind.SEPARATOR,
			Action.Kind.LABEL };

	/*package*/static final Action.InterfaceKind IFACE_KINDS[] = { InterfaceKind.NORMAL,
			InterfaceKind.NONE,InterfaceKind.ARROWS,null,null,InterfaceKind.CODE,InterfaceKind.TEXT };

	/*package*/static final Action.Execution EXECUTIONS[] = { Execution.NOTHING,Execution.FUNCTION,
			Execution.CODE };

	/*package*/static final Argument.Kind ARG_KINDS[] = { Argument.Kind.EXPRESSION,
			Argument.Kind.STRING,Argument.Kind.BOTH,Argument.Kind.BOOLEAN,Argument.Kind.MENU,
			Argument.Kind.SPRITE,Argument.Kind.SOUND,Argument.Kind.BACKGROUND,Argument.Kind.PATH,
			Argument.Kind.SCRIPT,Argument.Kind.OBJECT,Argument.Kind.ROOM,Argument.Kind.FONT,
			Argument.Kind.COLOR,Argument.Kind.TIMELINE,Argument.Kind.FONT_STRING };

	public static Library loadFile(File f) throws LibFormatException
		{
		try
			{
			return loadFile(new GmStreamDecoder(f),f.getName());
			}
		catch (FileNotFoundException e)
			{
			throw new LibFormatException(Messages.format("LibReader.ERROR_NOTFOUND",f.getName())); //$NON-NLS-1$
			}
		}

	/**
	 * Loads a library file of given fileName of either LIB or LGL format
	 * @param in
	 * @param filename for error reporting
	 * @return the library
	 * @throws LibFormatException
	 */
	public static Library loadFile(GmStreamDecoder in, String filename) throws LibFormatException
		{
		Library lib = null;
		try
			{
			int header = in.read3();
			if (header == (('L' << 16) | ('G' << 8) | 'L'))
				lib = loadLgl(in);
			else if (header == 500 || header == 520)
				lib = loadLib(in);
			else
				throw new LibFormatException(Messages.format("LibReader.ERROR_INVALIDFILE",filename)); //$NON-NLS-1$
			}
		catch (IOException ex)
			{
			throw new LibFormatException(Messages.format("LibReader.ERROR_READING",filename, //$NON-NLS-1$
					ex.getMessage()));
			}
		catch (LibFormatException ex)
			{
			//Fill in the %s with the filename, now that we know it.
			throw new LibFormatException(String.format(ex.getMessage(),filename));
			}
		finally
			{
			try
				{
				if (in != null) in.close();
				}
			catch (IOException ex)
				{
				String msg = Messages.getString("LibReader.ERROR_CLOSEFAILED"); //$NON-NLS-1$
				throw new LibFormatException(msg);
				}
			}
		return lib;
		}

	/**
	 * Workhorse for constructing a library out of given StreamDecoder of LIB format
	 * @param in
	 * @return the library (not yet added to the libs list)
	 * @throws LibFormatException
	 * @throws IOException
	 */
	public static Library loadLib(GmStreamDecoder in) throws LibFormatException,IOException
		{
		if (in.read() != 0)
			throw new LibFormatException(Messages.format("LibReader.ERROR_INVALIDFILE","%s")); //$NON-NLS-1$ //$NON-NLS-2$
		Library lib = new Library();
		in.readStr(lib.properties,PLibrary.CAPTION);
		in.read4(lib.properties,PLibrary.ID);
		in.readStr(lib.properties,PLibrary.AUTHOR);
		in.read4(lib.properties,PLibrary.VERSION);
		in.readD(); //lib.changed
		in.readStr(lib.properties,PLibrary.INFO,PLibrary.INIT_CODE);
		in.readBool(lib.properties,PLibrary.ADVANCED);
		in.skip(4); // no of actions/official lib identifier thingy
		int acts = in.read4();
		for (int j = 0; j < acts; j++)
			{
			int ver = in.read4();
			if (ver != 500 && ver != 520)
				throw new LibFormatException(Messages.format("LibReader.ERROR_INVALIDACTION",j,"%s",ver)); //$NON-NLS-1$ //$NON-NLS-2$

			Action act = new Action();
			lib.actions.add(act);
			act.parent = lib;
			in.readStr(act.properties,PAction.NAME);
			in.read4(act.properties,PAction.ID);

			byte[] data = new byte[in.read4()];
			in.read(data);
			act.put(PAction.IMAGE,ImageIO.read(new ByteArrayInputStream(data)));

			in.readBool(act.properties,PAction.HIDDEN,PAction.ADVANCED);
			if (ver == 520) in.readBool(act.properties,PAction.REGISTERED);
			in.readStr(act.properties,PAction.DESCRIPTION,PAction.LIST,PAction.HINT);
			act.put(PAction.KIND,ACT_KINDS[in.read4()]);
			act.put(PAction.IFACE_KIND,IFACE_KINDS[in.read4()]);
			in.readBool(act.properties,PAction.QUESTION,PAction.APPLY,PAction.RELATIVE);
			in.read4(act.properties,PAction.ARG_NUM);
			int args = in.read4();
			for (int k = 0; k < args; k++)
				{
				if (k < act.arguments.length)
					{
					Argument arg = act.arguments[k];
					in.readStr(arg.properties,PArgument.CAPTION);
					arg.put(PArgument.KIND,ARG_KINDS[in.read4()]);
					in.readStr(arg.properties,PArgument.DEF_VALUE,PArgument.MENU_OPTS);
					}
				else
					{
					in.skip(in.read4());
					in.skip(4);
					in.skip(in.read4());
					in.skip(in.read4());
					}
				}
			Execution execType = EXECUTIONS[in.read4()];
			act.put(PAction.EXEC_TYPE,execType);
			if (execType == Action.Execution.FUNCTION)
				in.readStr(act.properties,PAction.EXEC_INFO);
			else
				in.skip(in.read4());
			if (execType == Action.Execution.CODE)
				in.readStr(act.properties,PAction.EXEC_INFO);
			else
				in.skip(in.read4());
			}
		return lib;
		}

	enum Size
		{
		I1,I2,I3,I4,S1,S4
		}

	private static <P extends Enum<P>>void read(GmStreamDecoder in, Size[] sizes, PropertyMap<P> map,
			P...keys) throws IOException
		{
		if (sizes.length != keys.length) throw new IllegalArgumentException();
		for (int i = 0; i < sizes.length; i++)
			map.put(keys[i],read(in,sizes[i]));
		}

	private static Object read(GmStreamDecoder in, Size size) throws IOException
		{
		switch (size)
			{
			case I1:
				return in.read();
			case I2:
				return in.read2();
			case I3:
				return in.read3();
			case I4:
				return in.read4();
			case S1:
				return in.readStr1();
			case S4:
				return in.readStr();
			default:
				return null;
			}
		}

	/**
	 * Workhorse for constructing a library out of given StreamDecoder of LGL format
	 * @param in
	 * @return the library (not yet added to the libs list)
	 * @throws LibFormatException
	 * @throws IOException
	 */
	public static Library loadLgl(GmStreamDecoder in) throws LibFormatException,IOException
		{
		if (in.read2() != 160)
			{
			String invalidFile = Messages.getString("LibReader.ERROR_INVALIDFILE"); //$NON-NLS-1$
			throw new LibFormatException(invalidFile);
			}
		Library lib = new Library();

		Size s[] = { Size.I3,Size.S1,Size.S1,Size.I4 };
		PLibrary pl[] = { PLibrary.ID,PLibrary.CAPTION,PLibrary.AUTHOR,PLibrary.VERSION };
		read(in,s,lib.properties,pl);
		in.skip(8); //lib.changed
		in.readStr(lib.properties,PLibrary.INFO,PLibrary.INIT_CODE);
		int acts = in.read();
		lib.properties.put(PLibrary.ADVANCED,mask(acts,128));
		acts &= 127;
		for (int j = 0; j < acts; j++)
			{
			if (in.read2() != 160)
				throw new LibFormatException(Messages.format("LibReader.ERROR_INVALIDACTION",j,"%s",160)); //$NON-NLS-1$ //$NON-NLS-2$
			Action act = new Action();
			lib.actions.add(act);
			act.parent = lib;

			s = new Size[] { Size.I2,Size.S1,Size.S1,Size.S1,Size.S1 };
			PAction[] pa = { PAction.ID,PAction.NAME,PAction.DESCRIPTION,PAction.LIST,PAction.HINT };
			read(in,s,act.properties,pa);

			int tags = in.read();
			act.put(PAction.HIDDEN,mask(tags,128));
			act.put(PAction.ADVANCED,mask(tags,64));
			act.put(PAction.REGISTERED,mask(tags,32));
			act.put(PAction.QUESTION,mask(tags,16));
			act.put(PAction.APPLY,mask(tags,8));
			act.put(PAction.RELATIVE,mask(tags,4));
			act.put(PAction.EXEC_TYPE,EXECUTIONS[tags & 3]);
			act.put(PAction.EXEC_INFO,in.readStr());
			tags = in.read();
			act.put(PAction.KIND,ACT_KINDS[tags >> 4]);
			act.put(PAction.IFACE_KIND,IFACE_KINDS[tags & 15]);

			int argNum = in.read();
			act.put(PAction.ARG_NUM,argNum);
			for (int k = 0; k < argNum; k++)
				{
				Argument arg = act.arguments[k];
				arg.put(PArgument.CAPTION,in.readStr1());
				arg.put(PArgument.KIND,ARG_KINDS[in.read()]);
				arg.put(PArgument.DEF_VALUE,in.readStr1());
				arg.put(PArgument.MENU_OPTS,in.readStr1());
				}
			}
		loadLGLIcons(lib,in.getInputStream());
		return lib;
		}

	protected static void loadLGLIcons(Library lib, InputStream in) throws IOException
		{
		BufferedImage icons = ImageIO.read(in);
		int i = 0;
		int cc = icons.getWidth() / 24;
		for (Action a : lib.actions)
			{
			Action.Kind k = a.get(PAction.KIND);
			if (k != Action.Kind.PLACEHOLDER && k != Action.Kind.SEPARATOR && k != Action.Kind.LABEL)
				{
				a.put(PAction.IMAGE,icons.getSubimage(24 * (i % cc),24 * (i / cc),24,24));
				i++;
				}
			}
		}
	}
