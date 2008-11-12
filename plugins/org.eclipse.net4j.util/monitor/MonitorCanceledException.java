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
package org.eclipse.net4j.util.om.monitor;

/**
 * @author Eike Stepper
 */
public class MonitorCanceledException extends MonitorException
{
  private static final long serialVersionUID = 1L;

  public MonitorCanceledException()
  {
  }

  public MonitorCanceledException(String s)
  {
    super(s);
  }

  public MonitorCanceledException(Throwable cause)
  {
    super(cause);
  }

  public MonitorCanceledException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
