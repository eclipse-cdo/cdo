/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.IJDBCDelegateProvider;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class StatementJDBCDelegateProvider implements IJDBCDelegateProvider
{
  public StatementJDBCDelegateProvider()
  {
  }

  public IJDBCDelegate getJDBCDelegate()
  {
    return new StatementJDBCDelegate();
  }

  public void setProperties(Map<String, String> properties)
  {
    // ignore -- no properties for this delegate
  }
}
