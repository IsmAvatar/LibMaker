/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.backend;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

	public enum PAction
		{
		PARENT,NAME,ID,IMAGE,HIDDEN,ADVANCED,REGISTERED,DESCRIPTION,LIST,HINT,KIND,EXEC_TYPE,EXEC_INFO,
		IFACE_KIND,QUESTION,APPLY,RELATIVE,ARG_NUM
		}

	private static final EnumMap<PAction,Object> DEFS = PropertyMap.makeDefaultMap(PAction.class,
			null,null,null,null,false,false,false,null,null,null,Kind.NORMAL,Execution.CODE,null,
			InterfaceKind.NORMAL,false,true,true,null);

	public final PropertyMap<PAction> properties = new PropertyMap<PAction>(PAction.class,DEFS);

	//Fields
	public Library parent;
	//General Fields
	protected String name;
	public int id;
	public BufferedImage image;
	public boolean hidden, advanced, registered;
	public String description, list, hint;
	public Kind kind = Kind.NORMAL;
	public Execution execType = Execution.CODE;
	public String execInfo;
	//Interface Fields
	public InterfaceKind ifaceKind = InterfaceKind.NORMAL;
	public boolean question, apply = true, relative = true;
	public int argNum;
	/** .length is always MAX_ARGS. Use argNum to determine actual used argument count. */
	public Argument arguments[] = new Argument[MAX_ARGS];

	public Action()
		{
		id = lastId++;
		name = "Action " + id;
		properties.put(PAction.ID,id);
		properties.put(PAction.NAME,name);

		for (int arg = 0; arg < MAX_ARGS; arg++)
			arguments[arg] = new Argument(arg);
		}

	List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public void addChangeListener(ChangeListener l)
		{
		listeners.add(l);
		}

	public void setName(String name)
		{
		this.name = name;

		ChangeEvent evt = new ChangeEvent(this);
		for (ChangeListener l : listeners)
			l.stateChanged(evt);
		}

	public String getName()
		{
		return name;
		}
	}
