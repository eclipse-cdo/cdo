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

import org.eclipse.net4j.util.stream.BufferInputStream;
import org.eclipse.net4j.util.stream.BufferOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class IndicationWithResponse extends StrictSignalReactor
{
  protected IndicationWithResponse()
  {
  }

  @Override
  protected final void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    System.out.println("================ Indicating " + this);
    inputAllowed = true;
    indicating(getDataInputStream());
    inputAllowed = false;

    System.out.println("================ Responding " + this);
    outputAllowed = true;
    responding(getDataOutputStream());
    outputAllowed = false;
    out.flush();
  }

  protected abstract void indicating(DataInputStream in) throws IOException;

  protected abstract void responding(DataOutputStream out) throws IOException;
}
