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
package org.eclipse.emf.cdo.internal.server.syncing;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

/**
 * @author Eike Stepper
 */
public class OfflineClone extends SynchronizableRepository
{
  public OfflineClone()
  {
    setState(OFFLINE);
  }

  @Override
  public final Type getType()
  {
    return CLONE;
  }

  @Override
  public final void setType(Type type)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    CDOBranch branch = transaction.getBranch();
    if (branch.isLocal())
    {
      return createNormalCommitContext(transaction);
    }
  
    if (getState() != ONLINE)
    {
      return createBranchingCommitContext(transaction, branch);
    }
  
    return createWriteThroughCommitContext(transaction);
  }
}
