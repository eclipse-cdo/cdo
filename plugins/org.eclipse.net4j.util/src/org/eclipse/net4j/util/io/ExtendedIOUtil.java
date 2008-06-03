/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

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

  private ExtendedIOUtil()
  {
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

  public static void writeObject(final DataOutput out, Object object) throws IOException
  {
    ObjectOutput wrapper = null;
    if (out instanceof ObjectOutput)
    {
      wrapper = (ObjectOutput)out;
    }
    else
    {
      wrapper = new ObjectOutputStream(new OutputStream()
      {
        @Override
        public void write(int b) throws IOException
        {
          out.writeByte((b & 0xff) + Byte.MIN_VALUE);
        }
      });
    }

    wrapper.writeObject(object);
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

  public static byte[] readByteArray(DataInput in) throws IOException
  {
    int length = in.readInt();
    if (length < 0)
    {
      return null;
    }

    byte[] b;
    try
    {
      b = new byte[length];
    }
    catch (Throwable t)
    {
      throw new IOException("Unable to allocate " + length + " bytes");
    }

    in.readFully(b);
    return b;
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
    ObjectInput wrapper = null;
    if (in instanceof ObjectInput)
    {
      wrapper = (ObjectInput)in;
    }
    else
    {
      wrapper = new ObjectInputStream(new InputStream()
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
              TRACER.format("Deserializing class {0}", desc.getName());
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
    }

    try
    {
      return wrapper.readObject();
    }
    catch (ClassNotFoundException ex)
    {
      OM.LOG.error(ex);
      throw WrappedException.wrap(ex);
    }
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
    private ClassLoader classLoader;

    public ClassLoaderClassResolver(ClassLoader classLoader)
    {
      this.classLoader = classLoader;
    }

    public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException
    {
      String className = v.getName();

      try
      {
        return classLoader.loadClass(className);
      }
      catch (ClassNotFoundException ex)
      {
        OM.LOG.error(ex);
        return null;
      }
    }
  }
}
