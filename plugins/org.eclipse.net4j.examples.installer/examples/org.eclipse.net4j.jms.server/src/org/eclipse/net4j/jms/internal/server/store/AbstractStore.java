/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server.store;

import org.eclipse.net4j.jms.server.IStore;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public abstract class AbstractStore extends Lifecycle implements IStore
{
  private String storeType;

  private String instanceID;

  public AbstractStore(String storeType)
  {
    this.storeType = storeType;
  }

  @Override
  public String getStoreType()
  {
    return storeType;
  }

  @Override
  public String getInstanceID()
  {
    return instanceID;
  }

  public void setInstanceID(String instanceID)
  {
    this.instanceID = instanceID;
  }
}
