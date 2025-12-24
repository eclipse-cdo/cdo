/*
 * Copyright (c) 2007, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.hsqldb;

import org.hsqldb.jdbc.JDBCDataSource;

import java.text.MessageFormat;

/**
 * An {@link JDBCDataSource HSQLDB data source} with a nicer {@link #toString()} method.
 *
 * @author Eike Stepper
 */
public class HSQLDBDataSource extends JDBCDataSource
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
