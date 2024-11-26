/*
 * Copyright (c) 2006-2012, 2020, 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.signal.EntityRequest;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.data.TinyData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.AsyncRequest;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.StringRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * @author Eike Stepper
 */
public class SignalTest extends AbstractConfigTest
{
  public void testInteger() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      int data = 0x0a;
      int result = new IntRequest(protocol, data).send();
      assertEquals(data, result);
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testArray() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      byte[] data = TinyData.getBytes();
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

  public void testAsync() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      OMPlatform.INSTANCE.setDebugging(false);
      protocol = new TestSignalProtocol(getConnector());
      String data = TinyData.getText();
      for (int i = 0; i < 1000; i++)
      {
        msg("Loop " + i); //$NON-NLS-1$
        new AsyncRequest(protocol, data).sendAsync();
        String result = new StringRequest(protocol, data).send();
        assertEquals(data, result);
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

  public void testCloseUnderlyingConnection() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      final ILifecycle lifecycle = protocol;

      config.closeUnderlyingConnection(getServerConnector());
      assertNoTimeout(() -> !lifecycle.isActive());
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testEntities() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      BiConsumer<String, Entity> entityHandler = (name, entity) -> System.out.println(name + " --> " + entity);

      int result = new EntityRequest(protocol, entityHandler, "users", "eike", "rene", "ed").send();
      assertEquals(2, result);
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
