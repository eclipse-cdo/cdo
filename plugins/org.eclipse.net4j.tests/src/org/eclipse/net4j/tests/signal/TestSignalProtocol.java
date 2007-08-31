/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class TestSignalProtocol extends SignalProtocol
{
  public static final String PROTOCOL_NAME = "signal.protocol";

  public static final short SIGNAL1 = 1;

  public static final short SIGNAL2 = 2;

  public TestSignalProtocol()
  {
  }

  public String getType()
  {
    return PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case SIGNAL1:
      return new Indication1();
    case SIGNAL2:
      return new Indication2();
    }

    return null;
  }
}
