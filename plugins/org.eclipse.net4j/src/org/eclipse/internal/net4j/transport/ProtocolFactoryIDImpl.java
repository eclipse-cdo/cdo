/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.transport.Connector.Type;
import org.eclipse.net4j.util.ObjectUtil;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class ProtocolFactoryIDImpl implements ProtocolFactoryID, Cloneable, Serializable
{
  private static final long serialVersionUID = 1L;

  private Type type;

  private String protocolID;

  public ProtocolFactoryIDImpl(Type type, String protocolID)
  {
    this.type = type;
    this.protocolID = protocolID;
  }

  public Type getType()
  {
    return type;
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
      return this.type == that.getType() && ObjectUtil.equals(this.protocolID, that.getProtocolID());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return type.hashCode() ^ protocolID.hashCode();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}]", type, protocolID);
  }
}
