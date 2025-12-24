/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * A {@link ConcurrentAccessException concurrent access exception} that indicates an attempt of the local transaction to introduce a <i>containment cycle</i>.
 * A containment cycle is an effect of a network race condition between two transactions that commit changes to possibly disjunct sets of objects. As a result
 * the overall tree structure of the model would be destroyed in a way that the tree root would no longer be reachable from objects involved in the containment cycle.
 * Commits that attempt to introduce containment cycles are detected by the repository and canceled. Note that locking all involved <b>dirty</b> objects
 * does not properly address the problem because the involved container objects may not be dirty.
 * <p>
 * It's usually possible and adequate to {@link CDOTransaction#rollback() rollback} the transaction, <i>replay</i> the model modifications and
 * commit the transaction again (optimistic strategy). Pessimistic locks on the dirty objects can not safely avoid the problem; you must expect this exception to occur.
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ContainmentCycleException extends ConcurrentAccessException
{
  private static final long serialVersionUID = 1L;

  public ContainmentCycleException(String message)
  {
    super(message);
  }
}
