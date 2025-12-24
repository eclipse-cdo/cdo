/*
 * Copyright (c) 2012, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class Bugzilla_259086_Test extends AbstractConfigTest
{
  private static final int SERVER_PROTOCOL_VERSION = 4711;

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
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
}
