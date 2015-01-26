/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.explorer.CDOCheckout;
import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.explorer.CDORepositoryManager;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.lifecycle.ShareableLifecycle;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class CDORepositoryImpl extends ShareableLifecycle implements CDORepository
{
  private final Set<CDOCheckout> checkouts = new HashSet<CDOCheckout>();

  private final LifecycleEventAdapter sessionListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      disconnect();
    }
  };

  private final CDORepositoryManager repositoryManager;

  private String label;

  private String repositoryName;

  private CDOSession session;

  public CDORepositoryImpl(CDORepositoryManager repositoryManager, String label, String repositoryName)
  {
    super(true);
    this.repositoryManager = repositoryManager;
    this.repositoryName = repositoryName;
  }

  public final CDORepositoryManager getRepositoryManager()
  {
    return repositoryManager;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public final String getRepositoryName()
  {
    return repositoryName;
  }

  public synchronized CDOSession getSession()
  {
    if (session == null)
    {
      session = openSession();
      session.addListener(sessionListener);
    }

    return session;
  }

  public boolean isConnected()
  {
    return isActive();
  }

  public void disconnect()
  {
    if (session != null)
    {
      session.removeListener(sessionListener);

      while (isActive())
      {
        deactivate();
      }
    }
  }

  public CDOCheckout[] getCheckouts()
  {
    synchronized (checkouts)
    {
      return checkouts.toArray(new CDOCheckout[checkouts.size()]);
    }
  }

  public void addCheckout(CDOCheckout checkout)
  {
    synchronized (checkouts)
    {
      checkouts.add(checkout);
    }
  }

  public boolean isEmpty()
  {
    if (session != null)
    {
      return session.getBranchManager().getMainBranch().isEmpty();
    }

    return false;
  }

  public CDOBranch[] getElements()
  {
    return getSession().getBranchManager().getMainBranch().getBranches();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORepository)
    {
      CDORepository that = (CDORepository)obj;
      return ObjectUtil.equals(getConnectorType(), that.getConnectorType())
          && ObjectUtil.equals(getConnectorDescription(), that.getConnectorDescription())
          && ObjectUtil.equals(repositoryName, that.getRepositoryName());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(getConnectorType()) ^ ObjectUtil.hashCode(getConnectorDescription())
        ^ ObjectUtil.hashCode(repositoryName);
  }

  @Override
  public String toString()
  {
    int refCount = LifecycleUtil.getRefCount(this);
    return getConnectorType() + "://" + getConnectorDescription() + "/" + repositoryName + " [" + refCount + "]";
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    session.close();
    session = null;

    super.doDeactivate();
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected IConnector getConnector()
  {
    IManagedContainer container = getContainer();
    return Net4jUtil.getConnector(container, getConnectorType(), getConnectorDescription());
  }

  protected CDOSessionConfiguration createSessionConfiguration()
  {
    IConnector connector = getConnector();

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(repositoryName);
    return config;
  }

  protected CDOSession openSession()
  {
    CDOSessionConfiguration sessionConfiguration = createSessionConfiguration();
    sessionConfiguration.setPassiveUpdateEnabled(true);
    sessionConfiguration.setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    CDOSession session = sessionConfiguration.openSession();
    session.options().setGeneratedPackageEmulationEnabled(true);
    return session;
  }
}
