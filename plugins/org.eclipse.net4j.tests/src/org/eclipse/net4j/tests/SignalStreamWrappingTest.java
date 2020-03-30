/*
 * Copyright (c) 2010-2012, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.signal.wrapping.XORStreamWrapperInjector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.data.HugeData;
import org.eclipse.net4j.tests.data.TinyData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.XORStreamWrapper;

import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class SignalStreamWrappingTest extends AbstractConfigTest
{
  private static final int[] KEY = { 1, 2, 3, 4, 5 };

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.addPostProcessor(new XORStreamWrapperInjector(TestSignalProtocol.PROTOCOL_NAME, KEY));
    return container;
  }

  public void testXORStreamWrapping() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      protocol.setStreamWrapper(new XORStreamWrapper(KEY));
      protocol.setTimeout(1000000L);

      byte[] data = HugeData.getBytes();
      byte[] result = new ArrayRequest(protocol, data).send();
      assertEquals(true, Arrays.equals(data, result));
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testXORStreamWrappingWithMultipleSignals() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      protocol.setStreamWrapper(new XORStreamWrapper(KEY));
      protocol.setTimeout(1000000L);

      for (int i = 0; i < 20; i++)
      {
        byte[] data = TinyData.getBytes();
        byte[] result = new ArrayRequest(protocol, data).send();
        assertEquals(true, Arrays.equals(data, result));
      }
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }
}
