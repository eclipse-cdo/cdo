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
public class MonitorAlreadyBegunException extends MonitorException
{
  private static final long serialVersionUID = 1L;

  public MonitorAlreadyBegunException()
  {
  }

  public MonitorAlreadyBegunException(String s)
  {
    super(s);
  }

  public MonitorAlreadyBegunException(Throwable cause)
  {
    super(cause);
  }

  public MonitorAlreadyBegunException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
