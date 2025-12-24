/*
 * Copyright (c) 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOSavepoint;

import org.eclipse.emf.spi.cdo.InternalCDOXASavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction;

import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOXASavepointImpl extends CDOUserSavepointImpl implements InternalCDOXASavepoint
{
  private List<CDOSavepoint> savepoints;

  public CDOXASavepointImpl(InternalCDOXATransaction transaction, InternalCDOXASavepoint lastSavepoint)
  {
    super(transaction, lastSavepoint);
  }

  @Override
  public InternalCDOXATransaction getTransaction()
  {
    return (InternalCDOXATransaction)super.getTransaction();
  }

  @Override
  public InternalCDOXASavepoint getFirstSavePoint()
  {
    return (InternalCDOXASavepoint)super.getFirstSavePoint();
  }

  @Override
  public InternalCDOXASavepoint getNextSavepoint()
  {
    return (InternalCDOXASavepoint)super.getNextSavepoint();
  }

  @Override
  public InternalCDOXASavepoint getPreviousSavepoint()
  {
    return (InternalCDOXASavepoint)super.getPreviousSavepoint();
  }

  @Override
  public List<CDOSavepoint> getSavepoints()
  {
    return savepoints;
  }

  @Override
  public void setSavepoints(List<CDOSavepoint> savepoints)
  {
    this.savepoints = savepoints;
  }

  @Override
  public void rollback()
  {
    getTransaction().rollback(this);
  }
}
