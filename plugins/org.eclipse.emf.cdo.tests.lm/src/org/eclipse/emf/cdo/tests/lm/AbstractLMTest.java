/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lm;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.Updates;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.lm.bundle.OM;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.core.runtime.CoreException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLMTest
{
  public static final String SYSTEM_NAME = "System2022";

  public static final String MODULE_DEFINITION_PATH = "module.md";

  public static final IPluginContainer CONTAINER = IPluginContainer.INSTANCE;

  @SuppressWarnings("restriction")
  private static final String BROWSER_PORT = org.eclipse.emf.cdo.internal.server.bundle.CDOServerApplication.PROP_BROWSER_PORT;

  private static final boolean STAND_ALONE = !OMPlatform.INSTANCE.isOSGiRunning();

  private static final File TEST_FOLDER = STAND_ALONE ? //
      TMPUtil.createTempFolder("lm-tests-") : //
      new File(OM.BUNDLE.getStateLocation());

  private static final String ACCEPTOR_NAME = "lm-test";

  private InternalRepository systemRepository;

  private AbstractLifecycleManager lifecycleManager;

  private IAcceptor acceptor;

  static
  {
    System.out.println("Test Folder: " + TEST_FOLDER);

    if (STAND_ALONE)
    {
      Net4jUtil.prepareContainer(CONTAINER);
      JVMUtil.prepareContainer(CONTAINER);
      CDONet4jServerUtil.prepareContainer(CONTAINER);
      CDODBUtil.prepareContainer(CONTAINER);
      CONTAINER.activate();

      ITypeMapping.Registry.INSTANCE.getClass();
    }

    String port = OMPlatform.INSTANCE.getProperty(BROWSER_PORT);
    if (port != null)
    {
      System.out.println("DB Browser:  http://localhost:" + port);
      CONTAINER.getElement("org.eclipse.emf.cdo.server.browsers", "default", port);
    }

    System.out.println();
  }

  private static InternalRepository createRepository(String name, File dbFolder)
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + dbFolder.toURI() + "/" + name);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true);
    IDBAdapter dbAdapter = new H2Adapter();
    IDBConnectionProvider dbConnectionProvider = dbAdapter.createConnectionProvider(dataSource);
    IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);

    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    props.put(IRepository.Props.SUPPORTING_BRANCHES, "true");

    InternalRepository repository = (InternalRepository)CDOServerUtil.createRepository(name, store, props);
    CDOServerUtil.addRepository(CONTAINER, repository);
    return repository;
  }

  @BeforeEach
  @SuppressWarnings("restriction")
  protected void setUp(TestInfo testInfo) throws Exception
  {
    String testName = testInfo.getTestClass().get().getSimpleName() + "." + testInfo.getTestMethod().get().getName();
    System.out.println("+++++++++++++++++++++++ " + testName + "() +++++++++++++++++++++++");

    File testFolder = new File(TEST_FOLDER, testName);
    System.out.println("Test folder: " + testFolder);

    File dbFolder = new File(testFolder, "db");
    dbFolder.mkdirs();

    systemRepository = createRepository(SYSTEM_NAME, dbFolder);

    lifecycleManager = new AbstractLifecycleManager()
    {
      @Override
      protected InternalRepository createModuleRepository(String moduleName) throws CoreException
      {
        return createRepository(moduleName, dbFolder);
      }
    };

    lifecycleManager.setProcessInitializer(process -> {
      process.getDropTypes().add(LMFactory.eINSTANCE.createDropType("Tag", false));
      process.getDropTypes().add(LMFactory.eINSTANCE.createDropType("Milestone", false));
      process.getDropTypes().add(LMFactory.eINSTANCE.createDropType("Release", true));
    });

    lifecycleManager.setSystemRepository(systemRepository);
    lifecycleManager.setSystemName(SYSTEM_NAME);
    lifecycleManager.setModuleDefinitionPath(MODULE_DEFINITION_PATH);
    lifecycleManager.activate();

    acceptor = JVMUtil.getAcceptor(CONTAINER, ACCEPTOR_NAME);

    if (STAND_ALONE)
    {
      org.eclipse.emf.cdo.internal.explorer.bundle.OM.initializeManagers(new File(testFolder, "explorer"));
      org.eclipse.emf.cdo.lm.internal.client.bundle.OM.initializeManagers();
    }
  }

  @AfterEach
  @SuppressWarnings("restriction")
  protected void tearDown() throws Exception
  {
    if (STAND_ALONE)
    {
      org.eclipse.emf.cdo.lm.internal.client.bundle.OM.disposeManagers();
      org.eclipse.emf.cdo.internal.explorer.bundle.OM.disposeManagers();
    }
    else
    {
      for (CDOCheckout checkout : CDOExplorerUtil.getCheckoutManager().getCheckouts())
      {
        checkout.delete(true);
      }

      for (CDORepository repository : CDOExplorerUtil.getRepositoryManager().getRepositories())
      {
        repository.delete(true);
      }
    }

    LifecycleUtil.deactivate(acceptor);
    LifecycleUtil.deactivate(lifecycleManager);
    LifecycleUtil.deactivate(systemRepository);

    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
  }

  protected static ISystemDescriptor createSystemRepository()
  {
    Properties props = new Properties();
    props.put("type", "remote");
    props.put("name", SYSTEM_NAME);
    props.put("label", SYSTEM_NAME);
    props.put("connectorType", "jvm");
    props.put("connectorDescription", ACCEPTOR_NAME);

    CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
    CDORepository repository = repositoryManager.addRepository(props);
    repository.connect();

    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(SYSTEM_NAME, 5000);
    systemDescriptor.open();
    return systemDescriptor;
  }

  protected static Updates waitForUpdates(IAssemblyDescriptor assemblyDescriptor) throws InterruptedException
  {
    long end = System.currentTimeMillis() + 10000L;
    while (!assemblyDescriptor.hasUpdatesAvailable())
    {
      if (System.currentTimeMillis() > end)
      {
        throw new InterruptedException();
      }

      Thread.sleep(10);
    }

    return assemblyDescriptor.getAvailableUpdates();
  }
}
