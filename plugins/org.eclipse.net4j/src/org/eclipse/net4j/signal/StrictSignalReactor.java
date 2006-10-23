/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author Eike Stepper
 */
abstract class StrictSignalReactor extends SignalReactor
{
  boolean inputAllowed;

  boolean outputAllowed;

  protected StrictSignalReactor()
  {
  }

  @Override
  protected final DataInputStream getDataInputStream()
  {
    if (!inputAllowed)
    {
      throw new IllegalStateException("Input not allowed");
    }

    return super.getDataInputStream();
  }

  @Override
  protected final DataOutputStream getDataOutputStream()
  {
    if (!outputAllowed)
    {
      throw new IllegalStateException("Output not allowed");
    }

    return super.getDataOutputStream();
  }
}
