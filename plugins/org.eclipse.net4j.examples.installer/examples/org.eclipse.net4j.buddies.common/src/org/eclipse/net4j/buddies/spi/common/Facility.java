/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.spi.common;

import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.IFacility;
import org.eclipse.net4j.buddies.common.IMessage;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public abstract class Facility extends Lifecycle implements IFacility
{
  private String type;

  private Collaboration collaboration;

  public Facility(String type)
  {
    this.type = type;
  }

  @Override
  public String getType()
  {
    return type;
  }

  @Override
  public Collaboration getCollaboration()
  {
    return collaboration;
  }

  @Override
  public void setCollaboration(ICollaboration collaboration)
  {
    this.collaboration = (Collaboration)collaboration;
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public void sendMessage(IMessage message)
  {
    collaboration.sendMessage(collaboration.getID(), type, message);
  }

  public abstract void handleMessage(IMessage message);
}
