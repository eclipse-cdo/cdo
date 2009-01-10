/***************************************************************************
 * Copyright (c) 2004 - 2009 Springsite B.V. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.hibernate.internal.teneo;

import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;

import org.w3c.dom.Element;

/**
 * Reads the hibernate mapping file from one or more resource locations and adds them to the configuration.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class TeneoHibernateMappingProviderFactory implements IHibernateMappingProvider.Factory
{
  public static final String TYPE = "teneo";

  public TeneoHibernateMappingProviderFactory()
  {
  }

  public String getType()
  {
    return TYPE;
  }

  public TeneoHibernateMappingProvider create(Element config)
  {
    return new TeneoHibernateMappingProvider();
  }
}
