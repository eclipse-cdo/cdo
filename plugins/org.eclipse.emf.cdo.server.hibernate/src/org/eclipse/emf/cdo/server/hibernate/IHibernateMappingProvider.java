/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate;

import org.w3c.dom.Element;

/**
 * A mapping provider adds a hibernate mapping to a hibernate configuration object.
 *
 * @author Martin Taal
 */
public interface IHibernateMappingProvider
{
  /**
   * @return the mapping as a String.
   * @since 3.0
   */
  public String getMapping();

  /**
   * Sets the Store in the mapping provider, is called before addMapping.
   */
  public void setHibernateStore(IHibernateStore hibernateStore);

  /**
   * Creates {@link IHibernateMappingProvider mapping provider} instances.
   *
   * @author Eike Stepper
   * @since 2.0
   */
  public interface Factory
  {
    /**
     * Returns the type of the mapping providers this factory can create.
     */
    public String getType();

    /**
     * Creates a Hibernate mapping provider from the given element of the <code>cdo-server.xml</code> configuration
     * file.
     */
    public IHibernateMappingProvider create(Element config);
  }
}
