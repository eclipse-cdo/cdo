/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
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


public class ClientSeparatedServerTopology extends AbstractTopology
{
  private Container serverNet4j;

  private Container net4jServer;

  private Container cdoServer;

  private Container clientNet4j;

  private Container net4jClient;

  private Acceptor acceptor;

  public ClientSeparatedServerTopology()
  {
  }

  public String getName()
  {
    return ITopologyConstants.CLIENT_SEPARATED_SERVER_MODE;
  }

  public void start() throws Exception
  {
    super.start();

    // Start server
    serverNet4j = createContainer("server", NET4J_LOCATION, null);
    net4jServer = createContainer("socket", NET4J_SERVER_LOCATION, serverNet4j);
    cdoServer = createContainer("cdo", CDO_SERVER_LOCATION, net4jServer);

    acceptor = (Acceptor) cdoServer.getBean("acceptor", Acceptor.class);
    acceptor.start();

    // Start client
    clientNet4j = createContainer("client", NET4J_LOCATION, null);
    net4jClient = createContainer("socket", NET4J_CLIENT_LOCATION, clientNet4j);
    createCDOClient("cdo", net4jClient);
  }

  public void stop() throws Exception
  {
    super.stop();

    try
    {
      net4jClient.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      net4jClient = null;
    }

    try
    {
      clientNet4j.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      clientNet4j = null;
    }

    try
    {
      acceptor.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      acceptor = null;
    }

    try
    {
      cdoServer.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      cdoServer = null;
    }

    try
    {
      net4jServer.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      net4jServer = null;
    }

    try
    {
      serverNet4j.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      serverNet4j = null;
    }
  }

  public DataSource getDataSource()
  {
    return (DataSource) cdoServer.getBean("dataSource");
  }
}
