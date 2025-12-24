/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

  @Override
  public IStoreTransaction startTransaction()
  {
    return new NOOPTransaction(this);
  }

  @Override
  public void commitTransaction(IStoreTransaction transaction)
  {
  }

  @Override
  public void rollbackTransaction(IStoreTransaction transaction)
  {
  }
}
