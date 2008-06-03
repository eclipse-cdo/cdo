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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class LoadLibrariesRequest extends RequestWithConfirmation<Integer>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadLibrariesRequest.class);

  private Collection<String> libraryNames;

  private File cacheFolder;

  public LoadLibrariesRequest(IChannel channel, Collection<String> libraryNames, File cacheFolder)
  {
    super(channel);
    this.libraryNames = libraryNames;
    this.cacheFolder = cacheFolder;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_LIBRARIES;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    int size = libraryNames.size();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} library names", size);
    }

    out.writeInt(size);
    for (String libraryName : libraryNames)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing library name: {0}", libraryName);
      }

      out.writeString(libraryName);
    }
  }

  @Override
  protected Integer confirming(ExtendedDataInputStream in) throws IOException
  {
    byte[] buffer = new byte[IOUtil.DEFAULT_BUFFER_SIZE];
    int count = 0;
    for (String libraryName : libraryNames)
    {
      int size = in.readInt();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Reading library {0}: {1} bytes", libraryName, size);
      }

      File file = new File(cacheFolder, libraryName);
      FileOutputStream out = null;

      try
      {
        out = new FileOutputStream(file);
        IOUtil.copy(in, out, size, buffer);
      }
      finally
      {
        IOUtil.close(out);
      }

      ++count;
    }

    return count;
  }
}
