/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
