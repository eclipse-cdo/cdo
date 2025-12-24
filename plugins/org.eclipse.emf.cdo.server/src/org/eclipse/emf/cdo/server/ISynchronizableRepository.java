/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

/**
 * A repository with the ability to {@link IRepositorySynchronizer synchronize} its content with another repository.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISynchronizableRepository extends IRepository
{
  public IRepositorySynchronizer getSynchronizer();

  public ISession getReplicatorSession();

  public int getLastReplicatedBranchID();

  public long getLastReplicatedCommitTime();

  /**
   * @since 4.2
   */
  public boolean hasBeenReplicated();

  /**
   * @since 4.1
   */
  public void goOnline();

  /**
   * @since 4.1
   */
  public void goOffline();
}
