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


public class EmbeddedTopology extends AbstractTopology
{
  private Container net4j;

  private Container cdoServer;

  private Container net4jEmbedded;

  public EmbeddedTopology()
  {
  }

  public String getName()
  {
    return ITopologyConstants.EMBEDDED_MODE;
  }

  public void start() throws Exception
  {
    super.start();
    net4j = createContainer("net4j", NET4J_LOCATION, null);
    net4jEmbedded = createContainer("embedded", NET4J_EMBEDDED_LOCATION, net4j);
    cdoServer = createContainer("cdo-server", CDO_SERVER_LOCATION, net4jEmbedded);
    createCDOClient("cdo-client", net4jEmbedded);
  }

  public void stop() throws Exception
  {
    super.stop();
    cdoServer.stop();
    net4jEmbedded.stop();
    net4j.stop();
  }

  public DataSource getDataSource()
  {
    return (DataSource) cdoServer.getBean("dataSource");
  }
}
