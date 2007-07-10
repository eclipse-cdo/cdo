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
package org.eclipse.net4j.internal.util.transaction;

import org.eclipse.net4j.util.transaction.IStoreManager;
import org.eclipse.net4j.util.transaction.IStoreTransaction;

/**
 * @author Eike Stepper
 */
public abstract class StoreTransaction implements IStoreTransaction
{
  private IStoreManager<? extends IStoreTransaction> storeManager;

  public StoreTransaction(IStoreManager<? extends IStoreTransaction> storeManager)
  {
    this.storeManager = storeManager;
  }

  public IStoreManager<? extends IStoreTransaction> getStoreManager()
  {
    return storeManager;
  }
}