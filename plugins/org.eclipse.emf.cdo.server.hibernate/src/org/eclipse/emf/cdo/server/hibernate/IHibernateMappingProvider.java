/***************************************************************************
 * Copyright (c) 2004 - 2008 Martin Taal and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.hibernate;

import java.util.Collection;
import java.util.Properties;

/**
 * Provides or generates a hibernate mapping on the basis of a set of EPackages and properties. An example of a
 * hibernate mapping provider is Teneo, another provider can read the mapping from a hbm file based on the file location
 * passed as a property.
 * 
 * @author Martin Taal
 */
public interface IHibernateMappingProvider
{

  // the passed modelObjects collection is defined as a collection of Objects
  // to prevent binary dependency on emf.
  public String provideMapping(Collection<Object> modelObjects, Properties properties);
}
