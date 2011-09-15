/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.derby;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;

import javax.sql.DataSource;

import java.sql.Driver;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EmbeddedDerbyAdapter extends DerbyAdapter
{
  public static final String NAME = "derby-embedded"; //$NON-NLS-1$

  public EmbeddedDerbyAdapter()
  {
    super(NAME);
  }

  public Driver getJDBCDriver()
  {
    return new EmbeddedDriver();
  }

  public DataSource createJDBCDataSource()
  {
    return new EmbeddedDataSource();
  }
}
