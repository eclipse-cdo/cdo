/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public class DataInputOutputFile implements DataInput, DataOutput, Flushable, Closeable
{
  private RandomAccessFile raf;

  private long position;

  private boolean eof;

  private byte buffer[];

  private long start;

  private long end;

  private int used;

  private boolean modified;

  public DataInputOutputFile(File file, String mode) throws FileNotFoundException
  {
    this(file, mode, 2 * 4096);
  }

  public DataInputOutputFile(File file, String mode, int bufferSize) throws FileNotFoundException
  {
    raf = new RandomAccessFile(file, mode);
    buffer = new byte[bufferSize];
  }

  @Override
  public void close() throws IOException
  {
    if (raf != null)
    {
      flush();

      raf.close();
      raf = null;
    }
  }

  @Override
  public void flush() throws IOException
  {
    if (modified)
    {
      modified = false;
      raf.seek(start);
      raf.write(buffer, 0, used);
    }
  }

  public void seek(long filePointer) throws IOException
  {
    if (filePointer >= start && filePointer < end)
    {
      position = filePointer;
    }
    else
    {
      if (modified)
      {
        flush();
      }

      start = filePointer;
      position = filePointer;

      raf.seek(filePointer);
      used = raf.read(buffer, 0, buffer.length);
      if (used <= 0)
      {
        used = 0;
        eof = true;
      }
      else
      {
        eof = false;
      }

      end = start + used;
    }
  }

  public long getFilePointer() throws IOException
  {
    return position;
  }

  public long length() throws IOException
  {
    long fileLength = raf.length();
    if (fileLength < end)
    {
      return end;
    }

    return fileLength;
  }

  @Override
  public int skipBytes(int n) throws IOException
  {
    seek(getFilePointer() + n);
    return n;
  }

  public int read() throws IOException
  {
    if (position < end)
    {
      int offset = (int)(position - start);
      position++;
      return buffer[offset] & 0xff;
    }

    if (eof)
    {
      return -1;
    }

    seek(position);
    return read();
  }

  public int read(byte[] array, int offset, int length) throws IOException
  {
    if (eof)
    {
      return -1;
    }

    int available = (int)(end - position);
    if (available < 1)
    {
      seek(position);
      return read(array, offset, length);
    }

    int bytesToCopy = Math.min(length, available);
    System.arraycopy(buffer, (int)(position - start), array, offset, bytesToCopy);
    position += bytesToCopy;

    if (bytesToCopy < length)
    {
      int remaining = length - bytesToCopy;
      if (remaining > buffer.length)
      {
        raf.seek(position);
        remaining = raf.read(array, offset + bytesToCopy, length - bytesToCopy);
      }
      else
      {
        seek(position);
        if (eof)
        {
          remaining = -1;
        }
        else
        {
          remaining = remaining > used ? used : remaining;
          System.arraycopy(buffer, 0, array, offset + bytesToCopy, remaining);
        }
      }

      if (remaining > 0)
      {
        position += remaining;
        return bytesToCopy + remaining;
      }
    }

    return bytesToCopy;
  }

  public int read(byte[] array) throws IOException
  {
    return read(array, 0, array.length);
  }

  public byte[] readBytes(int count) throws IOException
  {
    byte[] array = new byte[count];
    readFully(array);
    return array;
  }

  @Override
  public void readFully(byte[] array) throws IOException
  {
    readFully(array, 0, array.length);
  }

  @Override
  public void readFully(byte[] array, int offset, int length) throws IOException
  {
    int n = 0;
    while (n < length)
    {
      int count = this.read(array, offset + n, length - n);
      if (count < 0)
      {
        throw new EOFException();
      }

      n += count;
    }
  }

  @Override
  public boolean readBoolean() throws IOException
  {
    int ch = this.read();
    if (ch < 0)
    {
      throw new EOFException();
    }

    return ch != 0;
  }

  @Override
  public byte readByte() throws IOException
  {
    int ch = this.read();
    if (ch < 0)
    {
      throw new EOFException();
    }

    return (byte)ch;
  }

  @Override
  public int readUnsignedByte() throws IOException
  {
    int ch = this.read();
    if (ch < 0)
    {
      throw new EOFException();
    }

    return ch;
  }

  @Override
  public short readShort() throws IOException
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
    {
      throw new EOFException();
    }

    return (short)((ch1 << 8) + ch2);
  }

  @Override
  public int readUnsignedShort() throws IOException
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
    {
      throw new EOFException();
    }

    return (ch1 << 8) + ch2;
  }

  @Override
  public char readChar() throws IOException
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
    {
      throw new EOFException();
    }

    return (char)((ch1 << 8) + ch2);
  }

  @Override
  public int readInt() throws IOException
  {
    int ch1 = this.read();
    int ch2 = this.read();
    int ch3 = this.read();
    int ch4 = this.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
    {
      throw new EOFException();
    }

    return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
  }

  @Override
  public long readLong() throws IOException
  {
    return ((long)readInt() << 32) + (readInt() & 0xFFFFFFFFL);
  }

  @Override
  public float readFloat() throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }

  @Override
  public double readDouble() throws IOException
  {
    return Double.longBitsToDouble(readLong());
  }

  @Override
  public String readLine() throws IOException
  {
    StringBuffer input = new StringBuffer();
    int c = -1;
    boolean eol = false;

    while (!eol)
    {
      switch (c = read())
      {
      case -1:
      case '\n':
        eol = true;
        break;

      case '\r':
        eol = true;
        long cur = getFilePointer();
        if (read() != '\n')
        {
          seek(cur);
        }

        break;

      default:
        input.append((char)c);
      }
    }

    if (c == -1 && input.length() == 0)
    {
      return null;
    }

    return input.toString();
  }

  @Override
  public String readUTF() throws IOException
  {
    return DataInputStream.readUTF(this);
  }

  @Override
  public void write(int byteValue) throws IOException
  {
    if (position < end)
    {
      int filePointer = (int)(position - start);
      buffer[filePointer] = (byte)byteValue;
      modified = true;
      position++;
    }
    else
    {
      if (used != buffer.length)
      {
        int filePointer = (int)(position - start);
        position++;

        buffer[filePointer] = (byte)byteValue;
        modified = true;

        end++;
        used++;
      }
      else
      {
        seek(position);
        write(byteValue);
      }
    }
  }

  @Override
  public void write(byte[] array) throws IOException
  {
    write(array, 0, array.length);
  }

  @Override
  public void write(byte[] array, int offset, int length) throws IOException
  {
    if (length < buffer.length)
    {
      int freeInBuffer = 0;
      int bytesToCopy = 0;
      if (position >= start)
      {
        freeInBuffer = (int)(start + buffer.length - position);
      }

      if (freeInBuffer > 0)
      {
        bytesToCopy = freeInBuffer > length ? length : freeInBuffer;
        System.arraycopy(array, offset, buffer, (int)(position - start), bytesToCopy);
        modified = true;

        long newBufferEnd = position + bytesToCopy;
        end = newBufferEnd > end ? newBufferEnd : end;
        used = (int)(end - start);
        position += bytesToCopy;
      }

      if (bytesToCopy < length)
      {
        seek(position);
        System.arraycopy(array, offset + bytesToCopy, buffer, (int)(position - start), length - bytesToCopy);
        modified = true;

        long newBufferEnd = position + (length - bytesToCopy);
        end = newBufferEnd > end ? newBufferEnd : end;
        used = (int)(end - start);
        position += length - bytesToCopy;
      }
    }
    else
    {
      if (modified)
      {
        flush();
      }

      raf.seek(position);
      raf.write(array, offset, length);

      position += length;
      start = position;
      used = 0;
      end = start + used;
    }
  }

  @Override
  public void writeBoolean(boolean value) throws IOException
  {
    write(value ? 1 : 0);
  }

  @Override
  public void writeByte(int value) throws IOException
  {
    write(value);
  }

  @Override
  public void writeShort(int value) throws IOException
  {
    write(value >>> 8 & 0xFF);
    write(value & 0xFF);
  }

  @Override
  public void writeChar(int value) throws IOException
  {
    write(value >>> 8 & 0xFF);
    write(value & 0xFF);
  }

  @Override
  public void writeInt(int value) throws IOException
  {
    write(value >>> 24 & 0xFF);
    write(value >>> 16 & 0xFF);
    write(value >>> 8 & 0xFF);
    write(value & 0xFF);
  }

  @Override
  public void writeLong(long value) throws IOException
  {
    write((int)(value >>> 56) & 0xFF);
    write((int)(value >>> 48) & 0xFF);
    write((int)(value >>> 40) & 0xFF);
    write((int)(value >>> 32) & 0xFF);
    write((int)(value >>> 24) & 0xFF);
    write((int)(value >>> 16) & 0xFF);
    write((int)(value >>> 8) & 0xFF);
    write((int)value & 0xFF);
  }

  @Override
  public void writeFloat(float value) throws IOException
  {
    writeInt(Float.floatToIntBits(value));
  }

  @Override
  public void writeDouble(double value) throws IOException
  {
    writeLong(Double.doubleToLongBits(value));
  }

  @Override
  public void writeBytes(String value) throws IOException
  {
    int length = value.length();
    for (int i = 0; i < length; i++)
    {
      write((byte)value.charAt(i));
    }
  }

  @Override
  public void writeChars(String value) throws IOException
  {
    int length = value.length();
    for (int i = 0; i < length; i++)
    {
      int c = value.charAt(i);
      write(c >>> 8 & 0xFF);
      write(c & 0xFF);
    }
  }

  @Override
  public void writeUTF(String value) throws IOException
  {
    int length = value.length();
    int utf = 0;

    for (int i = 0; i < length; i++)
    {
      int c = value.charAt(i);
      if (c >= 0x0001 && c <= 0x007F)
      {
        utf++;
      }
      else if (c > 0x07FF)
      {
        utf += 3;
      }
      else
      {
        utf += 2;
      }
    }

    if (utf > 65535)
    {
      throw new UTFDataFormatException();
    }

    write(utf >>> 8 & 0xFF);
    write(utf & 0xFF);
    for (int i = 0; i < length; i++)
    {
      int c = value.charAt(i);
      if (c >= 0x0001 && c <= 0x007F)
      {
        write(c);
      }
      else if (c > 0x07FF)
      {
        write(0xE0 | c >> 12 & 0x0F);
        write(0x80 | c >> 6 & 0x3F);
        write(0x80 | c & 0x3F);
      }
      else
      {
        write(0xC0 | c >> 6 & 0x1F);
        write(0x80 | c & 0x3F);
      }
    }
  }
}
