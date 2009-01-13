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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class LoadLibrariesRequest extends RequestWithConfirmation<Integer>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadLibrariesRequest.class);

  private Collection<String> libraryNames;

  private File cacheFolder;

  public LoadLibrariesRequest(CDOClientProtocol protocol, Collection<String> libraryNames, File cacheFolder)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_LIBRARIES);
    this.libraryNames = libraryNames;
    this.cacheFolder = cacheFolder;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    int size = libraryNames.size();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} library names", size);
    }

    out.writeInt(size);
    for (String libraryName : libraryNames)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Writing library name: {0}", libraryName);
      }

      out.writeString(libraryName);
    }
  }

  @Override
  protected Integer confirming(ExtendedDataInputStream in) throws Exception
  {
    byte[] buffer = new byte[IOUtil.DEFAULT_BUFFER_SIZE];
    int count = 0;
    for (String libraryName : libraryNames)
    {
      int size = in.readInt();
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Reading library {0}: {1} bytes", libraryName, size);
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
