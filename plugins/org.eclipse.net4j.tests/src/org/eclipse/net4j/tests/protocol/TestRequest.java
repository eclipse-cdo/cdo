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


import org.eclipse.net4j.core.impl.AbstractRequestWithConfirmation;


public class TestRequest extends AbstractRequestWithConfirmation
{
  protected Object[] values;

  public TestRequest(Object[] values)
  {
    this.values = values;
  }

  public short getSignalId()
  {
    return Net4jTestProtocol.TEST_SIGNAL;
  }

  public void request()
  {
    ValueHelper.transmitValues(getChannel(), values);
  }

  public Object confirm()
  {
    return ValueHelper.receiveValues(getChannel());
  }
}
