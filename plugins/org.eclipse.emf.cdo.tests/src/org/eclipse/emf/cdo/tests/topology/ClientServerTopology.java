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


import org.eclipse.net4j.core.Acceptor;
import org.eclipse.net4j.spring.Container;

import javax.sql.DataSource;


public class ClientServerTopology extends AbstractTopology
{
  private Container net4j;

  private Container net4jServer;

  private Container cdoServer;

  private Container net4jClient;

  private Acceptor acceptor;

  public ClientServerTopology()
  {
  }

  public void start() throws Exception
  {
    super.start();
    net4j = createContainer("net4j", NET4J_LOCATION, null);

    // Start server
    net4jServer = createContainer("server", NET4J_SERVER_LOCATION, net4j);
    cdoServer = createContainer("cdo", CDO_SERVER_LOCATION, net4jServer);

    acceptor = (Acceptor) cdoServer.getBean("acceptor", Acceptor.class);
    acceptor.start();

    // Start client
    net4jClient = createContainer("client", NET4J_CLIENT_LOCATION, net4j);
    createCDOClient("cdo", net4jClient);
  }

  public void stop() throws Exception
  {
    super.stop();

    //Stop client
    net4jClient.stop();

    //Stop server
    acceptor.stop();
    acceptor = null;

    cdoServer.stop();
    net4jServer.stop();

    net4j.stop();
  }

  public DataSource getDataSource()
  {
    return (DataSource) cdoServer.getBean("dataSource");
  }
}
