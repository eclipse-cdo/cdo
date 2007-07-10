/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.transaction;

/**
 * @author Eike Stepper
 */
public interface ITransaction
{
  public boolean isActive();

  public <R> R execute(ITransactionalOperation<ITransaction, R> operation) throws TransactionException;

  public void commit() throws TransactionException;

  public void rollback() throws TransactionException;
}
