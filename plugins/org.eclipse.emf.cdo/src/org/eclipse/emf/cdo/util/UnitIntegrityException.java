/*
 * Copyright (c) 20116 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

/**
 * A {@link DataIntegrityException data integrity exception} that indicates an attempt to move objects between units.
 *
 * @author Eike Stepper
 * @since 4.5
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class UnitIntegrityException extends DataIntegrityException
{
  private static final long serialVersionUID = 1L;

  public UnitIntegrityException(String msg)
  {
    super(msg);
  }
}
