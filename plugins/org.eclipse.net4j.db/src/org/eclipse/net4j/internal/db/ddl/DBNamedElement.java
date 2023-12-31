/*
 * Copyright (c) 2013, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.ddl.IDBNamedElement;
import org.eclipse.net4j.spi.db.ddl.InternalDBNamedElement;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.util.io.IORuntimeException;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @since 4.2
 * @author Eike Stepper
 */
public abstract class DBNamedElement extends DBElement implements InternalDBNamedElement
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

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj)
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
  public int hashCode()
  {
    return name.hashCode();
  }

  @Override
  public String toString()
  {
    return name;
  }

  @Override
  public String dumpToString()
  {
    try
    {
      CharArrayWriter writer = new CharArrayWriter();
      dump(writer);
      return writer.toString();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  public final void dump()
  {
    try
    {
      OutputStreamWriter writer = new OutputStreamWriter(System.out);
      dump(writer);
      writer.flush();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static <E extends IDBNamedElement> E findElement(InternalDBSchema schema, E[] elements, String name)
  {
    for (int i = 0; i < elements.length; i++)
    {
      E element = elements[i];

      if (schema.equalNames(element.getName(), name))
      {
        return element;
      }
    }

    return null;
  }
}
