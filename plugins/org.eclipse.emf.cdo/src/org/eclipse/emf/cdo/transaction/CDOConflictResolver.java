/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver;

import java.util.List;
import java.util.Map;

/**
 * A strategy used to customize the default conflict resolution behaviour of {@link CDOTransaction transactions}.
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
   * <p>
   * Depending on the decisions taken to resolve the conflict, it may be necessary to adjust the notifications that will
   * be sent to the adapters in the current transaction. This can be achieved by adjusting the {@link CDORevisionDelta}
   * in <code>deltas</code>.
   * <p>
   * The implementor might want to use/extend {@link AbstractObjectConflictResolver}.
   */
  public void resolveConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      List<CDORevisionDelta> deltas);
}
