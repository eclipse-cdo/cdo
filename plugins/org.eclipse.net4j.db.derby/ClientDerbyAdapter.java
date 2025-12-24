/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
