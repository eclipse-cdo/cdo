/*******************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Flüuege - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.util.exceptions;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class EClassIncompatibleException extends Exception
{

  private static final long serialVersionUID = 1L;

  public EClassIncompatibleException()
  {
    super();
  }

  public EClassIncompatibleException(String s)
  {
    super(s);
  }
}
