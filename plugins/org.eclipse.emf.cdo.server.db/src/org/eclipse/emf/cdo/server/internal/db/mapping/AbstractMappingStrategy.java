/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444
 *    Victor Roldan Betancort - 289360: [DB] [maintenance] Support FeatureMaps
 *    Caspar De Groot - https://bugs.eclipse.org/333260
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.ObjectIDIterator;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractHorizontalClassMapping;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
public abstract class AbstractMappingStrategy extends Lifecycle implements IMappingStrategy
{
  // --------- database name generation strings --------------
  protected static final String NAME_SEPARATOR = "_"; //$NON-NLS-1$

  protected static final String TYPE_PREFIX_FEATURE = "F"; //$NON-NLS-1$

  protected static final String TYPE_PREFIX_CLASS = "C"; //$NON-NLS-1$

  protected static final String TYPE_PREFIX_PACKAGE = "P"; //$NON-NLS-1$

  protected static final String FEATEURE_TABLE_SUFFIX = "_list"; //$NON-NLS-1$

  private IDBStore store;

  private Map<String, String> properties;

  private ConcurrentMap<EClass, IClassMapping> classMappings;

  private boolean allClassMappingsCreated;

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

  private int getMaxTableNameLength()
  {
    String value = getProperties().get(PROP_MAX_TABLE_NAME_LENGTH);
    return value == null ? store.getDBAdapter().getMaxTableNameLength() : Integer.valueOf(value);
  }

  private int getMaxFieldNameLength()
  {
    String value = getProperties().get(PROP_MAX_FIELD_NAME_LENGTH);
    return value == null ? store.getDBAdapter().getMaxFieldNameLength() : Integer.valueOf(value);
  }

  private boolean isQualifiedNames()
  {
    String value = getProperties().get(PROP_QUALIFIED_NAMES);
    return value == null ? false : Boolean.valueOf(value);
  }

  private boolean isForceNamesWithID()
  {
    String value = getProperties().get(PROP_FORCE_NAMES_WITH_ID);
    return value == null ? false : Boolean.valueOf(value);
  }

  private String getTableNamePrefix()
  {
    String value = getProperties().get(PROP_TABLE_NAME_PREFIX);
    return StringUtil.safe(value);
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

  public abstract boolean hasAuditSupport();

  public abstract boolean hasDeltaSupport();

  // -- object id related methods ----------------------------------------

  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor dbStoreAccessor)
  {
    Collection<EClass> classes = getClassesWithObjectInfo();
    final Iterator<EClass> classIt = classes.iterator();

    return new ObjectIDIterator(this, dbStoreAccessor)
    {
      private PreparedStatement currentStatement = null;

      @Override
      protected ResultSet getNextResultSet()
      {
        while (classIt.hasNext())
        {
          EClass eClass = classIt.next();
          IClassMapping mapping = getClassMapping(eClass);
          currentStatement = mapping.createObjectIdStatement(getAccessor());

          ResultSet rset = null;
          try
          {
            rset = currentStatement.executeQuery();
            return rset;
          }
          catch (SQLException ex)
          {
            DBUtil.close(rset); // only on error
            getAccessor().getStatementCache().releasePreparedStatement(currentStatement);
            throw new DBException(ex);
          }
        }

        return null;
      }

      @Override
      protected void closeCurrentResultSet()
      {
        super.closeCurrentResultSet();
        getAccessor().getStatementCache().releasePreparedStatement(currentStatement);
        currentStatement = null;
      }
    };
  };

  public abstract CDOClassifierRef readObjectType(IDBStoreAccessor dbStoreAccessor, CDOID id);

  public abstract long repairAfterCrash(IDBAdapter dbAdapter, Connection connection);

  protected abstract Collection<EClass> getClassesWithObjectInfo();

  // -- resource query handling ------------------------------------------

  public abstract void queryResources(IDBStoreAccessor dbStoreAccessor, QueryResourcesContext context);

  // -- database name demangling methods ---------------------------------

  public String getTableName(ENamedElement element)
  {
    String name = null;
    String typePrefix = null;

    if (element instanceof EClass)
    {
      name = isQualifiedNames() ? EMFUtil.getQualifiedName((EClass)element, NAME_SEPARATOR) : element.getName();
      typePrefix = TYPE_PREFIX_CLASS;
    }
    else if (element instanceof EPackage)
    {
      name = isQualifiedNames() ? EMFUtil.getQualifiedName((EPackage)element, NAME_SEPARATOR) : element.getName();
      typePrefix = TYPE_PREFIX_PACKAGE;
    }
    else
    {
      throw new ImplementationError("Unknown element: " + element); //$NON-NLS-1$
    }

    String prefix = getTableNamePrefix();
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    return getName(prefix + name, typePrefix + getMetaDataManager().getMetaID(element), getMaxTableNameLength());
  }

