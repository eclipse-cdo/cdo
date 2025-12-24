/*
 * Copyright (c) 2007-2013, 2015-2017, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public final class ExtendedIOUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ExtendedIOUtil.class);

  private static final int UTF_HEADER_SIZE = 2;

  private static final int MAX_16_BIT = (1 << 16) - 1;

  private static final int MAX_UTF_LENGTH = MAX_16_BIT - UTF_HEADER_SIZE;

  private static final int MAX_UTF_CHARS = MAX_UTF_LENGTH / 3;

  private static final int MAX_ENUM_LITERALS = Byte.MAX_VALUE - Byte.MIN_VALUE;

  private static final byte NO_ENUM_LITERAL = Byte.MIN_VALUE;

  private static final int BYTE_BUFFER_SIZE = 8192;

  private static final int CHARACTER_BUFFER_SIZE = 4096;

  private static final byte[] EMPTY_BYTE_ARRAY = {};

  private ExtendedIOUtil()
  {
  }

  /**
   * @since 3.7
   */
  public static void writeVarInt(DataOutput out, int v) throws IOException
  {
    while ((v & ~0x7f) != 0)
    {
      out.writeByte((byte)(0x80 | v & 0x7f));
      v >>>= 7;
    }

    out.writeByte((byte)v);
  }

  /**
   * @since 3.7
   */
  public static int readVarInt(DataInput in) throws IOException
  {
    int b = in.readByte();
    if (b >= 0)
    {
      return b;
    }

    int v = b & 0x7f;
    b = in.readByte();
    if (b >= 0)
    {
      return v | b << 7;
    }

    v |= (b & 0x7f) << 7;
    b = in.readByte();
    if (b >= 0)
    {
      return v | b << 14;
    }

    v |= (b & 0x7f) << 14;
    b = in.readByte();
    if (b >= 0)
    {
      return v | b << 21;
    }

    v |= (b & 0x7f) << 21;
    b = in.readByte();
    return v | b << 28;
  }

  /**
   * @since 3.7
   */
  public static void writeVarLong(DataOutput out, long v) throws IOException
  {
    while ((v & ~0x7f) != 0)
    {
      out.writeByte((byte)(v & 0x7f | 0x80));
      v >>>= 7;
    }

    out.writeByte((byte)v);
  }

  /**
   * @since 3.7
   */
  public static long readVarLong(DataInput in) throws IOException
  {
    long v = in.readByte();
    if (v >= 0)
    {
      return v;
    }

    v &= 0x7f;
    for (int s = 7;; s += 7)
    {
      long b = in.readByte();
      v |= (b & 0x7f) << s;
      if (b >= 0)
      {
        return v;
      }
    }
  }

  public static void writeByteArray(DataOutput out, byte[] b) throws IOException
  {
    if (b != null)
    {
      out.writeInt(b.length);
      out.write(b);
    }
    else
    {
      out.writeInt(-1);
    }
  }

  public static byte[] readByteArray(DataInput in) throws IOException
  {
    int length = in.readInt();
    if (length < 0)
    {
      return null;
    }

    if (length == 0)
    {
      return EMPTY_BYTE_ARRAY;
    }

    byte[] b;
    try
    {
      b = new byte[length];
    }
    catch (Throwable t)
    {
      throw new IOException("Unable to allocate " + length + " bytes"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    in.readFully(b);
    return b;
  }

  public static void writeObject(final DataOutput out, Object object) throws IOException
  {
    ObjectOutput objectOutput = null;
    if (out instanceof ObjectOutput)
    {
      objectOutput = (ObjectOutput)out;
    }
    else
    {
      ObjectOutputStream wrapper = new ObjectOutputStream(new OutputStream()
      {
        @Override
        public void write(int b) throws IOException
        {
          out.writeByte((b & 0xff) + Byte.MIN_VALUE);
        }
      });

      if (ObjectUtil.never())
      {
        // Suppress resource leak warning.
        wrapper.close();
      }

      objectOutput = wrapper;
    }

    objectOutput.writeObject(object);
  }

  public static Object readObject(final DataInput in) throws IOException
  {
    return readObject(in, (ClassResolver)null);
  }

  public static Object readObject(final DataInput in, ClassLoader classLoader) throws IOException
  {
    return readObject(in, new ClassLoaderClassResolver(classLoader));
  }

  public static Object readObject(final DataInput in, final ClassResolver classResolver) throws IOException
  {
    ObjectInput objectInput = null;
    if (in instanceof ObjectInput)
    {
      objectInput = (ObjectInput)in;
    }
    else
    {
      ObjectInputStream wrapper = new ObjectInputStream(new InputStream()
      {
        @Override
        public int read() throws IOException
        {
          return in.readByte() - Byte.MIN_VALUE;
        }
      })
      {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
        {
          if (classResolver != null)
          {
            if (TRACER.isEnabled())
            {
              TRACER.format("Deserializing class {0}", desc.getName()); //$NON-NLS-1$
            }

            Class<?> c = classResolver.resolveClass(desc);
            if (c != null)
            {
              return c;
            }
          }

          return super.resolveClass(desc);
        }
      };

      if (ObjectUtil.never())
      {
        // Suppress resource leak warning.
        wrapper.close();
      }

      objectInput = wrapper;
    }

    try
    {
      return objectInput.readObject();
    }
    catch (ClassNotFoundException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void writeString(DataOutput out, String str) throws IOException
  {
    if (str != null)
    {
      int size = str.length();
      int start = 0;

      do
      {
        out.writeBoolean(true);
        int chunk = Math.min(size, MAX_UTF_CHARS);
        int end = start + chunk;
        out.writeUTF(str.substring(start, end));
        start = end;
        size -= chunk;
      } while (size > 0);
    }

    out.writeBoolean(false);
  }

  public static String readString(DataInput in) throws IOException
  {
    boolean more = in.readBoolean();
    if (!more)
    {
      return null;
    }

    StringBuilder builder = new StringBuilder();

    do
    {
      String chunk = in.readUTF();
      builder.append(chunk);
      more = in.readBoolean();
    } while (more);

    return builder.toString();
  }

  /**
   * @since 3.3
   */
  public static long writeBinaryStream(DataOutput out, InputStream inputStream) throws IOException
  {
    long length = 0;
    byte[] buffer = new byte[BYTE_BUFFER_SIZE];

    for (;;)
    {
      int n = inputStream.read(buffer);
      if (n == IOUtil.EOF)
      {
        out.writeShort(0);
        break;
      }

      out.writeShort(n);
      out.write(buffer, 0, n);
      length += n;
    }

    return length;
  }

  /**
   * @since 3.3
   */
  public static long readBinaryStream(DataInput in, OutputStream outputStream) throws IOException
  {
    long length = 0;
    byte[] buffer = new byte[BYTE_BUFFER_SIZE];

    for (;;)
    {
      int n = in.readShort();
      if (n == 0)
      {
        break;
      }

      in.readFully(buffer, 0, n);
      outputStream.write(buffer, 0, n);
      length += n;
    }

    return length;
  }

  /**
   * @since 3.3
   */
  public static long writeCharacterStream(DataOutput out, Reader reader) throws IOException
  {
    long length = 0;
    char[] buffer = new char[CHARACTER_BUFFER_SIZE];

    for (;;)
    {
      int n = reader.read(buffer);
      if (n == IOUtil.EOF)
      {
        out.writeShort(0);
        break;
      }

      out.writeShort(n);
      for (int i = 0; i < n; i++)
      {
        out.writeChar(buffer[i]);
      }

      length += n;
    }

    return length;
  }

  /**
   * @since 3.3
   */
  public static long readCharacterStream(DataInput in, Writer writer) throws IOException
  {
    long length = 0;
    char[] buffer = new char[CHARACTER_BUFFER_SIZE];

    for (;;)
    {
      int n = in.readShort();
      if (n == 0)
      {
        break;
      }

      for (int i = 0; i < n; i++)
      {
        buffer[i] = in.readChar();
      }

      writer.write(buffer, 0, n);
      length += n;
    }

    return length;
  }

  /**
   * @since 3.0
   */
  public static void writeEnum(DataOutput out, Enum<?> literal) throws IOException
  {
    if (literal == null)
    {
      out.writeByte(NO_ENUM_LITERAL);
    }
    else
    {
      getEnumLiterals(literal.getDeclaringClass()); // Check valid size

      int ordinal = literal.ordinal();
      out.writeByte(ordinal + Byte.MIN_VALUE + 1);
    }
  }

  /**
   * @since 3.0
   */
  public static <T extends Enum<?>> T readEnum(DataInput in, Class<T> type) throws IOException
  {
    T[] literals = getEnumLiterals(type);

    int ordinal = in.readByte();
    if (ordinal == NO_ENUM_LITERAL)
    {
      return null;
    }

    return literals[ordinal - Byte.MIN_VALUE - 1];
  }

  /**
   * @since 3.25
   */
  public static void writeProperties(DataOutput out, Map<String, String> properties) throws IOException
  {
    int size = properties == null ? 0 : properties.size();
    writeVarInt(out, size);

    for (Entry<String, String> entry : properties.entrySet())
    {
      writeString(out, entry.getKey());
      writeString(out, entry.getValue());
    }
  }

  /**
   * @since 3.25
   */
  public static Map<String, String> readProperties(DataInput in) throws IOException
  {
    int size = readVarInt(in);
    Map<String, String> properties = new HashMap<>(size);

    for (int i = 0; i < size; i++)
    {
      String key = readString(in);
      String value = readString(in);
      properties.put(key, value);
    }

    return properties;
  }

  /**
   * @since 3.4
   */
  public static void writeException(DataOutput out, Throwable t) throws IOException
  {
    String message = StringUtil.formatException(t);
    writeString(out, message);

    byte[] bytes = ExtendedIOUtil.serializeThrowable(t);
    writeByteArray(out, bytes);
  }

  /**
   * @since 3.4
   */
  public static Throwable readException(DataInput in) throws IOException
  {
    String message = readString(in);
    byte[] bytes = readByteArray(in);

    try
    {
      return ExtendedIOUtil.deserializeThrowable(bytes);
    }
    catch (Throwable couldNotLoadExceptionClass)
    {
      return new RuntimeException(message);
    }
  }

  /**
   * @since 3.4
   */
  public static byte[] serializeThrowable(Throwable t)
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      writeObject(dos, t);
      return baos.toByteArray();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  /**
   * @since 3.4
   */
  public static Throwable deserializeThrowable(byte[] bytes)
  {
    try
    {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      DataInputStream dis = new DataInputStream(bais);
      return (Throwable)readObject(dis, OM.class.getClassLoader());
    }
    catch (IOException ex)
    {
      return null;
    }
  }

  private static <T> T[] getEnumLiterals(Class<T> type)
  {
    T[] literals = type.getEnumConstants();

    int size = literals.length;
    if (size > MAX_ENUM_LITERALS)
    {
      throw new AssertionError("Enum too large: " + size + " literals");
    }

    return literals;
  }

  /**
   * @author Eike Stepper
   */
  public interface ClassResolver
  {
    public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException;
  }

  /**
   * @author Eike Stepper
   */
  public static class ClassLoaderClassResolver implements ClassResolver
  {
    private static final String STACK_TRACE_ELEMENT = StackTraceElement[].class.getName();

    private ClassLoader classLoader;

    public ClassLoaderClassResolver(ClassLoader classLoader)
    {
      this.classLoader = classLoader;
    }

    @Override
    public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException
    {
      String className = v.getName();

      try
      {
        return classLoader.loadClass(className);
      }
      catch (ClassNotFoundException ex)
      {
        if (!STACK_TRACE_ELEMENT.equals(className))
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Exception in resolver", ex); //$NON-NLS-1$
          }
        }

        return null;
      }
    }
  }
}
