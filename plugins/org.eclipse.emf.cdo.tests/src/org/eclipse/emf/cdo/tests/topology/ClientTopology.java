/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.topology;


import org.eclipse.net4j.spring.Container;

import javax.sql.DataSource;


public class ClientTopology extends AbstractTopology
{
  private Container net4j;

  private Container net4jClient;

  public ClientTopology()
  {
  }

  public void start() throws Exception
  {
    super.start();

    // Start client
    net4j = createContainer("net4j", NET4J_LOCATION, null);
    net4jClient = createContainer("client", NET4J_CLIENT_LOCATION, net4j);
    createCDOClient("cdo", net4jClient);
  }

  public void stop() throws Exception
  {
    super.stop();

    //Stop client
    net4jClient.stop();
    net4j.stop();
  }

  public DataSource getDataSource()
  {
    return null;
  }
}
