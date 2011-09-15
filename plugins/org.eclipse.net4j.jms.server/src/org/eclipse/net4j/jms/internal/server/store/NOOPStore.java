/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server.store;

import org.eclipse.net4j.jms.server.IStoreTransaction;

/**
 * @author Eike Stepper
 */
public class NOOPStore extends AbstractStore
{
  private static final String STORE_TYPE = "NOOP"; //$NON-NLS-1$

  public NOOPStore()
  {
    super(STORE_TYPE);
    setInstanceID(STORE_TYPE);
  }

  public IStoreTransaction startTransaction()
  {
    return new NOOPTransaction(this);
  }

  public void commitTransaction(IStoreTransaction transaction)
  {
  }

  public void rollbackTransaction(IStoreTransaction transaction)
  {
  }
}
