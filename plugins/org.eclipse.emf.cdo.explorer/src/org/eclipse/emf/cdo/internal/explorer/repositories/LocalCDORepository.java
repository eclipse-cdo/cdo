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

import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.spi.security.SecurityManagerFactory;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsUpdate;
import org.eclipse.net4j.util.security.PasswordCredentials;

import org.eclipse.core.runtime.CoreException;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public class LocalCDORepository extends CDORepositoryImpl
{
  public static final String PROP_SECURITY_DISABLED = "securityDisabled";

  public static final String PROP_SECURITY_CONFIG = "securityConfig";

  public static final String PROP_LM_ENABLED = "lmEnabled";

  public static final String PROP_LM_CONFIG = "lmConfig";

  public static final String PROP_TCP_DISABLED = "tcpDisabled";

  public static final String PROP_TCP_PORT = "tcpPort";

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(User.ADMINISTRATOR, User.ADMINISTRATOR_DEFAULT_PASSWORD);

  private boolean securityDisabled;

  private String securityConfig;

  private boolean lmEnabled;

  private String lmConfig;

  private boolean tcpDisabled;

  private int tcpPort;

  private IRepository repository;

  private IDeactivateable lifecycleManager;

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
    return Net4jUtil.LOCAL_ACCEPTOR_TYPE;
  }

  @Override
  public final String getConnectorDescription()
  {
    return Net4jUtil.LOCAL_ACCEPTOR_DESCRIPTION;
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

  public final boolean isLMEnabled()
  {
    return lmEnabled;
  }

  public final String getLMConfig()
  {
    return lmConfig;
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
    securityDisabled = Boolean.parseBoolean(properties.getProperty(PROP_SECURITY_DISABLED, StringUtil.TRUE));
    securityConfig = StringUtil.safe(properties.get(PROP_SECURITY_CONFIG));
    lmEnabled = Boolean.parseBoolean(properties.getProperty(PROP_LM_ENABLED, StringUtil.FALSE));
    lmConfig = StringUtil.safe(properties.getProperty(PROP_LM_CONFIG));
    tcpDisabled = Boolean.parseBoolean(properties.getProperty(PROP_TCP_DISABLED, StringUtil.TRUE));
    tcpPort = Integer.parseInt(properties.getProperty(PROP_TCP_PORT, "0"));
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_SECURITY_DISABLED, Boolean.toString(securityDisabled));
    properties.setProperty(PROP_SECURITY_CONFIG, StringUtil.safe(securityConfig));
    properties.setProperty(PROP_LM_ENABLED, Boolean.toString(lmEnabled));
    properties.setProperty(PROP_LM_CONFIG, StringUtil.safe(lmConfig));
    properties.setProperty(PROP_TCP_DISABLED, Boolean.toString(tcpDisabled));
    properties.setProperty(PROP_TCP_PORT, Integer.toString(tcpPort));
  }

  @Override
  public CDOSession openSession()
  {
    IManagedContainer container = getContainer();

    String repositoryName = getName();
    File folder = getFolder();
    File dbPrefix = new File(folder, "db");

    VersioningMode versioningMode = getVersioningMode();
    Map<String, String> mappingStrategyProperties = getMappingStrategyProperties();
    Map<String, String> repositoryProperties = getRepositoryProperties();

    repository = createRepository(container, repositoryName, dbPrefix, versioningMode, mappingStrategyProperties, repositoryProperties);

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

    if (Support.SERVER_LM.isAvailable() && lmEnabled)
    {
      IDGeneration idGeneration = getIDGeneration();
      lifecycleManager = LMHelper.start(container, folder, repository, lmConfig, idGeneration);
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

  private static InternalRepository createRepository(IManagedContainer container, String repositoryName, File dbPrefix, VersioningMode versioningMode,
      Map<String, String> mappingStrategyProperties, Map<String, String> repositoryProperties)
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + dbPrefix);

    boolean supportingAudits = versioningMode.isSupportingAudits();
    boolean supportingBranches = versioningMode.isSupportingBranches();
    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(supportingAudits, supportingBranches, false);
    mappingStrategy.setProperties(mappingStrategyProperties);

    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IDBStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    IRepository repository = CDOServerUtil.createRepository(repositoryName, store, repositoryProperties);
    CDOServerUtil.addRepository(container, repository);
    return (InternalRepository)repository;
  }

  @Override
  protected void closeSession()
  {
    super.closeSession();

    LifecycleUtil.deactivate(tcpAcceptor);
    tcpAcceptor = null;

    LifecycleUtil.deactivate(lifecycleManager);
    lifecycleManager = null;

    LifecycleUtil.deactivate(repository);
    repository = null;
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
    IDGeneration idGeneration = getIDGeneration();
    return getRepositoryProperties(versioningMode, idGeneration);
  }

  private static Map<String, String> getRepositoryProperties(VersioningMode versioningMode, IDGeneration idGeneration)
  {
    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(versioningMode.isSupportingAudits()));
    props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(versioningMode.isSupportingBranches()));
    props.put(IRepository.Props.ID_GENERATION_LOCATION, idGeneration.getLocation().toString());
    return props;
  }

  protected Map<String, String> getMappingStrategyProperties()
  {
    return getMappingStrategyPropertiesDefault();
  }

  private static Map<String, String> getMappingStrategyProperties(boolean eagerTableCreation, boolean qualifiedNames, boolean withRanges, boolean copyOnBranch)
  {
    Map<String, String> props = new HashMap<>();
    props.put(IMappingStrategy.Props.EAGER_TABLE_CREATION, Boolean.toString(eagerTableCreation));
    props.put(IMappingStrategy.Props.QUALIFIED_NAMES, Boolean.toString(qualifiedNames));
    props.put(CDODBUtil.PROP_WITH_RANGES, Boolean.toString(withRanges));
    props.put(CDODBUtil.PROP_COPY_ON_BRANCH, Boolean.toString(copyOnBranch));
    return props;
  }

  private static Map<String, String> getMappingStrategyPropertiesDefault()
  {
    boolean eagerTableCreation = OMPlatform.INSTANCE.isProperty("cdo.explorer.eagerTableCreation", false);
    boolean qualifiedNames = OMPlatform.INSTANCE.isProperty("cdo.explorer.qualifiedNames", true);
    boolean withRanges = OMPlatform.INSTANCE.isProperty("cdo.explorer.withRanges", true); // Used to be false!!!
    boolean copyOnBranch = OMPlatform.INSTANCE.isProperty("cdo.explorer.copyOnBranch", false);
    return getMappingStrategyProperties(eagerTableCreation, qualifiedNames, withRanges, copyOnBranch);
  }

  /**
   * @author Eike Stepper
   */
  private static final class LMHelper
  {
    private static final String MANY_VALUED_PREFIX = "(";

    private static final String MANY_VALUED_SUFFIX = ")";

    private static final int MANY_VALUED_SUFFIX_LENGTH = MANY_VALUED_SUFFIX.length();

    public static IDeactivateable start(IManagedContainer container, File folder, IRepository repository, String lmConfig, IDGeneration idGeneration)
    {
      String systemName = null;
      String moduleDefinitionPath = null;
      Set<String> moduleTypes = new LinkedHashSet<>();
      Set<String> dropTypes = new LinkedHashSet<>();
      Set<String> releaseTypes = new LinkedHashSet<>();

      StringTokenizer tokenizer = new StringTokenizer(lmConfig, ":");
      while (tokenizer.hasMoreTokens())
      {
        String token = tokenizer.nextToken();
        token = StringUtil.unescape(token, ':');

        if (systemName == null)
        {
          systemName = token;
          continue;
        }

        if (moduleDefinitionPath == null)
        {
          moduleDefinitionPath = token;
          continue;
        }

        if (manyValuedToken(token, "moduleTypes", moduleTypes))
        {
          continue;
        }

        if (manyValuedToken(token, "dropTypes", dropTypes))
        {
          continue;
        }

        if (manyValuedToken(token, "releaseTypes", releaseTypes))
        {
          continue;
        }
      }

      AbstractLifecycleManager lifecycleManager = new AbstractLifecycleManager()
      {
        private final File lmFolder;

        {
          lmFolder = new File(folder, "lm"); // LMManager.STATE_FOLDER_NAME
          lmFolder.mkdirs();
        }

        @Override
        protected InternalRepository createModuleRepository(String moduleName) throws CoreException
        {
          File dbPrefix = new File(lmFolder, moduleName);

          Map<String, String> mappingStrategyProperties = getMappingStrategyPropertiesDefault();
          Map<String, String> repositoryProperties = getRepositoryProperties(VersioningMode.Branching, idGeneration);

          return createRepository(container, moduleName, dbPrefix, VersioningMode.Branching, mappingStrategyProperties, repositoryProperties);
        }
      };

      lifecycleManager.setSystemRepository((InternalRepository)repository);
      lifecycleManager.setSystemName(systemName);
      lifecycleManager.setModuleDefinitionPath(moduleDefinitionPath);

      lifecycleManager.setProcessInitializer(process -> {
        moduleTypes.forEach(name -> process.getModuleTypes().add(LMFactory.eINSTANCE.createModuleType(name)));
        dropTypes.forEach(name -> process.getDropTypes().add(LMFactory.eINSTANCE.createDropType(name, false)));
        releaseTypes.forEach(name -> process.getDropTypes().add(LMFactory.eINSTANCE.createDropType(name, true)));
      });

      lifecycleManager.activate();

      return new IDeactivateable()
      {
        @Override
        public Exception deactivate()
        {
          return lifecycleManager.deactivate();
        }
      };
    }

    private static boolean manyValuedToken(String token, String tag, Set<String> set)
    {
      String prefix = tag + MANY_VALUED_PREFIX;
      if (token.startsWith(prefix) && token.endsWith(MANY_VALUED_SUFFIX))
      {
        token = token.substring(prefix.length(), token.length() - MANY_VALUED_SUFFIX_LENGTH);

        StringUtil.tokenize(token, ",", singleValue -> {
          singleValue = StringUtil.unescape(singleValue, ',');
          singleValue = singleValue.trim();

          if (!StringUtil.isEmpty(singleValue))
          {
            set.add(singleValue);
          }
        });

        return true;
      }

      return false;
    }
  }
}
