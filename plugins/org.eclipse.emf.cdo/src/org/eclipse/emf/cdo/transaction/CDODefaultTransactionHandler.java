/*
 * Copyright (c) 2009-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

/**
 * An empty default implementation of {@link CDOTransactionHandler1} and {@link CDOTransactionHandler2}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class CDODefaultTransactionHandler extends CDODefaultTransactionHandler1 implements CDOTransactionHandler
{
  protected CDODefaultTransactionHandler()
  {
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    handleDefault(transaction);
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    handleDefault(transaction);
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void rolledBackTransaction(CDOTransaction transaction)
  {
    handleDefault(transaction);
  }
}
