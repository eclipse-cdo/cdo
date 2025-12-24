/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class AbstractConflictResolver implements CDOConflictResolver
{
  private CDOTransaction transaction;

  public AbstractConflictResolver()
  {
  }

  @Override
  public CDOTransaction getTransaction()
  {
    return transaction;
  }

  @Override
  public void setTransaction(CDOTransaction transaction)
  {
    if (this.transaction != transaction)
    {
      if (this.transaction != null)
      {
        unhookTransaction(this.transaction);
      }

      this.transaction = transaction;

      if (this.transaction != null)
      {
        hookTransaction(this.transaction);
      }
    }
  }

  protected void hookTransaction(CDOTransaction transaction)
  {
  }

  protected void unhookTransaction(CDOTransaction transaction)
  {
  }
}
