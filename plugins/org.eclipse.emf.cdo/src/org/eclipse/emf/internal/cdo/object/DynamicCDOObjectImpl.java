/*
 * Copyright (c) 2011-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - EMap support
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class DynamicCDOObjectImpl extends CDOObjectImpl
{
  public DynamicCDOObjectImpl(EClass eClass)
  {
    eSetClass(eClass);
  }

  /**
   * @author Martin Fluegge
   * @since 3.0
   */
  public static final class BasicEMapEntry<K, V> extends DynamicCDOObjectImpl implements BasicEMap.Entry<K, V>
  {
    protected int hash = -1;

    protected EStructuralFeature keyFeature;

    protected EStructuralFeature valueFeature;

    /**
     * Creates a dynamic EObject.
     */
    public BasicEMapEntry(EClass eClass)
    {
      super(eClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public K getKey()
    {
      return (K)eGet(keyFeature);
    }

    @Override
    public void setKey(Object key)
    {
      eSet(keyFeature, key);
    }

    @Override
    public int getHash()
    {
      if (hash == -1)
      {
        Object theKey = getKey();
        hash = theKey == null ? 0 : theKey.hashCode();
      }

      return hash;
    }

    @Override
    public void setHash(int hash)
    {
      this.hash = hash;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getValue()
    {
      return (V)eGet(valueFeature);
    }

    @Override
    public V setValue(V value)
    {
      @SuppressWarnings("unchecked")
      V result = (V)eGet(valueFeature);
      eSet(valueFeature, value);
      return result;
    }

    @Override
    public void eSetClass(EClass eClass)
    {
      super.eSetClass(eClass);
      keyFeature = eClass.getEStructuralFeature("key");
      valueFeature = eClass.getEStructuralFeature("value");
    }
  }
}
