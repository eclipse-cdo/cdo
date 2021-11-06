/*
 * Copyright (c) 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.repositories;

import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.spi.security.SecurityManagerFactory;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdate;
import org.eclipse.net4j.util.security.PasswordCredentials;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class LocalCDORepository extends CDORepositoryImpl
{
  public static final String PROP_SECURITY_DISABLED = "securityDisabled";

  public static final String PROP_SECURITY_CONFIG = "securityConfig";

  public static final String PROP_TCP_DISABLED = "tcpDisabled";

  public static final String PROP_TCP_PORT = "tcpPort";

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(User.ADMINISTRATOR, User.ADMINISTRATOR_DEFAULT_PASSWORD);

  private boolean securityDisabled;

  private String securityConfig;

  private boolean tcpDisabled;

  private int tcpPort;

  private IRepository repository;

  private IAcceptor tcpAcceptor;

  public LocalCDORepository()
  {
  }

  @Override
  public boolean isRemote()
  {
    return false;
  }

  @Override
  public boolean isClone()
  {
    return false;
  }

  @Override
  public boolean isLocal()
  {
    return true;
  }

  @Override
  public final String getConnectorType()
  {
    return "jvm";
  }

  @Override
  public final String getConnectorDescription()
  {
    return "local";
  }

  @Override
  public String getURI()
  {
    if (tcpDisabled)
    {
      return getConnectorType() + "://" + getConnectorDescription() + "/" + getName();
    }

    return "tcp://localhost:" + tcpPort + "/" + getName();
  }

  public final boolean isSecurityDisabled()
  {
    return securityDisabled;
  }

  public final String getSecurityConfig()
  {
    return securityConfig;
  }

  public final boolean isTCPDisabled()
  {
    return tcpDisabled;
  }

  public final int getTCPPort()
  {
    return tcpPort;
  }

  @Override
  protected void init(File folder, String type, Properties properties)
  {
    super.init(folder, type, properties);
    securityDisabled = Boolean.parseBoolean(properties.getProperty(PROP_SECURITY_DISABLED, Boolean.TRUE.toString()));
    securityConfig = StringUtil.safe(properties.get(PROP_SECURITY_CONFIG));
    tcpDisabled = Boolean.parseBoolean(properties.getProperty(PROP_TCP_DISABLED, Boolean.TRUE.toString()));
    tcpPort = Integer.parseInt(properties.getProperty(PROP_TCP_PORT, "0"));
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_SECURITY_DISABLED, Boolean.toString(securityDisabled));
    properties.setProperty(PROP_SECURITY_CONFIG, StringUtil.safe(securityConfig));
    properties.setProperty(PROP_TCP_DISABLED, Boolean.toString(tcpDisabled));
    properties.setProperty(PROP_TCP_PORT, Integer.toString(tcpPort));
  }

  @Override
  public CDOSession openSession()
  {
    String repositoryName = getName();
    File folder = new File(getFolder(), "db");
    VersioningMode versioningMode = getVersioningMode();

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + folder);

    boolean supportingAudits = versioningMode.isSupportingAudits();
    boolean supportingBranches = versioningMode.isSupportingBranches();
    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(supportingAudits, supportingBranches, false);
    mappingStrategy.setProperties(getMappingStrategyProperties());

    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IDBStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    repository = CDOServerUtil.createRepository(repositoryName, store, getRepositoryProperties());

    IManagedContainer container = getContainer();
    CDOServerUtil.addRepository(container, repository);

    if (!securityDisabled)
    {
      try
      {
        String qualifiedDescription = String.format("%s:%s", repositoryName, securityConfig); //$NON-NLS-1$
        if (container.getElement(SecurityManagerFactory.PRODUCT_GROUP, "default", qualifiedDescription) == null)
        {
          throw new IllegalStateException("Security manager for repository " + repositoryName + " could not be created: " + securityConfig);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    String connectorType = getConnectorType();
    String connectorDescription = getConnectorDescription();
    Net4jUtil.getAcceptor(container, connectorType, connectorDescription);

    if (!tcpDisabled)
    {
      tcpAcceptor = Net4jUtil.getAcceptor(container, "tcp", "0.0.0.0:" + tcpPort);
    }

    return super.openSession();
  }

  @Override
  public boolean isAuthenticating()
  {
    return !isSecurityDisabled();
  }

  @Override
  public IPasswordCredentials getCredentials(String realm)
  {
    if (!isSecurityDisabled())
    {
      return CREDENTIALS;
    }

    return super.getCredentials(realm);
  }

  @Override
  public void setCredentials(IPasswordCredentials credentials)
  {
    if (!isSecurityDisabled())
    {
      return;
    }

    super.setCredentials(credentials);
  }

  @Override
  public IPasswordCredentialsUpdate getCredentialsUpdate(String realm, String userID, CredentialsUpdateOperation operation)
  {
    if (!isSecurityDisabled())
    {
      return null;
    }

    return super.getCredentialsUpdate(realm, userID, operation);
  }

  protected Map<String, String> getRepositoryProperties()
  {
    VersioningMode versioningMode = getVersioningMode();

    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(versioningMode.isSupportingAudits()));
    props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(versioningMode.isSupportingBranches()));
    props.put(IRepository.Props.ID_GENERATION_LOCATION, getIDGeneration().getLocation().toString());
    return props;
  }

  protected Map<String, String> getMappingStrategyProperties()
  {
    Map<String, String> props = new HashMap<>();
    props.put(IMappingStrategy.Props.QUALIFIED_NAMES, "true");
    props.put(CDODBUtil.PROP_COPY_ON_BRANCH, "true");
    return props;
  }

  @Override
  protected void closeSession()
  {
    super.closeSession();

    LifecycleUtil.deactivate(tcpAcceptor);
    tcpAcceptor = null;

    LifecycleUtil.deactivate(repository);
    repository = null;
  }
}
