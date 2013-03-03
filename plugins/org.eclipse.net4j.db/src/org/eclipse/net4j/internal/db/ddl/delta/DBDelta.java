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
package org.eclipse.net4j.internal.db.ddl.delta;

import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.delta.IDBDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.internal.db.DBElement;
import org.eclipse.net4j.util.CheckUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class DBDelta extends DBElement implements IDBDelta
{
  private static final long serialVersionUID = 1L;

  private IDBDelta parent;

  private String name;

  private ChangeKind changeKind;

  private Map<String, IDBPropertyDelta<?>> propertyDeltas = new HashMap<String, IDBPropertyDelta<?>>();

  public DBDelta(IDBDelta parent, String name, ChangeKind changeKind)
  {
    CheckUtil.checkArg(name, "name");

    this.parent = parent;
    this.name = name;
    this.changeKind = changeKind;
  }

  /**
   * Constructor for deserialization.
   */
  protected DBDelta()
  {
  }

  public IDBDelta getParent()
  {
    return parent;
  }

  public final String getName()
  {
    return name;
  }

  public final ChangeKind getChangeKind()
  {
    return changeKind;
  }

  public <T> IDBPropertyDelta<T> getPropertyDelta(String name)
  {
    @SuppressWarnings("unchecked")
    IDBPropertyDelta<T> propertyDelta = (IDBPropertyDelta<T>)propertyDeltas.get(name);
    return propertyDelta;
  }

  public <T> T getPropertyValue(String name)
  {
    return getPropertyValue(name, false);
  }

  public <T> T getPropertyValue(String name, boolean old)
  {
    IDBPropertyDelta<T> propertyDelta = getPropertyDelta(name);
    if (old)
    {
      return propertyDelta.getOldValue();
    }

    return propertyDelta.getValue();
  }

  public final Map<String, IDBPropertyDelta<?>> getPropertyDeltas()
  {
    return Collections.unmodifiableMap(propertyDeltas);
  }

  public final void addPropertyDelta(IDBPropertyDelta<?> propertyDelta)
  {
    propertyDeltas.put(propertyDelta.getName(), propertyDelta);
  }

  public static String getName(IDBSchemaElement element, IDBSchemaElement oldElement)
  {
    return oldElement == null ? element.getName() : oldElement.getName();
  }

  public static ChangeKind getChangeKind(Object object, Object oldObject)
  {
    return object == null ? ChangeKind.REMOVED : oldObject == null ? ChangeKind.ADDED : ChangeKind.CHANGED;
  }

  protected static <T extends IDBSchemaElement> void compare(T[] elements, T[] oldElements,
      SchemaElementComparator<T> comparator)
  {
    for (int i = 0; i < elements.length; i++)
    {
      T element = elements[i];
      String name = element.getName();

      T oldElement = findElement(oldElements, name);
      comparator.compare(element, oldElement);
    }

    for (int i = 0; i < oldElements.length; i++)
    {
      T oldElement = oldElements[i];
      String name = oldElement.getName();

      if (findElement(elements, name) == null)
      {
        comparator.compare(null, oldElement);
      }
    }
  }

  private static <T extends IDBSchemaElement> T findElement(T[] elements, String name)
  {
    for (int i = 0; i < elements.length; i++)
    {
      T element = elements[i];
      if (element.getName().equals(name))
      {
        return element;
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public interface SchemaElementComparator<T extends IDBSchemaElement>
  {
    public void compare(T element, T oldElement);
  }
}
