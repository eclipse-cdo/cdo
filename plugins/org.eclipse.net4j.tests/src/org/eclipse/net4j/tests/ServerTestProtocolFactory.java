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

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class ServerTestProtocolFactory extends ServerProtocolFactory
{
  public static final String TYPE = "test.protocol";

  private CountDownLatch counter;

  public ServerTestProtocolFactory(CountDownLatch counter)
  {
    super(TYPE);
    this.counter = counter;
  }

  public TestProtocol create(String description) throws ProductCreationException
  {
    return new TestProtocol(counter);
  }
}
