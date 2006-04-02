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
package org.eclipse.net4j.test.protocol;


import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;


public class TestIndication extends AbstractIndicationWithResponse
{
  protected Object[] values;

  public TestIndication()
  {
  }

  public short getSignalId()
  {
    return Net4jTestProtocol.TEST_SIGNAL;
  }

  public void indicate()
  {
    values = ValueHelper.receiveValues(getChannel());
  }

  public void respond()
  {
    ValueHelper.transmitValues(getChannel(), values);
  }
}
