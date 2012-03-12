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
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.backend.Library;

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
		lib.caption = in.readStr();
		lib.id = in.read4();
		lib.author = in.readStr();
		lib.version = in.read4();
		in.readD(); //lib.changed
		lib.info = in.readStr();
		lib.initCode = in.readStr();
		lib.advanced = in.readBool();
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
			act.name = in.readStr();
			act.id = in.read4();

			byte[] data = new byte[in.read4()];
			in.read(data);
			act.image = ImageIO.read(new ByteArrayInputStream(data));

			act.hidden = in.readBool();
			act.advanced = in.readBool();
			if (ver == 520) act.registered = in.readBool();
			act.description = in.readStr();
			act.list = in.readStr();
			act.hint = in.readStr();
			act.kind = ACT_KINDS[in.read4()];
			act.ifaceKind = IFACE_KINDS[in.read4()];
			act.question = in.readBool();
			act.apply = in.readBool();
			act.relative = in.readBool();
			act.argNum = in.read4();
			int args = in.read4();
			for (int k = 0; k < args; k++)
				{
				if (k < act.arguments.length)
					{
					Argument arg = act.arguments[k];
					arg.caption = in.readStr();
					arg.kind = ARG_KINDS[in.read4()];
					arg.defValue = in.readStr();
					arg.menuOptions = in.readStr();
					}
				else
					{
					in.skip(in.read4());
					in.skip(4);
					in.skip(in.read4());
					in.skip(in.read4());
					}
				}
			act.execType = EXECUTIONS[in.read4()];
			if (act.execType == Action.Execution.FUNCTION)
				act.execInfo = in.readStr();
			else
				in.skip(in.read4());
			if (act.execType == Action.Execution.CODE)
				act.execInfo = in.readStr();
			else
				in.skip(in.read4());
			}
		return lib;
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
		lib.id = in.read3();
		lib.caption = in.readStr1();
		lib.author = in.readStr1();
		lib.version = in.read4();
		in.skip(8); //lib.changed
		lib.info = in.readStr();
		lib.initCode = in.readStr();
		int acts = in.read();
		lib.advanced = mask(acts,128);
		acts &= 127;
		for (int j = 0; j < acts; j++)
			{
			if (in.read2() != 160)
				throw new LibFormatException(Messages.format("LibReader.ERROR_INVALIDACTION",j,"%s",160)); //$NON-NLS-1$ //$NON-NLS-2$
			Action act = new Action();
			lib.actions.add(act);
			act.parent = lib;
			act.id = in.read2();
			act.name = in.readStr1();
			act.description = in.readStr1();
			act.list = in.readStr1();
			act.hint = in.readStr1();
			int tags = in.read();
			act.hidden = mask(tags,128);
			act.advanced = mask(tags,64);
			act.registered = mask(tags,32);
			act.question = mask(tags,16);
			act.apply = mask(tags,8);
			act.relative = mask(tags,4);
			act.execType = EXECUTIONS[tags & 3];
			act.execInfo = in.readStr();
			tags = in.read();
			act.kind = ACT_KINDS[tags >> 4];
			act.ifaceKind = IFACE_KINDS[tags & 15];
			act.argNum = in.read();
			for (int k = 0; k < act.argNum; k++)
				{
				Argument arg = act.arguments[k];
				arg.caption = in.readStr1();
				arg.kind = ARG_KINDS[in.read()];
				arg.defValue = in.readStr1();
				arg.menuOptions = in.readStr1();
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
			if (a.kind != Action.Kind.PLACEHOLDER && a.kind != Action.Kind.SEPARATOR
					&& a.kind != Action.Kind.LABEL)
				{
				a.image = icons.getSubimage(24 * (i % cc),24 * (i / cc),24,24);
				i++;
				}
			}
		}
	}
