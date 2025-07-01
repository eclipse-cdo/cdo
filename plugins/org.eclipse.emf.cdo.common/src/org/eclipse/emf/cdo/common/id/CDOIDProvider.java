/*
 * Copyright (c) 2008-2012, 2015, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.id;

/**
 * Provides the {@link CDOID IDs} of passed objects.
 *
 * @author Eike Stepper
 */
@FunctionalInterface
public interface CDOIDProvider
{
  /**
   * @since 3.0
   */
  public static final CDOIDProvider NOOP = new CDOIDProvider()
  {
    @Override
    public CDOID provideCDOID(Object id)
    {
      return (CDOID)id;
    }
  };

  public CDOID provideCDOID(Object idOrObject);
}
