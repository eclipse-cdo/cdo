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
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOWorkspaceBase implements InternalCDOWorkspaceBase
{
  private InternalCDOWorkspace workspace;

  private Set<CDOID> ids;

  protected AbstractCDOWorkspaceBase()
  {
  }

  public void init(InternalCDOWorkspace workspace)
  {
    this.workspace = workspace;
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

  @Deprecated
  public final void updateAfterCommit(CDOTransaction transaction)
  {
    throw new UnsupportedOperationException();
  }

  public final synchronized void registerChangedOrDetachedObject(InternalCDORevision revision)
  {
    getIDs().add(revision.getID());
    doRegisterChangedOrDetachedObject(revision);
  }

  public final synchronized void registerAddedObject(CDOID id)
  {
    getIDs().add(id);
    doRegisterAddedObject(id);
  }

  public final synchronized void deregisterObject(CDOID id)
  {
    if (ids != null)
    {
      ids.remove(id);
    }

    doDeregisterObject(id);
  }

  public final synchronized void clear()
  {
    ids = new HashSet<CDOID>();
    doClear();
  }

  public final synchronized boolean isEmpty()
  {
    return getIDs().isEmpty();
  }

  public final synchronized boolean containsID(CDOID id)
  {
    return getIDs().contains(id);
  }

  protected CDODataInput createCDODataInput(ExtendedDataInputStream edis) throws IOException
  {
    InternalCDOPackageRegistry packageRegistry = workspace.getLocalRepository().getPackageRegistry(false);
    InternalCDOBranchManager branchManager = workspace.getLocalRepository().getBranchManager();
    CDORevisionFactory revisionFactory = CDORevisionFactory.DEFAULT;
    CDOListFactory listFactory = CDOListFactory.DEFAULT;

    return CDOCommonUtil.createCDODataInput(edis, packageRegistry, branchManager, null, revisionFactory, listFactory,
        null);
  }

  protected CDODataOutput createCDODataOutput(ExtendedDataOutputStream edos)
  {
    InternalCDOPackageRegistry packageRegistry = workspace.getLocalRepository().getPackageRegistry(false);
    CDOIDProvider idProvider = CDOIDProvider.NOOP;

    return CDOCommonUtil.createCDODataOutput(edos, packageRegistry, idProvider);
  }

  protected abstract void doClear();

  protected abstract Set<CDOID> doGetIDs();

  protected abstract void doRegisterChangedOrDetachedObject(InternalCDORevision revision);

  protected abstract void doRegisterAddedObject(CDOID id);

  protected abstract void doDeregisterObject(CDOID id);
}
