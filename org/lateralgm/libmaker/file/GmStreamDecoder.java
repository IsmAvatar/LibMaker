/*
 * Copyright (C) 2007, 2009, 2010 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2006, 2007 Clam <clamisgood@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.PropertyMap;

public class GmStreamDecoder extends StreamDecoder
	{
	public GmStreamDecoder(InputStream in)
		{
		super(in);
		}

	public GmStreamDecoder(String path) throws FileNotFoundException
		{
		super(path);
		}

	public GmStreamDecoder(File f) throws FileNotFoundException
		{
		super(f);
		}

	public int read(byte b[]) throws IOException
		{
		return read(b,0,b.length);
		}

	public int read(byte b[], int off, int len) throws IOException
		{
		int total = 0;
		while (true)
			{
			int n = in.read(b,off + total,len - total);
			if (n <= 0)
				{
				if (total == 0) total = n;
				break;
				}
			total += n;
			if (total == len) break;
			}

		if (total != len)
			{
			String error = Messages.format("StreamDecoder.UNEXPECTED_EOF",getPosString()); //$NON-NLS-1$
			throw new IOException(error);
			}

		pos += len;
		return total;
		}

	public int read() throws IOException
		{
		int t = in.read();
		if (t == -1)
			{
			String error = Messages.format("StreamDecoder.UNEXPECTED_EOF",getPosString()); //$NON-NLS-1$
			throw new IOException(error);
			}
		pos++;
		return t;
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

	public String readStr() throws IOException
		{
		byte data[] = new byte[read4()];
		read(data);
		return new String(data,charset);
		}

	public String readStr1() throws IOException
		{
		byte data[] = new byte[read()];
		read(data);
		return new String(data,charset);
		}

	public boolean readBool() throws IOException
		{
		int val = read4();
		if (val != 0 && val != 1)
			{
			String error = Messages.format("GmStreamDecoder.INVALID_BOOLEAN",val,getPosString()); //$NON-NLS-1$
			throw new IOException(error);
			}
		return val == 0 ? false : true;
		}

	public <P extends Enum<P>>void read4(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			map.put(key,read4());
		}

	public <P extends Enum<P>>void readStr(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			map.put(key,readStr());
		}

	public <P extends Enum<P>>void readBool(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			map.put(key,readBool());
		}

	public <P extends Enum<P>>void readD(PropertyMap<P> map, P...keys) throws IOException
		{
		for (P key : keys)
			map.put(key,readD());
		}

	/**
	 * Convenience method to retrieve whether the given bit is masked in bits,
	 * That is, if given flag is set.
	 * E.g.: to find out if the 3rd flag from right is set in 00011*0*10, use mask(26,4);
	 * @param bits - A cluster of flags/bits
	 * @param bit - The desired (and already shifted) bit or bits to mask
	 * @return Whether bit is masked in bits
	 */
	public static boolean mask(int bits, int bit)
		{
		return (bits & bit) == bit;
		}

	/**
	 * If the stream is currently reading zlib data,
	 * this returns a string in the format:
	 * <code>&lt;file offset&gt;[&lt;decompressed data offset&gt;]</code><br/>
	 * Otherwise just the file offset is returned.
	 */
	protected String getPosString()
		{
		return Integer.toString(pos);
		}
	}
