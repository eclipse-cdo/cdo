/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.internal.net4j.util.Value;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class ProtocolFactoryIDImpl extends Value implements ProtocolFactoryID
{
  private static final long serialVersionUID = 1L;

  private ConnectorLocation location;

  private String protocolID;

  public ProtocolFactoryIDImpl(ConnectorLocation location, String protocolID)
  {
    this.location = location;
    this.protocolID = protocolID;
  }

  public ConnectorLocation getLocation()
  {
    return location;
  }

  public String getProtocolID()
  {
    return protocolID;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException
  {
    return this;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof ProtocolFactoryID)
    {
      ProtocolFactoryID that = (ProtocolFactoryID)obj;
      return this.location == that.getLocation() && ObjectUtil.equals(this.protocolID, that.getProtocolID());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return location.hashCode() ^ protocolID.hashCode();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}]", location, protocolID);
  }
}
