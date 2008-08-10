/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class SyncRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, SyncRevisionIndication.class);

  private List<InternalCDORevision> dirtyObjects = new ArrayList<InternalCDORevision>();

  private int referenceChunk;

  public SyncRevisionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_SYNC;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    IStoreReader reader = StoreUtil.getReader();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Refreshing reader : " + reader);
    }

    reader.refreshRevisions();
    referenceChunk = in.readInt();
    int size = in.readInt();
    CDOIDObjectFactory factory = getStore().getCDOIDObjectFactory();
    for (int i = 0; i < size; i++)
    {
      CDOID cdoID = CDOIDUtil.read(in, factory);
      int version = in.readInt();
      if (version > 0)
      {
        InternalCDORevision revision = getRevisionManager().getRevision(cdoID, referenceChunk);
        if (revision.getVersion() != version)
        {
          dirtyObjects.add(revision);
        }
      }
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Sync found " + dirtyObjects.size() + " dirty objects");
    }

    out.writeInt(dirtyObjects.size());
    for (InternalCDORevision revision : dirtyObjects)
    {
      revision.write(out, getSession(), referenceChunk);
    }
  }
}
