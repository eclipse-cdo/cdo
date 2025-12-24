/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.2
 */
public abstract class DelegatingQueryResourcesContext implements QueryResourcesContext
{
  @Override
  public CDOBranch getBranch()
  {
    return getDelegate().getBranch();
  }

  @Override
  public long getTimeStamp()
  {
    return getDelegate().getTimeStamp();
  }

  @Override
  public CDOID getFolderID()
  {
    return getDelegate().getFolderID();
  }

  @Override
  public String getName()
  {
    return getDelegate().getName();
  }

  @Override
  public boolean exactMatch()
  {
    return getDelegate().exactMatch();
  }

  @Override
  public int getMaxResults()
  {
    return getDelegate().getMaxResults();
  }

  @Override
  public boolean addResource(CDOID resourceID)
  {
    return getDelegate().addResource(resourceID);
  }

  protected abstract QueryResourcesContext getDelegate();
}
