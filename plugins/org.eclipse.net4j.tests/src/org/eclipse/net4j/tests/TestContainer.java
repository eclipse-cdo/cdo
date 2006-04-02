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
package org.eclipse.net4j.test;


import org.eclipse.net4j.core.Connector;
import org.eclipse.net4j.spring.Container;
import org.eclipse.net4j.spring.impl.ContainerImpl;

import org.springframework.beans.BeansException;


public class TestContainer extends ContainerImpl
{
  private Connector connector;

  protected TestContainer(String baseResourcePath, String configLocation, String name,
      Container parent, ClassLoader classLoader) throws BeansException
  {
    super(baseResourcePath, configLocation, name, parent, classLoader);
  }

  protected TestContainer(String baseResourcePath, String[] configLocations, String name,
      Container parent, ClassLoader classLoader) throws BeansException
  {
    super(baseResourcePath, configLocations, name, parent, classLoader);
  }

  public Connector getConnector()
  {
    if (connector == null)
    {
      connector = (Connector) getBean("connector");

      try
      {
        connector.start();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        return null;
      }
    }

    return connector;
  }


  public static class SocketClient extends TestContainer
  {
    public SocketClient(String name) throws BeansException
    {
      super(".", "META-INF/socketClient.xml", name, null, null);
    }
  }


  public static class SocketServer extends TestContainer
  {
    public SocketServer() throws BeansException
    {
      super(".", "META-INF/socketServer.xml", "server", null, null);
    }
  }


  public static class Embedded extends TestContainer
  {
    public Embedded() throws BeansException
    {
      super(".", "META-INF/embedded.xml", "embedded", null, null);
    }
  }
}
