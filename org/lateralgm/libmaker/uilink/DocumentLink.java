/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2009 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.uilink;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentLink<K extends Enum<K>> extends PropertyLink<K,String> implements
		DocumentListener
	{
	public final Document document;

	public DocumentLink(Document d, K k)
		{
		super(k);
		document = d;
		//		reset();
		d.addDocumentListener(this);
		}

	@Override
	public void insertUpdate(DocumentEvent e)
		{
		update();
		}

	@Override
	public void removeUpdate(DocumentEvent e)
		{
		update();
		}

	@Override
	public void changedUpdate(DocumentEvent e)
		{
		update();
		}

	private void update()
		{
		try
			{
			editProperty(document.getText(0,document.getLength()));
			}
		catch (BadLocationException e)
			{
			e.printStackTrace(); //Should never happen
			}
		}

	protected void setComponent(String t)
		{
		try
			{
			document.remove(0,document.getLength());
			document.insertString(0,t,null);
			}
		catch (BadLocationException e)
			{
			e.printStackTrace(); //Should never happen
			}
		}
	}
