/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.repositories;

import java.io.File;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RemoteCDORepository extends CDORepositoryImpl
{
  public static final String PROP_CONNECTOR_TYPE = "connectorType";

  public static final String PROP_CONNECTOR_DESCRIPTION = "connectorDescription";

  public static final String PROP_AUTHENTICATING = "authenticating";

  private String connectorType;

  private String connectorDescription;

  private boolean authenticating;

  public RemoteCDORepository()
  {
  }

  @Override
  public final boolean isRemote()
  {
    return true;
  }

  @Override
  public final boolean isClone()
  {
    return false;
  }

  @Override
  public final boolean isLocal()
  {
    return false;
  }

  @Override
  public final String getConnectorType()
  {
    return connectorType;
  }

  @Override
  public final String getConnectorDescription()
  {
    return connectorDescription;
  }

  @Override
  public final boolean isAuthenticating()
  {
    return authenticating;
  }

  @Override
  public String getURI()
  {
    return connectorType + "://" + connectorDescription + "/" + getName();
  }

  @Override
  protected void init(File folder, String type, Properties properties)
  {
    super.init(folder, type, properties);
    connectorType = properties.getProperty(PROP_CONNECTOR_TYPE);
    connectorDescription = properties.getProperty(PROP_CONNECTOR_DESCRIPTION);
    authenticating = Boolean.parseBoolean(properties.getProperty(PROP_AUTHENTICATING, Boolean.TRUE.toString()));
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_CONNECTOR_TYPE, connectorType);
    properties.setProperty(PROP_CONNECTOR_DESCRIPTION, connectorDescription);
    properties.setProperty(PROP_AUTHENTICATING, Boolean.toString(authenticating));
  }
}
