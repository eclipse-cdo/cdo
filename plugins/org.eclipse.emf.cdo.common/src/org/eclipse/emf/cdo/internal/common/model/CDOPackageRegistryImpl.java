/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
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
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
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

  private static final ContextTracer METAID_TRACER = new ContextTracer(OM.DEBUG_METAID, MetaInstanceMapperImpl.class);

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

  private Map<Enumerator, EEnumLiteral> enumLiterals = new HashMap<Enumerator, EEnumLiteral>();

  private Set<CDOPackageInfo> visitedPackages = new HashSet<CDOPackageInfo>();

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

  public Set<String> getAllKeys()
  {
    Set<String> result = new HashSet<String>();
    result.addAll(keySet());
    if (delegateRegistry != null)
    {
      if (delegateRegistry instanceof InternalCDOPackageRegistry)
      {
        result.addAll(((InternalCDOPackageRegistry)delegateRegistry).getAllKeys());
      }
      else
      {
        result.addAll(delegateRegistry.keySet());
      }
    }

    return result;
  }

  public Object getWithDelegation(String nsURI, boolean resolve)
  {
    Object result = getFrom(this, nsURI, resolve);
    if (result == null && delegateRegistry != null)
    {
      result = getFrom(delegateRegistry, nsURI, resolve);
    }

    return result;
  }

  private static Object getFrom(EPackage.Registry registry, String nsURI, boolean resolve)
  {
    if (resolve)
    {
      return registry.getEPackage(nsURI);
    }

    return registry.get(nsURI);
  }

  public Object basicPut(String nsURI, Object value)
  {
    LifecycleUtil.checkActive(this);
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0} --> {1}", nsURI, value); //$NON-NLS-1$
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
    else if (oldValue instanceof EPackage && value instanceof InternalCDOPackageInfo)
    {
      EPackage oldPackage = (EPackage)oldValue;
      InternalCDOPackageInfo oldPackageInfo = getPackageInfo(oldPackage);
      InternalCDOPackageInfo newPackageInfo = (InternalCDOPackageInfo)value;
      if (oldPackageInfo.getMetaIDRange().isTemporary() && !newPackageInfo.getMetaIDRange().isTemporary())
      {
        oldPackageInfo.setMetaIDRange(newPackageInfo.getMetaIDRange());
      }

      InternalCDOPackageUnit oldPackageUnit = oldPackageInfo.getPackageUnit();
      InternalCDOPackageUnit newPackageUnit = newPackageInfo.getPackageUnit();
      if (oldPackageUnit.getState() == CDOPackageUnit.State.NEW
          && newPackageUnit.getState() != CDOPackageUnit.State.NEW)
      {
        oldPackageUnit.setState(CDOPackageUnit.State.LOADED);
      }

      // Keep old value!
      return null;
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
    if (packageInfo == null)
    {
      putEPackage(ePackage);
      packageInfo = getPackageInfo(ePackage);
      if (packageInfo == null)
      {
        throw new ImplementationError(MessageFormat.format(Messages.getString("CDOPackageRegistryImpl.0"), ePackage)); //$NON-NLS-1$
      }
    }

    return (InternalCDOPackageUnit)packageInfo.getPackageUnit();
  }

  public synchronized InternalCDOPackageUnit getPackageUnit(String id)
  {
    LifecycleUtil.checkActive(this);
    for (Object value : values())
    {
      InternalCDOPackageUnit packageUnit = null;
      if (value instanceof InternalCDOPackageInfo)
      {
        packageUnit = ((InternalCDOPackageInfo)value).getPackageUnit();
      }
      else if (value instanceof EPackage)
      {
        InternalCDOPackageInfo packageInfo = getPackageInfo((EPackage)value);
        if (packageInfo != null)
        {
          packageUnit = packageInfo.getPackageUnit();
        }
      }

      if (packageUnit != null && id.equals(packageUnit.getID()))
      {
        return packageUnit;
      }
    }

    return null;
  }

  public InternalCDOPackageUnit[] getPackageUnits(long startTime, long endTime)
  {
    LifecycleUtil.checkActive(this);
    if (endTime == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      endTime = Long.MAX_VALUE;
    }

    Set<InternalCDOPackageUnit> result = new HashSet<InternalCDOPackageUnit>();
    for (Object value : values())
    {
      InternalCDOPackageUnit packageUnit = null;
      if (value instanceof InternalCDOPackageInfo)
      {
        packageUnit = ((InternalCDOPackageInfo)value).getPackageUnit();
      }
      else if (value instanceof EPackage)
      {
        InternalCDOPackageInfo packageInfo = getPackageInfo((EPackage)value);
        if (packageInfo != null)
        {
          packageUnit = packageInfo.getPackageUnit();
        }
      }

      if (packageUnit != null)
      {
        long timeStamp = packageUnit.getTimeStamp();
        if (startTime <= timeStamp && timeStamp <= endTime)
        {
          result.add(packageUnit);
        }
      }
    }

    return result.toArray(new InternalCDOPackageUnit[result.size()]);
  }

  public InternalCDOPackageUnit[] getPackageUnits(boolean withSystemPackages)
  {
    LifecycleUtil.checkActive(this);
    return collectPackageUnits(withSystemPackages);
  }

  public synchronized InternalCDOPackageUnit[] getPackageUnits()
  {
    LifecycleUtil.checkActive(this);
    if (packageUnits == null)
    {
      packageUnits = collectPackageUnits(true);
      Arrays.sort(packageUnits);
    }

    return packageUnits;
  }

  private InternalCDOPackageUnit[] collectPackageUnits(boolean withSystemPackages)
  {
    Set<InternalCDOPackageUnit> result = new HashSet<InternalCDOPackageUnit>();
    for (Object value : values())
    {
      InternalCDOPackageUnit packageUnit = collectPackageUnit(value);
      if (packageUnit != null && (withSystemPackages || !packageUnit.isSystem()))
      {
        result.add(packageUnit);
      }
    }

    return result.toArray(new InternalCDOPackageUnit[result.size()]);
  }

  private InternalCDOPackageUnit collectPackageUnit(Object value)
  {
    if (value instanceof InternalCDOPackageInfo)
    {
      return ((InternalCDOPackageInfo)value).getPackageUnit();
    }

    if (value instanceof EPackage)
    {
      InternalCDOPackageInfo packageInfo = getPackageInfo((EPackage)value);
      if (packageInfo != null)
      {
        InternalCDOPackageUnit packageUnit = packageInfo.getPackageUnit();
        return packageUnit;
      }
    }

    return null;
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

  public EEnumLiteral getEnumLiteralFor(Enumerator value)
  {
    EEnumLiteral result = enumLiterals.get(value);
    if (result != null)
    {
      return result;
    }

    for (CDOPackageUnit packageUnit : getPackageUnits())
    {
      for (CDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        if (visitedPackages.add(packageInfo))
        {
          result = visitPackage(packageInfo, value);
          if (result != null)
          {
            return result;
          }
        }
      }
    }

    return null;
  }

  private EEnumLiteral visitPackage(CDOPackageInfo packageInfo, Enumerator value)
  {
    EEnumLiteral result = null;
    for (EClassifier classifier : packageInfo.getEPackage().getEClassifiers())
    {
      if (classifier instanceof EEnum)
      {
        EEnum eenum = (EEnum)classifier;
        for (EEnumLiteral eEnumLiteral : eenum.getELiterals())
        {
          Enumerator instance = eEnumLiteral.getInstance();
          enumLiterals.put(instance, eEnumLiteral);
          if (instance == value)
          {
            result = eEnumLiteral;
          }
        }
      }
    }

    return result;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[packageLoader={1}]", getClass().getSimpleName(), getPackageLoader()); //$NON-NLS-1$
  }

  public void addListener(IListener listener)
  {
    // Do nothing
  }

  public void removeListener(IListener listener)
  {
    // Do nothing
  }

  public IListener[] getListeners()
  {
    return EventUtil.NO_LISTENERS;
  }

  public boolean hasListeners()
  {
    return false;
  }

  public synchronized boolean isActive()
  {
    return active;
  }

  public synchronized LifecycleState getLifecycleState()
  {
    return active ? LifecycleState.ACTIVE : LifecycleState.INACTIVE;
  }

  public synchronized void activate() throws LifecycleException
  {
    if (!active)
    {
      CheckUtil.checkState(packageLoader, "packageLoader"); //$NON-NLS-1$
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

      throw new IllegalStateException(
          MessageFormat.format(Messages.getString("CDOPackageRegistryImpl.1"), id) + "\n" + dump()); //$NON-NLS-1$ //$NON-NLS-2$
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
          if (TRACER.isEnabled())
          {
            TRACER.trace(ex);
          }
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

      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOPackageRegistryImpl.6"),
          metaInstance, ePackage) // $NON-NLS-1$
      // + "\n" + dump() // $NON-NLS-1$
      );
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
        throw new IllegalStateException("range.size() != metaIDRange.size()"); //$NON-NLS-1$
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
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOPackageRegistryImpl.10"), oldID)); //$NON-NLS-1$
      }

      if (METAID_TRACER.isEnabled())
      {
        METAID_TRACER.format("Remapping meta instance: {0} --> {1} <-> {2}", oldID, newID, metaInstance); //$NON-NLS-1$
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
        stream.println("    " + entry.getKey() + " --> " + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
      }

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
      checkID(id);
      if (METAID_TRACER.isEnabled())
      {
        METAID_TRACER.format("Registering meta instance: {0} <-> {1}", id, metaInstance); //$NON-NLS-1$
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
      checkID(metaID);
      idToMetaInstanceMap.put(metaID, metaInstance);
      metaInstanceToIDMap.put(metaInstance, metaID);
    }

    private void checkID(CDOID id)
    {
      if (!id.isMeta())
      {
        throw new IllegalArgumentException("Not a meta ID: " + id);
      }
    }
  }
}
