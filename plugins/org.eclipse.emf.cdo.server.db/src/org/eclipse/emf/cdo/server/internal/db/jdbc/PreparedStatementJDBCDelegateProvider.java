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
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.IJDBCDelegateProvider;
import org.eclipse.emf.cdo.server.internal.db.jdbc.PreparedStatementJDBCDelegate.CachingEnablement;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class PreparedStatementJDBCDelegateProvider implements IJDBCDelegateProvider
{
  public static final String CACHE_STATEMENT_PROPERTY_KEY = "cacheStatements";

  private CachingEnablement cachingEnablement = CachingEnablement.GUESS;

  public PreparedStatementJDBCDelegateProvider()
  {
  }

  public IJDBCDelegate getJDBCDelegate()
  {
    PreparedStatementJDBCDelegate delegate = new PreparedStatementJDBCDelegate();
    delegate.setCachingEnablement(cachingEnablement);
    return delegate;
  }

  public void setProperties(Map<String, String> properties)
  {
    String value = properties.get(CACHE_STATEMENT_PROPERTY_KEY);
    if (value == null)
    {
      value = "GUESS";
    }

    cachingEnablement = CachingEnablement.valueOf(value.toUpperCase());
  }
}
