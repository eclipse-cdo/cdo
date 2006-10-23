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
package org.eclipse.net4j.util.stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class ExtendedDataOutputStream extends DataOutputStream implements ExtendedDataOutput
{
  public ExtendedDataOutputStream(OutputStream out)
  {
    super(out);
  }

  public void writeByteArray(byte[] b) throws IOException
  {
    writeInt(b.length);
    write(b);
  }
}
