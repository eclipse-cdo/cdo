/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.IStoreManager;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public abstract class StoreManager extends Lifecycle implements IStoreManager
{
  private String storeType;

  private String instanceID;

  public StoreManager(String storeType)
  {
    this.storeType = storeType;
  }

  public String getStoreType()
  {
    return storeType;
  }

  public String getInstanceID()
  {
    return instanceID;
  }

  public void setInstanceID(String instanceID)
  {
    this.instanceID = instanceID;
  }
}
