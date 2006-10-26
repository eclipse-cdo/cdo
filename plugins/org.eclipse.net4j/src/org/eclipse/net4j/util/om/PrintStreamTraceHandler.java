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
package org.eclipse.net4j.util.om;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class PrintStreamTraceHandler implements OMTraceHandler
{
  public static final PrintStreamTraceHandler CONSOLE = new PrintStreamTraceHandler();

  private PrintStream stream;

  public PrintStreamTraceHandler(PrintStream stream)
  {
    this.stream = stream;
  }

  private PrintStreamTraceHandler()
  {
    this(System.out);
  }

  public void traced(OMTracer tracer, Class context, String msg, Throwable t)
  {
    stream.println("[TRACE] " + msg); //$NON-NLS-1$
    if (t != null)
    {
      t.printStackTrace(stream);
    }
  }
}
