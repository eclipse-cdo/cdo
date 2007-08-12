/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.RepositoryPackageManager;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IORuntimeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("unused")
public class CommitTransactionIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionIndication.class);

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CommitTransactionIndication.class);

  private CDOPackageImpl[] newPackages;

  private CDORevisionImpl[] newResources;

  private CDORevisionImpl[] newObjects;

  private CDORevisionImpl[] dirtyObjects;

  private Map<CDOID, CDOID> idMappings = new HashMap();

  private long timeStamp;

  public CommitTransactionIndication()
  {
    super(CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION);
  }

  @Override
  protected void indicating(final ExtendedDataInputStream in) throws IOException
  {
    timeStamp = System.currentTimeMillis();
    int viewID = in.readInt();
    transact(new Runnable()
    {
      public void run()
      {
        try
        {
          addNewPackages(in);
          newResources = readNewResources(in);
          newObjects = readNewObjects(in);
          dirtyObjects = readDirtyObjects(in);
        }
        catch (IOException ex)
        {
          throw new IORuntimeException(ex);
        }

        addRevisions(newResources);
        addRevisions(newObjects);
        addRevisions(dirtyObjects);
      }
    });

  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeLong(timeStamp);
    if (newPackages != null)
    {
      for (CDOPackageImpl newPackage : newPackages)
      {
        CDOIDRangeImpl.write(out, newPackage.getMetaIDRange());
      }
    }

    writeIDMappings(out);
    if (dirtyObjects.length > 0)
    {
      getSessionManager().notifyInvalidation(timeStamp, dirtyObjects, getSession());
    }
  }

  private void addNewPackages(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new packages", size);
    }

    if (size != 0)
    {
      RepositoryPackageManager packageManager = getPackageManager();
      newPackages = new CDOPackageImpl[size];
      for (int i = 0; i < size; i++)
      {
        newPackages[i] = new CDOPackageImpl(packageManager, in);
        CDOIDRange oldRange = newPackages[i].getMetaIDRange();

        packageManager.addPackage(newPackages[i]);
        CDOIDRange newRange = newPackages[i].getMetaIDRange();

        long count = oldRange.getCount();
        for (long l = 0; l < count; l++)
        {
          CDOID oldID = oldRange.get(l);
          CDOID newID = newRange.get(l);

          if (TRACER.isEnabled())
          {
            TRACER.format("Mapping ID: {0} --> {1}", oldID, newID);
          }

          idMappings.put(oldID, newID);
        }
      }
    }
  }

  private CDORevisionImpl[] readNewResources(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new resources", size);
    }

    return readRevisions(in, size);
  }

  private CDORevisionImpl[] readNewObjects(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} new objects", size);
    }

    return readRevisions(in, size);
  }

  private CDORevisionImpl[] readDirtyObjects(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} dirty objects", size);
    }

    return readRevisions(in, size);
  }

  private CDORevisionImpl[] readRevisions(ExtendedDataInputStream in, int size) throws IOException
  {
    CDOPackageManagerImpl packageManager = getRepository().getPackageManager();
    CDORevisionImpl[] revisions = new CDORevisionImpl[size];
    for (int i = 0; i < size; i++)
    {
      revisions[i] = new CDORevisionImpl(packageManager, in);
      mapTemporaryID(revisions[i]);
    }

    return revisions;
  }

  private void addRevisions(CDORevisionImpl[] revisions)
  {
    RevisionManager revisionManager = getRevisionManager();
    for (CDORevisionImpl revision : revisions)
    {
      revision.setCreated(timeStamp);
      revision.adjustReferences(idMappings);
      revisionManager.addRevision(revision);
    }
  }

  private void mapTemporaryID(CDORevisionImpl revision)
  {
    CDOID oldID = revision.getID();
    if (oldID.isTemporary())
    {
      CDOID newID = getRepository().getNextCDOID();
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapping ID: {0} --> {1}", oldID, newID);
      }

      idMappings.put(oldID, newID);
      revision.setID(newID);
    }
  }

  private void writeIDMappings(ExtendedDataOutputStream out) throws IOException
  {
    for (Entry<CDOID, CDOID> entry : idMappings.entrySet())
    {
      CDOID oldID = entry.getKey();
      if (!oldID.isMeta())
      {
        CDOID newID = entry.getValue();
        CDOIDImpl.write(out, oldID);
        CDOIDImpl.write(out, newID);
      }
    }

    CDOIDImpl.write(out, CDOID.NULL);
  }
}
