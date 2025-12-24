/*
 * Copyright (c) 2011, 2012, 2014, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

/**
 * An empty default implementation of {@link CDOTransactionHandler3}.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class CDODefaultTransactionHandler3 implements CDOTransactionHandler3
{
  protected CDODefaultTransactionHandler3()
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

  @Override
  @Deprecated
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    // Do nothing
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext, CDOCommitInfo result)
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
}
