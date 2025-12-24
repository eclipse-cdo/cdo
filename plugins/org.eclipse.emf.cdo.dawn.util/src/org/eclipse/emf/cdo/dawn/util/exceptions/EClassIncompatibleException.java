/*
 * Copyright (c) 2010, 2011, 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fl�uege - initial API and implementation
 */
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
