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
import org.eclipse.net4j.db.ddl.delta.IDBFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.spi.db.DBNamedElement;
import org.eclipse.net4j.spi.db.DBSchemaElement;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class DBDelta extends DBNamedElement implements IDBDelta
{
  private static final long serialVersionUID = 1L;

  private DBDelta parent;

  private ChangeKind changeKind;

  private Map<String, IDBPropertyDelta<?>> propertyDeltas = new HashMap<String, IDBPropertyDelta<?>>();

  public DBDelta(DBDelta parent, String name, ChangeKind changeKind)
  {
    super(name);
    this.parent = parent;
    this.changeKind = changeKind;
  }

  /**
   * Constructor for deserialization.
   */
  protected DBDelta()
  {
  }

  public DBDelta getParent()
  {
    return parent;
  }

  public final ChangeKind getChangeKind()
  {
    return changeKind;
  }

  public <T> DBPropertyDelta<T> getPropertyDelta(String name)
  {
    name = name(name);

    @SuppressWarnings("unchecked")
    DBPropertyDelta<T> propertyDelta = (DBPropertyDelta<T>)propertyDeltas.get(name);
    return propertyDelta;
  }

  public <T> T getPropertyValue(String name)
  {
    return getPropertyValue(name, false);
  }

  public <T> T getPropertyValue(String name, boolean old)
  {
    IDBPropertyDelta<T> propertyDelta = getPropertyDelta(name);
    if (propertyDelta == null)
    {
      return null;
    }

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

      T oldElement = DBSchemaElement.findElement(oldElements, name);
      comparator.compare(element, oldElement);
    }

    for (int i = 0; i < oldElements.length; i++)
    {
      T oldElement = oldElements[i];
      String name = oldElement.getName();

      if (DBSchemaElement.findElement(elements, name) == null)
      {
        comparator.compare(null, oldElement);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface SchemaElementComparator<T extends IDBSchemaElement>
  {
    public void compare(T element, T oldElement);
  }

  /**
   * @author Eike Stepper
   */
  public static final class PositionComparator implements Comparator<IDBDelta>
  {
    public int compare(IDBDelta delta1, IDBDelta delta2)
    {
      int v1 = getValue(delta1);
      int v2 = getValue(delta2);
      return v2 - v1;
    }

    private Integer getValue(IDBDelta delta)
    {
      Integer value = delta.getPropertyValue(IDBFieldDelta.POSITION_PROPERTY);
      if (value == null)
      {
        return 0;
      }

      return value;
    }
  }
}
