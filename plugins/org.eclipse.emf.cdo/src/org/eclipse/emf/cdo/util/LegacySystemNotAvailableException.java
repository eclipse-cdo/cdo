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
package org.eclipse.emf.cdo.util;

/**
 * @author Eike Stepper
 */
public class LegacySystemNotAvailableException extends RuntimeException
{
  public static final String LEGACY_SYSTEM_NOT_AVAILABLE = "Legacy system not available";

  private static final long serialVersionUID = 1L;

  public LegacySystemNotAvailableException()
  {
    super(LEGACY_SYSTEM_NOT_AVAILABLE);
  }
}
