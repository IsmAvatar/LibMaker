/*
 * Copyright (C) 2007, 2009, 2010 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2006, 2007, 2008 Clam <clamisgood@gmail.com>
 * Copyright (C) 2009 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.lateralgm.libmaker.backend.PropertyMap;

public class GmStreamEncoder extends StreamEncoder
	{
	public GmStreamEncoder(OutputStream o)
		{
		super(o);
		}

	public GmStreamEncoder(File f) throws FileNotFoundException
		{
		super(f);
		}

	public GmStreamEncoder(String filePath) throws FileNotFoundException
		{
		super(filePath);
		}

	/** 
	 * ISO-8859-1 was the fixed charset in earlier LGM versions, so those parts of the code which
	 * have not been updated to set the charset explicitly should continue to use it to avoid
	 * regressions.
	 */
	private Charset charset = Charset.forName("ISO-8859-1"); //$NON-NLS-1$

	public Charset getCharset()
		{
		return charset;
		}

	public void setCharset(Charset charset)
		{
		this.charset = charset;
		}

	public void writeStr(String str) throws IOException
		{
		if (str == null)
			{
			write4(0);
			return;
			}
		byte[] encoded = str.getBytes(charset);
		write4(encoded.length);
		write(encoded);
		}

	public void writeStr1(String str) throws IOException
		{
		if (str == null)
			{
			write(0);
			return;
			}
		byte[] encoded = str.getBytes(charset);
		int writeSize = Math.min(encoded.length,255);
		write(writeSize);
		write(encoded,0,writeSize);
		}

	public void writeBool(boolean val) throws IOException
		{
		write4(val ? 1 : 0);
		}

	public <P extends Enum<P>>void write4(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			write4((Integer) map.get(key));
		}

	public <P extends Enum<P>>void writeStr(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			writeStr((String) map.get(key));
		}

	public <P extends Enum<P>>void writeBool(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			writeBool((Boolean) map.get(key));
		}

	public <P extends Enum<P>>void writeD(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			writeD((Double) map.get(key));
		}
	}
