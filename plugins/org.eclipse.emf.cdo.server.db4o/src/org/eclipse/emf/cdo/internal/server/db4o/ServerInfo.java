/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.db4o;

import java.util.HashMap;
import java.util.Map;

/**
 * Object meant to stores IStore related information
 * 
 * @author Victor Roldan Betancort
 */
public class ServerInfo
{
  private boolean isFirstTime;

  private long creationTime;

  private Map<String, String> properties = new HashMap<String, String>();

  public boolean isFirstTime()
  {
    return isFirstTime;
  }

  public void setFirstTime(boolean isFirstTime)
  {
    this.isFirstTime = isFirstTime;
  }

  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

}
