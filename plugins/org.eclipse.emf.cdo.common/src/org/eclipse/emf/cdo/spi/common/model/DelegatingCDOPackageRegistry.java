/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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
  public DelegatingCDOPackageRegistry()
  {
  }

  protected abstract InternalCDOPackageRegistry getDelegate();

  @Override
  public Object basicPut(String nsURI, Object value)
  {
    return getDelegate().basicPut(nsURI, value);
  }

  @Override
  public void clear()
  {
    getDelegate().clear();
  }

  @Override
  public boolean containsKey(Object key)
  {
    return getDelegate().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value)
  {
    return getDelegate().containsValue(value);
  }

  @Override
  public Set<java.util.Map.Entry<String, Object>> entrySet()
  {
    return getDelegate().entrySet();
  }

  @Override
  public Object get(Object key)
  {
    return getDelegate().get(key);
  }

  @Override
  public EFactory getEFactory(String nsURI)
  {
    return getDelegate().getEFactory(nsURI);
  }

  @Override
  public EPackage getEPackage(String nsURI)
  {
    return getDelegate().getEPackage(nsURI);
  }

  @Override
  public EPackage[] getEPackages()
  {
    return getDelegate().getEPackages();
  }

  @Override
  public InternalCDOPackageInfo getPackageInfo(EPackage ePackage)
  {
    return getDelegate().getPackageInfo(ePackage);
  }

  @Override
  public InternalCDOPackageInfo[] getPackageInfos()
  {
    return getDelegate().getPackageInfos();
  }

  @Override
  public PackageLoader getPackageLoader()
  {
    return getDelegate().getPackageLoader();
  }

  @Override
  public PackageProcessor getPackageProcessor()
  {
    return getDelegate().getPackageProcessor();
  }

  @Override
  public InternalCDOPackageUnit getPackageUnit(EPackage ePackage)
  {
    return getDelegate().getPackageUnit(ePackage);
  }

  @Override
  public InternalCDOPackageUnit[] getPackageUnits()
  {
    return getDelegate().getPackageUnits();
  }

  @Override
  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  @Override
  public boolean isReplacingDescriptors()
  {
    return getDelegate().isReplacingDescriptors();
  }

  @Override
  public Set<String> keySet()
  {
    return getDelegate().keySet();
  }

  @Override
  public Object put(String key, Object value)
  {
    return getDelegate().put(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> t)
  {
    getDelegate().putAll(t);
  }

  @Override
  public Object putEPackage(EPackage ePackage)
  {
    return getDelegate().putEPackage(ePackage);
  }

  @Override
  public void putPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    getDelegate().putPackageUnit(packageUnit);
  }

  @Override
  public Object remove(Object key)
  {
    return getDelegate().remove(key);
  }

  @Override
  public void setPackageLoader(PackageLoader packageLoader)
  {
    getDelegate().setPackageLoader(packageLoader);
  }

  @Override
  public void setPackageProcessor(PackageProcessor packageProcessor)
  {
    getDelegate().setPackageProcessor(packageProcessor);
  }

  @Override
  public void setReplacingDescriptors(boolean replacingDescriptors)
  {
    getDelegate().setReplacingDescriptors(replacingDescriptors);
  }

  @Override
  public int size()
  {
    return getDelegate().size();
  }

  @Override
  public Collection<Object> values()
  {
    return getDelegate().values();
  }
}
