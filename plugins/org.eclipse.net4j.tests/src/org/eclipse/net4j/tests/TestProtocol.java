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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.spi.net4j.Protocol;

import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public final class TestProtocol extends Protocol<CountDownLatch>
{
  public TestProtocol(CountDownLatch counter)
  {
    setInfraStructure(counter);
  }

  public String getType()
  {
    return ServerTestProtocolFactory.TYPE;
  }

  public void handleBuffer(IBuffer buffer)
  {
    IOUtil.OUT().println("BUFFER ARRIVED");
    buffer.release();
    getInfraStructure().countDown();
  }
}
