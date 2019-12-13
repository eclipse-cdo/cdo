/*
 * Copyright (c) 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

  @Override
  public String getPerspective()
  {
    return properties.getProperty("perspective");
  }

  @Override
  public String getStation()
  {
    return properties.getProperty("station");
  }

  @Override
  public String getServer()
  {
    return properties.getProperty("server");
  }

  @Override
  public String getRepository()
  {
    return properties.getProperty("repository");
  }

  @Override
  public String getRestaurant()
  {
    return properties.getProperty("restaurant");
  }

  @Override
  public Date getBusinessDay()
  {
    return new Date();
  }
}
