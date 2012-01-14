/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.internal.rcp;

import org.gastro.rcp.IConfiguration;

import java.util.Date;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class Configuration implements IConfiguration
{
  public static final Configuration INSTANCE = new Configuration();

  private Properties properties;

  private Configuration()
  {
  }

  public Properties getProperties()
  {
    return properties;
  }

  public void setProperties(Properties properties)
  {
    this.properties = properties;
  }

  public String getPerspective()
  {
    return properties.getProperty("perspective");
  }

  public String getStation()
  {
    return properties.getProperty("station");
  }

  public String getServer()
  {
    return properties.getProperty("server");
  }

  public String getRepository()
  {
    return properties.getProperty("repository");
  }

  public String getRestaurant()
  {
    return properties.getProperty("restaurant");
  }

  public Date getBusinessDay()
  {
    return new Date();
  }
}
