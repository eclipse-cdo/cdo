/*
 * Copyright (c) 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfoHandler;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.spi.common.CDORawReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalSynchronizableRepository
    extends ISynchronizableRepository, InternalRepository, CDOReplicationContext, CDORawReplicationContext, CDOLockChangeInfoHandler
{
  @Override
  public InternalRepositorySynchronizer getSynchronizer();

  public void setSynchronizer(InternalRepositorySynchronizer synchronizer);

  @Override
  public InternalSession getReplicatorSession();

  public void setLastReplicatedBranchID(int lastReplicatedBranchID);

  public void setLastReplicatedCommitTime(long lastReplicatedCommitTime);
}
