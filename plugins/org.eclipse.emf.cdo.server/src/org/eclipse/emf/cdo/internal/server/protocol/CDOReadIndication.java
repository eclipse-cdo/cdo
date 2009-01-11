/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.server.StoreThreadLocal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;

/**
 * @author Eike Stepper
 */
public abstract class CDOReadIndication extends CDOServerIndication
{
  public CDOReadIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    try
    {
      Session session = getSession();
      StoreThreadLocal.setSession(session);
      super.execute(in, out);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }
}
