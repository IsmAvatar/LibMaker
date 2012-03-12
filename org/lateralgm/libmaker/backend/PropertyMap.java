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

public class PropertyMap<K extends Enum<K>> extends EnumMap<K,Object>
	{
	private static final long serialVersionUID = 1L;

	public PropertyMap(Class<K> type, EnumMap<K,Object> defaults)
		{
		super(defaults == null ? new EnumMap<K,Object>(type) : defaults);
		}

	@SuppressWarnings("unchecked")
	public <V>V get(K key)
		{
		return (V) super.get(key);
		}

	//TODO: Add Listening capabilities
	
	public static <K extends Enum<K>>EnumMap<K,Object> makeDefaultMap(Class<K> type, Object...values)
		{
		K[] ec = type.getEnumConstants();
		if (ec.length != values.length) throw new IllegalArgumentException();
		EnumMap<K,Object> m = new EnumMap<K,Object>(type);
		for (K k : ec)
			m.put(k,values[k.ordinal()]);
		return m;
		}
	}
