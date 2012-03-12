/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2009 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.uilink;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.text.JTextComponent;

import org.lateralgm.libmaker.backend.PropertyMap;

public abstract class PropertyLink<K extends Enum<K>, V>
	{
	PropertyMap<K> map;
	public final K key;

	PropertyLink(K k)
		{
		key = k;
		}

	protected abstract void setComponent(V t);

	void setMap(PropertyMap<K> m)
		{
		map = m;
		reset();
		}

	protected void reset()
		{
		V v = map.get(key);
		setComponent(v);
		}

	protected void editProperty(Object v)
		{
		if (map != null) map.put(key,v);
		}

	public static class PLFactory<K extends Enum<K>>
		{
		List<PropertyLink<K,?>> links = new LinkedList<PropertyLink<K,?>>();

		private PropertyLink<K,?> add(PropertyLink<K,?> pl)
			{
			links.add(pl);
			return pl;
			}

		public void setMap(PropertyMap<K> m)
			{
			for (PropertyLink<K,?> pl : links)
				pl.setMap(m);
			}

		public PropertyLink<K,?> make(JCheckBox cb, K k)
			{
			return add(new ButtonModelLink<K>(cb.getModel(),k));
			}

		public PropertyLink<K,?> make(JTextComponent tf, K k)
			{
			return add(new DocumentLink<K>(tf.getDocument(),k));
			}

		public PropertyLink<K,?> make(JFormattedTextField tf, K k)
			{
			return add(new FormattedLink<K>(tf,k));
			}
		}
	}
