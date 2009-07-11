/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.id;

import org.eclipse.emf.cdo.common.id.CDOIDObject;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalCDOIDObject extends CDOIDObject
{
  public SubType getSubType();

  /**
   * @author Eike Stepper
   */
  public enum SubType
  {
    LONG, STRING, LONG_WITH_ECLASS, STRING_WITH_ECLASS
  }
}
