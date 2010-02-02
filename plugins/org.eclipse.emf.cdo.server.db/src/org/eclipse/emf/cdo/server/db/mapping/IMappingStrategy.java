/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444  
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.util.Map;

/**
 * The mapping strategy acts as a connection between the DBStore and the database management (and OR-mapping) classes.
 * The {@link DBStore} uses methods of this interface to create and lookup mappings (or mappers, as they could also be
 * named as such) and to get properties and informations about the mappings used. The mapping classes (e.g., instances
 * of IClassMapping and IListMapping) also use this class as a central point of information and as a resource of common
 * functionalities.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IMappingStrategy
{
  /**
   * Name of the integer property that configures the maximum length for table names. A value of zero indicates the
   * value of the {@link IDBAdapter#getMaxTableNameLength() db adapter} to be used.
   */
  public static final String PROP_MAX_TABLE_NAME_LENGTH = "maxTableNameLength"; //$NON-NLS-1$

  /**
   * Name of the integer property that configures the maximum length for column names. A value of zero indicates the
   * value of the {@link IDBAdapter#getMaxFieldNameLength() db adapter} to be used.
   */
  public static final String PROP_MAX_FIELD_NAME_LENGTH = "maxFieldNameLength"; //$NON-NLS-1$

  /**
   * Name of the String property that specifies a common prefix for table names.
   */
  public static final String PROP_TABLE_NAME_PREFIX = "tableNamePrefix"; //$NON-NLS-1$

  /**
   * Name of the boolean property that configures whether the table names are made of simple class names or of qualified
   * class names.
   */
  public static final String PROP_QUALIFIED_NAMES = "qualifiedNames"; //$NON-NLS-1$

  /**
   * Name of the boolean property that configures whether table names and column names are always suffixed with the
   * internal DBID or only in cases where generated names violate the naming constraints of the underlying backend.
   */
  public static final String PROP_FORCE_NAMES_WITH_ID = "forceNamesWithID"; //$NON-NLS-1$

  /**
   * @return the store, this MappingStrategy instance belongs to.
   */
  public IDBStore getStore();

  /**
   * Set the store to which this MappingStrategy instance belongs. Should only be called by the {@link DBStore}, and
   * only once to initialize the connection between {@link DBStore} and mapping strategy.
   * 
   * @param dbStore
   *          the DBStore instance to which this MappingStrategy instance belongs.
   */
  public void setStore(IDBStore dbStore);

  /**
   * Factory for value mappings of single-valued attributes.
   * 
   * @param feature
   *          the feature for which a mapping should be created. It must hold <code>feature.isMany() == false</code>.
   * @return the mapping created.
   */
  public ITypeMapping createValueMapping(EStructuralFeature feature);

  /**
   * Factory for value mappings of multi-valued-attributes.
   * 
   * @param containingClass
   *          the class containing the feature.
   * @param feature
   *          the feature for which a mapping should be created. It must hold <code>feature.isMany() == true</code>.
   * @return
   */
  public IListMapping createListMapping(EClass containingClass, EStructuralFeature feature);

  /**
   * Create a suitable table name which can be used to map the given element. Should only be called by mapping classes.
   * 
   * @param element
   *          the element for which the name should be created. It must hold:
   *          <code>element instanceof EClass || element instanceof EPackage</code>.
   * @return the created table name. It is guaranteed that the table name is compatible with the chosen database.
   */
  public String getTableName(ENamedElement element);

  /**
   * Create a suitable table name which can be used to map the given element. Should only be called by mapping classes.
   * Should only be called by mapping classes.
   * 
   * @param containingClass
   *          the class containeng the feature.
   * @param feature
   *          the feature for which the table name should be created.
   * @return the created table name. It is guaranteed that the table name is compatible with the chosen database.
   */
  public String getTableName(EClass containingClass, EStructuralFeature feature);

  /**
   * Create a suitable column name which can be used to map the given element. Should only be called by mapping classes.
   * 
   * @param feature
   *          the feature for which the column name should be created.
   * @return the created column name. It is guaranteed that the name is compatible with the chosen database.
   */
  public String getFieldName(EStructuralFeature feature);

  /**
   * Create and initialize the mapping infrastructure for the given packages. Should be called from the DBStore or the
   * DBStoreAccessor.
   * 
   * @param connection
   *          the connection to use.
   * @param packageUnits
   *          the packages whose elements should be mapped.
   * @param monitor
   *          the monitor to report progress.
   */
  public void createMapping(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor);

  /**
   * Look up an existing class mapping for the given class. Before this method is called, the class mapping must have
   * been initialized by calling {@link #createMapping(Connection, InternalCDOPackageUnit[], OMMonitor)} on its
   * containing package.
   * 
   * @param eClass
   *          the class to look up.
   * @return the class mapping.
   */
  public IClassMapping getClassMapping(EClass eClass);

  /**
   * Query if this mapping supports revision deltas. <br>
   * If this method returns <code>true</code>, it is guaranteed that all class mappings returned by
   * {@link #getClassMapping(EClass)} implement {@link IClassMappingDeltaSupport}.
   * 
   * @return <code>true</code> if revision deltas are supported, <code>false</code> else.
   */
  public boolean hasDeltaSupport();

  /**
   * Query if this mapping supports audits. <br>
   * If this method returns <code>true</code>, it is guaranteed that all class mappings returned by
   * {@link #getClassMapping(EClass)} implement {@link IClassMappingAuditSupport}.
   * 
   * @return <code>true</code> if audits are supported, <code>false</code> else.
   */
  public boolean hasAuditSupport();

  /**
   * Query if this mapping supports branches. <br>
   * If this method returns <code>true</code>, it is guaranteed that all class mappings returned by
   * {@link #getClassMapping(EClass)} implement {@link IClassMappingBranchingSupport}.
   * 
   * @return <code>true</code> if branches are supported, <code>false</code> else.
   * @since 3.0
   */
  public boolean hasBranchingSupport();

  /**
   * Execute a resource query.
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @param context
   *          the context from which the query parameters are read and to which the result is written.
   */
  public void queryResources(IDBStoreAccessor dbStoreAccessor, QueryResourcesContext context);

  /**
   * Read the type (i.e. class) of the object referred to by a given ID.
   * 
   * @param dbStoreAccessor
   *          the accessor to use to look up the type.
   * @param id
   *          the ID of the object for which the type is to be determined.
   * @return the type of the object.
   */
  public CDOClassifierRef readObjectType(IDBStoreAccessor dbStoreAccessor, CDOID id);

  /**
   * Get an iterator over all instances of objects in the store.
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @return the iterator.
   */
  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor dbStoreAccessor);

  /**
   * Return the maximum object id used in the store. This is used by the DBStore if a previous crash is discovered
   * during the startup process. Should only be called by the DBStore and only during startup.
   * 
   * @param dbAdapter
   *          the dbAdapter to use to access the database
   * @param connection
   *          the connection to use to access the database
   * @return the maximum object id used in the store.
   */
  public long repairAfterCrash(IDBAdapter dbAdapter, Connection connection);

  /**
   * Set configuration properties for this mapping strategy. Should only be called by the factory creating the mapping
   * strategy instance.
   * 
   * @param properties
   *          the configuration properties to set.
   */
  public void setProperties(Map<String, String> properties);
}
