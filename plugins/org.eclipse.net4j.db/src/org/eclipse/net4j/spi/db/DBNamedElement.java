/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.spi.db;

import org.eclipse.net4j.db.IDBNamedElement;
import org.eclipse.net4j.db.ddl.IDBSchemaElement;

/**
 * @author Eike Stepper
 * @since 4.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class DBNamedElement extends DBElement implements IDBNamedElement
{
  private static final long serialVersionUID = 1L;

  private String name;

  public DBNamedElement(String name)
  {
    setName(name);
  }

  /**
   * Constructor for deserialization.
   */
  protected DBNamedElement()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name(name);
  }

  @Override
  public final boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof IDBNamedElement)
    {
      IDBNamedElement that = (IDBNamedElement)obj;
      return name == that.getName();
    }

    return false;
  }

  @Override
  public final int hashCode()
  {
    return name.hashCode();
  }

  @Override
  public String toString()
  {
    return name;
  }

  public static String name(String name)
  {
    return name.toUpperCase().intern();
  }

  public static <T extends IDBSchemaElement> T findElement(T[] elements, String name)
  {
    name = name(name);
    for (int i = 0; i < elements.length; i++)
    {
      T element = elements[i];
      if (element.getName() == name)
      {
        return element;
      }
    }

    return null;
  }
}
