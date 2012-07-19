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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public abstract class AbstractProtocolTest extends AbstractTransportTest
{
  protected AbstractProtocolTest()
  {
  }

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.registerFactory(new TestSignalProtocol.Factory());
    return container;
  }
}
