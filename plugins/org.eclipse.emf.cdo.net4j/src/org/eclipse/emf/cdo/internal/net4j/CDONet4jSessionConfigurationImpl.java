/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.internal.cdo.session.CDOSessionConfigurationImpl;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.signal.failover.NOOPFailOverStrategy;
import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.OpenSessionResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RepositoryTimeResult;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionConfigurationImpl extends CDOSessionConfigurationImpl implements
    org.eclipse.emf.cdo.net4j.CDOSessionConfiguration
{
  private String repositoryName;

  private IConnector connector;

  private IFailOverStrategy failOverStrategy;

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDOBranchManager branchManager;

  private InternalCDORevisionManager revisionManager;

  public CDONet4jSessionConfigurationImpl()
  {
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    checkNotOpen();
    this.repositoryName = repositoryName;
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    checkNotOpen();
    this.connector = connector;
  }

  public IFailOverStrategy getFailOverStrategy()
  {
    return failOverStrategy;
  }

  public void setFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    checkNotOpen();
    this.failOverStrategy = failOverStrategy;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    checkNotOpen();
    this.packageRegistry = (InternalCDOPackageRegistry)packageRegistry;
  }

  public InternalCDOBranchManager getBranchManager()
  {
    return branchManager;
  }

  public void setBranchManager(CDOBranchManager branchManager)
  {
    checkNotOpen();
    this.branchManager = (InternalCDOBranchManager)branchManager;
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public void setRevisionManager(CDORevisionManager revisionManager)
  {
    checkNotOpen();
    this.revisionManager = (InternalCDORevisionManager)revisionManager;
  }

  @Override
  public org.eclipse.emf.cdo.net4j.CDOSession openSession()
  {
    return (org.eclipse.emf.cdo.net4j.CDOSession)super.openSession();
  }

  public InternalCDOSession createSession()
  {
    if (isActivateOnOpen())
    {
      CheckUtil.checkState(connector != null ^ failOverStrategy != null,
          "Specify exactly one of connector or failOverStrategy"); //$NON-NLS-1$
    }

    return new CDONet4jSessionImpl(this);
  }

  @Override
  public void activateSession(InternalCDOSession session) throws Exception
  {
    super.activateSession(session);
    CDOClientProtocol protocol = new CDOClientProtocol();
    protocol.setInfraStructure(session);
    session.setSessionProtocol(protocol);
    if (connector != null)
    {
      protocol.setFailOverStrategy(new NOOPFailOverStrategy(connector));
    }
    else if (failOverStrategy != null)
    {
      protocol.setFailOverStrategy(failOverStrategy);
    }

    OpenSessionResult result = protocol.openSession(repositoryName, isPassiveUpdateEnabled());
    session.setSessionID(result.getSessionID());
    session.setLastUpdateTime(result.getLastUpdateTime());
    session.setRepositoryInfo(new RepositoryInfo(repositoryName, result));

    packageRegistry = new CDOPackageRegistryImpl();
    packageRegistry.setPackageProcessor(session);
    packageRegistry.setPackageLoader(session);
    packageRegistry.activate();

    revisionManager = new CDORevisionManagerImpl();
    revisionManager.setSupportingBranches(session.getRepositoryInfo().isSupportingBranches());
    revisionManager.setRevisionLoader(session.getSessionProtocol());
    revisionManager.setRevisionLocker(session);
    // if (result.isRepositorySupportingBranches())
    // {
    // revisionManager = new DelegatingCDORevisionManager.SyntheticsConverter(revisionManager);
    // }

    revisionManager.activate();

    branchManager = CDOBranchUtil.createBranchManager();
    branchManager.setBranchLoader(session.getSessionProtocol());
    branchManager.setTimeProvider(session.getRepositoryInfo());
    branchManager.initMainBranch(session.getRepositoryInfo().getCreationTime());
    branchManager.activate();

    for (InternalCDOPackageUnit packageUnit : result.getPackageUnits())
    {
      if (EcorePackage.eINSTANCE.getNsURI().equals(packageUnit.getID()))
      {
        EMFUtil.addAdapter(EcorePackage.eINSTANCE, packageUnit.getTopLevelPackageInfo());
        packageUnit.setState(CDOPackageUnit.State.LOADED);
      }
      else if (EresourcePackage.eINSTANCE.getNsURI().equals(packageUnit.getID()))
      {
        EMFUtil.addAdapter(EresourcePackage.eINSTANCE, packageUnit.getTopLevelPackageInfo());
        packageUnit.setState(CDOPackageUnit.State.LOADED);
      }

      getPackageRegistry().putPackageUnit(packageUnit);
    }
  }

  @Override
  public void deactivateSession(InternalCDOSession session) throws Exception
  {
    revisionManager.deactivate();
    revisionManager = null;

    packageRegistry.deactivate();
    packageRegistry = null;

    super.deactivateSession(session);
  }

  /**
   * @author Eike Stepper
   */
  protected class RepositoryInfo implements CDORepositoryInfo
  {
    private String name;

    private String uuid;

    private long creationTime;

    private RepositoryTimeResult timeResult;

    private boolean supportingAudits;

    private boolean supportingBranches;

    public RepositoryInfo(String name, OpenSessionResult result)
    {
      this.name = name;
      uuid = result.getRepositoryUUID();
      creationTime = result.getRepositoryCreationTime();
      timeResult = result.getRepositoryTimeResult();
      supportingAudits = result.isRepositorySupportingAudits();
      supportingBranches = result.isRepositorySupportingBranches();
    }

    public String getName()
    {
      return name;
    }

    /**
     * Must be callable before session activation has finished!
     */
    public String getUUID()
    {
      return uuid;
    }

    public long getCreationTime()
    {
      return creationTime;
    }

    public long getTimeStamp()
    {
      return getTimeStamp(false);
    }

    public long getTimeStamp(boolean forceRefresh)
    {
      if (timeResult == null || forceRefresh)
      {
        timeResult = refreshTime();
      }

      return timeResult.getAproximateRepositoryTime();
    }

    public boolean isSupportingAudits()
    {
      return supportingAudits;
    }

    public boolean isSupportingBranches()
    {
      return supportingBranches;
    }

    private RepositoryTimeResult refreshTime()
    {
      return getSession().getSessionProtocol().getRepositoryTime();
    }
  }
}
