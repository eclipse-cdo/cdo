/*
 * Copyright (c) 2009-2016, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - Bug 282976: [DB] Influence Mappings through EAnnotations
 *    Kai Schlamp - Bug 284680 - [DB] Provide annotation to bypass ClassMapping
 *    Stefan Winkler - maintenance
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeatureType;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy3;
import org.eclipse.emf.cdo.server.db.mapping.INamingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.DBAnnotation;
import org.eclipse.emf.cdo.server.internal.db.ObjectIDIterator;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This abstract base class implements those methods which are most likely common to most mapping strategies. It can be
 * used to derive custom mapping strategy implementation.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractMappingStrategy extends Lifecycle implements IMappingStrategy3
{
  private static final ThreadLocal<Boolean> SKIP_MAPPING_INITIALIZATION = new ThreadLocal<Boolean>();

  private IDBStore store;

  private Map<String, String> properties;

  private INamingStrategy namingStrategy;

  private ConcurrentMap<EClass, IClassMapping> classMappings;

  private boolean allClassMappingsCreated;

  private Set<CDOFeatureType> forceIndexes;

  public AbstractMappingStrategy()
  {
    classMappings = new ConcurrentHashMap<EClass, IClassMapping>();
  }

  // -- property related methods -----------------------------------------

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public INamingStrategy getNamingStrategy()
  {
    return namingStrategy;
  }

  public void setNamingStrategy(INamingStrategy namingStrategy)
  {
    checkInactive();
    this.namingStrategy = namingStrategy;
  }

  public Set<CDOFeatureType> getForceIndexes()
  {
    if (forceIndexes == null)
    {
      forceIndexes = doGetForceIndexes(this);
    }

    return forceIndexes;
  }

  // -- getters and setters ----------------------------------------------

  public final IDBStore getStore()
  {
    return store;
  }

  public final void setStore(IDBStore dbStore)
  {
    checkInactive();
    store = dbStore;
  }

  protected final IMetaDataManager getMetaDataManager()
  {
    return getStore().getMetaDataManager();
  }

  // -- object id related methods ----------------------------------------

  public void handleRevisions(IDBStoreAccessor accessor, EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    if (eClass == null)
    {
      Collection<IClassMapping> values = getClassMappings().values();
      for (IClassMapping mapping : values)
      {
        mapping.handleRevisions(accessor, branch, timeStamp, exactTime, handler);
      }
    }
    else
    {
      IClassMapping classMapping = getClassMapping(eClass);
      classMapping.handleRevisions(accessor, branch, timeStamp, exactTime, handler);
    }
  }

  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, OMMonitor monitor, CDOChangeSetSegment[] segments)
  {
    Set<CDOID> result = new HashSet<CDOID>();
    Collection<IClassMapping> classMappings = getClassMappings().values();

    monitor.begin(classMappings.size());

    try
    {
      for (IClassMapping mapping : classMappings)
      {
        Async async = monitor.forkAsync();

        try
        {
          Set<CDOID> ids = mapping.readChangeSet(accessor, segments);
          result.addAll(ids);
        }
        finally
        {
          async.stop();
        }
      }

      return result;
    }
    finally
    {
      monitor.done();
    }
  }

  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor accessor)
  {
    Collection<EClass> classes = getClassesWithObjectInfo();
    final Iterator<EClass> classIt = classes.iterator();

    return new ObjectIDIterator(this, accessor)
    {
      private PreparedStatement currentStatement;

      @Override
      protected ResultSet getNextResultSet()
      {
        while (classIt.hasNext())
        {
          EClass eClass = classIt.next();
          IClassMapping mapping = getClassMapping(eClass);
          currentStatement = mapping.createObjectIDStatement(getAccessor());

          ResultSet resultSet = null;

          try
          {
            resultSet = currentStatement.executeQuery();
            return resultSet;
          }
          catch (Exception ex)
          {
            DBUtil.close(resultSet); // only on error
            releaseCurrentStatement();
            throw new DBException(ex);
          }
        }

        return null;
      }

      @Override
      protected void closeCurrentResultSet()
      {
        super.closeCurrentResultSet();
        releaseCurrentStatement();
      }

      private void releaseCurrentStatement()
      {
        DBUtil.close(currentStatement);
        currentStatement = null;
      }
    };
  }

  protected abstract Collection<EClass> getClassesWithObjectInfo();

  public String getTableName(ENamedElement element)
  {
    return namingStrategy.getTableName(element);
  }

  public String getTableName(EClass containingClass, EStructuralFeature feature)
  {
    return namingStrategy.getTableName(containingClass, feature);
  }

  public String getFieldName(EStructuralFeature feature)
  {
    return namingStrategy.getFieldName(feature);
  }

  public String getUnsettableFieldName(EStructuralFeature feature)
  {
    return namingStrategy.getUnsettableFieldName(feature);
  }

  public void createMapping(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin();
    Async async = null;

    try
    {
      if (packageUnits != null && packageUnits.length != 0)
      {
        async = monitor.forkAsync();

        for (EClass eClass : getMappedClasses(packageUnits))
        {
          IClassMapping classMapping = createClassMapping(eClass);
          addClassMapping(classMapping);
        }
      }
    }
    finally
    {
      if (async != null)
      {
        async.stop();
      }

      monitor.done();
    }
  }

  public void removeMapping(Connection connection, InternalCDOPackageUnit[] packageUnits)
  {
    for (EClass eClass : getMappedClasses(packageUnits))
    {
      removeClassMapping(eClass);
    }
  }

  public boolean isMapped(EClass eClass)
  {
    String mappingAnnotation = DBAnnotation.TABLE_MAPPING.getValue(eClass);
    return mappingAnnotation == null || mappingAnnotation.equalsIgnoreCase(DBAnnotation.TABLE_MAPPING_NONE);
  }

  public List<EClass> getMappedClasses(CDOPackageUnit[] packageUnits)
  {
    List<EClass> result = new ArrayList<EClass>();

    if (packageUnits != null && packageUnits.length != 0)
    {
      for (CDOPackageUnit packageUnit : packageUnits)
      {
        for (CDOPackageInfo packageInfo : packageUnit.getPackageInfos())
        {
          for (EClassifier classifier : packageInfo.getEPackage().getEClassifiers())
          {
            if (classifier instanceof EClass)
            {
              EClass eClass = (EClass)classifier;
              if (isMapped(eClass))
              {
                result.add(eClass);
              }
            }
          }
        }
      }
    }

    return result;
  }

  private void addClassMapping(IClassMapping classMapping)
  {
    if (classMapping != null)
    {
      EClass eClass = classMapping.getEClass();
      classMappings.put(eClass, classMapping);
    }
  }

  private IClassMapping removeClassMapping(EClass eClass)
  {
    IClassMapping classMapping = classMappings.remove(eClass);
    if (classMapping != null)
    {
      IDBSchema schema = getStore().getDBSchema();
      for (IDBTable table : classMapping.getDBTables())
      {
        if (table != null)
        {
          schema.removeTable(table.getName());
        }
      }
    }

    return classMapping;
  }

  public final IClassMapping getClassMapping(EClass eClass)
  {
    if (!isMapped(eClass))
    {
      throw new IllegalArgumentException("Class is not mapped: " + eClass);
    }

    // Try without synchronization first; this will almost always succeed, so it avoids the
    // performance penalty of syncing in the majority of cases
    IClassMapping result = classMappings.get(eClass);
    if (result == null)
    {
      // Synchronize on the classMappings to prevent concurrent invocations of createClassMapping
      // (Synchronizing on the eClass allows for more concurrency, but is risky because application
      // code may be syncing on the eClass also.)
      synchronized (classMappings)
      {
        // Check again, because other thread may have just added the mapping
        result = classMappings.get(eClass);
        if (result == null)
        {
          result = createClassMapping(eClass);
          addClassMapping(result);
        }
      }
    }

    return result;
  }

  public final Map<EClass, IClassMapping> getClassMappings()
  {
    return getClassMappings(true);
  }

  public final Map<EClass, IClassMapping> getClassMappings(boolean createOnDemand)
  {
    if (createOnDemand)
    {
      synchronized (classMappings)
      {
        if (!allClassMappingsCreated)
        {
          ensureAllClassMappings();
          allClassMappingsCreated = true;
        }
      }
    }

    return classMappings;
  }

  private void ensureAllClassMappings()
  {
    InternalRepository repository = (InternalRepository)getStore().getRepository();
    InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
    for (InternalCDOPackageInfo packageInfo : packageRegistry.getPackageInfos())
    {
      for (EClassifier eClassifier : packageInfo.getEPackage().getEClassifiers())
      {
        if (eClassifier instanceof EClass)
        {
          EClass eClass = (EClass)eClassifier;
          if (isMapped(eClass))
          {
            getClassMapping(eClass); // Get or create it
          }
        }
      }
    }
  }

  public void clearClassMappings()
  {
    synchronized (classMappings)
    {
      classMappings.clear();
      allClassMappingsCreated = false;
    }
  }

  public ITypeMapping createValueMapping(EStructuralFeature feature)
  {
    ITypeMapping.Provider provider = getTypeMappingProvider();
    return provider.createTypeMapping(this, feature);
  }

  protected ITypeMapping.Provider getTypeMappingProvider()
  {
    return ITypeMapping.Provider.INSTANCE;
  }

  public final IListMapping createListMapping(EClass containingClass, EStructuralFeature feature)
  {
    checkArg(feature.isMany(), "Only many-valued features allowed"); //$NON-NLS-1$
    return doCreateListMapping(containingClass, feature);
  }

  public final IListMapping createFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    checkArg(FeatureMapUtil.isFeatureMap(feature), "Only FeatureMaps allowed"); //$NON-NLS-1$
    return doCreateFeatureMapMapping(containingClass, feature);
  }

  public abstract IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature);

  /**
   * @deprecated As 4.5 feature maps are no longer supported.
   */
  @Deprecated
  public abstract IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature);

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (namingStrategy == null)
    {
      namingStrategy = new DefaultNamingStrategy();
    }

    namingStrategy.initialize(this);
    LifecycleUtil.activate(namingStrategy);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(namingStrategy);
    deactivateClassMappings();
    super.doDeactivate();
  }

  protected void deactivateClassMappings()
  {
    for (IClassMapping classMapping : classMappings.values())
    {
      Exception exception = LifecycleUtil.deactivate(classMapping);
      if (exception != null)
      {
        OM.LOG.warn(exception);
      }
    }
  }

  private static Set<CDOFeatureType> doGetForceIndexes(IMappingStrategy mappingStrategy)
  {
    return CDOFeatureType.readCombination(mappingStrategy.getProperties().get(Props.FORCE_INDEXES));
  }

  public static Set<CDOFeatureType> getForceIndexes(IMappingStrategy mappingStrategy)
  {
    if (mappingStrategy instanceof AbstractMappingStrategy)
    {
      return ((AbstractMappingStrategy)mappingStrategy).getForceIndexes();
    }

    return doGetForceIndexes(mappingStrategy);
  }

  public static boolean isEagerTableCreation(IMappingStrategy mappingStrategy)
  {
    String value = mappingStrategy.getProperties().get(Props.EAGER_TABLE_CREATION);
    return value == null ? false : Boolean.valueOf(value);
  }

  public static boolean isSkipMappingInitialization()
  {
    return SKIP_MAPPING_INITIALIZATION.get() == Boolean.TRUE;
  }

  public static boolean setSkipMappingInitialization(boolean value)
  {
    boolean oldValue = isSkipMappingInitialization();

    if (value != oldValue)
    {
      if (value)
      {
        SKIP_MAPPING_INITIALIZATION.set(Boolean.TRUE);
      }
      else
      {
        SKIP_MAPPING_INITIALIZATION.remove();
      }
    }

    return oldValue;
  }
}
