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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalStore extends IStore, ILifecycle
{
  public void setRepository(IRepository repository);

  public void setRevisionTemporality(RevisionTemporality revisionTemporality);

  public void setRevisionParallelism(RevisionParallelism revisionParallelism);

  public CDOIDMetaRange getNextMetaIDRange(int count);

  public int getNextBranchID();

  public long getLastCommitTime();

  public void setLastCommitTime(long lastCommitTime);
}
