/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase2;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.io.IOException;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOWorkspaceBase implements InternalCDOWorkspaceBase, CDOWorkspaceBase2
{
  private InternalCDOWorkspace workspace;

  private InternalStore store;

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDOBranchManager branchManager;

  private Set<CDOID> ids;

  protected AbstractCDOWorkspaceBase()
  {
  }

  public void init(InternalCDOWorkspace workspace)
  {
    this.workspace = workspace;
    InternalRepository localRepository = workspace.getLocalRepository();
    store = localRepository.getStore();
    packageRegistry = localRepository.getPackageRegistry(false);
    branchManager = localRepository.getBranchManager();
  }

  public final InternalCDOWorkspace getWorkspace()
  {
    return workspace;
  }

  public final synchronized Set<CDOID> getIDs()
  {
    if (ids == null)
    {
      ids = doGetIDs();
    }

    return ids;
  }

  public final synchronized void updateAfterCommit(CDOTransaction transaction)
  {
    InternalCDOTransaction tx = (InternalCDOTransaction)transaction;
    Set<CDOID> dirtyObjects = tx.getDirtyObjects().keySet();
    Set<CDOID> detachedObjects = tx.getDetachedObjects().keySet();
    for (InternalCDORevision revision : tx.getCleanRevisions().values())
    {
      CDOID id = revision.getID();
      if (dirtyObjects.contains(id) || detachedObjects.contains(id))
      {
        if (isAddedObject(id))
        {
          if (ids != null)
          {
            ids.remove(id);
          }

          deregisterObject(id);
        }
        else
        {
          getIDs().add(id);
          registerChangedOrDetachedObject(revision);
        }
      }
    }

    // Don't use keySet() because only the values() are ID-mapped!
    for (CDOObject object : tx.getNewObjects().values())
    {
      CDOID id = object.cdoID();
      getIDs().add(id);
      registerAddedObject(id);
    }
  }

  public final synchronized void clear()
  {
    ids = null;
    doClear();
  }

  public final synchronized boolean isEmpty()
  {
    return ids == null || ids.isEmpty();
  }

  public final synchronized boolean containsID(CDOID id)
  {
    return getIDs().contains(id);
  }

  protected boolean isAddedObject(CDOID id)
  {
    // throw new RuntimeException("Check whether CDOID.isLocal() is still valid with UUIDs");
    return store.isLocal(id);
  }

  protected CDODataInput createCDODataInput(ExtendedDataInputStream edis) throws IOException
  {
    CDORevisionFactory revisionFactory = CDORevisionFactory.DEFAULT;
    CDOListFactory listFactory = CDOListFactory.DEFAULT;
    return CDOCommonUtil.createCDODataInput(edis, packageRegistry, branchManager, null, revisionFactory, listFactory,
        null);
  }

  protected CDODataOutput createCDODataOutput(ExtendedDataOutputStream edos)
  {
    CDOIDProvider idProvider = CDOIDProvider.NOOP;
    return CDOCommonUtil.createCDODataOutput(edos, packageRegistry, idProvider);
  }

  protected abstract void doClear();

  protected abstract Set<CDOID> doGetIDs();

  protected abstract void registerChangedOrDetachedObject(InternalCDORevision revision);

  protected abstract void registerAddedObject(CDOID id);

  protected abstract void deregisterObject(CDOID id);
}
