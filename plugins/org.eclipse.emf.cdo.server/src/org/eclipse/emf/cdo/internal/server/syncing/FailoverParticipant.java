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

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

/**
 * @author Eike Stepper
 */
public class FailoverParticipant extends SynchronizableRepository
{
  public FailoverParticipant()
  {
    setState(State.OFFLINE);
  }

  @Override
  public void setType(Type type)
  {
    checkArg(type == CDOCommonRepository.Type.MASTER || type == CDOCommonRepository.Type.BACKUP,
        "Type must be MASTER or BACKUP");
    super.setType(type);
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    if (getType() == CDOCommonRepository.Type.BACKUP)
    {
      if (transaction.getSession() != getReplicatorSession())
      {
        throw new IllegalStateException(
            "Only the repository synchronizer is allowed to commit transactions to a backup repository");
      }
    }

    return super.createCommitContext(transaction);
  }
}
