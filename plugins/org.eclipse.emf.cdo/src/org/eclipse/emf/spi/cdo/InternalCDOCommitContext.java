/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.transaction.CDOCommitContext;

import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

/**
 * Provides a context for a commit operation.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface InternalCDOCommitContext extends CDOCommitContext
{
  public InternalCDOTransaction getTransaction();

  public void preCommit();

  public void postCommit(CommitTransactionResult result);
}
