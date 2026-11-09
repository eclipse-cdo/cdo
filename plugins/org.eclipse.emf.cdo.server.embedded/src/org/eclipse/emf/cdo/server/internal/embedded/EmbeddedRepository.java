/*
 * Copyright (c) 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class EmbeddedRepository extends Repository.Default
{
  private CDOEmbeddedRepositoryConfig config;

  private IAcceptor acceptor;

  private IConnector connector;

  public EmbeddedRepository(CDOEmbeddedRepositoryConfig config)
  {
    this.config = config;
  }

  public CDONet4jSession openClientSession()
  {
    CDOBranchManager branchManager = new ClientBranchManager(getBranchManager());
    CDORevisionManager revisionManager = new ClientRevisionManager(getRevisionManager());

    CDONet4jSessionConfiguration configuration = config.createSessionConfiguration(connector, branchManager, revisionManager);

    CDONet4jSession session = configuration.openNet4jSession();
    config.modifySession(this, session);
    return session;
  }

  @Override
  public void initSystemPackages(boolean firstStart)
  {
    if (firstStart)
    {
      // Initialize packages.
      List<EPackage> packages = new ArrayList<>();
      config.initPackages(this, packages);
      if (!packages.isEmpty())
      {
        setInitialPackages(packages.toArray(new EPackage[packages.size()]));
      }
    }

    super.initSystemPackages(firstStart);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    if (getProtector() != null)
    {
      throw new IllegalStateException("Repository protectors are unsupported in Embedded CDO; configure an IAuthenticator directly without authorization."); //$NON-NLS-1$
    }

    if (getSessionManager() != null && !(getSessionManager() instanceof EmbeddedSessionManager))
    {
      throw new IllegalStateException("Custom session managers are unsupported in Embedded CDO because they can bypass the embedded security contract."); //$NON-NLS-1$
    }

    if (isAuthorizingOperations())
    {
      throw new IllegalStateException("Operation authorizers are unsupported in Embedded CDO; all authenticated sessions have unrestricted repository access."); //$NON-NLS-1$
    }

    super.doBeforeActivate();

    if (!(getSessionManager() instanceof EmbeddedSessionManager))
    {
      throw new IllegalStateException("Embedded CDO requires its embedded session manager to enforce the supported security contract."); //$NON-NLS-1$
    }

    InternalCDOBranchManager branchManager = getBranchManager();
    branchManager.setBranchLoader(new EmbeddedBranchLoader(this));

    InternalCDORevisionManager revisionManager = getRevisionManager();
    revisionManager.setRevisionLoader(new EmbeddedRevisionLoader(this));
  }

  @Override
  protected InternalSessionManager createSessionManager()
  {
    return new EmbeddedSessionManager();
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    IManagedContainer container = getContainer();
    container.putElement(RepositoryFactory.PRODUCT_GROUP, RepositoryFactory.TYPE, getName(), this);

    acceptor = config.createAcceptor(container);
    connector = config.createConnector(container);

    InternalStore store = getStore();
    if (store.isFirstStart())
    {
      config.afterFirstStart(this);
    }
    else
    {
      config.afterReStart(this);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(connector);
    connector = null;

    LifecycleUtil.deactivate(acceptor);
    acceptor = null;

    super.doDeactivate();
  }
}
