/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - https://bugs.eclipse.org/333260    
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Store extends Lifecycle implements IStore
{
  protected static final long CRASHED = -1L;

  @ExcludeFromDump
  private final transient String type;

  @ExcludeFromDump
  private final transient Set<ChangeFormat> supportedChangeFormats;

  @ExcludeFromDump
  private final transient Set<RevisionTemporality> supportedRevisionTemporalities;

  @ExcludeFromDump
  private final transient Set<RevisionParallelism> supportedRevisionParallelisms;

  private RevisionTemporality revisionTemporality = RevisionTemporality.NONE;

  private RevisionParallelism revisionParallelism = RevisionParallelism.NONE;

  private IRepository repository;

  @ExcludeFromDump
  private transient long lastMetaID;

  @ExcludeFromDump
  private transient Object lastMetaIDLock = new Object();

  @ExcludeFromDump
  private transient ProgressDistributor indicatingCommitDistributor = new ProgressDistributor.Geometric()
  {
    @Override
    public String toString()
    {
      String result = "indicatingCommitDistributor"; //$NON-NLS-1$
      if (repository != null)
      {
        result += ": " + repository.getName(); //$NON-NLS-1$
      }

      return result;
    }
  };

  public Store(String type, Set<ChangeFormat> supportedChangeFormats,
      Set<RevisionTemporality> supportedRevisionTemporalities, Set<RevisionParallelism> supportedRevisionParallelisms)
  {
    checkArg(!StringUtil.isEmpty(type), "Empty type"); //$NON-NLS-1$
    this.type = type;

    checkArg(supportedChangeFormats != null && !supportedChangeFormats.isEmpty(), "Empty supportedChangeFormats"); //$NON-NLS-1$
    this.supportedChangeFormats = supportedChangeFormats;

    checkArg(supportedRevisionTemporalities != null && !supportedRevisionTemporalities.isEmpty(),
        "Empty supportedRevisionTemporalities"); //$NON-NLS-1$
    this.supportedRevisionTemporalities = supportedRevisionTemporalities;

    checkArg(supportedRevisionParallelisms != null && !supportedRevisionParallelisms.isEmpty(),
        "Empty supportedRevisionParallelisms"); //$NON-NLS-1$
    this.supportedRevisionParallelisms = supportedRevisionParallelisms;
  }

  public String getType()
  {
    return type;
  }

  public IRepository getRepository()
  {
    return repository;
  }

  public void setRepository(IRepository repository)
  {
    this.repository = repository;
  }

  public Set<ChangeFormat> getSupportedChangeFormats()
  {
    return supportedChangeFormats;
  }

  public Set<RevisionTemporality> getSupportedRevisionTemporalities()
  {
    return supportedRevisionTemporalities;
  }

  public Set<RevisionParallelism> getSupportedRevisionParallelisms()
  {
    return supportedRevisionParallelisms;
  }

  public RevisionTemporality getRevisionTemporality()
  {
    return revisionTemporality;
  }

  public void setRevisionTemporality(RevisionTemporality revisionTemporality)
  {
    checkInactive();
    checkState(supportedRevisionTemporalities.contains(revisionTemporality), "Revision temporality not supported: " //$NON-NLS-1$
        + revisionTemporality);
    this.revisionTemporality = revisionTemporality;
  }

  public RevisionParallelism getRevisionParallelism()
  {
    return revisionParallelism;
  }

  public void setRevisionParallelism(RevisionParallelism revisionParallelism)
  {
    checkInactive();
    checkState(supportedRevisionParallelisms.contains(revisionParallelism), "Revision parallelism not supported: " //$NON-NLS-1$
        + revisionParallelism);
    this.revisionParallelism = revisionParallelism;
  }

  public long getLastMetaID()
  {
    synchronized (lastMetaIDLock)
    {
      return lastMetaID;
    }
  }

  public void setLastMetaID(long lastMetaID)
  {
    synchronized (lastMetaIDLock)
    {
      this.lastMetaID = lastMetaID;
    }
  }

  public CDOIDMetaRange getNextMetaIDRange(int count)
  {
    synchronized (lastMetaIDLock)
    {
      CDOID lowerBound = CDOIDUtil.createMeta(lastMetaID + 1);
      lastMetaID += count;
      return CDOIDUtil.createMetaRange(lowerBound, count);
    }
  }

  public IStoreAccessor getReader(ISession session)
  {
    IStoreAccessor reader = null;
    StoreAccessorPool pool = getReaderPool(session, false);
    if (pool != null)
    {
      reader = pool.removeStoreAccessor();
    }

    if (reader == null && session != null)
    {
      CDOCommonView[] views = session.getViews();
      for (CDOCommonView view : views)
      {
        pool = getWriterPool((IView)view, false);
        if (pool != null)
        {
          reader = pool.removeStoreAccessor();
          if (reader != null)
          {
            break;
          }
        }
      }
    }

    if (reader == null)
    {
      reader = createReader(session);
      LifecycleUtil.activate(reader);
    }

    return reader;
  }

  public IStoreAccessor getWriter(ITransaction transaction)
  {
    IStoreAccessor writer = null;
    StoreAccessorPool pool = getWriterPool(transaction, false);
    if (pool != null)
    {
      writer = pool.removeStoreAccessor();
    }

    if (writer == null)
    {
      writer = createWriter(transaction);
      LifecycleUtil.activate(writer);
    }

    return writer;
  }

  public ProgressDistributor getIndicatingCommitDistributor()
  {
    return indicatingCommitDistributor;
  }

  protected void releaseAccessor(StoreAccessor accessor)
  {
    StoreAccessorPool pool = null;
    if (accessor.isReader())
    {
      pool = getReaderPool(accessor.getSession(), true);
    }
    else
    {
      pool = getWriterPool(accessor.getTransaction(), true);
    }

    if (pool != null)
    {
      pool.addStoreAccessor(accessor);
    }
    else
    {
      accessor.deactivate();
    }
  }

  /**
   * Returns a {@link StoreAccessorPool pool} that may contain {@link IStoreAccessor} instances that are compatible with
   * the given session. The implementor may return <code>null</code> to indicate that no pooling occurs. It's also left
   * to the implementors choice how to determine the appropriate pool instance to be used for the given session, for
   * example it could always return the same pool instance, regardless of the given session.
   * <p>
   * If the implementor of this method decides to create pools that are only compatible with certain sessions or views,
   * then it is his responsibility to listen to {@link IContainerDelta.Kind#REMOVED REMOVED} events sent by either the
   * {@link ISessionManager} (indicating that a session is closed) or any of its sessions (indicating that a view is
   * closed). <b>Note:</b> Closing a session <em>implies</em> that all contained views are closed sliently without
   * firing respective events!
   * 
   * @param session
   *          The context which the pool must be compatible with. Must not be <code>null</code>.
   * @param forReleasing
   *          Enables lazy pool creation. The implementor is not supposed to create a new pool if <code>false</code> is
   *          passed. If <code>true</code> is passed it's up to the implementor whether to create a new pool or not.
   */
  protected abstract StoreAccessorPool getReaderPool(ISession session, boolean forReleasing);

  /**
   * Returns a {@link StoreAccessorPool pool} that may contain {@link IStoreAccessor} instances that are compatible with
   * the given session. The implementor may return <code>null</code> to indicate that no pooling occurs. It's also left
   * to the implementors choice how to determine the appropriate pool instance to be used for the given session, for
   * example it could always return the same pool instance, regardless of the given session.
   * <p>
   * If the implementor of this method decides to create pools that are only compatible with certain sessions or views,
   * then it is his responsibility to listen to {@link IContainerDelta.Kind#REMOVED REMOVED} events sent by either the
   * {@link ISessionManager} (indicating that a session is closed) or any of its sessions (indicating that a view is
   * closed). <b>Note:</b> Closing a session <em>implies</em> that all contained views are closed sliently without
   * firing respective events!
   * 
   * @param view
   *          The context which the pool must be compatible with. Must not be <code>null</code>.
   * @param forReleasing
   *          Enables lazy pool creation. The implementor is not supposed to create a new pool if <code>false</code> is
   *          passed. If <code>true</code> is passed it's up to the implementor whether to create a new pool or not.
   */
  protected abstract StoreAccessorPool getWriterPool(IView view, boolean forReleasing);

  /**
   * Creates and returns a <b>new</b> {@link IStoreAccessor} instance. The caller of this method is responsible for
   * {@link Lifecycle#activate() activating} the new instance.
   */
  protected abstract IStoreAccessor createReader(ISession session);

  /**
   * Creates and returns a <b>new</b> {@link IStoreAccessor} instance. The caller of this method is responsible for
   * {@link Lifecycle#activate() activating} the new instance.
   */
  protected abstract IStoreAccessor createWriter(ITransaction transaction);

  public static IStoreAccessor.QueryResourcesContext.ExactMatch createExactMatchContext(final CDOID folderID,
      final String name, final long timeStamp)
  {
    return new IStoreAccessor.QueryResourcesContext.ExactMatch()
    {
      private CDOID resourceID;

      public CDOID getResourceID()
      {
        return resourceID;
      }

      public long getTimeStamp()
      {
        return timeStamp;
      }

      public CDOID getFolderID()
      {
        return folderID;
      }

      public String getName()
      {
        return name;
      }

      public boolean exactMatch()
      {
        return true;
      }

      public int getMaxResults()
      {
        return 1;
      }

      public boolean addResource(CDOID resourceID)
      {
        this.resourceID = resourceID;
        return false;
      }
    };
  }

  protected static <T> Set<T> set(T... elements)
  {
    return Collections.unmodifiableSet(new HashSet<T>(Arrays.asList(elements)));
  }

  public CDOID getRootResourceID()
  {
    throw new RuntimeException("Method getRootResourceID not supported");
  }
}
