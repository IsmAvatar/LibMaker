/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.file;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lateralgm.libmaker.backend.Action;
import org.lateralgm.libmaker.backend.Action.Execution;
import org.lateralgm.libmaker.backend.Action.PAction;
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.backend.Library;
import org.lateralgm.libmaker.backend.Library.PLibrary;
import org.lateralgm.libmaker.backend.PropertyMap;

public class LibWriter
	{
	private static final String STR_EMPTY = new String();
	private static final Map<Action.Kind,Integer> ACT_KINDS;
	private static final Map<Action.InterfaceKind,Integer> IFACE_KINDS;
	private static final Map<Action.Execution,Integer> EXECUTIONS;
	private static final Map<Argument.Kind,Integer> ARG_KINDS;
	static
		{
		EnumMap<Action.Kind,Integer> m = new EnumMap<Action.Kind,Integer>(Action.Kind.class);
		for (int i = 0; i < LibReader.ACT_KINDS.length; i++)
			m.put(LibReader.ACT_KINDS[i],i);
		ACT_KINDS = Collections.unmodifiableMap(m);

		EnumMap<Action.InterfaceKind,Integer> m2 = new EnumMap<Action.InterfaceKind,Integer>(
				Action.InterfaceKind.class);
		for (int i = 0; i < LibReader.IFACE_KINDS.length; i++)
			m2.put(LibReader.IFACE_KINDS[i],i);
		IFACE_KINDS = Collections.unmodifiableMap(m2);

		EnumMap<Action.Execution,Integer> m3 = new EnumMap<Action.Execution,Integer>(
				Action.Execution.class);
		for (int i = 0; i < LibReader.EXECUTIONS.length; i++)
			m3.put(LibReader.EXECUTIONS[i],i);
		EXECUTIONS = Collections.unmodifiableMap(m3);

		EnumMap<Argument.Kind,Integer> m4 = new EnumMap<Argument.Kind,Integer>(Argument.Kind.class);
		for (int i = 0; i < LibReader.ARG_KINDS.length; i++)
			m4.put(LibReader.ARG_KINDS[i],i);
		ARG_KINDS = Collections.unmodifiableMap(m4);
		}

	/** Workhorse for writing a library of LIB format out of given GmStreamEncoder */
	public static void saveLib(Library lib, GmStreamEncoder out, int ver) throws IOException
		{
		ver = ver >= 520 ? 520 : 500;
		out.write4(ver);
		out.writeStr(lib.properties,PLibrary.CAPTION);
		out.write4(lib.properties,PLibrary.ID);
		out.writeStr(lib.properties,PLibrary.AUTHOR);
		out.write4(lib.properties,PLibrary.VERSION);
		out.writeD(0); //changed
		out.writeStr(lib.properties,PLibrary.INFO,PLibrary.INIT_CODE);
		out.writeBool(lib.properties,PLibrary.ADVANCED);
		out.write4(lib.actions.size()); // no of actions/official lib identifier thingy
		out.write4(lib.actions.size());
		for (Action act : lib.actions)
			{
			out.write4(ver);
			out.writeStr(act.properties,PAction.NAME);
			out.write4(act.properties,PAction.ID);

			BufferedImage img = act.get(PAction.IMAGE);
			ImageIO.write(img,"bmp",out); //write image data //$NON-NLS-1$

			out.writeBool(act.properties,PAction.HIDDEN,PAction.ADVANCED);
			if (ver == 520) out.writeBool(act.properties,PAction.REGISTERED);
			out.writeStr(act.properties,PAction.DESCRIPTION,PAction.LIST,PAction.HINT);
			out.write4(ACT_KINDS.get(act.get(PAction.KIND)));
			out.write4(IFACE_KINDS.get(act.get(PAction.IFACE_KIND)));
			out.writeBool(act.properties,PAction.QUESTION,PAction.APPLY,PAction.RELATIVE);
			out.write4(act.argNum);
			out.write4(8); //8 vs act.argNum vs MAX_ARGS?

			//This always writes MAX_ARGS arguments. Alternatively, we could just write
			//argNum arguments, and truncate the remaining invisible/unused arguments.
			for (Argument arg : act.arguments)
				{
				out.writeStr(arg.caption);
				out.write4(ARG_KINDS.get(arg.kind));
				out.writeStr(arg.defValue);
				out.writeStr(arg.menuOptions);
				}
			for (int k = act.arguments.length; k < 8; k++)
				{
				out.writeStr(STR_EMPTY);
				out.write4(0);
				out.writeStr(STR_EMPTY);
				out.writeStr(STR_EMPTY);
				}

			Execution execType = act.get(PAction.EXEC_TYPE);
			String execInfo = act.get(PAction.EXEC_INFO);
			out.write4(EXECUTIONS.get(execType));
			out.writeStr(execType == Action.Execution.FUNCTION ? execInfo : STR_EMPTY);
			out.writeStr(execType == Action.Execution.CODE ? execInfo : STR_EMPTY);
			}
		}

	enum Size
		{
		I1,I2,I3,I4,S1,S4
		}

	private static <P extends Enum<P>>void write(GmStreamEncoder out, Size[] sizes,
			PropertyMap<P> map, P...keys) throws IOException
		{
		if (sizes.length != keys.length) throw new IllegalArgumentException();
		for (int i = 0; i < sizes.length; i++)
			write(out,sizes[i],map.get(keys[i]));
		}

	private static void write(GmStreamEncoder out, Size size, Object o) throws IOException
		{
		switch (size)
			{
			case I1:
				out.write((Integer) o);
				return;
			case I2:
				out.write2((Integer) o);
				return;
			case I3:
				out.write3((Integer) o);
				return;
			case I4:
				out.write4((Integer) o);
				return;
			case S1:
				out.writeStr1((String) o);
				return;
			case S4:
				out.writeStr((String) o);
				return;
			default:
				throw new IllegalArgumentException();
			}
		}

	/** Workhorse for write a library of LGL format out of given GmStreamEncoder */
	public static void saveLgl(Library lib, GmStreamEncoder out, int iconColumns) throws IOException
		{
		final byte[] HDR = { 'L','G','L' };
		final int VER = 160;

		out.write(HDR);
		out.write2(VER);

		Size s[] = { Size.I3,Size.S1,Size.S1,Size.I4 };
		PLibrary pl[] = { PLibrary.ID,PLibrary.CAPTION,PLibrary.AUTHOR,PLibrary.VERSION };
		write(out,s,lib.properties,pl);
		out.writeD(0); //lib.changed
		out.writeStr(lib.properties,PLibrary.INFO,PLibrary.INIT_CODE);
		int acts = lib.get(PLibrary.ADVANCED) ? 128 : 0;
		acts |= lib.actions.size();
		out.write4(acts);

		for (Action act : lib.actions)
			{
			out.write4(VER);

			s = new Size[] { Size.I2,Size.S1,Size.S1,Size.S1,Size.S1 };
			PAction[] pa = { PAction.ID,PAction.NAME,PAction.DESCRIPTION,PAction.LIST,PAction.HINT };
			write(out,s,act.properties,pa);

			int mask = act.get(PAction.HIDDEN) ? 128 : 0;
			mask |= act.get(PAction.ADVANCED) ? 64 : 0;
			mask |= act.get(PAction.REGISTERED) ? 32 : 0;
			mask |= act.get(PAction.QUESTION) ? 16 : 0;
			mask |= act.get(PAction.APPLY) ? 8 : 0;
			mask |= act.get(PAction.RELATIVE) ? 4 : 0;
			mask |= EXECUTIONS.get(act.get(PAction.EXEC_TYPE)); //(0-2)
			out.write(mask);
			out.writeStr(act.properties,PAction.EXEC_INFO);

			int kind = ACT_KINDS.get(act.get(PAction.KIND)) << 4;
			kind |= IFACE_KINDS.get(act.get(PAction.IFACE_KIND));
			out.write(kind);

			out.write(act.argNum);
			for (int k = 0; k < act.argNum; k++)
				{
				Argument arg = act.arguments[k];
				out.writeStr1(arg.caption);
				out.write(ARG_KINDS.get(arg.kind));
				out.writeStr1(arg.defValue);
				out.writeStr1(arg.menuOptions);
				}
			}

		saveLGLIcons(lib,out.getOutputStream(),iconColumns);
		}

	protected static void saveLGLIcons(Library lib, OutputStream out, int columns) throws IOException
		{
		//Oh god this hack
		int actNum = 0;
		for (Action a : lib.actions)
			{
			Action.Kind k = a.get(PAction.KIND);
			if (k != Action.Kind.PLACEHOLDER && k != Action.Kind.SEPARATOR && k != Action.Kind.LABEL)
				actNum++;
			}

		if (actNum == 0) //seriously, who does that?
			{
			ImageIO.write(new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB),"png",out); //$NON-NLS-1$
			return;
			}

		int rows = (actNum - 1) / columns + 1; //yer a wizard, harry

		//Don't premultiply alpha since we're not even drawing the image
		BufferedImage icons = new BufferedImage(columns * 24,rows * 24,BufferedImage.TYPE_INT_ARGB);
		Graphics g = icons.getGraphics();

		int i = 0;
		for (Action a : lib.actions)
			{
			Action.Kind k = a.get(PAction.KIND);
			if (k != Action.Kind.PLACEHOLDER && k != Action.Kind.SEPARATOR && k != Action.Kind.LABEL)
				{
				BufferedImage img = a.get(PAction.IMAGE);
				g.drawImage(img,24 * (i % columns),24 * (i / columns),(ImageObserver) null);
				i++;
				}
			}

		ImageIO.write(icons,"png",out); //$NON-NLS-1$
		}
	}
