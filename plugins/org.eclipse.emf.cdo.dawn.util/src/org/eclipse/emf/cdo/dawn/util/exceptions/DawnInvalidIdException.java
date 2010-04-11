/*******************************************************************************
 * Copyright (c) 2009 - 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.util.exceptions;

/**
 * @author Martin Fluegge
 */
public class DawnInvalidIdException extends RuntimeException
{
  public DawnInvalidIdException()
  {
    super();
  }

  public DawnInvalidIdException(String s)
  {
    super(s);
  }

}
