/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.internal.derby;

import org.apache.derby.jdbc.ClientDataSource;
import org.apache.derby.jdbc.ClientDriver;

import javax.sql.DataSource;

import java.sql.Driver;

/**
 * @author Eike Stepper
 */
public class ClientDerbyAdapter extends DerbyAdapter
{
  public static final String NAME = "derby-client";

  public ClientDerbyAdapter()
  {
    super(NAME);
  }

  public Driver getJDBCDriver()
  {
    return new ClientDriver();
  }

  public DataSource createJDBCDataSource()
  {
    return new ClientDataSource();
  }
}
