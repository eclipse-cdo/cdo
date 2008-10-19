/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/233273    
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryElement;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.StoreThreadLocal;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Repository extends Container<IRepositoryElement> implements IRepository
{
  private String name;

  private IStore store;

  private String uuid;

  private Map<String, String> properties;

  private boolean supportingRevisionDeltas;

  private boolean supportingAudits;

  private boolean verifyingRevisions;

  private PackageManager packageManager;

  private SessionManager sessionManager;

  private RevisionManager revisionManager;

  private QueryManager queryManager;

  private NotificationManager notificationManager;

  private CommitManager commitManager;

  private IQueryHandlerProvider queryHandlerProvider;

  private List<ReadAccessHandler> readAccessHandlers = new ArrayList<ReadAccessHandler>();

  private List<WriteAccessHandler> writeAccessHandlers = new ArrayList<WriteAccessHandler>();

  private IRepositoryElement[] elements;

  @ExcludeFromDump
  private transient long lastMetaID;

  @ExcludeFromDump
  private transient long lastCommitTimeStamp;

  @ExcludeFromDump
  private transient Object lastCommitTimeStampLock = new Object();

  public Repository()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public IStore getStore()
  {
    return store;
  }

  public void setStore(IStore store)
  {
    this.store = store;
    store.setRepository(this);
  }

  public String getUUID()
  {
    if (uuid == null)
    {
      uuid = getProperties().get(Props.OVERRIDE_UUID);
      if (uuid == null)
      {
        uuid = UUID.randomUUID().toString();
      }
      else if (uuid.length() == 0)
      {
        uuid = getName();
      }
    }

    return uuid;
  }

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public boolean isSupportingRevisionDeltas()
  {
    return supportingRevisionDeltas;
  }

  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  public boolean isVerifyingRevisions()
  {
    return verifyingRevisions;
  }

  public PackageManager getPackageManager()
  {
    return packageManager;
  }

  /**
   * @since 2.0
   */
  public void setPackageManager(PackageManager packageManager)
  {
    this.packageManager = packageManager;
  }

  public SessionManager getSessionManager()
  {
    return sessionManager;
  }

  /**
   * @since 2.0
   */
  public void setSessionManager(SessionManager sessionManager)
  {
    this.sessionManager = sessionManager;
  }

  public RevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  /**
   * @since 2.0
   */
  public void setRevisionManager(RevisionManager revisionManager)
  {
    this.revisionManager = revisionManager;
  }

  /**
   * @since 2.0
   */
  public QueryManager getQueryManager()
  {
    return queryManager;
  }

  /**
   * @since 2.0
   */
  public void setQueryManager(QueryManager queryManager)
  {
    this.queryManager = queryManager;
  }

  /**
   * @since 2.0
   */
  public NotificationManager getNotificationManager()
  {
    return notificationManager;
  }

  /**
   * @since 2.0
   */
  public void setNotificationManager(NotificationManager notificationManager)
  {
    this.notificationManager = notificationManager;
  }

  /**
   * @since 2.0
   */
  public CommitManager getCommitManager()
  {
    return commitManager;
  }

  /**
   * @since 2.0
   */
  public void setCommitManager(CommitManager commitManager)
  {
    this.commitManager = commitManager;
  }

  /**
   * @since 2.0
   */
  public long createCommitTimeStamp()
  {
    long now = System.currentTimeMillis();
    synchronized (lastCommitTimeStampLock)
    {
      if (lastCommitTimeStamp != 0)
      {
        while (lastCommitTimeStamp == now)
        {
          ConcurrencyUtil.sleep(1);
          now = System.currentTimeMillis();
        }
      }

      // TODO Persist lastCommitTimeStamp in store
      lastCommitTimeStamp = now;
      return now;
    }
  }

  /**
   * @since 2.0
   */
  public IQueryHandlerProvider getQueryHandlerProvider()
  {
    return queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider)
  {
    this.queryHandlerProvider = queryHandlerProvider;
  }

  /**
   * @since 2.0
   */
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    IQueryHandler handler = null;
    if (CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES.equals(info.getQueryLanguage()))
    {
      handler = new ResourcesQueryHandler();
    }

    if (handler == null)
    {
      if (queryHandlerProvider != null)
      {
        handler = queryHandlerProvider.getQueryHandler(info);
      }
      else if (OMPlatform.INSTANCE.isOSGiRunning())
      {
        try
        {
          IQueryHandlerProvider provider = new ContainerQueryHandlerProvider(IPluginContainer.INSTANCE);
          handler = provider.getQueryHandler(info);
        }
        catch (Throwable t)
        {
          OM.LOG.warn("Problem with ContainerQueryHandlerProvider: " + t.getMessage());
        }
      }
    }

    if (handler == null)
    {
      handler = StoreThreadLocal.getStoreReader();
    }

    return handler;
  }

  public IRepositoryElement[] getElements()
  {
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  public synchronized CDOIDMetaRange getMetaIDRange(int count)
  {
    CDOID lowerBound = CDOIDUtil.createMeta(lastMetaID + 1);
    lastMetaID += count;
    return CDOIDUtil.createMetaRange(lowerBound, count);
  }

  public long getLastMetaID()
  {
    return lastMetaID;
  }

  public void setLastMetaID(long lastMetaID)
  {
    this.lastMetaID = lastMetaID;
  }

  /**
   * @since 2.0
   */
  public long getCreationTime()
  {
    return store.getCreationTime();
  }

  /**
   * @since 2.0
   */
  public void validateTimeStamp(long timeStamp) throws IllegalArgumentException
  {
    long creationTimeStamp = getCreationTime();
    if (timeStamp < creationTimeStamp)
    {
      throw new IllegalArgumentException("timeStamp < repository creation time: " + creationTimeStamp);
    }

    long currentTimeStamp = System.currentTimeMillis();
    if (timeStamp > currentTimeStamp)
    {
      throw new IllegalArgumentException("timeStamp > current time: " + currentTimeStamp);
    }
  }

  /**
   * @since 2.0
   */
  public void addHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        if (!readAccessHandlers.contains(handler))
        {
          readAccessHandlers.add((ReadAccessHandler)handler);
        }
      }
    }
    else if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        if (!writeAccessHandlers.contains(handler))
        {
          writeAccessHandlers.add((WriteAccessHandler)handler);
        }
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid handler: " + handler);
    }
  }

  /**
   * @since 2.0
   */
  public void removeHandler(Handler handler)
  {
    if (handler instanceof ReadAccessHandler)
    {
      synchronized (readAccessHandlers)
      {
        readAccessHandlers.remove(handler);
      }
    }
    else if (handler instanceof WriteAccessHandler)
    {
      synchronized (writeAccessHandlers)
      {
        writeAccessHandlers.remove(handler);
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid handler: " + handler);
    }
  }

  /**
   * @since 2.0
   */
  public void notifyReadAccessHandlers(Session session, CDORevision[] revisions, List<CDORevision> additionalRevisions)
  {
    ReadAccessHandler[] handlers;
    synchronized (readAccessHandlers)
    {
      int size = readAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = readAccessHandlers.toArray(new ReadAccessHandler[size]);
    }

    for (ReadAccessHandler handler : handlers)
    {
      // Do *not* protect against unchecked exceptions from handlers!
      handler.handleRevisionsBeforeSending(session, revisions, additionalRevisions);
    }
  }

  /**
   * @since 2.0
   */
  public void notifyWriteAccessHandlers(Transaction transaction, IStoreWriter.CommitContext commitContext)
  {
    WriteAccessHandler[] handlers;
    synchronized (writeAccessHandlers)
    {
      int size = writeAccessHandlers.size();
      if (size == 0)
      {
        return;
      }

      handlers = writeAccessHandlers.toArray(new WriteAccessHandler[size]);
    }

    for (WriteAccessHandler handler : handlers)
    {
      // Do *not* protect against unchecked exceptions from handlers!
      handler.handleTransactionBeforeCommitting(transaction, commitContext);
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Repository[{0}]", name);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(!StringUtil.isEmpty(name), "name is empty");
    checkState(packageManager, "packageManager");
    checkState(sessionManager, "sessionManager");
    checkState(revisionManager, "revisionManager");
    checkState(queryManager, "queryManager");
    checkState(notificationManager, "notificationManager");
    checkState(commitManager, "commitManager");

    packageManager.setRepository(this);
    sessionManager.setRepository(this);
    revisionManager.setRepository(this);
    queryManager.setRepository(this);
    notificationManager.setRepository(this);
    commitManager.setRepository(this);

    checkState(store, "store");
    supportingRevisionDeltas = store.getSupportedChangeFormats().contains(IStore.ChangeFormat.DELTA);

    {
      String value = getProperties().get(Props.SUPPORTING_AUDITS);
      if (value != null)
      {
        supportingAudits = Boolean.valueOf(value);
        store.setRevisionTemporality(supportingAudits ? IStore.RevisionTemporality.AUDITING
            : IStore.RevisionTemporality.NONE);
      }
      else
      {
        supportingAudits = store.getRevisionTemporality() == IStore.RevisionTemporality.AUDITING;
      }
    }

    {
      String value = getProperties().get(Props.VERIFYING_REVISIONS);
      verifyingRevisions = value == null ? false : Boolean.valueOf(value);
    }

    elements = new IRepositoryElement[] { packageManager, sessionManager, revisionManager, queryManager,
        notificationManager, commitManager, store };
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(store);
    LifecycleUtil.activate(packageManager);
    if (store.wasCrashed())
    {
      OM.LOG.info("Crash of repository " + name + " detected");
      store.repairAfterCrash();
    }

    setLastMetaID(store.getLastMetaID());

    LifecycleUtil.activate(sessionManager);
    LifecycleUtil.activate(revisionManager);
    LifecycleUtil.activate(queryManager);
    LifecycleUtil.activate(notificationManager);
    LifecycleUtil.activate(commitManager);
    LifecycleUtil.activate(queryHandlerProvider);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(queryHandlerProvider);
    LifecycleUtil.deactivate(commitManager);
    LifecycleUtil.deactivate(notificationManager);
    LifecycleUtil.deactivate(queryManager);
    LifecycleUtil.deactivate(revisionManager);
    LifecycleUtil.deactivate(sessionManager);

    LifecycleUtil.deactivate(packageManager);
    LifecycleUtil.deactivate(store);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Default extends Repository
  {
    public Default()
    {
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      if (getPackageManager() == null)
      {
        setPackageManager(createPackageManager());
      }

      if (getSessionManager() == null)
      {
        setSessionManager(createSessionManager());
      }

      if (getRevisionManager() == null)
      {
        setRevisionManager(createRevisionManager());
      }

      if (getQueryManager() == null)
      {
        setQueryManager(createQueryManager());
      }

      if (getNotificationManager() == null)
      {
        setNotificationManager(createNotificationManager());
      }

      if (getCommitManager() == null)
      {
        setCommitManager(createCommitManager());
      }

      super.doBeforeActivate();
    }

    protected PackageManager createPackageManager()
    {
      return new PackageManager();
    }

    protected SessionManager createSessionManager()
    {
      return new SessionManager();
    }

    protected RevisionManager createRevisionManager()
    {
      return new RevisionManager();
    }

    protected QueryManager createQueryManager()
    {
      return new QueryManager();
    }

    protected NotificationManager createNotificationManager()
    {
      return new NotificationManager();
    }

    protected CommitManager createCommitManager()
    {
      return new CommitManager();
    }
  }
}
