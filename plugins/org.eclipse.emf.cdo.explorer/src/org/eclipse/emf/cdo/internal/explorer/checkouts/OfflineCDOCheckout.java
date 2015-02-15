/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceConfiguration;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class OfflineCDOCheckout extends CDOCheckoutImpl
{
  private CDOWorkspace workspace;

  public OfflineCDOCheckout()
  {
  }

  @Override
  protected CDOView openView(CDOSession session)
  {
    final IManagedContainer container = getContainer();
    final CDORepository repository = getRepository();

    final String connectorType = repository.getConnectorType();
    final String connectorDescription = repository.getConnectorDescription();
    final String repositoryName = repository.getName();

    CDOSessionConfigurationFactory remote = new CDOSessionConfigurationFactory()
    {
      public CDOSessionConfiguration createSessionConfiguration()
      {
        IConnector connector = Net4jUtil.getConnector(container, connectorType, connectorDescription);

        CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
        config.setConnector(connector);
        config.setRepositoryName(repositoryName);
        config.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));

        return config;
      }
    };

    File folder = getFolder();
    File storeFolder = new File(folder, "store");
    File dbPrefix = new File(storeFolder, "db");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + dbPrefix);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true, false);
    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    File baseFolder = new File(folder, "base");
    CDOWorkspaceBase base = CDOWorkspaceUtil.createFolderWorkspaceBase(baseFolder);

    String localRepositoryName = repositoryName + "-workspace" + getID();
    int branchID = getBranchID();
    long timeStamp = getTimeStamp();

    CDOWorkspaceConfiguration configuration = CDOWorkspaceUtil.createWorkspaceConfiguration();
    configuration.setLocalRepositoryName(localRepositoryName);
    configuration.setRemote(remote);
    configuration.setStore(store);
    configuration.setBase(base);

    if (storeFolder.isDirectory())
    {
      workspace = configuration.open();
    }
    else
    {
      configuration.setBranchID(branchID);
      configuration.setTimeStamp(timeStamp);

      workspace = configuration.checkout();
    }

    if (isReadOnly())
    {
      return workspace.openView();
    }

    return workspace.openTransaction();
  }

  @Override
  protected void closeView()
  {
    super.closeView();

    if (workspace != null)
    {
      workspace.close();
      workspace = null;
    }
  }

  @Override
  protected CDOTransaction doOpenTransaction()
  {
    if (workspace == null)
    {
      return null;
    }

    return workspace.openTransaction();
  }
}
