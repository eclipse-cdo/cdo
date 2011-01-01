/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.hsqldb;

import org.hsqldb.jdbc.jdbcDataSource;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class HSQLDBDataSource extends jdbcDataSource
{
  private static final long serialVersionUID = 1L;

  public HSQLDBDataSource()
  {
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("HSQLDBDataSource[database={0}, user={1}]", getDatabase(), getUser()); //$NON-NLS-1$
  }
}
