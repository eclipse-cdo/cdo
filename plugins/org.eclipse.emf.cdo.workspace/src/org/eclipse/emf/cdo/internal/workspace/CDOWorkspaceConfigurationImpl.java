/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceConfiguration;

/**
 * @author Eike Stepper
 * @since 4.1
 */
public class CDOWorkspaceConfigurationImpl implements CDOWorkspaceConfiguration
{
  private String localRepositoryName = DEFAULT_LOCAL_REPOSITORY_NAME;

  private IStore store;

  private CDOWorkspaceBase base;

  private CDOSessionConfigurationFactory remote;

  private int branchID = InternalCDOWorkspace.NO_BRANCH_ID;

  private String branchPath;

  private long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

  private IDGenerationLocation idGenerationLocation = IDGenerationLocation.CLIENT;

  private CDOIDGenerator idGenerator;

  public CDOWorkspaceConfigurationImpl()
  {
  }

  @Override
  public String getLocalRepositoryName()
  {
    return localRepositoryName;
  }

  @Override
  public void setLocalRepositoryName(String localRepositoryName)
  {
    this.localRepositoryName = localRepositoryName;
  }

  @Override
  public IStore getStore()
  {
    return store;
  }

  @Override
  public void setStore(IStore store)
  {
    this.store = store;
  }

  @Override
  public CDOWorkspaceBase getBase()
  {
    return base;
  }

  @Override
  public void setBase(CDOWorkspaceBase base)
  {
    this.base = base;
  }

  @Override
  public CDOSessionConfigurationFactory getRemote()
  {
    return remote;
  }

  @Override
  public void setRemote(CDOSessionConfigurationFactory remote)
  {
    this.remote = remote;
  }

  @Override
  public int getBranchID()
  {
    return branchID;
  }

  @Override
  public void setBranchID(int branchID)
  {
    this.branchID = branchID;
  }

  @Override
  public String getBranchPath()
  {
    return branchPath;
  }

  @Override
  public void setBranchPath(String branchPath)
  {
    this.branchPath = branchPath;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  @Override
  public IDGenerationLocation getIDGenerationLocation()
  {
    return idGenerationLocation;
  }

  @Override
  public void setIDGenerationLocation(IDGenerationLocation idGenerationLocation)
  {
    this.idGenerationLocation = idGenerationLocation;
  }

  @Override
  public CDOIDGenerator getIDGenerator()
  {
    return idGenerator;
  }

  @Override
  public void setIDGenerator(CDOIDGenerator idGenerator)
  {
    this.idGenerator = idGenerator;
  }

  @Override
  public CDOWorkspace open()
  {
    return new CDOWorkspaceImpl(localRepositoryName, store, idGenerationLocation, idGenerator, (InternalCDOWorkspaceBase)base, remote);
  }

  @Override
  public CDOWorkspace checkout()
  {
    return new CDOWorkspaceImpl(localRepositoryName, store, idGenerationLocation, idGenerator, (InternalCDOWorkspaceBase)base, remote, branchID, branchPath,
        timeStamp);
  }
}
