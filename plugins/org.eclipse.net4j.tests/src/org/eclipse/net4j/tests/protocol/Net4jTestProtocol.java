/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.protocol;


import org.eclipse.net4j.core.Indication;
import org.eclipse.net4j.core.Protocol;
import org.eclipse.net4j.core.impl.AbstractProtocol;
import org.eclipse.net4j.util.ImplementationError;


public class Net4jTestProtocol extends AbstractProtocol implements Protocol
{
  public static final String PROTOCOL_NAME = "test";

  public static final short TEST_SIGNAL = 4711;

  public Indication createIndication(short signalId)
  {
    switch (signalId)
    {
      case TEST_SIGNAL:
        return new TestIndication();

      default:
        throw new ImplementationError("Invalid signalId: " + signalId);
    }
  }

  public String getName()
  {
    return PROTOCOL_NAME;
  }

  public int getType()
  {
    return SYMMETRIC;
  }
}
