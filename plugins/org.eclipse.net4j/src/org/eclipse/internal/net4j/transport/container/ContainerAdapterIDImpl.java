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
package org.eclipse.internal.net4j.transport.container;

import org.eclipse.net4j.transport.container.ContainerAdapterID;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.Value;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class ContainerAdapterIDImpl extends Value implements ContainerAdapterID
{
  private static final long serialVersionUID = 1L;

  private Type type;

  private String name;

  public ContainerAdapterIDImpl(Type type, String name)
  {
    this.type = type;
    this.name = name;
  }

  public Type getType()
  {
    return type;
  }

  public String getName()
  {
    return name;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException
  {
    return new ContainerAdapterIDImpl(type, name);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof ContainerAdapterID)
    {
      ContainerAdapterID that = (ContainerAdapterID)obj;
      return this.type.equals(that.getType()) && ObjectUtil.equals(this.name, that.getName());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return type.hashCode() ^ name.hashCode();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}:{1}", type, name);
  }
}
