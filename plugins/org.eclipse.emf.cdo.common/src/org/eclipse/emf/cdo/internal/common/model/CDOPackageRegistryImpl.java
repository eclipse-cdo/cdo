/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDTempMeta;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.ILifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOPackageRegistryImpl extends EPackageRegistryImpl implements InternalCDOPackageRegistry
{
  private static final long serialVersionUID = 1L;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOPackageRegistryImpl.class);

  private static final boolean eagerInternalCaches = false;

  private MetaInstanceMapperImpl metaInstanceMapper = new MetaInstanceMapperImpl();

  private boolean replacingDescriptors;

  private PackageProcessor packageProcessor;

  private PackageLoader packageLoader;

  private transient boolean active;

  @ExcludeFromDump
  private transient InternalCDOPackageInfo[] packageInfos;

  @ExcludeFromDump
  private transient InternalCDOPackageUnit[] packageUnits;

  public CDOPackageRegistryImpl()
  {
  }

  public MetaInstanceMapper getMetaInstanceMapper()
  {
    return metaInstanceMapper;
  }

  public boolean isReplacingDescriptors()
  {
    return replacingDescriptors;
  }

  public void setReplacingDescriptors(boolean replacingDescriptors)
  {
    this.replacingDescriptors = replacingDescriptors;
  }

  public PackageProcessor getPackageProcessor()
  {
    return packageProcessor;
  }

  public void setPackageProcessor(PackageProcessor packageProcessor)
  {
    this.packageProcessor = packageProcessor;
  }

  public PackageLoader getPackageLoader()
  {
    return packageLoader;
  }

  public void setPackageLoader(PackageLoader packageLoader)
  {
    LifecycleUtil.checkInactive(this);
    this.packageLoader = packageLoader;
  }

  @Override
  public Object get(Object key)
  {
    LifecycleUtil.checkActive(this);
    return super.get(key);
  }

  public Object basicPut(String nsURI, Object value)
  {
    LifecycleUtil.checkActive(this);
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0} --> {1}", nsURI, value);
    }

    if (packageProcessor != null)
    {
      value = packageProcessor.processPackage(value);
    }

    Object oldValue = get(nsURI);
    if (oldValue instanceof InternalCDOPackageInfo && value instanceof EPackage)
    {
      InternalCDOPackageInfo oldPackageInfo = (InternalCDOPackageInfo)oldValue;
      EPackage newValue = (EPackage)value;
      if (oldPackageInfo.getEPackage(false) == null)
      {
        EMFUtil.addAdapter(newValue, oldPackageInfo);
        oldPackageInfo.getPackageUnit().setState(CDOPackageUnit.State.LOADED);
      }
    }

    return super.put(nsURI, value);
  }

  @Override
  public synchronized Object put(String nsURI, Object value)
  {
    LifecycleUtil.checkActive(this);
    if (replacingDescriptors && value instanceof EPackage.Descriptor)
    {
      EPackage.Descriptor descriptor = (EPackage.Descriptor)value;
      value = descriptor.getEPackage();
    }

    if (value instanceof EPackage)
    {
      EPackage ePackage = (EPackage)value;
      InternalCDOPackageInfo packageInfo = getPackageInfo(ePackage);
      if (packageInfo == null)
      {
        initPackageUnit(ePackage);
        return null;
      }
    }

    return basicPut(nsURI, value);
  }

  public synchronized Object putEPackage(EPackage ePackage)
  {
    return put(ePackage.getNsURI(), ePackage);
  }

  public synchronized void putPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    LifecycleUtil.checkActive(this);
    packageUnit.setPackageRegistry(this);
    for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
    {
      EPackage ePackage = packageInfo.getEPackage(false);
      if (ePackage != null)
      {
        EMFUtil.addAdapter(ePackage, packageInfo);
        basicPut(ePackage.getNsURI(), ePackage);
      }
      else
      {
        basicPut(packageInfo.getPackageURI(), packageInfo);
      }
    }

    resetInternalCaches();
  }

  public synchronized InternalCDOPackageInfo getPackageInfo(EPackage ePackage)
  {
    LifecycleUtil.checkActive(this);
    // Looks in the registry
    Object object = get(ePackage.getNsURI());
    if (object instanceof InternalCDOPackageInfo)
    {
      InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)object;
      if (packageInfo.getPackageUnit().getPackageRegistry() == this)
      {
        return packageInfo;
      }
    }

    // Looks in the adapters
    synchronized (ePackage)
    {
      EList<Adapter> adapters = ePackage.eAdapters();
      for (int i = 0, size = adapters.size(); i < size; ++i)
      {
        Adapter adapter = adapters.get(i);
        if (adapter instanceof InternalCDOPackageInfo)
        {
          InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)adapter;
          if (packageInfo.getPackageUnit().getPackageRegistry() == this)
          {
            return packageInfo;
          }
        }
      }
    }

    return null;
  }

  public synchronized InternalCDOPackageInfo[] getPackageInfos()
  {
    LifecycleUtil.checkActive(this);
    if (packageInfos == null)
    {
      List<InternalCDOPackageInfo> result = new ArrayList<InternalCDOPackageInfo>();
      for (Object value : values())
      {
        if (value instanceof InternalCDOPackageInfo)
        {
          result.add((InternalCDOPackageInfo)value);
        }
        else if (value instanceof EPackage)
        {
          InternalCDOPackageInfo packageInfo = getPackageInfo((EPackage)value);
          if (packageInfo != null)
          {
            result.add(packageInfo);
          }
        }
      }

      packageInfos = result.toArray(new InternalCDOPackageInfo[result.size()]);
      Arrays.sort(packageInfos);
    }

    return packageInfos;
  }

  public InternalCDOPackageUnit getPackageUnit(EPackage ePackage)
  {
    CDOPackageInfo packageInfo = getPackageInfo(ePackage);
    if (packageInfo != null)
    {
      return (InternalCDOPackageUnit)packageInfo.getPackageUnit();
    }

    return null;
  }

  public synchronized InternalCDOPackageUnit[] getPackageUnits()
  {
    LifecycleUtil.checkActive(this);
    if (packageUnits == null)
    {
      Set<InternalCDOPackageUnit> result = new HashSet<InternalCDOPackageUnit>();
      for (Object value : values())
      {
        if (value instanceof InternalCDOPackageInfo)
        {
          result.add(((InternalCDOPackageInfo)value).getPackageUnit());
        }
        else if (value instanceof EPackage)
        {
          InternalCDOPackageInfo packageInfo = getPackageInfo((EPackage)value);
          if (packageInfo != null)
          {
            InternalCDOPackageUnit packageUnit = packageInfo.getPackageUnit();
            result.add(packageUnit);
          }
        }
      }

      packageUnits = result.toArray(new InternalCDOPackageUnit[result.size()]);
      Arrays.sort(packageUnits);
    }

    return packageUnits;
  }

  public synchronized EPackage[] getEPackages()
  {
    LifecycleUtil.checkActive(this);
    List<EPackage> result = new ArrayList<EPackage>();
    for (String packageURI : keySet())
    {
      EPackage ePackage = getEPackage(packageURI);
      if (ePackage != null)
      {
        result.add(ePackage);
      }
    }

    return result.toArray(new EPackage[result.size()]);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[packageLoader={1}]", getClass().getSimpleName(), getPackageLoader());
  }

  public synchronized boolean isActive()
  {
    return active;
  }

  public synchronized ILifecycleState getLifecycleState()
  {
    return active ? ILifecycleState.ACTIVE : ILifecycleState.INACTIVE;
  }

  public synchronized void activate() throws LifecycleException
  {
    if (!active)
    {
      CheckUtil.checkState(packageLoader, "packageLoader");
      active = true;
    }
  }

  public synchronized Exception deactivate()
  {
    if (active)
    {
      try
      {
        disposePackageUnits();
        metaInstanceMapper.clear();
        metaInstanceMapper = null;

        clear();
        active = false;
      }
      catch (RuntimeException ex)
      {
        return ex;
      }
    }

    return null;
  }

  protected void disposePackageUnits()
  {
    for (InternalCDOPackageUnit packageUnit : getPackageUnits())
    {
      packageUnit.dispose();
    }

    packageInfos = null;
    packageUnits = null;
  }

  protected void initPackageUnit(EPackage ePackage)
  {
    InternalCDOPackageUnit packageUnit = createPackageUnit();
    packageUnit.setPackageRegistry(this);
    packageUnit.init(ePackage);
    resetInternalCaches();
  }

  protected void resetInternalCaches()
  {
    packageInfos = null;
    packageUnits = null;
    if (eagerInternalCaches)
    {
      getPackageInfos();
      getPackageUnits();
    }
  }

  protected InternalCDOPackageUnit createPackageUnit()
  {
    return (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
  }

  /**
   * @author Eike Stepper
   */
  public class MetaInstanceMapperImpl implements MetaInstanceMapper
  {
    private Map<CDOID, InternalEObject> idToMetaInstanceMap = new HashMap<CDOID, InternalEObject>();

    private Map<InternalEObject, CDOID> metaInstanceToIDMap = new HashMap<InternalEObject, CDOID>();

    @ExcludeFromDump
    private transient int lastTempMetaID;

    public MetaInstanceMapperImpl()
    {
    }

    public synchronized InternalEObject lookupMetaInstance(CDOID id)
    {
      LifecycleUtil.checkActive(CDOPackageRegistryImpl.this);
      InternalEObject metaInstance = idToMetaInstanceMap.get(id);
      if (metaInstance != null)
      {
        return metaInstance;
      }

      if (delegateRegistry instanceof InternalCDOPackageRegistry)
      {
        try
        {
          InternalCDOPackageRegistry delegate = (InternalCDOPackageRegistry)delegateRegistry;
          return delegate.getMetaInstanceMapper().lookupMetaInstance(id);
        }
        catch (RuntimeException ex)
        {
          // Fall-through
        }
      }

      for (InternalCDOPackageInfo packageInfo : getPackageInfos())
      {
        CDOIDMetaRange metaIDRange = packageInfo.getMetaIDRange();
        if (metaIDRange != null && metaIDRange.contains(id))
        {
          EPackage ePackage = packageInfo.getEPackage();
          mapMetaInstances(ePackage, packageInfo.getMetaIDRange());
          metaInstance = idToMetaInstanceMap.get(id);
          if (metaInstance != null)
          {
            return metaInstance;
          }

          break;
        }
      }

      throw new IllegalStateException("No meta instance mapped for " + id + "\n" + dump());
    }

    public synchronized CDOID lookupMetaInstanceID(InternalEObject metaInstance)
    {
      LifecycleUtil.checkActive(CDOPackageRegistryImpl.this);
      CDOID metaID = metaInstanceToIDMap.get(metaInstance);
      if (metaID != null)
      {
        return metaID;
      }

      if (delegateRegistry instanceof InternalCDOPackageRegistry)
      {
        try
        {
          InternalCDOPackageRegistry delegate = (InternalCDOPackageRegistry)delegateRegistry;
          return delegate.getMetaInstanceMapper().lookupMetaInstanceID(metaInstance);
        }
        catch (RuntimeException ex)
        {
          // Fall-through
        }
      }

      EPackage ePackage = getContainingPackage(metaInstance);
      if (ePackage != null)
      {
        InternalCDOPackageInfo packageInfo = getPackageInfo(ePackage);
        if (packageInfo != null)
        {
          mapMetaInstances(ePackage, packageInfo.getMetaIDRange());
          metaID = metaInstanceToIDMap.get(metaInstance);
          if (metaID != null)
          {
            return metaID;
          }
        }
      }

      throw new IllegalStateException("No meta ID mapped for " + metaInstance + "\nContaining package: " + ePackage
          + "\n" + dump());
    }

    private EPackage getContainingPackage(InternalEObject metaInstance)
    {
      EObject object = metaInstance;
      while ((object = object.eContainer()) != null)
      {
        if (object instanceof EPackage)
        {
          return (EPackage)object;
        }
      }

      return null;
    }

    public synchronized CDOIDMetaRange mapMetaInstances(EPackage ePackage)
    {
      LifecycleUtil.checkActive(CDOPackageRegistryImpl.this);
      CDOIDMetaRange range = map(ePackage, lastTempMetaID + 1);
      lastTempMetaID = ((CDOIDTempMeta)range.getUpperBound()).getIntValue();
      return range;
    }

    public synchronized void mapMetaInstances(EPackage ePackage, CDOIDMetaRange metaIDRange)
    {
      LifecycleUtil.checkActive(CDOPackageRegistryImpl.this);
      CDOIDMetaRange range = CDOIDUtil.createMetaRange(metaIDRange.getLowerBound(), 0);
      range = map((InternalEObject)ePackage, range);
      if (range.size() != metaIDRange.size())
      {
        throw new IllegalStateException("range.size() != metaIDRange.size()");
      }
    }

    public void mapMetaInstances(MetaInstanceMapper source)
    {
      for (Map.Entry<CDOID, InternalEObject> entry : source.getEntrySet())
      {
        map(entry.getKey(), entry.getValue());
      }
    }

    public Set<Map.Entry<CDOID, InternalEObject>> getEntrySet()
    {
      return idToMetaInstanceMap.entrySet();
    }

    public synchronized void remapMetaInstanceID(CDOID oldID, CDOID newID)
    {
      LifecycleUtil.checkActive(CDOPackageRegistryImpl.this);
      InternalEObject metaInstance = idToMetaInstanceMap.remove(oldID);
      if (metaInstance == null)
      {
        throw new IllegalArgumentException("Unknown meta instance ID: " + oldID);
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Remapping meta instance: {0} --> {1} <-> {2}", oldID, newID, metaInstance);
      }

      map(newID, metaInstance);
    }

    public void clear()
    {
      idToMetaInstanceMap.clear();
      metaInstanceToIDMap.clear();
      lastTempMetaID = 0;
    }

    private String dump()
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream stream = new PrintStream(baos);

      stream.println();
      stream.println();
      stream.println(CDOPackageRegistryImpl.this);

      stream.println();
      List<Map.Entry<CDOID, InternalEObject>> list = new ArrayList<Map.Entry<CDOID, InternalEObject>>(
          idToMetaInstanceMap.entrySet());
      Collections.sort(list, new Comparator<Map.Entry<CDOID, InternalEObject>>()
      {
        public int compare(Map.Entry<CDOID, InternalEObject> o1, Map.Entry<CDOID, InternalEObject> o2)
        {
          return o1.getKey().compareTo(o2.getKey());
        }
      });

      for (Map.Entry<CDOID, InternalEObject> entry : list)
      {
        stream.println("    " + entry.getKey() + " --> " + entry.getValue());
      }

      // ReflectUtil.dump(idToMetaInstanceMap, "    ", stream);
      // stream.println();
      // ReflectUtil.dump(metaInstanceToIDMap, "    ", stream);

      return baos.toString();
    }

    private CDOIDMetaRange map(EPackage ePackage, int firstMetaID)
    {
      CDOIDTemp lowerBound = CDOIDUtil.createTempMeta(firstMetaID);
      CDOIDMetaRange range = CDOIDUtil.createMetaRange(lowerBound, 0);
      return map((InternalEObject)ePackage, range);
    }

    private CDOIDMetaRange map(InternalEObject metaInstance, CDOIDMetaRange range)
    {
      range = range.increase();
      CDOID id = range.getUpperBound();
      if (TRACER.isEnabled())
      {
        TRACER.format("Registering meta instance: {0} <-> {1}", id, metaInstance);
      }

      idToMetaInstanceMap.put(id, metaInstance);
      CDOID oldID = metaInstanceToIDMap.put(metaInstance, id);
      if (oldID != null)
      {
        idToMetaInstanceMap.remove(oldID);
      }

      for (EObject content : metaInstance.eContents())
      {
        if (!(content instanceof EPackage))
        {
          range = map((InternalEObject)content, range);
        }
      }

      return range;
    }

    private void map(CDOID metaID, InternalEObject metaInstance)
    {
      idToMetaInstanceMap.put(metaID, metaInstance);
      metaInstanceToIDMap.put(metaInstance, metaID);
    }
  }
}
