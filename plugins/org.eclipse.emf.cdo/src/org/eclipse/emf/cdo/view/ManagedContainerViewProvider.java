/*
 * Copyright (c) 2009, 2011, 2012, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

/**
 * Base class for <code>ManagedContainer</code> based <code>CDOViewProvider</code>
 *
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public abstract class ManagedContainerViewProvider extends AbstractCDOViewProvider
{
  private IManagedContainer container;

  public ManagedContainerViewProvider(IManagedContainer container, String regex, int priority)
  {
    super(regex, priority);
    this.container = container;
  }

  protected IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public int getPriority()
  {
    if (!OMPlatform.INSTANCE.isOSGiRunning())
    {
      return Integer.MIN_VALUE;
    }

    return super.getPriority();
  }
}
