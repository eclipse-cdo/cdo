/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;

import java.util.concurrent.Callable;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOViewSet extends CDOViewSet, Adapter, IExecutorServiceProvider
{
  public void add(InternalCDOView view);

  public void remove(InternalCDOView view);

  /**
   * @since 4.5
   */
  public void remapView(InternalCDOView view);

  /**
   * @since 4.4
   */
  @Override
  public InternalCDOView resolveView(URI viewURI);

  @Override
  @Deprecated
  public InternalCDOView resolveView(String repositoryUUID);

  /**
   * @since 4.1
   */
  public <V> V executeWithoutNotificationHandling(Callable<V> callable);
}
