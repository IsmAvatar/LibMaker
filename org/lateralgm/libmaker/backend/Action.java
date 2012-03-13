/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.backend;

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
		NAME,ID,IMAGE,HIDDEN,ADVANCED,REGISTERED,DESCRIPTION,LIST,HINT,KIND,EXEC_TYPE,EXEC_INFO,
		IFACE_KIND,QUESTION,APPLY,RELATIVE,ARG_NUM
		}

	private static final EnumMap<PAction,Object> DEFS = PropertyMap.makeDefaultMap(PAction.class,
			null,null,null,false,false,false,null,null,null,Kind.NORMAL,Execution.CODE,null,
			InterfaceKind.NORMAL,false,true,true,0);

	public final PropertyMap<PAction> properties = new PropertyMap<PAction>(PAction.class,DEFS);

	//Fields
	public Library parent;
	/** .length is always MAX_ARGS. Use argNum to determine actual used argument count. */
	public Argument arguments[] = new Argument[MAX_ARGS];

	public Action()
		{
		int id = lastId++;
		properties.put(PAction.ID,id);
		properties.put(PAction.NAME,"Action " + id);

		for (int arg = 0; arg < MAX_ARGS; arg++)
			arguments[arg] = new Argument(arg);
		}

	public void put(PAction key, Object value)
		{
		properties.put(key,value);
		}

	public <V>V get(PAction key)
		{
		return properties.get(key);
		}

	//XXX: We're keeping this just until PropertyMap has Listener
	List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public void addChangeListener(ChangeListener l)
		{
		listeners.add(l);
		}

	public void setName(String name)
		{
		put(PAction.NAME,name);

		ChangeEvent evt = new ChangeEvent(this);
		for (ChangeListener l : listeners)
			l.stateChanged(evt);
		}
	}
