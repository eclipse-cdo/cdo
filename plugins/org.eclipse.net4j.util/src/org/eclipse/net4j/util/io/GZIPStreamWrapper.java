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
public class GZIPStreamWrapper implements IStreamWrapper
{
  public GZIPStreamWrapper()
  {
  }

  public InputStream wrapInputStream(InputStream in)
  {
    if (in instanceof GZIPInputStream)
    {
      return in;
    }

    try
    {
      return new GZIPInputStream(in);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public OutputStream wrapOutputStream(OutputStream out)
  {
    if (out instanceof GZIPOutputStream)
    {
      return out;
    }

    try
    {
      return new GZIPOutputStream(out);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
