/*
 * Copyright (c) 2007, 2009, 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class GZIPStreamWrapper implements IStreamWrapper
{
  /**
   * @since 3.6
   */
  public static final int DEFAULT_BUFFER_SIZE = 512;

  /**
   * @since 3.6
   */
  public static final int DEFAULT_COMPRESSION_LEVEL = Deflater.BEST_SPEED;

  private final int bufferSize;

  private final int compressionLevel;

  public GZIPStreamWrapper()
  {
    this(DEFAULT_BUFFER_SIZE, DEFAULT_COMPRESSION_LEVEL);
  }

  /**
   * @since 3.6
   */
  public GZIPStreamWrapper(int bufferSize, int compressionLevel)
  {
    this.bufferSize = bufferSize;
    this.compressionLevel = compressionLevel;
  }

  @Override
  public GZIPInputStream wrapInputStream(InputStream in) throws IOException
  {
    if (in instanceof GZIPInputStream)
    {
      return (GZIPInputStream)in;
    }

    return new GZIPInputStream(in, bufferSize);
  }

  @Override
  public GZIPOutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (out instanceof GZIPOutputStream)
    {
      return (GZIPOutputStream)out;
    }

    return new GZIPOutputStream(out, bufferSize)
    {
      {
        def.setLevel(compressionLevel);
      }
    };
  }

  @Override
  public void finishInputStream(InputStream in) throws IOException
  {
  }

  @Override
  public void finishOutputStream(OutputStream out) throws IOException
  {
    ((GZIPOutputStream)out).finish();
  }

  /**
   * @author Esteban Dugueperoux
   * @since 3.6
   */
  public static class Factory extends IStreamWrapper.Factory
  {
    public static final String TYPE = "gzip"; //$NON-NLS-1$

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public IStreamWrapper create(String description) throws ProductCreationException
    {
      int bufferSize = GZIPStreamWrapper.DEFAULT_BUFFER_SIZE;
      int compressionLevel = GZIPStreamWrapper.DEFAULT_COMPRESSION_LEVEL;

      String[] properties = description.split(",");
      for (String property : properties)
      {
        String[] tokens = property.split("=");
        if (tokens.length == 2)
        {
          String propertyName = tokens[0];
          String propertyValue = tokens[1];

          if ("bufferSize".equals(propertyName))
          {
            try
            {
              bufferSize = Integer.valueOf(propertyValue);
            }
            catch (NumberFormatException ex)
            {
              throw new ProductCreationException(ex);
            }
          }
          else if ("compressionLevel".equals(propertyName))
          {
            try
            {
              compressionLevel = Integer.valueOf(propertyValue);
            }
            catch (NumberFormatException ex)
            {
              throw new ProductCreationException(ex);
            }
          }
        }
      }

      return new GZIPStreamWrapper(bufferSize, compressionLevel);
    }
  }
}
