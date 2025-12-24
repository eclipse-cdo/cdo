/*
 * Copyright (c) 2011, 2012, 2020-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.server.StoreThreadLocal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerWriteIndication extends CDOServerIndication
{
  public CDOServerWriteIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    try
    {
      prepareStoreThreadLocal();
      super.execute(in, out);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  protected void prepareStoreThreadLocal()
  {
    StoreThreadLocal.setSession(getSession());

    // The call to setAccessor() must come after the call to setSession() !!!
    StoreThreadLocal.setAccessor(getStore().getWriter(null));
  }
}
