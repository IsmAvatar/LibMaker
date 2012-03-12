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
import org.lateralgm.libmaker.backend.Argument;
import org.lateralgm.libmaker.backend.Library;

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
		out.writeStr(lib.caption);
		out.write4(lib.id);
		out.writeStr(lib.author);
		out.write4(lib.version);
		out.writeD(0); //changed
		out.writeStr(lib.info);
		out.writeStr(lib.initCode);
		out.writeBool(lib.advanced);
		out.write4(lib.actions.size()); // no of actions/official lib identifier thingy
		out.write4(lib.actions.size());
		for (Action act : lib.actions)
			{
			out.write4(ver);
			out.writeStr(act.getName());
			out.write4(act.id);

			ImageIO.write(act.image,"bmp",out); //write image data //$NON-NLS-1$

			out.writeBool(act.hidden);
			out.writeBool(act.advanced);
			if (ver == 520) out.writeBool(act.registered);
			out.writeStr(act.description);
			out.writeStr(act.list);
			out.writeStr(act.hint);
			out.write4(ACT_KINDS.get(act.kind));
			out.write4(IFACE_KINDS.get(act.ifaceKind));
			out.writeBool(act.question);
			out.writeBool(act.apply);
			out.writeBool(act.relative);
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

			out.write4(EXECUTIONS.get(act.execType));
			out.writeStr(act.execType == Action.Execution.FUNCTION ? act.execInfo : STR_EMPTY);
			out.writeStr(act.execType == Action.Execution.CODE ? act.execInfo : STR_EMPTY);
			}
		}

	/** Workhorse for write a library of LGL format out of given GmStreamEncoder */
	public static void saveLgl(Library lib, GmStreamEncoder out, int iconColumns) throws IOException
		{
		final byte[] HDR = { 'L','G','L' };
		final int VER = 160;

		out.write(HDR);
		out.write2(VER);
		out.write3(lib.id);
		out.writeStr1(lib.caption);
		out.writeStr(lib.author);
		out.write4(lib.version);
		out.writeD(0); //lib.changed
		out.writeStr(lib.info);
		out.writeStr(lib.initCode);
		int acts = lib.advanced ? 128 : 0;
		acts |= lib.actions.size();
		out.write4(acts);

		for (Action act : lib.actions)
			{
			out.write4(VER);
			out.write2(act.id);
			out.writeStr1(act.getName());
			out.writeStr1(act.description);
			out.writeStr1(act.list);
			out.writeStr1(act.hint);
			int mask = act.hidden ? 128 : 0;
			mask |= act.advanced ? 64 : 0;
			mask |= act.registered ? 32 : 0;
			mask |= act.question ? 16 : 0;
			mask |= act.apply ? 8 : 0;
			mask |= act.relative ? 4 : 0;
			mask |= EXECUTIONS.get(act.execType); //(0-2)
			out.write(mask);
			out.writeStr(act.execInfo);

			int kind = ACT_KINDS.get(act.kind) << 4;
			kind |= IFACE_KINDS.get(act.ifaceKind);
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
			if (a.kind != Action.Kind.PLACEHOLDER && a.kind != Action.Kind.SEPARATOR
					&& a.kind != Action.Kind.LABEL) actNum++;

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
			if (a.kind != Action.Kind.PLACEHOLDER && a.kind != Action.Kind.SEPARATOR
					&& a.kind != Action.Kind.LABEL)
				{
				g.drawImage(a.image,24 * (i % columns),24 * (i / columns),(ImageObserver) null);
				i++;
				}
			}

		ImageIO.write(icons,"png",out); //$NON-NLS-1$
		}
	}
