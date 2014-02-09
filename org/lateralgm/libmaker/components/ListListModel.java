/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.components;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * A ListModel backed by a List<K>.
 * You can freely modify the backend list while it is in use.
 * Generally, after modifying the list, however, you should
 * call JList.updateUI() to ensure that the latest version is repainted.
 * 
 * Additionally, note that inserting and removing elements may
 * alter the JList selection in undesired ways,
 * so consider updating the selection as well.
 * 
 * @author ismavatar
 */
public class ListListModel<K> extends AbstractListModel<Object>
	{
	private static final long serialVersionUID = 1L;

	protected List<K> list;

	/**
	 * Creates an empty list model. Since no backing list is provided,
	 * the list continues to be empty until a new list is provided via setList.
	 */
	public ListListModel()
		{
		this(null);
		}

	public ListListModel(List<K> l)
		{
		super();
		setList(l);
		}

	/**
	 * Changes the backing list for this model. A value of null indicates that
	 * the model should be empty with no backing list until another call to setList.
	 * <p>
	 * Note that this implementation does not fire any events.
	 * @param l
	 */
	public void setList(List<K> l)
		{
		list = l == null ? list = Collections.emptyList() : l;
		}

	@Override
	public int getSize()
		{
		return list.size();
		}

	@Override
	public Object getElementAt(int index)
		{
		return list.get(index);
		}

	public K getValueAt(int index)
		{
		return list.get(index);
		}
	}
