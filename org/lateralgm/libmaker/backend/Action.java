/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.backend;

import java.awt.image.BufferedImage;

public class Action
	{
	//Enums
	public static enum Kind
		{
		NORMAL,BEGIN_GROUP,END_GROUP,ELSE,EXIT,REPEAT,VARIABLE,CODE,PLACEHOLDER,SEPARATOR,LABEL
		}

	public static enum InterfaceKind
		{
		NORMAL,NONE,ARROWS,CODE,TEXT
		}

	public static enum Execution
		{
		NOTHING,FUNCTION,CODE
		}

	public static int MAX_ARGS = 6;
	private static int lastId = 1;

	//Fields
	public Library parent;
	//General Fields
	public String name;
	public int id;
	public BufferedImage image;
	public boolean hidden, advanced, registered;
	public String description, list, hint;
	public Kind kind = Kind.NORMAL;
	//Interface Fields
	public InterfaceKind ifaceKind = InterfaceKind.NORMAL;
	public boolean question, apply = true, relative = true;
	public int argNum;
	/** .length is always MAX_ARGS. Use argNum to determine actual used argument count. */
	public Argument arguments[] = new Argument[MAX_ARGS];
	//Execution Fields
	public Execution execType = Execution.CODE;
	public String execInfo;

	public Action()
		{
		id = lastId++;
		name = "Action " + id;

		for (int arg = 0; arg < MAX_ARGS; arg++)
			arguments[arg] = new Argument(arg);
		}
	}
