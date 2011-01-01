/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 */
public interface ISynchronizableRepository extends IRepository
{
  public IRepositorySynchronizer getSynchronizer();

  public ISession getReplicatorSession();

  public int getLastReplicatedBranchID();

  public long getLastReplicatedCommitTime();
}
