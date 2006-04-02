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
package org.eclipse.net4j.util;


public class ImplementationError extends Net4jException
{

  /**
   * 
   */
  private static final long serialVersionUID = 3256718498427318836L;

  public ImplementationError()
  {
    super();
  }

  public ImplementationError(String arg0)
  {
    super(arg0);
  }

  public ImplementationError(Throwable arg0)
  {
    super(arg0);
  }

  public ImplementationError(String arg0, Throwable arg1)
  {
    super(arg0, arg1);
  }

}
