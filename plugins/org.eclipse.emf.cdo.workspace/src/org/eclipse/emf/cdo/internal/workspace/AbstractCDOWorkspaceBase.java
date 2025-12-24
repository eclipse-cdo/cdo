/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.emf.cdo.spi.server.InternalRepository;
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

  @Override
  public void init(InternalCDOWorkspace workspace)
  {
    this.workspace = workspace;
  }

  @Override
  public final InternalCDOWorkspace getWorkspace()
  {
    return workspace;
  }

  @Override
  public final synchronized Set<CDOID> getIDs()
  {
    if (ids == null)
    {
      ids = doGetIDs();
    }

    return ids;
  }

  @Override
  @Deprecated
  public final void updateAfterCommit(CDOTransaction transaction)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public final synchronized void registerChangedOrDetachedObject(InternalCDORevision revision)
  {
    getIDs().add(revision.getID());
    doRegisterChangedOrDetachedObject(revision);
  }

  @Override
  public final synchronized void registerAddedAndDetachedObject(InternalCDORevision revision)
  {
    doRegisterAddedAndDetachedObject(revision);
    deregisterObject(revision.getID());
  }

  @Override
  public final synchronized void registerAddedObject(CDOID id)
  {
    getIDs().add(id);
    doRegisterAddedObject(id);
  }

  @Override
  public final synchronized void deregisterObject(CDOID id)
  {
    if (ids != null)
    {
      ids.remove(id);
    }

    doDeregisterObject(id);
  }

  @Override
  public final synchronized void clear()
  {
    ids = new HashSet<>();
    doClear();
  }

  @Override
  public final synchronized boolean isEmpty()
  {
    return getIDs().isEmpty();
  }

  @Override
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

    return CDOCommonUtil.createCDODataInput(edis, packageRegistry, branchManager, null, revisionFactory, listFactory, null);
  }

  protected CDODataOutput createCDODataOutput(ExtendedDataOutputStream edos)
  {
    InternalRepository localRepository = workspace.getLocalRepository();
    InternalCDOPackageRegistry packageRegistry = localRepository.getPackageRegistry(false);
    CDOIDProvider idProvider = CDOIDProvider.NOOP;

    return CDOCommonUtil.createCDODataOutput(edos, packageRegistry, idProvider, localRepository);
  }

  protected abstract void doClear();

  protected abstract Set<CDOID> doGetIDs();

  protected abstract void doRegisterChangedOrDetachedObject(InternalCDORevision revision);

  protected abstract void doRegisterAddedAndDetachedObject(InternalCDORevision revision);

  protected abstract void doRegisterAddedObject(CDOID id);

  protected abstract void doDeregisterObject(CDOID id);
}
