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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamMode;
import org.eclipse.emf.cdo.lm.StreamSpec;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.Updates;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager;
import org.eclipse.emf.cdo.lm.util.LMMerger;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.lm.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLMTest extends junit.framework.TestCase
{
  public static final String SYSTEM_NAME = "System2022";

  public static final String MODULE_DEFINITION_PATH = "module.md";

  public static final IPluginContainer CONTAINER = IPluginContainer.INSTANCE;

  @SuppressWarnings("restriction")
  private static final String BROWSER_PORT = org.eclipse.emf.cdo.internal.server.bundle.CDOServerApplication.PROP_BROWSER_PORT;

  private static final File TEST_FOLDER = new File(OM.BUNDLE.getStateLocation());

  private static final String ACCEPTOR_NAME = "lm-test";

  private InternalRepository systemRepository;

  private AbstractLifecycleManager lifecycleManager;

  private IAcceptor acceptor;

  static
  {
    msg("Test Folder: " + TEST_FOLDER);

    if (!OMPlatform.INSTANCE.isOSGiRunning())
    {
      // Equinox P2 requires OSGi running.
      fail("The test must be run iside OSGi, i.e., as 'JUnit Plug-in Test', to make p2 happy!");
    }

    String port = OMPlatform.INSTANCE.getProperty(BROWSER_PORT);
    if (port != null)
    {
      msg("DB Browser:  http://localhost:" + port);
      CONTAINER.getElement("org.eclipse.emf.cdo.server.browsers", "default", port);
    }

    msg();
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

  @Override
  @SuppressWarnings("restriction")
  protected void setUp() throws Exception
  {
    String testName = getClass().getSimpleName() + "." + getName();
    msg("+++++++++++++++++++++++ " + testName + "() +++++++++++++++++++++++");

    File testFolder = new File(TEST_FOLDER, testName);
    msg("Test folder: " + testFolder);

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
  }

  @Override
  @SuppressWarnings("restriction")
  protected void tearDown() throws Exception
  {
    for (CDOCheckout checkout : CDOExplorerUtil.getCheckoutManager().getCheckouts())
    {
      checkout.delete(true);
    }

    for (CDORepository repository : CDOExplorerUtil.getRepositoryManager().getRepositories())
    {
      repository.delete(true);
    }

    LifecycleUtil.deactivate(acceptor);
    LifecycleUtil.deactivate(lifecycleManager);
    LifecycleUtil.deactivate(systemRepository);

    msg("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
  }

  protected Stream createStream(ISystemDescriptor systemDescriptor, Module module, Drop base, int major, int minor, String streamCodeName) throws Exception
  {
    Stream stream = systemDescriptor.createStream(module, base, new StreamSpec(major, minor, streamCodeName), monitor());

    assertThat(stream.getModule(), is(module));
    assertThat(stream.getSystem(), is(systemDescriptor.getSystem()));
    assertThat(stream.getMajorVersion(), is(major));
    assertThat(stream.getMinorVersion(), is(minor));
    assertThat(stream.getCodeName(), is(streamCodeName));
    assertThat(stream.getMode(), is(StreamMode.DEVELOPMENT));
    assertThat(stream.getBranchPoint(), is(CDOBranchPointRef.MAIN_HEAD));
    assertThat(stream, is(module.getStream(streamCodeName)));

    return stream;
  }

  protected ModuleCreationResult createModule(ISystemDescriptor systemDescriptor, System system, String moduleName, String streamCodeName, int major, int minor)
      throws Exception
  {
    Module module = systemDescriptor.createModule(moduleName, new StreamSpec(major, minor, streamCodeName), monitor());

    Stream stream = module.getStream(0, 1);
    String label = moduleName + "- Stream " + major + "." + minor;

    IAssemblyDescriptor assemblyDescriptor = IAssemblyManager.INSTANCE.createDescriptor(label, stream, monitor());
    {
      assertThat(stream.getModule(), is(module));
      assertThat(stream.getSystem(), is(system));
      assertThat(stream.getMajorVersion(), is(major));
      assertThat(stream.getMinorVersion(), is(minor));
      assertThat(stream.getCodeName(), is(streamCodeName));
      assertThat(stream.getMode(), is(StreamMode.DEVELOPMENT));
      assertThat(stream.getBase(), nullValue());
      assertThat(stream.getBranchPoint(), is(CDOBranchPointRef.MAIN_HEAD));
      assertThat(stream, is(module.getStream(streamCodeName)));

      assertThat(assemblyDescriptor.getSystemDescriptor(), is(systemDescriptor));
      assertThat(assemblyDescriptor.getName(), is(label));
      assertThat(assemblyDescriptor.getBaseline(), is(stream));

      CDOCheckout checkout = assemblyDescriptor.getCheckout();
      assertThat(checkout.isOnline(), is(true));
      assertThat(checkout.isOpen(), is(true));
      assertThat(checkout.getLabel(), is(label));
      assertThat(checkout.getBranchPath(), is(CDOBranch.MAIN_BRANCH_NAME));
      assertThat(checkout.getTimeStamp(), is(CDOBranchPoint.UNSPECIFIED_DATE));
      assertThat(checkout.getRepository(), is(systemDescriptor.getModuleRepository(moduleName)));
      assertThat(checkout.getView().getViewSet().getViews().length, is(1));

      Assembly assembly = assemblyDescriptor.getAssembly();
      assertThat(assembly.getSystemName(), is(SYSTEM_NAME));
      assertThat(assembly.getModules().size(), is(1));

      AssemblyModule assemblyModule = assembly.getModules().get(0);
      assertThat(assemblyModule.getName(), is(moduleName));
      assertThat(assemblyModule.getBranchPoint(), is(CDOBranchPointRef.MAIN_HEAD));

      systemDescriptor.withModuleSession(moduleName, session -> {
        CDOView view = session.openView();
        CDOResource resource = view.getResource(MODULE_DEFINITION_PATH);
        ModuleDefinition moduleDefinition = (ModuleDefinition)resource.getContents().get(0);
        assertThat(moduleDefinition.getName(), is(moduleName));
        assertThat(moduleDefinition.getVersion(), is(Version.createOSGi(major, minor, 0)));
        assertThat(moduleDefinition.getDependencies().size(), is(0));
      });
    }

    return new ModuleCreationResult(module, stream, assemblyDescriptor);

  }

  protected void setModuleVersion(Stream stream1_0, String moduleName, int major, int minor, int micro) throws Exception
  {
    IAssemblyDescriptor stream1_0Descriptor = IAssemblyManager.INSTANCE.createDescriptor("Checkout - Stream 1.0", stream1_0, monitor());

    editStream(stream1_0Descriptor, transaction -> {
      CDOResource moduleDefinitionResource = transaction.getResource(MODULE_DEFINITION_PATH);

      EObject rootObject = moduleDefinitionResource.getContents().get(0);
      assertThat(rootObject, notNullValue());
      assertThat(rootObject, instanceOf(ModuleDefinition.class));

      if (rootObject instanceof ModuleDefinition)
      {
        ModuleDefinition moduleDefinition = (ModuleDefinition)rootObject;
        assertThat(moduleDefinition.getName(), is(moduleName));
        moduleDefinition.setVersion(Version.createOSGi(major, minor, micro));

      }

    });
  }

  protected Change createChange(ISystemDescriptor systemDescriptor, Stream stream, String changeLabel, String changeDescriptorLabel,
      Consumer<CDOTransaction> editFunction) throws Exception
  {
    Change change = systemDescriptor.createChange(stream, null, changeLabel, monitor());

    IAssemblyDescriptor change1Descriptor = IAssemblyManager.INSTANCE.createDescriptor(changeDescriptorLabel, change, monitor());
    CDOCheckout checkout = change1Descriptor.getCheckout();
    assertThat(checkout.getView().getViewSet().getViews().length, is(1));

    CDOTransaction transaction = checkout.openTransaction();
    editFunction.accept(transaction);
    transaction.commit();
    transaction.close();

    checkout.delete(true);
    return change;
  }

  protected void editStream(IAssemblyDescriptor streamDescriptor, Consumer<CDOTransaction> editFunction) throws Exception
  {
    CDOTransaction transaction = streamDescriptor.getCheckout().openTransaction();
    editFunction.accept(transaction);
    transaction.commit();
    transaction.close();
  }

  protected Delivery deliverChange(ISystemDescriptor systemDescriptor, Stream stream, Change change) throws Exception
  {
    return systemDescriptor.createDelivery(stream, change, LMMerger.CORE, monitor());
  }

  protected void removeDependencies(IAssemblyDescriptor clientStreamDescriptor) throws Exception
  {
    CDOCheckout checkout = clientStreamDescriptor.getCheckout();

    CDOTransaction transaction = checkout.openTransaction();
    CDOResource resource = transaction.getResource(MODULE_DEFINITION_PATH);
    ModuleDefinition moduleDefinition = (ModuleDefinition)resource.getContents().get(0);
    moduleDefinition.getDependencies().clear();
    transaction.commit();

    Updates updates = waitForUpdates(clientStreamDescriptor);
    assertThat(updates.getAdditions().size(), is(0));
    assertThat(updates.getModifications().size(), is(1)); // Dependency removed from Module2.
    assertThat(updates.getRemovals().size(), is(1)); // Module1 removed.

    CDOView[] views = transaction.getViewSet().getViews();
    assertThat(views.length, is(2));

    clientStreamDescriptor.update();
    views = transaction.getViewSet().getViews();
    assertThat(views.length, is(1));
    assertThat(views[0], is(transaction));
    transaction.close();
  }

  protected long publishDrop(ISystemDescriptor systemDescriptor, System system, Stream stream, String name, String type) throws Exception
  {
    long timeStamp = java.lang.System.currentTimeMillis();
    DropType dropType = system.getProcess().getDropType(type);

    Drop drop = systemDescriptor.createDrop(stream, dropType, timeStamp, name, monitor());
    assertThat(drop.getBranchPoint().getTimeStamp(), is(timeStamp));

    return timeStamp;
  }

  protected long publishRelease(ISystemDescriptor systemDescriptor, System system, Stream stream, String releaseName) throws Exception
  {
    return publishDrop(systemDescriptor, system, stream, releaseName, "Release");
  }

  protected long publishTag(ISystemDescriptor systemDescriptor, System system, Stream stream, String tagName) throws Exception
  {
    return publishDrop(systemDescriptor, system, stream, tagName, "Tag");
  }

  protected void publishTagUpdateClientAndCheckUri(ISystemDescriptor systemDescriptor, System system, Stream supplierStream,
      IAssemblyDescriptor clientStreamDescriptor, URI uri) throws Exception
  {
    // Check that no updates are available in clientStreamDescriptor
    assertThat(clientStreamDescriptor.hasUpdatesAvailable(), is(false));

    // Verify that the uri resource is not visible, yet.
    CDOCheckout checkout = clientStreamDescriptor.getCheckout();
    CDOView view = checkout.openView();
    ResourceSet resourceSet = view.getResourceSet();

    try
    {
      resourceSet.getResource(uri, true);
      fail("InvalidURIException expected");
    }
    catch (InvalidURIException expected)
    {
      // Success.
    }

    long timeStamp = publishTag(systemDescriptor, system, supplierStream, "TagName");

    Updates updates = waitForUpdates(clientStreamDescriptor);
    assertThat(updates.getAdditions().size(), is(0));
    assertThat(updates.getModifications().size(), is(1));
    assertThat(updates.getRemovals().size(), is(0));

    clientStreamDescriptor.update();

    // Verify that the uri resource is now visible.
    CDOResource resource = (CDOResource)resourceSet.getResource(uri, true);
    assertThat(resource, notNullValue());
    assertThat(resource.cdoView().getTimeStamp(), is(timeStamp));
    view.close();
  }

  protected void updateAssemblyDescriptor(IAssemblyDescriptor assemblyDescriptor) throws Exception
  {
    waitForUpdates(assemblyDescriptor);
    assemblyDescriptor.update();
  }

  protected void updateDependency(IAssemblyDescriptor clientStreamDescriptor, String supplierName, VersionRange newVersionRange) throws Exception
  {
    CDOCheckout checkout = clientStreamDescriptor.getCheckout();

    CDOTransaction transaction = checkout.openTransaction();
    CDOResource resource = transaction.getResource(MODULE_DEFINITION_PATH);
    ModuleDefinition moduleDefinition = (ModuleDefinition)resource.getContents().get(0);

    List<DependencyDefinition> dependencies = moduleDefinition.getDependencies().stream()
        .filter(dependencyDefinition -> dependencyDefinition.getTargetName().equals(supplierName)).collect(Collectors.toList());
    assertThat(dependencies.size(), is(1));

    dependencies.get(0).setVersionRange(newVersionRange);

    transaction.commit();
  }

  protected void createDependency(IAssemblyDescriptor clientStreamDescriptor, String supplierName, VersionRange versionRange) throws Exception
  {
    CDOCheckout checkout = clientStreamDescriptor.getCheckout();

    CDOTransaction transaction = checkout.openTransaction();
    CDOResource resource = transaction.getResource(MODULE_DEFINITION_PATH);
    ModuleDefinition moduleDefinition = (ModuleDefinition)resource.getContents().get(0);
    moduleDefinition.getDependencies().add(ModulesFactory.eINSTANCE.createDependencyDefinition(supplierName, versionRange));
    transaction.commit();

    transaction.close();
  }

  protected void createDependencyAndUpdate(IAssemblyDescriptor clientStreamDescriptor, String supplierName, VersionRange versionRange) throws Exception
  {
    CDOCheckout checkout = clientStreamDescriptor.getCheckout();

    createDependency(clientStreamDescriptor, supplierName, versionRange);

    CDOTransaction transaction = checkout.openTransaction();
    Updates updates = waitForUpdates(clientStreamDescriptor);
    assertThat(updates.getAdditions().get(supplierName), notNullValue()); // supplier added.
    assertThat(updates.getModifications().size(), is(1)); // Dependency added to source.
    assertThat(updates.getRemovals().size(), is(0));
    assertThat(checkout.getView().getViewSet().getViews().length, is(1));

    CDOView[] views = transaction.getViewSet().getViews();
    assertThat(views.length, is(1));
    assertThat(views[0], is(transaction));

    clientStreamDescriptor.update();
    views = transaction.getViewSet().getViews();
    assertThat(views.length, is(2));
    assertThat(views[0], is(transaction));
    assertThat(views[1].isReadOnly(), is(true));
    assertThat(checkout.getView().getViewSet().getViews().length, is(2));

    transaction.close();
  }

  protected void createDependencyAndUpdate(IAssemblyDescriptor clientStreamDescriptor, String supplierName) throws Exception
  {
    createDependencyAndUpdate(clientStreamDescriptor, supplierName, null);
  }

  protected static URI createModuleResourceURI(String moduleName, String resourcePath)
  {
    return URI.createURI("cdo.lm://" + SYSTEM_NAME + ":" + moduleName + "/" + resourcePath);
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
    long end = millis() + 10000L;
    while (!assemblyDescriptor.hasUpdatesAvailable())
    {
      if (millis() > end)
      {
        throw new InterruptedException();
      }

      Thread.sleep(10);
    }

    return assemblyDescriptor.getAvailableUpdates();
  }

  public static IProgressMonitor monitor()
  {
    return new NullProgressMonitor();
  }

  public static void msg(String... messages)
  {
    for (String message : messages)
    {
      java.lang.System.out.println(message);
    }
  }

  public static long millis()
  {
    return java.lang.System.currentTimeMillis();
  }

  /**
   * @author Eike Stepper
   */
  public static final class ModuleCreationResult
  {
    public final Module module;

    public final Stream stream;

    public final IAssemblyDescriptor assemblyDescriptor;

    public ModuleCreationResult(Module module, Stream stream, IAssemblyDescriptor assemblyDescriptor)
    {
      this.module = module;
      this.stream = stream;
      this.assemblyDescriptor = assemblyDescriptor;
    }
  }
}
