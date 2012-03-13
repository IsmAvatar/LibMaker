/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2009 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.uilink;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;

public class FormattedLink<K extends Enum<K>> extends PropertyLink<K,Object> implements
		PropertyChangeListener
	{
	public final JFormattedTextField field;

	public FormattedLink(JFormattedTextField f, K k)
		{
		super(k);
		field = f;
		//		reset();
		field.addPropertyChangeListener("value",this); //$NON-NLS-1$
		}

	@Override
	protected void setComponent(Object v)
		{
		field.setValue(v);
		}

	public void propertyChange(PropertyChangeEvent evt)
		{
		editProperty(field.getValue());
		}
	}
