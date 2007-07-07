/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.ITransaction;

import org.eclipse.net4j.internal.util.store.StoreManager;

/**
 * @author Eike Stepper
 */
public class EmptyStoreManager extends StoreManager<ITransaction>
{
  private static final String STORE_TYPE = "EMPTY";

  public EmptyStoreManager()
  {
    super(STORE_TYPE);
    setInstanceID(STORE_TYPE);
  }

  public ITransaction startTransaction()
  {
    return new EmptyStoreTransaction(this);
  }

  public void commitTransaction(ITransaction transaction)
  {
  }

  public void rollbackTransaction(ITransaction transaction)
  {
  }
}
