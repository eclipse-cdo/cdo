/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

/**
 * @author Eike Stepper
 */
public abstract class AbstractClientManager<T extends ILifecycle> implements ILifecycle
{
  protected final T delegate;

  protected CDONet4jSession clientSession;

  protected InternalSession serverSession;

  private LifecycleState lifecycleState = LifecycleState.INACTIVE;

  public AbstractClientManager(T delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  @Override
  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  @Override
  public void addListener(IListener listener)
  {
    delegate.addListener(listener);
  }

  @Override
  public void removeListener(IListener listener)
  {
    delegate.removeListener(listener);
  }

  @Override
  public LifecycleState getLifecycleState()
  {
    return lifecycleState;
  }

  @Override
  public boolean isActive()
  {
    return lifecycleState == LifecycleState.ACTIVE;
  }

  @Override
  public void activate() throws LifecycleException
  {
    lifecycleState = LifecycleState.ACTIVE;
  }

  @Override
  public Exception deactivate()
  {
    lifecycleState = LifecycleState.INACTIVE;
    clientSession = null;
    serverSession = null;
    return null;
  }

  protected final void initServerSession(CDONet4jSession clientSession)
  {
    this.clientSession = clientSession;

    InternalRepository repository = getRepository(delegate);
    InternalSessionManager sessionManager = repository.getSessionManager();
    serverSession = sessionManager.getSession(clientSession.getSessionID());
  }

  protected abstract InternalRepository getRepository(T delegate);
}
