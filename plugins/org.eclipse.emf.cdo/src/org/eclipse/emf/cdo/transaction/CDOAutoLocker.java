/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import org.eclipse.net4j.util.WrappedException;

import java.util.concurrent.TimeoutException;

/**
 * A {@link CDOTransactionHandler1 transaction handler} that automatically acquires {@link CDOObject#cdoWriteLock() write locks} when
 * {@link CDOObject objects} are modified.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOAutoLocker extends CDODefaultTransactionHandler1
{
  private long timeout;

  public CDOAutoLocker(long timeout)
  {
    this.timeout = timeout;
  }

  public CDOAutoLocker()
  {
    this(10000);
  }

  @Override
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
  {
    try
    {
      object.cdoWriteLock().lock(timeout);
    }
    catch (TimeoutException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
