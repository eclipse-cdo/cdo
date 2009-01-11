/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.db;

import java.util.Map;

/**
 * Wraps the creation of JDBCDelegates.
 * 
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IJDBCDelegateProvider
{
  /**
   * Creates and returns a JDBC delegate.
   * <p>
   * This is part of the org.eclipse.emf.cdo.server.db.jdbcDelegateProviders extension point.
   */
  public IJDBCDelegate getJDBCDelegate();

  /**
   * Set configuration properties
   */
  public void setProperties(Map<String, String> properties);
}
