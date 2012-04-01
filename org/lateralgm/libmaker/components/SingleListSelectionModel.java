/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.components;

import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Lightweight single-selection data model for list selections.
 * Generally this behaves like DefaultListSelectionModel.SINGLE_SELECTION.
 * Unlike DefaultListSelectionModel, this will also fire
 * valueChanged(ListSelectionEvent) when the selection is cleared,
 * making this especially useful for dynamic lists.
 * Note that clearing an already cleared selection will still fire.
 * 
 * @author ismavatar
 */
public class SingleListSelectionModel implements ListSelectionModel
	{
	/**
	 * Currently selected index. A value of -1 indicates no selection.
	 * Should only be set with setSelectedIndex (to alert listeners).
	 */
	protected int sel = -1;
	/** Whether the value is adjusting. Currently only used with the getter/setter. */
	protected boolean adjust;
	protected EventListenerList listenerList = new EventListenerList();

	public void setSelectedIndex(int ind)
		{
		int oldSel = sel;
		sel = ind;
		fire(oldSel);
		}

	@Override
	public void setSelectionInterval(int index0, int index1)
		{
		setSelectedIndex(index1);
		}

	@Override
	public void addSelectionInterval(int index0, int index1)
		{
		setSelectionInterval(index0,index1);
		}

	@Override
	public void removeSelectionInterval(int index0, int index1)
		{
		if (sel >= Math.min(index0,index1) && sel <= Math.max(index0,index1)) setSelectedIndex(-1);
		}

	@Override
	public int getMinSelectionIndex()
		{
		return sel;
		}

	@Override
	public int getMaxSelectionIndex()
		{
		return sel;
		}

	@Override
	public boolean isSelectedIndex(int index)
		{
		return index == sel;
		}

	@Override
	public int getAnchorSelectionIndex()
		{
		return sel;
		}

	@Override
	public int getLeadSelectionIndex()
		{
		return sel;
		}

	@Override
	public void clearSelection()
		{
		setSelectedIndex(-1);
		}

	@Override
	public boolean isSelectionEmpty()
		{
		return sel == -1;
		}

	@Override
	public void setValueIsAdjusting(boolean valueIsAdjusting)
		{
		adjust = valueIsAdjusting;
		}

	@Override
	public boolean getValueIsAdjusting()
		{
		return adjust;
		}

	@Override
	public void setSelectionMode(int selectionMode)
		{
		if (selectionMode != SINGLE_SELECTION) throw new UnsupportedOperationException();
		}

	@Override
	public int getSelectionMode()
		{
		return SINGLE_SELECTION;
		}

	@Override
	public void addListSelectionListener(ListSelectionListener l)
		{
		listenerList.add(ListSelectionListener.class,l);
		}

	@Override
	public void removeListSelectionListener(ListSelectionListener l)
		{
		listenerList.remove(ListSelectionListener.class,l);
		}

	protected void fire(int oldSel)
		{
		if (sel != -1 && oldSel == sel) return;
		int first = oldSel;
		int last = sel;
		if (first > last)
			{
			first = sel;
			last = oldSel;
			}

		Object[] listeners = listenerList.getListenerList();
		ListSelectionEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == ListSelectionListener.class)
				{
				if (e == null) e = new ListSelectionEvent(this,first,last,adjust);
				((ListSelectionListener) listeners[i + 1]).valueChanged(e);
				}
		}

	//Unsupported methods
	@Override
	public void setAnchorSelectionIndex(int index)
		{
		throw new UnsupportedOperationException();
		}

	@Override
	public void setLeadSelectionIndex(int index)
		{
		throw new UnsupportedOperationException();
		}

	@Override
	public void insertIndexInterval(int index, int length, boolean before)
		{
		throw new UnsupportedOperationException();
		}

	@Override
	public void removeIndexInterval(int index0, int index1)
		{
		throw new UnsupportedOperationException();
		}
	}
