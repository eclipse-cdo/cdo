/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Eike Stepper
 */
public class GZIPStreamWrapper implements IStreamWrapper<GZIPInputStream, GZIPOutputStream>
{
  public GZIPStreamWrapper()
  {
  }

  public GZIPInputStream wrapInputStream(InputStream in) throws IOException
  {
    if (in instanceof GZIPInputStream)
    {
      return (GZIPInputStream)in;
    }

    return new GZIPInputStream(in);
  }

  public GZIPOutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (out instanceof GZIPOutputStream)
    {
      return (GZIPOutputStream)out;
    }

    return new GZIPOutputStream(out);
  }

  public void finishInputStream(GZIPInputStream in) throws IOException
  {
  }

  public void finishOutputStream(GZIPOutputStream out) throws IOException
  {
    out.finish();
  }
}
