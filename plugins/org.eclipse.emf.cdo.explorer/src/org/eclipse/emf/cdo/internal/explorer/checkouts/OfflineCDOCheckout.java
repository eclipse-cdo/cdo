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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
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

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OfflineCDOCheckout extends CDOCheckoutImpl
{
  private final IListener workspaceListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof DirtyStateChangedEvent)
      {
        fireElementChangedEvent(false);
      }
      else if (event instanceof ObjectStatesChangedEvent)
      {
        ObjectStatesChangedEvent e = (ObjectStatesChangedEvent)event;

        Set<Object> objects = new HashSet<Object>();
        CDOView view = getView();

        for (CDOID id : e.getChangedIDs())
        {
          CDOObject object = view.getObject(id, false);
          if (object != null)
          {
            objects.add(object);
          }
        }

        getManager().fireElementsChangedEvent(objects);
      }
    }
  };

  private InternalCDOWorkspace workspace;

  public OfflineCDOCheckout()
  {
  }

  public boolean isOffline()
  {
    return true;
  }

  public boolean isOnline()
  {
    return false;
  }

  public final InternalCDOWorkspace getWorkspace()
  {
    return workspace;
  }

  public final boolean isDirty()
  {
    if (workspace == null)
    {
      return false;
    }

    return workspace.isDirty();
  }

  @Override
  public String getBranchPath()
  {
    if (workspace == null)
    {
      return null;
    }

    return workspace.getBranchPath();
  }

  public CDOState getState(Object object)
  {
    if (object == this)
    {
      return isDirty() ? CDOState.DIRTY : CDOState.CLEAN;
    }

    if (workspace == null)
    {
      return null;
    }

    return workspace.getState(object);
  }

  @Override
  protected CDOView openView(CDOSession session)
  {
    CDOSessionConfigurationFactory remote = new CDOSessionConfigurationFactory()
    {
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

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true, false);
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

    workspace.addListener(workspaceListener);
    return workspace.openView();
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
  protected CDOView doOpenView(boolean readOnly)
  {
    if (workspace == null)
    {
      return null;
    }

    if (readOnly)
    {
      return workspace.openView();
    }

    return workspace.openTransaction();
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteSessionConfiguration implements CDOSessionConfiguration, Closeable
  {
    private boolean closed;

    public void addListener(IListener listener)
    {
    }

    public void removeListener(IListener listener)
    {
    }

    public boolean hasListeners()
    {
      return false;
    }

    public IListener[] getListeners()
    {
      return null;
    }

    public String getUserID()
    {
      return null;
    }

    public void setUserID(String userID)
    {
    }

    public boolean isPassiveUpdateEnabled()
    {
      return false;
    }

    public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
    {
    }

    public PassiveUpdateMode getPassiveUpdateMode()
    {
      return null;
    }

    public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
    {
    }

    public LockNotificationMode getLockNotificationMode()
    {
      return null;
    }

    public void setLockNotificationMode(LockNotificationMode mode)
    {
    }

    public ExceptionHandler getExceptionHandler()
    {
      return null;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler)
    {
    }

    public CDOIDGenerator getIDGenerator()
    {
      return null;
    }

    public void setIDGenerator(CDOIDGenerator idGenerator)
    {
    }

    public CDOFetchRuleManager getFetchRuleManager()
    {
      return null;
    }

    public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
    {
    }

    public CDOBranchManager getBranchManager()
    {
      return null;
    }

    public void setBranchManager(CDOBranchManager branchManager)
    {
    }

    @Deprecated
    public org.eclipse.emf.cdo.common.protocol.CDOAuthenticator getAuthenticator()
    {
      return null;
    }

    public IPasswordCredentialsProvider getCredentialsProvider()
    {
      return null;
    }

    public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
    {
    }

    public boolean isActivateOnOpen()
    {
      return false;
    }

    public void setActivateOnOpen(boolean activateOnOpen)
    {
    }

    public boolean isSessionOpen()
    {
      return !closed;
    }

    public CDOSession openSession()
    {
      CDORepository repository = getRepository();
      return repository.acquireSession();
    }

    public void close()
    {
      if (!closed)
      {
        CDORepository repository = getRepository();
        repository.releaseSession();

        closed = true;
      }
    }

    public boolean isClosed()
    {
      return closed;
    }
  }
}
