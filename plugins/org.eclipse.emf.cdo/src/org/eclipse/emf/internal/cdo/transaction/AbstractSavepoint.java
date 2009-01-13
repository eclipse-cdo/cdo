/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOUserTransaction;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public abstract class AbstractSavepoint implements CDOSavepoint
{
  private CDOUserTransaction userTransaction;

  private AbstractSavepoint previousSavepoint;

  private AbstractSavepoint nextSavepoint;

  public AbstractSavepoint(CDOUserTransaction transaction, AbstractSavepoint lastSavepoint)
  {
    userTransaction = transaction;
    previousSavepoint = lastSavepoint;
    if (previousSavepoint != null)
    {
      previousSavepoint.setNextSavepoint(this);
    }
  }

  public void setPreviousSavepoint(AbstractSavepoint previousSavepoint)
  {
    this.previousSavepoint = previousSavepoint;
  }

  public void setNextSavepoint(AbstractSavepoint nextSavepoint)
  {
    this.nextSavepoint = nextSavepoint;
  }

  public CDOSavepoint getNextSavepoint()
  {
    return nextSavepoint;
  }

  public CDOSavepoint getPreviousSavepoint()
  {
    return previousSavepoint;
  }

  public AbstractSavepoint getFirstSavePoint()
  {
    return previousSavepoint != null ? previousSavepoint.getFirstSavePoint() : this;
  }

  public CDOUserTransaction getUserTransaction()
  {
    return userTransaction;
  }

  public boolean isValid()
  {
    CDOSavepoint lastSavepoint = getUserTransaction().getLastSavepoint();
    for (CDOSavepoint savepoint = lastSavepoint; savepoint != null; savepoint = savepoint.getPreviousSavepoint())
    {
      if (savepoint == this)
      {
        return true;
      }
    }

    return false;
  }

  public void rollback()
  {
    getUserTransaction().rollback(this);
  }
}
