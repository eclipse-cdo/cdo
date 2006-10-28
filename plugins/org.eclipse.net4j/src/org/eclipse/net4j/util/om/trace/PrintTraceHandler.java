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
package org.eclipse.net4j.util.om.trace;

import org.eclipse.net4j.util.IOUtil;
import org.eclipse.net4j.util.om.OMTraceHandler;
import org.eclipse.net4j.util.om.OMTracer;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class PrintTraceHandler implements OMTraceHandler
{
  public static final PrintTraceHandler CONSOLE = new PrintTraceHandler();

  private PrintStream stream;

  public PrintTraceHandler(PrintStream stream)
  {
    this.stream = stream;
  }

  protected PrintTraceHandler()
  {
    this(IOUtil.OUT());
  }

  public void traced(OMTracer tracer, Class context, Object instance, String msg, Throwable t)
  {
    stream.println("[TRACE] " + msg); //$NON-NLS-1$
    if (t != null)
    {
      IOUtil.print(t, stream);
    }
  }
}
