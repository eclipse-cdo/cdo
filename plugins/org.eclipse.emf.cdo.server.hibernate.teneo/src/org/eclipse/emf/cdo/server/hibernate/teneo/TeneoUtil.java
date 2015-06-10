/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.internal.teneo.TeneoHibernateMappingProvider;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class TeneoUtil
{
  private TeneoUtil()
  {
  }

  public static IHibernateMappingProvider createMappingProvider()
  {
    return new TeneoHibernateMappingProvider();
  }
}
