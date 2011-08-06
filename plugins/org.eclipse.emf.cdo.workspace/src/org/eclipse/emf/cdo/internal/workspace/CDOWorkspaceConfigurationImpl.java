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

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
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

  private String branchPath;

  private long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

  private IDGenerationLocation idGenerationLocation = IDGenerationLocation.CLIENT;

  private CDOIDGenerator idGenerator;

  public CDOWorkspaceConfigurationImpl()
  {
  }

  public String getLocalRepositoryName()
  {
    return localRepositoryName;
  }

  public void setLocalRepositoryName(String localRepositoryName)
  {
    this.localRepositoryName = localRepositoryName;
  }

  public IStore getStore()
  {
    return store;
  }

  public void setStore(IStore store)
  {
    this.store = store;
  }

  public CDOWorkspaceBase getBase()
  {
    return base;
  }

  public void setBase(CDOWorkspaceBase base)
  {
    this.base = base;
  }

  public CDOSessionConfigurationFactory getRemote()
  {
    return remote;
  }

  public void setRemote(CDOSessionConfigurationFactory remote)
  {
    this.remote = remote;
  }

  public String getBranchPath()
  {
    return branchPath;
  }

  public void setBranchPath(String branchPath)
  {
    this.branchPath = branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    return idGenerationLocation;
  }

  public void setIDGenerationLocation(IDGenerationLocation idGenerationLocation)
  {
    this.idGenerationLocation = idGenerationLocation;
  }

  public CDOIDGenerator getIDGenerator()
  {
    return idGenerator;
  }

  public void setIDGenerator(CDOIDGenerator idGenerator)
  {
    this.idGenerator = idGenerator;
  }

  public CDOWorkspace open()
  {
    return new CDOWorkspaceImpl(localRepositoryName, store, idGenerationLocation, idGenerator,
        (InternalCDOWorkspaceBase)base, remote);
  }

  public CDOWorkspace checkout()
  {
    return new CDOWorkspaceImpl(localRepositoryName, store, idGenerationLocation, idGenerator,
        (InternalCDOWorkspaceBase)base, remote, branchPath, timeStamp);
  }
}
