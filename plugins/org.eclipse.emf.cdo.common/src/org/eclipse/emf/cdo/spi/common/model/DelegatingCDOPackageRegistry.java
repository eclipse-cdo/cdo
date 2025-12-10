/*
 * Copyright (c) 2011, 2012, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.model;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 */
@Deprecated
public abstract class DelegatingCDOPackageRegistry extends Lifecycle implements InternalCDOPackageRegistry
{
  @Deprecated
  public DelegatingCDOPackageRegistry()
  {
  }

  @Deprecated
  protected abstract InternalCDOPackageRegistry getDelegate();

  @Deprecated
  @Override
  public Object basicPut(String nsURI, Object value)
  {
    return getDelegate().basicPut(nsURI, value);
  }

  @Deprecated
  @Override
  public void clear()
  {
    getDelegate().clear();
  }

  @Deprecated
  @Override
  public boolean containsKey(Object key)
  {
    return getDelegate().containsKey(key);
  }

  @Deprecated
  @Override
  public boolean containsValue(Object value)
  {
    return getDelegate().containsValue(value);
  }

  @Deprecated
  @Override
  public Set<Map.Entry<String, Object>> entrySet()
  {
    return getDelegate().entrySet();
  }

  @Deprecated
  @Override
  public Object get(Object key)
  {
    return getDelegate().get(key);
  }

  @Deprecated
  @Override
  public EFactory getEFactory(String nsURI)
  {
    return getDelegate().getEFactory(nsURI);
  }

  @Deprecated
  @Override
  public EPackage getEPackage(String nsURI)
  {
    return getDelegate().getEPackage(nsURI);
  }

  @Deprecated
  @Override
  public EPackage[] getEPackages()
  {
    return getDelegate().getEPackages();
  }

  @Deprecated
  @Override
  public InternalCDOPackageInfo getPackageInfo(EPackage ePackage)
  {
    return getDelegate().getPackageInfo(ePackage);
  }

  @Deprecated
  @Override
  public InternalCDOPackageInfo[] getPackageInfos()
  {
    return getDelegate().getPackageInfos();
  }

  @Deprecated
  @Override
  public PackageLoader getPackageLoader()
  {
    return getDelegate().getPackageLoader();
  }

  @Deprecated
  @Override
  public PackageProcessor getPackageProcessor()
  {
    return getDelegate().getPackageProcessor();
  }

  @Deprecated
  @Override
  public InternalCDOPackageUnit getPackageUnit(EPackage ePackage)
  {
    return getDelegate().getPackageUnit(ePackage);
  }

  @Deprecated
  @Override
  public InternalCDOPackageUnit[] getPackageUnits()
  {
    return getDelegate().getPackageUnits();
  }

  @Deprecated
  @Override
  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  @Deprecated
  @Override
  public boolean isReplacingDescriptors()
  {
    return getDelegate().isReplacingDescriptors();
  }

  @Deprecated
  @Override
  public Set<String> keySet()
  {
    return getDelegate().keySet();
  }

  @Deprecated
  @Override
  public Object put(String key, Object value)
  {
    return getDelegate().put(key, value);
  }

  @Deprecated
  @Override
  public void putAll(Map<? extends String, ? extends Object> t)
  {
    getDelegate().putAll(t);
  }

  @Deprecated
  @Override
  public Object putEPackage(EPackage ePackage)
  {
    return getDelegate().putEPackage(ePackage);
  }

  @Deprecated
  @Override
  public void putPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    getDelegate().putPackageUnit(packageUnit);
  }

  @Deprecated
  @Override
  public Object remove(Object key)
  {
    return getDelegate().remove(key);
  }

  @Deprecated
  @Override
  public void setPackageLoader(PackageLoader packageLoader)
  {
    getDelegate().setPackageLoader(packageLoader);
  }

  @Deprecated
  @Override
  public void setPackageProcessor(PackageProcessor packageProcessor)
  {
    getDelegate().setPackageProcessor(packageProcessor);
  }

  @Deprecated
  @Override
  public void setReplacingDescriptors(boolean replacingDescriptors)
  {
    getDelegate().setReplacingDescriptors(replacingDescriptors);
  }

  @Deprecated
  @Override
  public int size()
  {
    return getDelegate().size();
  }

  @Deprecated
  @Override
  public Collection<Object> values()
  {
    return getDelegate().values();
  }
}
