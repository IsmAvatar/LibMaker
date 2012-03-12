/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.backend;

public class Argument
	{
	public static enum Kind
		{
		EXPRESSION,STRING,BOTH,BOOLEAN,MENU,COLOR,FONT_STRING,SPRITE,SOUND,BACKGROUND,PATH,SCRIPT,
		OBJECT,ROOM,FONT,TIMELINE
		}

	public String caption;
	public Kind kind = Kind.EXPRESSION;
	public String defValue = "0";
	public String menuOptions = "item 1|item 2";

	//Only available to Action
	/*package*/Argument(int id)
		{
		caption = "Argument " + id + ":";
		}
	}
