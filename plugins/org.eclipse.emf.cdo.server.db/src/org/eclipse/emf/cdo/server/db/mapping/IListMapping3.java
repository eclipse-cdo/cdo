/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

/**
 * Extension interface to {@link IListMapping2}.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public interface IListMapping3 extends IListMapping2
{
  /**
   * @since 4.14
   */
  @Override
  public default ITypeMapping getTypeMapping()
  {
    return null;
  }

  public void setClassMapping(IClassMapping classMapping);
}
