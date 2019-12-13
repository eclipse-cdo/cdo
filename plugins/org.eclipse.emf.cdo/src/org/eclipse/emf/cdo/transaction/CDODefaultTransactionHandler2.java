/*
 * Copyright (c) 2010-2012, 2014, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

/**
 * An empty default implementation of {@link CDOTransactionHandler2}.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class CDODefaultTransactionHandler2 implements CDOTransactionHandler2
{
  protected CDODefaultTransactionHandler2()
  {
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    // Do nothing
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void rolledBackTransaction(CDOTransaction transaction)
  {
    // Do nothing
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    // Do nothing
  }
}
