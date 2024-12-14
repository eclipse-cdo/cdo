/*
 * Copyright (c) 2015, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent.StructuralImpact;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace.DirtyStateChangedEvent;
import org.eclipse.emf.cdo.workspace.CDOWorkspace.ObjectStatesChangedEvent;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceConfiguration;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.ecore.resource.ResourceSet;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OfflineCDOCheckout extends CDOCheckoutImpl
{
  public static final String PROP_DIRTY = "dirty";

  private final IListener workspaceListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof DirtyStateChangedEvent)
      {
        DirtyStateChangedEvent e = (DirtyStateChangedEvent)event;
        setDirty(e.isDirty());
        fireElementChangedEvent(ElementsChangedEvent.StructuralImpact.NONE);
      }
      else if (event instanceof ObjectStatesChangedEvent)
      {
        ObjectStatesChangedEvent e = (ObjectStatesChangedEvent)event;

        Set<Object> objects = new HashSet<>();
        CDOView view = getView();

        for (CDOID id : e.getChangedIDs())
        {
          CDOObject object = view.getObject(id, false);
          if (object != null)
          {
            objects.add(object);
          }
        }

        getManager().fireElementsChangedEvent(objects.toArray());
      }
    }
  };

  private InternalCDOWorkspace workspace;

  private boolean dirty;

  public OfflineCDOCheckout()
  {
  }

  @Override
  public boolean isOffline()
  {
    return true;
  }

  @Override
  public boolean isOnline()
  {
    return false;
  }

  @Override
  public void setReadOnly(boolean readOnly)
  {
    throw new IllegalStateException("Checkout is offline: " + this);
  }

  public final InternalCDOWorkspace getWorkspace()
  {
    return workspace;
  }

  @Override
  public final boolean isDirty()
  {
    if (workspace != null)
    {
      return workspace.isDirty();
    }

    return dirty;
  }

  public final void setDirty(boolean dirty)
  {
    if (this.dirty != dirty)
    {
      this.dirty = dirty;
      save();
    }
  }

  @Override
  public CDOState getState(Object object)
  {
    if (object == this)
    {
      return isDirty() ? CDOState.DIRTY : CDOState.CLEAN;
    }

    if (workspace != null)
    {
      return workspace.getState(object);
    }

    return null;
  }

  public void refresh()
  {
    fireElementChangedEvent(StructuralImpact.ELEMENT);
  }

  @Override
  public String getBranchPath()
  {
    if (workspace != null)
    {
      return workspace.getBranchPath();
    }

    return super.getBranchPath();
  }

  @Override
  protected String doSetBranchPoint(int branchID, long timeStamp)
  {
    CDORepository repository = getRepository();
    CDOSession session = repository.acquireSession();

    try
    {
      CDOBranch branch = session.getBranchManager().getBranch(branchID);
      if (branch != null)
      {
        String branchPath = branch.getPathName();
        workspace.replace(branchPath, timeStamp);
        return branchPath;
      }
    }
    finally
    {
      repository.releaseSession();
    }

    return null;
  }

  @Override
  protected void init(File folder, String type, Properties properties)
  {
    super.init(folder, type, properties);
    dirty = Boolean.parseBoolean(properties.getProperty(PROP_DIRTY, "false"));
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_DIRTY, Boolean.toString(dirty));
  }

  @Override
  protected CDOView openView(CDOSession session, ResourceSet resourceSet)
  {
    CDOSessionConfigurationFactory remote = new CDOSessionConfigurationFactory()
    {
      @Override
      public CDOSessionConfiguration createSessionConfiguration()
      {
        return new RemoteSessionConfiguration();
      }
    };

    File folder = getFolder();
    File storeFolder = new File(folder, "store");
    File dbPrefix = new File(storeFolder, "db");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + dbPrefix);

    Map<String, String> props = new HashMap<>();
    props.put(IMappingStrategy.Props.QUALIFIED_NAMES, "true");

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true, false);
    mappingStrategy.setProperties(props);

    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    File baseFolder = new File(folder, "base");
    CDOWorkspaceBase base = CDOWorkspaceUtil.createFolderWorkspaceBase(baseFolder);

    String localRepositoryName = getRepository().getName() + "-workspace" + getID();
    int branchID = getBranchID();
    long timeStamp = getTimeStamp();

    CDOWorkspaceConfiguration configuration = CDOWorkspaceUtil.createWorkspaceConfiguration();
    configuration.setLocalRepositoryName(localRepositoryName);
    configuration.setRemote(remote);
    configuration.setStore(store);
    configuration.setBase(base);

    if (storeFolder.isDirectory())
    {
      workspace = (InternalCDOWorkspace)configuration.open();
    }
    else
    {
      configuration.setBranchID(branchID);
      configuration.setTimeStamp(timeStamp);

      workspace = (InternalCDOWorkspace)configuration.checkout();
    }

    setBranchPath(workspace.getBranchPath());
    setDirty(workspace.isDirty());

    workspace.addListener(workspaceListener);
    return workspace.openView(resourceSet);
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
  protected CDOView doOpenView(boolean readOnly, ResourceSet resourceSet)
  {
    if (workspace == null)
    {
      return null;
    }

    if (readOnly)
    {
      return workspace.openView(resourceSet);
    }

    return workspace.openTransaction(resourceSet);
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteSessionConfiguration implements CDOSessionConfiguration, Closeable
  {
    private boolean closed;

    @Override
    public void addListener(IListener listener)
    {
    }

    @Override
    public void removeListener(IListener listener)
    {
    }

    @Override
    public boolean hasListeners()
    {
      return false;
    }

    @Override
    public IListener[] getListeners()
    {
      return null;
    }

    @Override
    public String getUserID()
    {
      return null;
    }

    @Override
    public void setUserID(String userID)
    {
    }

    @Override
    public boolean isPassiveUpdateEnabled()
    {
      return false;
    }

    @Override
    public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
    {
    }

    @Override
    public PassiveUpdateMode getPassiveUpdateMode()
    {
      return null;
    }

    @Override
    public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
    {
    }

    @Override
    public LockNotificationMode getLockNotificationMode()
    {
      return null;
    }

    @Override
    public void setLockNotificationMode(LockNotificationMode mode)
    {
    }

    @Override
    public ExceptionHandler getExceptionHandler()
    {
      return null;
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler)
    {
    }

    @Override
    public CDOIDGenerator getIDGenerator()
    {
      return null;
    }

    @Override
    public void setIDGenerator(CDOIDGenerator idGenerator)
    {
    }

    @Override
    public CDOFetchRuleManager getFetchRuleManager()
    {
      return null;
    }

    @Override
    public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
    {
    }

    @Override
    public CDOBranchManager getBranchManager()
    {
      return null;
    }

    @Override
    public void setBranchManager(CDOBranchManager branchManager)
    {
    }

    @Override
    @Deprecated
    public org.eclipse.emf.cdo.common.protocol.CDOAuthenticator getAuthenticator()
    {
      return null;
    }

    @Override
    public IPasswordCredentialsProvider getCredentialsProvider()
    {
      return null;
    }

    @Override
    public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
    {
    }

    @Override
    public byte[] getOneTimeLoginToken()
    {
      return null;
    }

    @Override
    public void setOneTimeLoginToken(byte[] oneTimeLoginToken)
    {
    }

    @Override
    public boolean isActivateOnOpen()
    {
      return false;
    }

    @Override
    public void setActivateOnOpen(boolean activateOnOpen)
    {
    }

    @Override
    public boolean isSessionOpen()
    {
      return !closed;
    }

    @Override
    public CDOSession openSession()
    {
      CDORepository repository = getRepository();
      return repository.acquireSession();
    }

    @Override
    public void close()
    {
      if (!closed)
      {
        CDORepository repository = getRepository();
        repository.releaseSession();

        closed = true;
      }
    }

    @Override
    public boolean isClosed()
    {
      return closed;
    }
  }
}
