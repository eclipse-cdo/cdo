/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests.apps;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.data.HugeData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class Profiling extends AbstractConfigTest
{
  public static void main(String[] args) throws Exception
  {
    IManagedContainer container = ContainerUtil.createContainer();
    ContainerUtil.prepareContainer(container);
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    container.registerFactory(new TestSignalProtocol.Factory());
    container.activate();

    TCPUtil.getAcceptor(container, "0.0.0.0:2036");

    ITCPConnector connector = TCPUtil.getConnector(container, "127.0.0.1:2036");
    TestSignalProtocol protocol = new TestSignalProtocol(connector);

    byte[] data = HugeData.getBytes();
    for (int i = 0; i < 10000; i++)
    {
      new ArrayRequest(protocol, data).send();
    }
  }
}
