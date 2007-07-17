/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OverridablePackageRegistry extends EPackageRegistryImpl
{
  private static final long serialVersionUID = 1L;

  public OverridablePackageRegistry()
  {
  }

  public OverridablePackageRegistry(Registry delegateRegistry)
  {
    super(delegateRegistry);
  }

  @Override
  public final void clear()
  {
    if (!isEmpty())
    {
      List<EPackage> ePackages = new ArrayList(values());
      super.clear();
      removed(ePackages);
    }
  }

  @Override
  public final Set<java.util.Map.Entry<String, Object>> entrySet()
  {
    return super.entrySet();
  }

  @Override
  public final Object get(Object key)
  {
    return filterGet(super.get(key));
  }

  @Override
  public final Set<String> keySet()
  {
    Set<String> keySet = super.keySet();
    Iterator<String> iterator = keySet().iterator();
    return keySet;
  }

  @Override
  public final Object put(String key, Object value)
  {
    return super.put(key, value);
  }

  @Override
  public final void putAll(Map<? extends String, ? extends Object> m)
  {
    super.putAll(m);
  }

  @Override
  public final Object remove(Object key)
  {
    return super.remove(key);
  }

  @Override
  public final Collection<Object> values()
  {
    return super.values();
  }

  protected Object filterGet(Object object)
  {
    return object;
  }

  protected void removed(List<EPackage> ePackages)
  {
  }
}
