/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.net4j.util;


public class BeanException extends RuntimeException
{
  /**
   * 
   */
  private static final long serialVersionUID = 3979274633562501681L;

  /**
   * 
   */
  public BeanException()
  {
    super();
  }

  /**
   * @param arg0
   */
  public BeanException(String arg0)
  {
    super(arg0);
  }

  /**
   * @param arg0
   */
  public BeanException(Throwable arg0)
  {
    super(arg0);
  }

  /**
   * @param arg0
   * @param arg1
   */
  public BeanException(String arg0, Throwable arg1)
  {
    super(arg0, arg1);
  }
}