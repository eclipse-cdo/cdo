/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Simon McDuff
 * @since 3.0
 */
public class LockManager extends RWLockManager<Object, IView> implements InternalLockManager
{
  private InternalRepository repository;

  @ExcludeFromDump
  private transient IListener sessionListener = new ContainerEventAdapter<IView>()
  {
    @Override
    protected void onRemoved(IContainer<IView> container, IView view)
    {
      unlock(view);
    }

    // @Override
    // protected void notifyOtherEvent(IEvent event)
    // {
    // if (event instanceof ILifecycleEvent)
    // {
    // ILifecycleEvent e = (ILifecycleEvent)event;
    // if (e.getKind() == ILifecycleEvent.Kind.ABOUT_TO_DEACTIVATE)
    // {
    // ISession session = (ISession)e.getSource();
    // for (IView view : session.getElements())
    // {
    // unlock(view);
    // }
    // }
    // }
    // }
  };

  @ExcludeFromDump
  private transient IListener sessionManagerListener = new ContainerEventAdapter<ISession>()
  {
    @Override
    protected void onAdded(IContainer<ISession> container, ISession session)
    {
      session.addListener(sessionListener);
    }

    @Override
    protected void onRemoved(IContainer<ISession> container, ISession session)
    {
      session.removeListener(sessionListener);
    }
  };

  public LockManager()
  {
  }

  public InternalRepository getRepository()
  {
    return repository;
  }

  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;
  }

  public Object getLockEntryObject(Object key)
  {
    LockEntry<Object, IView> lockEntry = getLockEntry(key);
    return lockEntry.getObject();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    getRepository().getSessionManager().addListener(sessionManagerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    ISessionManager sessionManager = getRepository().getSessionManager();
    sessionManager.removeListener(sessionManagerListener);
    for (ISession session : sessionManager.getSessions())
    {
      session.removeListener(sessionListener);
    }

    super.doDeactivate();
  }
}
