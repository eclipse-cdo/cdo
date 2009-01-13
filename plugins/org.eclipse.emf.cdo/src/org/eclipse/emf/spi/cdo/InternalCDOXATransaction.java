/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalTempImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDOXATransaction extends CDOXATransaction
{
  public InternalCDOXACommitContext getCommitContext(CDOTransaction transaction);

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public interface InternalCDOXACommitContext extends Callable<Object>, CDOIDProvider, InternalCDOCommitContext
  {
    public InternalCDOXATransaction getTransactionManager();

    public Map<CDOIDExternalTempImpl, InternalCDOTransaction> getRequestedIDs();

    public CommitTransactionResult getResult();
  }
}
