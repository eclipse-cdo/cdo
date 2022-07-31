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
package org.eclipse.emf.cdo.lm.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Process;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.lm.server.bundle.OM;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.equinox.p2.metadata.Version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLifecycleManager extends Lifecycle implements LMPackage.Literals
{
  private static final String ACCEPTOR_NAME = "org.eclipse.emf.cdo.lm.server";

  private static final boolean SECURITY_AVAILABLE;

  private IManagedContainer container;

  private IJVMAcceptor acceptor;

  private IJVMConnector connector;

  private String systemName;

  private InternalRepository systemRepository;

  private CDOSession systemSession;

  private String moduleDefinitionPath;

  private final Map<String, InternalRepository> moduleRepositories = new HashMap<>();

  private final Map<String, CDOSession> moduleSessions = new HashMap<>();

  private final WriteAccessHandler writeAccessHandler = new WriteAccessHandler()
  {
    @Override
    public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
    {
      handleCommit(commitContext);
    }

    @Override
    public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
    {
      // Do nothing.
    }
  };

  private Consumer<Process> processInitializer;

  private SecuritySupport securitySupport = SecuritySupport.UNAVAILABLE;

  public AbstractLifecycleManager()
  {
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  public final IJVMConnector getConnector()
  {
    return connector;
  }

  public InternalRepository getSystemRepository()
  {
    return systemRepository;
  }

  public void setSystemRepository(InternalRepository repository)
  {
    checkInactive();
    container = repository.getContainer();
    systemRepository = repository;

    if (SECURITY_AVAILABLE)
    {
      securitySupport = new SecuritySupport.Available(systemRepository);
    }
  }

  public String getSystemName()
  {
    return systemName;
  }

  public void setSystemName(String systemName)
  {
    checkInactive();
    this.systemName = systemName;
  }

  public Consumer<Process> getProcessInitializer()
  {
    return processInitializer;
  }

  public void setProcessInitializer(Consumer<Process> processInitializer)
  {
    this.processInitializer = processInitializer;
  }

  public String getModuleDefinitionPath()
  {
    return moduleDefinitionPath;
  }

  public void setModuleDefinitionPath(String moduleDefinitionPath)
  {
    checkInactive();
    this.moduleDefinitionPath = moduleDefinitionPath;
  }

  public final Map<String, InternalRepository> getModuleRepositories()
  {
    return moduleRepositories;
  }

  public final CDOSession getModuleSession(String moduleName)
  {
    synchronized (moduleSessions)
    {
      CDOSession session = moduleSessions.get(moduleName);
      if (session == null)
      {
        InternalRepository repository = moduleRepositories.get(moduleName);
        if (repository != null)
        {
          session = openModuleSession(repository);
          session.addListener(new LifecycleEventAdapter()
          {
            @Override
            protected void onDeactivated(ILifecycle lifecycle)
            {
              if (isActive())
              {
                moduleSessions.remove(moduleName);
              }
            }
          });

          moduleSessions.put(moduleName, session);
        }
      }

      return session;
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(systemRepository, "systemRepository");
    checkState(systemName, "systemName");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    acceptor = JVMUtil.getAcceptor(container, ACCEPTOR_NAME);
    connector = JVMUtil.getConnector(container, ACCEPTOR_NAME);

    CDONet4jSessionConfiguration sessionConfiguration = createSessionConfiguration(systemRepository.getName());
    systemSession = sessionConfiguration.openNet4jSession();

    List<String> moduleNames = initSystemRepository(systemSession);
    if (moduleNames != null)
    {
      for (String moduleName : moduleNames)
      {
        addModule(moduleName);
      }
    }

    systemRepository.addHandler(writeAccessHandler);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    systemRepository.removeHandler(writeAccessHandler);
    LifecycleUtil.deactivate(systemSession);
    deactivate(moduleSessions);
    deactivate(moduleRepositories);
    LifecycleUtil.deactivate(connector);
    LifecycleUtil.deactivate(acceptor);
    super.doDeactivate();
  }

  protected List<String> initSystemRepository(CDOSession session) throws Exception
  {
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(System.RESOURCE_PATH);

    EList<EObject> contents = resource.getContents();
    if (contents.isEmpty())
    {
      OM.LOG.info("Initializing system resource");

      Process process = LMFactory.eINSTANCE.createProcess();
      process.setModuleDefinitionPath(moduleDefinitionPath);
      process.setInitialModuleVersion(Version.createOSGi(0, 1, 0));

      initProcess(process);

      System system = LMFactory.eINSTANCE.createSystem();
      system.setName(systemName);
      system.setProcess(process);

      contents.add(system);
      transaction.setCommitComment("<initialize system>");
      transaction.commit();
      return null;
    }

    EObject root = contents.get(0);
    if (root instanceof System)
    {
      System system = (System)root;

      String name = system.getName();
      if (!Objects.equals(name, systemName))
      {
        throw new IllegalStateException("System name '" + name + "' does not match configured name '" + systemName + "'");
      }

      List<String> moduleNames = new ArrayList<>();
      for (Module module : system.getModules())
      {
        moduleNames.add(module.getName());
      }

      return moduleNames;
    }

    throw new IllegalStateException("System resource does not contain a system");
  }

  protected void initProcess(Process process)
  {
    if (processInitializer != null)
    {
      processInitializer.accept(process);
    }
  }

  protected void handleCommit(CommitContext commitContext)
  {
    InternalCDORevisionDelta[] dirtyObjectDeltas = commitContext.getDirtyObjectDeltas();
    if (dirtyObjectDeltas != null)
    {
      List<Pair<String, CDOID>> newModules = new ArrayList<>(); // Module name + ID of initial stream.

      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        InternalCDORevisionDelta revisionDelta = dirtyObjectDeltas[i];
        EClass eClass = revisionDelta.getEClass();

        if (eClass == SYSTEM)
        {
          handleListDelta(commitContext, revisionDelta, SYSTEM__MODULES, //
              addedModule -> handleModuleAddition(addedModule, newModules), //
              removeModuleDelta -> handleModuleDeletion(commitContext, revisionDelta, removeModuleDelta));
        }
        else if (eClass == MODULE)
        {
          CDOFeatureDelta featureDelta = revisionDelta.getFeatureDelta(MODULE__NAME);
          if (featureDelta != null)
          {
            throw new CDOException("Renaming modules is not supported");
          }
        }
        else if (eClass == STREAM)
        {
          handleListDelta(commitContext, revisionDelta, STREAM__CONTENTS, //
              addedContent -> handleBaselineAddition(commitContext, addedContent), //
              AbstractLifecycleManager::preventRemoval);
        }

        // TODO Implement more server-side validations.
      }

      createNewModules(commitContext, newModules);

      // InternalCDORevision[] newObjects = commitContext.getNewObjects();
      // if (newObjects != null) {
      // for (int i = 0; i < newObjects.length; i++) {
      // InternalCDORevision addedStream = newObjects[i];
      // if (addedStream.getEClass() == STREAM) {
      // CDOID moduleID = (CDOID) addedStream.getContainerID();
      //
      // handleListDelta(commitContext, revision, MODULE__STREAMS, true, addedStream
      // -> {
      // CDOID streamID = addedStream.getID();
      // CDOID moduleID = (CDOID) addedStream.getContainerID();
      // CDOID baseDropID = (CDOID) addedStream.getValue(STREAM__BASE);
      //
      // // Fork and wait to avoid a mess with StoreThreadLocal.
      // long startTimeStamp = executeAgainstModuleSession(commitContext, moduleID,
      // session -> {
      // if (CDOIDUtil.isNull(baseDropID)) {
      // return session.getRepositoryInfo().getCreationTime();
      // }
      //
      // InternalCDORevision baseDrop = (InternalCDORevision) commitContext
      // .getRevision(baseDropID);
      // CDOBranchPointRef baseBranchPoint = (CDOBranchPointRef) baseDrop
      // .getValue(DROP__BRANCH_POINT);
      // return baseBranchPoint.getTimeStamp();
      // });
      //
      // commitContext.modify(context -> {
      // for (CDOIDAndVersion newObject : context.getChangeSetData().getNewObjects())
      // {
      // if (newObject.getID() == streamID) {
      // InternalCDORevision streamRevision = (InternalCDORevision) newObject;
      // streamRevision.set(STREAM__START_TIME_STAMP, 0, startTimeStamp);
      // }
      // }
      // });
      // });
      // }
      // }
      // }
    }
  }

  protected void handleModuleAddition(InternalCDORevision addedModule, List<Pair<String, CDOID>> newModules)
  {
    String name = (String)addedModule.get(MODULE__NAME, 0);

    if (!isValidModuleName(name))
    {
      throw new CDOException("Module name is invalid: " + name);
    }

    if (moduleRepositories.containsKey(name))
    {
      throw new CDOException("Module name is not unique: " + name);
    }

    CDOID initialStreamID = null;
    CDOList streams = addedModule.getListOrNull(MODULE__STREAMS);
    if (streams != null && !streams.isEmpty())
    {
      initialStreamID = (CDOID)streams.get(0);
    }

    newModules.add(Pair.create(name, initialStreamID));
  }

  protected void handleModuleDeletion(CommitContext commitContext, InternalCDORevisionDelta systemRevisionDelta, CDORemoveFeatureDelta removeModuleDelta)
  {
    CDOID systemID = systemRevisionDelta.getID();
    int index = removeModuleDelta.getIndex();

    InternalCDORevision systemRevision = commitContext.getOldRevisions().get(systemID);
    CDOID removedID = (CDOID)systemRevision.get(SYSTEM__MODULES, index);

    for (InternalCDORevision revision : ((InternalCommitContext)commitContext).getDetachedRevisions())
    {
      if (revision.getID() == removedID)
      {
        String moduleName = (String)revision.get(MODULE__NAME, 0);

        CDOSession session = moduleSessions.remove(moduleName);
        LifecycleUtil.deactivate(session);

        InternalRepository repository = moduleRepositories.remove(moduleName);
        LifecycleUtil.deactivate(repository);
        break;
      }
    }
  }

  protected void handleBaselineAddition(CommitContext commitContext, InternalCDORevision addedContent)
  {
    // if (addedContent.getEClass() == DROP)
    // {
    // CDOID dropID = addedContent.getID();
    //
    // CDOID streamID = (CDOID)addedContent.getContainerID();
    // InternalCDORevision stream = (InternalCDORevision)commitContext.getRevision(streamID);
    //
    // String branchPath = getBranch(stream).getBranchPath();
    //
    // CDOID moduleID = (CDOID)stream.getContainerID();
    // AtomicReference<ModuleDefinition> moduleDefinitionHolder = new AtomicReference<>();
    //
    // // Fork and wait to avoid a mess with StoreThreadLocal.
    // CDOBranchPointRef branchPointRef = executeAgainstModuleSession(commitContext, moduleID, session -> {
    // CDOBranch branch = session.getBranchManager().getBranch(branchPath);
    // CDOBranchPoint branchPoint = branch.getPoint(commitContext.getBranchPoint().getTimeStamp());
    // return new CDOBranchPointRef(branchPoint);
    // });
    //
    // commitContext.modify(context -> {
    // CDOChangeSetData changeSetData = context.getChangeSetData();
    // List<CDOIDAndVersion> newObjects = changeSetData.getNewObjects();
    // for (CDOIDAndVersion newObject : newObjects)
    // {
    // if (newObject.getID() == dropID)
    // {
    // InternalCDORevision dropRevision = (InternalCDORevision)newObject;
    // dropRevision.set(DROP__BRANCH_POINT, 0, branchPointRef.getURI());
    // }
    // }
    // });
    // }
  }

  protected void createNewModules(CommitContext commitContext, List<Pair<String, CDOID>> newModules)
  {
    for (Pair<String, CDOID> newModule : newModules)
    {
      try
      {
        String newModuleName = newModule.getElement1();
        CDOID initialStreamID = newModule.getElement2();

        createNewModule(newModuleName);

        CDOView systemView = systemSession.openView();
        Version initialModuleVersion;

        try
        {
          CDOResource systemResource = systemView.getResource(System.RESOURCE_PATH);
          System system = (System)systemResource.getContents().get(0);
          Process process = system.getProcess();
          initialModuleVersion = process.getInitialModuleVersion();
        }
        finally
        {
          systemView.close();
        }

        CDOSession moduleSession = getModuleSession(newModuleName);
        CDOTransaction moduleTransaction = moduleSession.openTransaction();

        try
        {
          // In case the module was already existing we don't modify its content.
          if (!moduleTransaction.hasResource(moduleDefinitionPath))
          {
            ModuleDefinition moduleDefinition = ModulesFactory.eINSTANCE.createModuleDefinition();
            moduleDefinition.setName(newModuleName);
            moduleDefinition.setVersion(initialModuleVersion);

            CDOResource moduleDefinitionResource = moduleTransaction.createResource(moduleDefinitionPath);
            moduleDefinitionResource.getContents().add(moduleDefinition);
            moduleTransaction.commit();

            long creationTime = moduleSession.getRepositoryInfo().getCreationTime();
            commitContext.modify(context -> {
              for (CDOIDAndVersion newObject : context.getChangeSetData().getNewObjects())
              {
                if (newObject.getID() == initialStreamID)
                {
                  InternalCDORevision streamRevision = (InternalCDORevision)newObject;
                  streamRevision.set(STREAM__START_TIME_STAMP, 0, creationTime);
                }
              }
            });
          }
        }
        finally
        {
          moduleTransaction.close();
        }
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
    }
  }

  protected void createNewModule(String moduleName) throws Exception
  {
    addModule(moduleName);
  }

  protected boolean isValidModuleName(String moduleName)
  {
    if (moduleName == null)
    {
      return false;
    }

    String trimmed = moduleName.trim();
    if (!Objects.equals(trimmed, moduleName))
    {
      return false;
    }

    if (trimmed.length() == 0)
    {
      return false;
    }

    return true;
  }

  protected void addModule(String moduleName) throws Exception
  {
    // Fork and wait to avoid a mess with StoreThreadLocal.
    RunnableWithException.forkAndWait(() -> {
      OM.LOG.info("Adding module " + moduleName + " to system " + systemName);

      InternalRepository moduleRepository = createModuleRepository(moduleName);
      CDOServerUtil.addRepository(container, moduleRepository);
      securitySupport.addModuleRepository(moduleRepository);
      moduleRepositories.put(moduleName, moduleRepository);
    });
  }

  protected abstract InternalRepository createModuleRepository(String moduleName) throws CoreException;

  protected CDONet4jSession openModuleSession(InternalRepository moduleRepository)
  {
    // Do not use org.eclipse.emf.cdo.server.internal.embedded.ClientBranchManager
    // because there are no branch notifications without branch requests!

    CDONet4jSessionConfiguration configuration = createSessionConfiguration(moduleRepository.getName());
    configuration.setSignalTimeout(Integer.MAX_VALUE);

    CDONet4jSession session = configuration.openNet4jSession();
    session.options().setCommitTimeout(Integer.MAX_VALUE);
    return session;
  }

  protected CDONet4jSessionConfiguration createSessionConfiguration(String repositoryName)
  {
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);

    IPasswordCredentials credentials = securitySupport.getCredentials();
    if (credentials != null)
    {
      configuration.setCredentialsProvider(new PasswordCredentialsProvider(credentials));
    }

    return configuration;
  }

  @SuppressWarnings("unused")
  private <R> R executeAgainstModuleSession(CommitContext commitContext, CDOID moduleID, Function<CDOSession, R> function)
  {
    CDORevision module = commitContext.getRevision(moduleID);
    String moduleName = (String)module.data().get(MODULE__NAME, 0);
    CDOSession moduleSession = getModuleSession(moduleName);

    Throwable[] exception = { null };
    AtomicReference<R> result = new AtomicReference<>();

    // Fork and wait to avoid a mess with StoreThreadLocal.
    Thread thread = new Thread(function.getClass().getName())
    {
      @Override
      public void run()
      {
        try
        {
          R value = function.apply(moduleSession);
          result.set(value);
        }
        catch (RuntimeException | Error ex)
        {
          exception[0] = ex;
        }
      }
    };

    thread.start();

    try
    {
      thread.join();
    }
    catch (InterruptedException ex)
    {
      thread.interrupt();
      throw WrappedException.wrap(ex);
    }

    if (exception[0] instanceof RuntimeException)
    {
      throw (RuntimeException)exception[0];
    }

    if (exception[0] instanceof Error)
    {
      throw (Error)exception[0];
    }

    return result.get();
  }

  private static void handleListDelta(CommitContext commitContext, InternalCDORevisionDelta revisionDelta, EReference reference,
      Consumer<InternalCDORevision> additionConsumer, Consumer<CDORemoveFeatureDelta> removalConsumer)
  {
    CDOListFeatureDelta listDelta = (CDOListFeatureDelta)revisionDelta.getFeatureDelta(reference);
    if (listDelta != null)
    {
      for (CDOFeatureDelta featureDelta : listDelta.getListChanges())
      {
        if (removalConsumer != null && featureDelta instanceof CDORemoveFeatureDelta)
        {
          CDORemoveFeatureDelta removeDelta = (CDORemoveFeatureDelta)featureDelta;
          removalConsumer.accept(removeDelta);
        }
        else if (additionConsumer != null && featureDelta instanceof CDOAddFeatureDelta)
        {
          CDOAddFeatureDelta addDelta = (CDOAddFeatureDelta)featureDelta;
          CDOID id = (CDOID)addDelta.getValue();
          InternalCDORevision revision = (InternalCDORevision)commitContext.getRevision(id);
          additionConsumer.accept(revision);
        }
      }
    }
  }

  private static void preventRemoval(CDORemoveFeatureDelta removeFeatureDelta) throws CDOException
  {
    EStructuralFeature feature = removeFeatureDelta.getFeature();
    EClass eClass = feature.getEContainingClass();
    throw new CDOException("Removing from " + eClass.getName() + "." + feature.getName() + " is not supported");
  }

  private static void deactivate(Map<String, ?> map)
  {
    for (Object value : map.values())
    {
      LifecycleUtil.deactivate(value);
    }

    map.clear();
  }

  public static CDOBranchRef getBranch(CDORevision floatingBaseline)
  {
    String branchPath = null;

    EClass eClass = floatingBaseline.getEClass();
    if (eClass == STREAM)
    {
      branchPath = (String)floatingBaseline.data().get(STREAM__MAINTENANCE_BRANCH, 0);
      if (branchPath == null)
      {
        branchPath = (String)floatingBaseline.data().get(STREAM__DEVELOPMENT_BRANCH, 0);
      }
    }
    else if (eClass == CHANGE)
    {
      branchPath = (String)floatingBaseline.data().get(CHANGE__BRANCH, 0);
    }

    if (branchPath == null)
    {
      return null;
    }

    return new CDOBranchRef(branchPath);
  }

  static
  {
    boolean securityAvailable;

    try
    {
      securityAvailable = CommonPlugin.loadClass("org.eclipse.emf.cdo.server.security", "org.eclipse.emf.cdo.server.security.ISecurityManager") != null;
    }
    catch (Throwable ex)
    {
      securityAvailable = false;
    }

    SECURITY_AVAILABLE = securityAvailable;
  }

  /**
   * @author Eike Stepper
   */
  private interface SecuritySupport
  {
    public static final SecuritySupport UNAVAILABLE = new SecuritySupport()
    {
      @Override
      public IPasswordCredentials getCredentials()
      {
        return null;
      }

      @Override
      public void addModuleRepository(InternalRepository moduleRepository)
      {
        // Do nothing.
      }
    };

    public IPasswordCredentials getCredentials();

    public void addModuleRepository(InternalRepository moduleRepository);

    /**
     * @author Eike Stepper
     */
    public static final class Available implements SecuritySupport
    {
      private static final String USER_NAME = "Lifecycle Manager";

      private static final char[] KEY = { 'c', 'd', 'o', ' ', 'r', 'o', 'c', 'k', 's', ' ', '(', 'e', 's', ')' };

      private final InternalSecurityManager securityManager;

      private final IPasswordCredentials credentials;

      public Available(InternalRepository systemRepository)
      {
        securityManager = (InternalSecurityManager)SecurityManagerUtil.getSecurityManager(systemRepository);

        if (securityManager != null)
        {
          String uuid = systemRepository.getUUID();

          byte[] bytes;

          try
          {
            bytes = SecurityUtil.pbeEncrypt(uuid.getBytes(), KEY, SecurityUtil.PBE_WITH_MD5_AND_DES, SecurityUtil.DEFAULT_SALT,
                SecurityUtil.DEFAULT_ITERATION_COUNT);
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
            bytes = new String(KEY).getBytes();
          }

          String hex = HexUtil.bytesToHex(bytes);
          credentials = new PasswordCredentials(USER_NAME, SecurityUtil.toCharArray(hex));

          securityManager.modify(realm -> {
            User lmUser = realm.getUser(credentials.getUserID());
            if (lmUser == null)
            {
              lmUser = realm.addUser(credentials);

              Role allObjectsReader = realm.getRole(Role.ALL_OBJECTS_READER);
              if (allObjectsReader != null)
              {
                lmUser.getRoles().add(allObjectsReader);
              }

              Role normalObjectsWriter = realm.getRole(Role.NORMAL_OBJECTS_WRITER);
              if (normalObjectsWriter != null)
              {
                lmUser.getRoles().add(normalObjectsWriter);
              }
            }
          });
        }
        else
        {
          credentials = null;
        }
      }

      @Override
      public IPasswordCredentials getCredentials()
      {
        return credentials;
      }

      @Override
      public void addModuleRepository(InternalRepository moduleRepository)
      {
        if (securityManager != null)
        {
          securityManager.addSecondaryRepository(moduleRepository);
        }
      }
    }
  }
}
