/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

/**
 * Stores system related information, is used to keep track that CDO is started for the first time. There should only be
 * one SystemInformation object in the database.
 * 
 * @author Martin Taal
 */
public class SystemInformation
{
  private long id = 1;

  private long creationTime;

  private boolean firstTime;

  public boolean isFirstTime()
  {
    return firstTime;
  }

  public void setFirstTime(boolean firstTime)
  {
    this.firstTime = firstTime;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    // on purposely not changing the id
    // this.id = id;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;
  }
}
