/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryProvider;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public class LoadLibrariesIndication extends CDOServerIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadLibrariesIndication.class);

  private String[] libraryNames;

  public LoadLibrariesIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_LIBRARIES);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} library names", size);
    }

    libraryNames = new String[size];
    for (int i = 0; i < size; i++)
    {
      libraryNames[i] = in.readString();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read library name: {0}", libraryNames[i]);
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    byte[] buffer = new byte[IOUtil.DEFAULT_BUFFER_SIZE];
    CDOIDLibraryProvider libraryProvider = getStore().getCDOIDLibraryProvider();
    for (String libraryName : libraryNames)
    {
      int size = libraryProvider.getSize(libraryName);
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing library {0}: {1} bytes", libraryName, size);
      }

      out.writeInt(size);
      InputStream in = null;

      try
      {
        in = libraryProvider.getContents(libraryName);
        IOUtil.copy(in, getCurrentOutputStream(), size, buffer);
      }
      finally
      {
        IOUtil.close(in);
      }
    }
  }
}
