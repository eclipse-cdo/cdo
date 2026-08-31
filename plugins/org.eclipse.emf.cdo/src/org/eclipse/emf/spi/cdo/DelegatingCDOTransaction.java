/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * SPI for a transaction wrapper that exposes exactly one delegation level.
 *
 * @author Eike Stepper
 * @since 4.30
 */
public interface DelegatingCDOTransaction extends CDOTransaction
{
  /**
   * Returns the directly wrapped transaction.
   *
   * @return the delegate, without recursively unwrapping it.
   */
  public CDOTransaction getDelegate();

  /**
   * Resolves a wrapper chain to its effective non-delegating transaction.
   * Identity-based cycle detection makes malformed cyclic chains terminate deterministically.
   *
   * @param transaction the transaction or wrapper to resolve.
   * @return the effective transaction, or the last safely reachable wrapper.
   */
  public static CDOTransaction getEffectiveTransaction(CDOTransaction transaction)
  {
    CDOTransaction result = transaction;
    Set<CDOTransaction> visited = Collections.newSetFromMap(new IdentityHashMap<>());

    while (result instanceof DelegatingCDOTransaction && visited.add(result))
    {
      CDOTransaction delegate = ((DelegatingCDOTransaction)result).getDelegate();
      if (delegate == null || delegate == result)
      {
        break;
      }

      result = delegate;
    }

    return result;
  }
}
