/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Transaction extends View implements InternalTransaction
{
  public Transaction(InternalSession session, int viewID)
  {
    super(session, viewID);
  }

  @Override
  public Type getViewType()
  {
    return CDOCommonView.Type.TRANSACTION;
  }

  public int getTransactionID()
  {
    return getViewID();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Transaction[{0}]", getTransactionID()); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public InternalCommitContext createCommitContext()
  {
    checkOpen();
    return new TransactionCommitContextImpl(this);
  }

  /**
   * For tests only.
   * 
   * @since 2.0
   */
  public InternalCommitContext testCreateCommitContext(final long timeStamp)
  {
    checkOpen();
    return new TransactionCommitContextImpl(this)
    {
      @Override
      protected long createTimeStamp()
      {
        return timeStamp;
      }
    };
  }

  private void checkOpen()
  {
    if (isClosed())
    {
      throw new IllegalStateException("View closed"); //$NON-NLS-1$
    }
  }
}
