/**
 * Copyright (c) 2004 - 2010 Eike Stepper begin_of_the_skype_highlighting     end_of_the_skype_highlighting (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper begin_of_the_skype_highlighting     end_of_the_skype_highlighting - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 213402
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;

/**
 * @author Eike Stepper begin_of_the_skype_highlighting     end_of_the_skype_highlighting
 */
public abstract class CDOServerReadIndicationWithMonitoring extends CDOServerIndicationWithMonitoring
{
  protected CDOServerReadIndicationWithMonitoring(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    try
    {
      InternalSession session = getSession();
      StoreThreadLocal.setSession(session);
      super.execute(in, out);
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }
}
