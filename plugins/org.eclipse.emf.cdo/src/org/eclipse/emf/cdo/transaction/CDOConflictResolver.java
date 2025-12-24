/*
 * Copyright (c) 2009-2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;

import java.util.Set;

/**
 * A strategy used to customize the default conflict resolution behavior of {@link CDOTransaction transactions}.
 *
 * @see CDOTransaction.Options#addConflictResolver(CDOConflictResolver)
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOConflictResolver
{
  /**
   * Returns the {@link CDOTransaction transaction} this conflict resolver is associated with.
   */
  public CDOTransaction getTransaction();

  /**
   * Sets the {@link CDOTransaction transaction} this conflict resolver is to be associated with.
   */
  public void setTransaction(CDOTransaction transaction);

  /**
   * Resolves conflicts after remote invalidations arrived for objects that are locally dirty or detached.
   */
  public void resolveConflicts(Set<CDOObject> conflicts);

  /**
   * A mix-in interface for {@link CDOConflictResolver conflict resolvers} that need to know about non-conflicting invalidations.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public interface NonConflictAware extends CDOConflictResolver
  {
    public void handleNonConflict(long updateTime);
  }
}
