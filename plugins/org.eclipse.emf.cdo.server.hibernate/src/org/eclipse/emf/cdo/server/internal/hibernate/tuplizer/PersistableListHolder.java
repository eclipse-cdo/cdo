/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.collection.spi.PersistentCollection;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps mappings from object/feature combinations to a hibernate persistable list. This works because the write action
 * is done in one thread.
 *
 * @author Martin Taal
 */
public class PersistableListHolder
{
  private static PersistableListHolder instance = new PersistableListHolder();

  private static ThreadLocal<Map<Key, PersistentCollection>> listMapping = new ThreadLocal<Map<Key, PersistentCollection>>();

  public PersistableListHolder()
  {
  }

  public void putListMapping(Object target, EStructuralFeature feature, PersistentCollection collection)
  {
    Key key = new Key(target, feature);
    final PersistentCollection currentCachedValue = getListMapping(target, feature);
    if (currentCachedValue == collection)
    {
      return;
    }

    if (currentCachedValue != null)
    {
      throw new IllegalStateException("There is already a list mapping present");
    }

    getListMapping().put(key, collection);
  }

  public PersistentCollection getListMapping(Object target, EStructuralFeature feature)
  {
    Key key = new Key(target, feature);
    return getListMapping().get(key);
  }

  private Map<Key, PersistentCollection> getListMapping()
  {
    if (listMapping.get() == null)
    {
      listMapping.set(new HashMap<Key, PersistentCollection>());
    }

    return listMapping.get();
  }

  public void clearListMapping()
  {
    listMapping.set(null);
  }

  public static PersistableListHolder getInstance()
  {
    return instance;
  }

  public static void setInstance(PersistableListHolder instance)
  {
    PersistableListHolder.instance = instance;
  }

  private static final class Key
  {
    private Object owner;

    private EStructuralFeature feature;

    public Key(Object owner, EStructuralFeature feature)
    {
      this.owner = owner;
      this.feature = feature;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (!(obj instanceof Key))
      {
        return false;
      }

      Key otherKey = (Key)obj;
      // the owner is uniquely present in mem, the same applies for the feature
      // therefore == is allowed
      return owner == otherKey.owner && feature == otherKey.feature;
    }

    @Override
    public int hashCode()
    {
      return owner.hashCode() + feature.hashCode();
    }
  }
}
