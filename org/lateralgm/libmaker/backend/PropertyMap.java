/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2009 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.backend;

import java.util.EnumMap;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PropertyMap<K extends Enum<K>> extends EnumMap<K,Object>
	{
	private static final long serialVersionUID = 1L;

	private final Class<K> keyType; //used to register individual keys with listeners

	public PropertyMap(Class<K> type, EnumMap<K,Object> defaults)
		{
		super(defaults == null ? new EnumMap<K,Object>(type) : defaults);
		keyType = type;
		}

	@SuppressWarnings("unchecked")
	public <V>V get(K key)
		{
		return (V) super.get(key);
		}

	@Override
	public Object put(K key, Object val)
		{
		Object r = super.put(key,val);
		fireUpdate(key);
		return r;
		}

	@Override
	public void putAll(Map<? extends K,? extends Object> m)
		{
		for (Map.Entry<? extends K,? extends Object> e : m.entrySet())
			put(e.getKey(),e.getValue());
		}

	public static <K extends Enum<K>>EnumMap<K,Object> makeDefaultMap(Class<K> type, Object...values)
		{
		K[] ec = type.getEnumConstants();
		if (ec.length != values.length) throw new IllegalArgumentException();
		EnumMap<K,Object> m = new EnumMap<K,Object>(type);
		for (K k : ec)
			m.put(k,values[k.ordinal()]);
		return m;
		}

	//Listening

	protected List<PropertyListener<K>> listenerList = new LinkedList<PropertyListener<K>>();
	private EnumMap<K,List<PropertyListener<K>>> keyListeners;

	protected void fireUpdate(K k)
		{
		PropertyUpdateEvent<K> e = null;
		for (PropertyListener<K> pl : listenerList)
			{
			if (e == null) e = new PropertyUpdateEvent<K>(this,k);
			pl.propertyUpdate(e);
			}
		if (keyListeners == null) return;
		List<PropertyListener<K>> ell = keyListeners.get(k);
		if (ell != null) for (PropertyListener<K> pl : ell)
			{
			if (e == null) e = new PropertyUpdateEvent<K>(this,k);
			pl.propertyUpdate(e);
			}
		}

	public void addPropertyListener(K key, PropertyListener<K> p)
		{
		List<PropertyListener<K>> ell = null;
		if (keyListeners == null)
			keyListeners = new EnumMap<K,List<PropertyListener<K>>>(keyType);
		else
			ell = keyListeners.get(key);
		if (ell == null) keyListeners.put(key,ell = new LinkedList<PropertyListener<K>>());
		ell.add(p);
		}

	public void removePropertyListener(K key, PropertyListener<K> p)
		{
		if (keyListeners == null) return;
		List<PropertyListener<K>> ell = keyListeners.get(key);
		if (ell != null) ell.remove(p);
		}

	public void addPropertyListener(PropertyListener<K> p)
		{
		listenerList.add(p);
		}

	public void removePropertyListener(PropertyListener<K> p)
		{
		listenerList.remove(p);
		}

	public static interface PropertyListener<K extends Enum<K>> extends EventListener
		{
		void propertyUpdate(PropertyUpdateEvent<K> evt);
		}

	public static class PropertyUpdateEvent<K extends Enum<K>>
		{
		public final PropertyMap<K> map;
		public final K key;

		public PropertyUpdateEvent(PropertyMap<K> m, K k)
			{
			map = m;
			key = k;
			}
		}
	}