  public String getTableName(EClass eClass, EStructuralFeature feature)
  {
    String name = isQualifiedNames() ? EMFUtil.getQualifiedName(eClass, NAME_SEPARATOR) : eClass.getName();
    name += NAME_SEPARATOR;
    name += feature.getName();
    name += FEATEURE_TABLE_SUFFIX;

    String prefix = getTableNamePrefix();
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    return getName(prefix + name, TYPE_PREFIX_FEATURE + getMetaDataManager().getMetaID(feature),
        getMaxTableNameLength());
  }

  public String getFieldName(EStructuralFeature feature)
  {
    return getName(feature.getName(), TYPE_PREFIX_FEATURE + getMetaDataManager().getMetaID(feature),
        getMaxFieldNameLength());
  }

  private String getName(String name, String suffix, int maxLength)
  {
    boolean forceNamesWithID = isForceNamesWithID();
    if (store.getDBAdapter().isReservedWord(name))
    {
      forceNamesWithID = true;
    }

    if (name.length() > maxLength || forceNamesWithID)
    {
      suffix = NAME_SEPARATOR + suffix.replace('-', 'S');
      int length = Math.min(name.length(), maxLength - suffix.length());
      name = name.substring(0, length) + suffix;
    }

    return name;
  }

  // -- factories for mapping of classes, values, lists ------------------

  public void createMapping(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      mapPackageUnits(packageUnits);
      getStore().getDBAdapter().createTables(getModelTables(), connection);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  private void mapPackageInfos(InternalCDOPackageInfo[] packageInfos)
  {
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      EPackage ePackage = packageInfo.getEPackage();
      if (!CDOModelUtil.isCorePackage(ePackage))
      {
        mapClasses(EMFUtil.getPersistentClasses(ePackage));
      }
    }
  }

  private void mapClasses(EClass... eClasses)
  {
    for (EClass eClass : eClasses)
    {
      if (!(eClass.isInterface() || eClass.isAbstract()))
      {
        createClassMapping(eClass);
      }
    }
  }

  private void mapPackageUnits(InternalCDOPackageUnit[] packageUnits)
  {
    if (packageUnits != null && packageUnits.length != 0)
    {
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        mapPackageInfos(packageUnit.getPackageInfos());
      }
    }
  }

  private Set<IDBTable> getModelTables()
  {
    Set<IDBTable> tables = new HashSet<IDBTable>();
    for (IClassMapping mapping : classMappings.values())
    {
      tables.addAll(mapping.getDBTables());
    }

    return tables;
  }

  private IClassMapping createClassMapping(EClass eClass)
  {
    IClassMapping mapping = doCreateClassMapping(eClass);
    if (mapping != null)
    {
      classMappings.put(eClass, mapping);
    }

    return mapping;
  }

  protected abstract IClassMapping doCreateClassMapping(EClass eClass);

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
          createAllClassMappings();
          allClassMappingsCreated = true;
        }
      }
    }

    return classMappings;
  }

  private void createAllClassMappings()
  {
    IRepository repository = getStore().getRepository();
    CDOPackageRegistry packageRegistry = repository.getPackageRegistry();
    for (CDOPackageInfo packageInfo : packageRegistry.getPackageInfos())
    {
      if (!packageInfo.isSystemPackage())
      {
        for (EClassifier eClassifier : packageInfo.getEPackage().getEClassifiers())
        {
          if (eClassifier instanceof EClass)
          {
            EClass eClass = (EClass)eClassifier;
            if (!eClass.isAbstract() && !eClass.isInterface())
            {
              getClassMapping(eClass); // Get or create it
            }
          }
        }
      }
    }
  }

  public final IClassMapping getClassMapping(EClass eClass)
  {
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
        }
      }
    }

    return result;
  }

  public ITypeMapping createValueMapping(EStructuralFeature feature)
  {
    return TypeMappingFactory.createTypeMapping(this, feature);
  }

  public final IListMapping createListMapping(EClass containingClass, EStructuralFeature feature)
  {
    checkArg(feature.isMany(), "Only many-valued features allowed."); //$NON-NLS-1$
    IListMapping mapping = doCreateListMapping(containingClass, feature);
    return mapping;
  }

  public final IListMapping createFeatureMapMapping(EClass containingClass, EStructuralFeature feature)
  {
    checkArg(FeatureMapUtil.isFeatureMap(feature), "Only FeatureMaps allowed."); //$NON-NLS-1$
    IListMapping mapping = doCreateFeatureMapMapping(containingClass, feature);
    return mapping;
  }

  public abstract IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature);

  public abstract IListMapping doCreateFeatureMapMapping(EClass containingClass, EStructuralFeature feature);

  public void handleRevisions(IDBStoreAccessor accessor, CDORevisionHandler handler)
  {
    Collection<IClassMapping> values = getClassMappings().values();
    for (IClassMapping mapping : values)
    {
      ((AbstractHorizontalClassMapping)mapping).handleRevisions(accessor, handler);
    }
  }
}
