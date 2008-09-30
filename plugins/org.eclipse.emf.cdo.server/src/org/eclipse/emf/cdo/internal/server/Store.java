/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.model.resource.CDOPathFeature;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class Store extends Lifecycle implements IStore
{
  private String type;

  // Exclude from dump due to contained db password
  @ExcludeFromDump
  private Map<String, String> properties;

  private IRepository repository;

  private long lastMetaID;

  private CDOPathFeature resourcePathFeature;

  public Store(String type)
  {
    this.type = type;
  }

  public String getStoreType()
  {
    return type;
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

  public IRepository getRepository()
  {
    return repository;
  }

  public void setRepository(IRepository repository)
  {
    this.repository = repository;
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
  public CDOPathFeature getResourcePathFeature()
  {
    return resourcePathFeature;
  }

  public final IStoreReader getReader(ISession session)
  {
    IStoreReader reader = null;
    StoreAccessorPool pool = getReaderPool(session, false);
    if (pool != null)
    {
      reader = (IStoreReader)pool.removeStoreAccessor();
    }

    if (reader == null && session != null)
    {
      CDOProtocolView[] views = session.getViews();
      for (CDOProtocolView view : views)
      {
        pool = getWriterPool((IView)view, false);
        if (pool != null)
        {
          reader = (IStoreReader)pool.removeStoreAccessor();
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

  public final IStoreWriter getWriter(IView view)
  {
    IStoreWriter writer = null;
    StoreAccessorPool pool = getWriterPool(view, false);
    if (pool != null)
    {
      writer = (IStoreWriter)pool.removeStoreAccessor();
    }

    if (writer == null)
    {
      writer = createWriter(view);
      LifecycleUtil.activate(writer);
    }

    return writer;
  }

  protected void releaseAccessor(StoreAccessor accessor)
  {
    StoreAccessorPool pool = null;
    if (accessor instanceof IStoreWriter)
    {
      pool = getWriterPool(accessor.getView(), true);
    }
    else
    {
      pool = getReaderPool(accessor.getSession(), true);
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
   * @since 2.0
   */
  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    resourcePathFeature = repository.getPackageManager().getCDOResourcePackage().getCDOResourceClass()
        .getCDOPathFeature();
  }

  /**
   * Returns a {@link StoreAccessorPool pool} that may contain {@link IStoreReader} instances that are compatible with
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
   * @since 2.0
   */
  protected abstract StoreAccessorPool getReaderPool(ISession session, boolean forReleasing);

  /**
   * Returns a {@link StoreAccessorPool pool} that may contain {@link IStoreWriter} instances that are compatible with
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
   * @since 2.0
   */
  protected abstract StoreAccessorPool getWriterPool(IView view, boolean forReleasing);

  /**
   * Creates and returns a <b>new</b> {@link IStoreReader} instance. The caller of this method is responsible for
   * {@link Lifecycle#activate() activating} the new instance.
   */
  protected abstract IStoreReader createReader(ISession session);

  /**
   * Creates and returns a <b>new</b> {@link IStoreWriter} instance. The caller of this method is responsible for
   * {@link Lifecycle#activate() activating} the new instance.
   */
  protected abstract IStoreWriter createWriter(IView view);
}
