/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;

/**
 * @author Eike Stepper
 */
public class Bugzilla_259086_Test extends AbstractTransportTest
{
  private static final int SERVER_PROTOCOL_VERSION = 4711;

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = new ManagedContainer();
    Net4jUtil.prepareContainer(container);
    JVMUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    SSLUtil.prepareContainer(container);
    container.registerFactory(new TestSignalProtocol.Factory(SERVER_PROTOCOL_VERSION));
    return container;
  }

  public void testVersionMatch() throws Exception
  {
    startTransport();

    IConnector connector = getConnector();
    connector.setOpenChannelTimeout(2000L);

    new TestSignalProtocol(connector, SERVER_PROTOCOL_VERSION);
  }

  public void testVersionMissing() throws Exception
  {
    startTransport();

    IConnector connector = getConnector();
    connector.setOpenChannelTimeout(2000L);

    try
    {
      new TestSignalProtocol(connector);
      fail("Exception expected"); //$NON-NLS-1$
    }
    catch (Exception expected)
    {
      expected.printStackTrace();
    }
  }

  public void testVersionMismatch() throws Exception
  {
    startTransport();

    IConnector connector = getConnector();
    connector.setOpenChannelTimeout(2000L);

    try
    {
      new TestSignalProtocol(connector, SERVER_PROTOCOL_VERSION - 1);
      fail("Exception expected"); //$NON-NLS-1$
    }
    catch (Exception expected)
    {
      expected.printStackTrace();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class JVM extends Bugzilla_259086_Test
  {
    @Override
    protected boolean useJVMTransport()
    {
      return true;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends Bugzilla_259086_Test
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SSL extends Bugzilla_259086_Test
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return true;
    }
  }
}
