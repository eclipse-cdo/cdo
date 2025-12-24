/*
 * Copyright (c) 2008-2012, 2015, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
