/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.evolution.util.ElementHandler;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.AbstractFeatureMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping2;
import org.eclipse.emf.cdo.server.db.mapping.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping4;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy.Props;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy3;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.evolution.Renamer;
import org.eclipse.emf.cdo.server.internal.db.mapping.AbstractMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractHorizontalMappingStrategy;
import org.eclipse.emf.cdo.server.spi.evolution.MigrationContext;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStoreMigrator
{
  private final IDBStoreAccessor accessor;

  private final MigrationContext context;

  private final Release release;

  private final Release oldRelease;

  private final IDBStore store;

  private final IDBAdapter adapter;

  private final IDBConnection connection;

  private final IMappingStrategy3 mappingStrategy;

  private final IMetaDataManager metaDataManager;

  private final InternalRepository repository;

  private final InternalCDOPackageRegistry repositoryPackageRegistry;

  private final Map<EModelElement, EModelElement> oldToRepositoryElements = new HashMap<EModelElement, EModelElement>();

  private final Map<EModelElement, EModelElement> repositoryToOldElements = new HashMap<EModelElement, EModelElement>();

  private final Map<IDBTable, ColumnRenamer> columnRenamers = new HashMap<IDBTable, ColumnRenamer>();

  public DBStoreMigrator(IDBStoreAccessor accessor, MigrationContext context, Release release)
  {
    this.accessor = accessor;
    this.context = context;
    this.release = release;

    store = accessor.getStore();
    adapter = store.getDBAdapter();
    connection = accessor.getDBConnection();
    mappingStrategy = (IMappingStrategy3)store.getMappingStrategy();
    metaDataManager = store.getMetaDataManager();

    repository = (InternalRepository)store.getRepository();
    repositoryPackageRegistry = repository.getPackageRegistry();
    oldRelease = release.getPreviousRelease();

    mapRepositoryAndOldElements();
    createAllRepositoryClassMappings();
  }

  public void migrate(OMMonitor monitor)
  {
    Statement statement = null;

    try
    {
      statement = connection.createStatement();
      migrate(statement, monitor);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(statement);
    }
  }

  private void migrate(Statement statement, OMMonitor monitor) throws SQLException
  {
    InternalCDOPackageRegistry newPackageRegistry = (InternalCDOPackageRegistry)release.createPackageRegistry();
    InternalCDOPackageUnit[] newPackageUnits = newPackageRegistry.getPackageUnits();
    Map<EModelElement, EModelElement> newToOldElements = release.getChange().getNewToOldElements();

    ////////////////////////////////////////////////////////////////////////////////////////
    // Compute all new class mappings in a temporary schema.
    ////////////////////////////////////////////////////////////////////////////////////////

    Map<EClass, IClassMapping2> newClassMappings = new HashMap<EClass, IClassMapping2>();
    boolean oldSkipMappingInitialization = AbstractMappingStrategy.setSkipMappingInitialization(true);

    try
    {
      IDBSchema schema = DBUtil.createSchema("v" + release.getVersion());

      for (EClass newClass : mappingStrategy.getMappedClasses(newPackageUnits))
      {
        IClassMapping2 newClassMapping = (IClassMapping2)mappingStrategy.createClassMapping(newClass);
        IDBTable newTable = newClassMapping.createTable(schema, mappingStrategy.getTableName(newClass));
        newClassMapping.setTable(newTable);
        newClassMappings.put(newClass, newClassMapping);

        for (IListMapping listMapping : newClassMapping.getListMappings())
        {
          IDBTable listTable = ((IListMapping4)listMapping).createTable(schema, mappingStrategy.getTableName(newClass, listMapping.getFeature()));
          ((IListMapping4)listMapping).setTable(listTable);
          monitor.checkCanceled();
        }

        monitor.checkCanceled();
      }

      // context.log("Computing " + DBUtil.dumpToString(schema));
    }
    finally
    {
      AbstractMappingStrategy.setSkipMappingInitialization(oldSkipMappingInitialization);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Create renaming rules for the tables that become obsolete after the migration.
    ////////////////////////////////////////////////////////////////////////////////////////

    Renamer tableRenamer = new TableRenamer(connection);
    Set<IDBTable> tablesToRemove = new HashSet<IDBTable>();

    for (ElementChange elementChange : release.getElementChanges(EcorePackage.Literals.ECLASS, ChangeKind.REMOVED))
    {
      EClass oldClass = (EClass)elementChange.getOldElement();
      EClass repositoryClass = (EClass)oldToRepositoryElements.get(oldClass);
      IClassMapping repositoryClassMapping = mappingStrategy.getClassMapping(repositoryClass);

      for (IDBTable table : repositoryClassMapping.getDBTables())
      {
        if (table != null)
        {
          tableRenamer.addName(table.getName());
          tablesToRemove.add(table);
        }

        monitor.checkCanceled();
      }

      monitor.checkCanceled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Create renaming rules for the mapped tables of renamed classes and features.
    // Create renaming rules for the columns that become obsolete after the migration.
    ////////////////////////////////////////////////////////////////////////////////////////

    Set<IClassMapping2> addedClassMappings = new HashSet<IClassMapping2>();
    Map<CDOID, Pair<CDOID, ? extends EModelElement>> oldToNewMetaIDs = new HashMap<CDOID, Pair<CDOID, ? extends EModelElement>>();
    List<IDBField> fieldsToRemove = new ArrayList<IDBField>();

    for (IClassMapping2 newClassMapping : newClassMappings.values())
    {
      EClass newClass = newClassMapping.getEClass();
      EClass oldClass = (EClass)newToOldElements.get(newClass);

      IClassMapping2 oldClassMapping = oldClass == null ? null : (IClassMapping2)mappingStrategy.getClassMapping(oldClass);
      IDBTable table = oldClassMapping == null ? null : oldClassMapping.getTable();

      String oldTableName = table == null ? null : mappingStrategy.getTableName(oldClass);
      String newTableName = mappingStrategy.getTableName(newClass);

      if (!newTableName.equals(oldTableName))
      {
        tableRenamer.addNames(oldTableName, newTableName);
      }

      if (oldClass != null)
      {
        if (newClass != null)
        {
          if (table != null)
          {
            for (ITypeMapping newValueMapping : newClassMapping.getValueMappings())
            {
              EStructuralFeature newFeature = newValueMapping.getFeature();
              EStructuralFeature oldFeature = (EStructuralFeature)newToOldElements.get(newFeature);

              String oldFieldName = oldFeature == null ? null : mappingStrategy.getFieldName(oldFeature);
              String newFieldName = mappingStrategy.getFieldName(newFeature);

              if (!newFieldName.equals(oldFieldName))
              {
                ColumnRenamer columnRenamer = getColumnRenamer(table);
                columnRenamer.addNames(oldFieldName, newFieldName);
              }
              else
              {
                renameChangedColumn(oldClass, oldFeature, newClassMapping, newValueMapping, table, fieldsToRemove);
              }

              monitor.checkCanceled();
            }
          }

          for (IListMapping newListMapping : newClassMapping.getListMappings())
          {
            EStructuralFeature newFeature = newListMapping.getFeature();
            EStructuralFeature oldFeature = (EStructuralFeature)newToOldElements.get(newFeature);

            IListMapping4 oldListMapping = oldFeature == null ? null : (IListMapping4)oldClassMapping.getListMapping(oldFeature);
            IDBTable listTable = oldListMapping == null ? null : oldListMapping.getTable();

            oldTableName = listTable == null ? null : mappingStrategy.getTableName(oldClass, oldFeature);
            newTableName = mappingStrategy.getTableName(newClass, newFeature);

            if (!newTableName.equals(oldTableName))
            {
              tableRenamer.addNames(oldTableName, newTableName);
            }

            if (table != null)
            {
              String oldListSizeFieldName = oldFeature == null ? null : mappingStrategy.getFieldName(oldFeature);
              String newListSizeFieldName = mappingStrategy.getFieldName(newFeature);

              if (!newListSizeFieldName.equals(oldListSizeFieldName))
              {
                ColumnRenamer columnRenamer = getColumnRenamer(table);
                columnRenamer.addNames(oldListSizeFieldName, newListSizeFieldName);
              }
              else
              {
                renameChangedColumn(oldClass, oldFeature, newClassMapping, newListMapping, table, fieldsToRemove);
              }
            }

            monitor.checkCanceled();
          }

          runColumnRenamer(table);

          CDOID oldMetaID = metaDataManager.getMetaID(oldClass, CDOBranchPoint.UNSPECIFIED_DATE);
          CDOID newMetaID = metaDataManager.getMetaID(newClass, CDOBranchPoint.UNSPECIFIED_DATE);
          if (!ObjectUtil.equals(oldMetaID, newMetaID))
          {
            oldToNewMetaIDs.put(oldMetaID, Pair.create(newMetaID, newClass));

            if (mappingStrategy instanceof AbstractHorizontalMappingStrategy)
            {
              AbstractHorizontalMappingStrategy horizontalMappingStrategy = (AbstractHorizontalMappingStrategy)mappingStrategy;

              int count = horizontalMappingStrategy.getObjectTypeMapper().changeObjectType(accessor, oldClass, newClass);
              if (count > 0)
              {
                context.log("Changed type of " + count + " objects from " + oldMetaID + " to " + newMetaID //
                    + " (" + ElementHandler.getLabel(oldClass) + " --> " + ElementHandler.getLabel(newClass) + ")");
              }
            }
          }
        }
      }
      else
      {
        addedClassMappings.add(newClassMapping);
      }

      monitor.checkCanceled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    tableRenamer.run();

    ////////////////////////////////////////////////////////////////////////////////////////
    // Create table columns for features added to existing classes, i.e.:
    // 1. A value column for a new single-valued feature
    // 2. A list size column for a new many-valued feature
    // 3. A boolean column for a new unsettable feature
    //
    // Create table columns for existing features that map to a different column type.
    ////////////////////////////////////////////////////////////////////////////////////////

    IDBSchemaTransaction schemaTransaction = null;

    try
    {
      for (IClassMapping2 newClassMapping : newClassMappings.values())
      {
        EClass newClass = newClassMapping.getEClass();
        EClass oldClass = (EClass)newToOldElements.get(newClass);
        if (oldClass != null)
        {
          EClass repositoryClass = (EClass)oldToRepositoryElements.get(oldClass);

          IClassMapping2 repositoryClassMapping = (IClassMapping2)mappingStrategy.getClassMapping(repositoryClass);
          if (repositoryClassMapping != null)
          {
            IDBTable table = repositoryClassMapping.getTable();
            if (table != null)
            {
              for (IFeatureMapping newFeatureMapping : newClassMapping.getFeatureMappings())
              {
                EStructuralFeature newFeature = newFeatureMapping.getFeature();
                EStructuralFeature oldFeature = (EStructuralFeature)newToOldElements.get(newFeature);
                if (oldFeature == null)
                {
                  // Create table column for the added feature.
                  if (schemaTransaction == null)
                  {
                    schemaTransaction = store.getDatabase().openSchemaTransaction(connection);
                  }

                  IDBTable workingTable = schemaTransaction.getWorkingCopy().getTable(table.getName());
                  IFeatureMapping featureMapping = repositoryClassMapping.createFeatureMappings(workingTable, false, newFeature)[0];

                  IDBField field = AbstractFeatureMapping.getField(repositoryClassMapping, featureMapping);
                  context.log("Creating column for new feature " + newClass.getName() + "." + newFeature.getName() + " --> " + field.getFullName());
                }
                else
                {
                  EStructuralFeature repositoryFeature = (EStructuralFeature)oldToRepositoryElements.get(oldFeature);
                  IFeatureMapping repositoryFeatureMapping = repositoryClassMapping.getFeatureMapping(repositoryFeature);

                  IDBField repositoryField = AbstractFeatureMapping.getField(repositoryClassMapping, repositoryFeatureMapping);
                  IDBField newField = AbstractFeatureMapping.getField(newClassMapping, newFeatureMapping);

                  if (!newField.isAssignableFrom(repositoryField))
                  {
                    // Create table column for the changed feature.
                    if (schemaTransaction == null)
                    {
                      schemaTransaction = store.getDatabase().openSchemaTransaction(connection);
                    }

                    IDBTable workingTable = schemaTransaction.getWorkingCopy().getTable(table.getName());
                    IFeatureMapping featureMapping = repositoryClassMapping.createFeatureMappings(workingTable, false, newFeature)[0];

                    IDBField field = AbstractFeatureMapping.getField(repositoryClassMapping, featureMapping);
                    context.log("Creating column for changed feature " + newClass.getName() + "." + newFeature.getName() + " --> " + field.getFullName());
                  }
                }
              }
            }
          }
        }
      }

      if (schemaTransaction != null)
      {
        IDBSchemaDelta schemaDelta = schemaTransaction.getSchemaDelta();
        context.log(DBUtil.dumpToString(schemaDelta));

        schemaTransaction.commit();
      }
    }
    finally
    {
      IOUtil.close(schemaTransaction);
      schemaTransaction = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    context.log("######################");
    context.log("Migrating instances...");
    context.log("######################");

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    if (!tablesToRemove.isEmpty())
    {
      context.log("Dropping obsolete tables:");

      for (IDBTable table : tablesToRemove)
      {
        context.log("   " + table);
        adapter.dropTable(table, statement);
        monitor.checkCanceled();
      }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    if (!fieldsToRemove.isEmpty())
    {
      context.log("Dropping obsolete columns:");
      schemaTransaction = store.getDatabase().openSchemaTransaction(connection);
      IDBSchema workingSchema = schemaTransaction.getWorkingCopy();

      try
      {
        for (IDBField field : fieldsToRemove)
        {
          context.log("   " + field.getFullName());

          IDBTable workingTable = workingSchema.getTableSafe(field.getTable().getName());
          IDBField workingField = workingTable.getFieldSafe(field.getName());
          workingField.remove();
          monitor.checkCanceled();
        }

        IDBSchemaDelta schemaDelta = schemaTransaction.getSchemaDelta();
        context.log(DBUtil.dumpToString(schemaDelta));

        schemaTransaction.commit();
      }
      finally
      {
        IOUtil.close(schemaTransaction);
        schemaTransaction = null;
      }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    if (oldRelease != null)
    {
      context.log("Removing old package units from system tables:");
      List<InternalCDOPackageUnit> repositoryPackageUnits = new ArrayList<InternalCDOPackageUnit>();

      for (EPackage oldRootPackage : oldRelease.getRootPackages())
      {
        EPackage repositoryRootPackage = (EPackage)oldToRepositoryElements.get(oldRootPackage);
        InternalCDOPackageUnit repositoryPackageUnit = repositoryPackageRegistry.getPackageUnit(repositoryRootPackage);
        repositoryPackageUnits.add(repositoryPackageUnit);
        context.log("   " + repositoryPackageUnit.getID());
      }

      InternalCDOPackageUnit[] packageUnits = repositoryPackageUnits.toArray(new InternalCDOPackageUnit[repositoryPackageUnits.size()]);
      metaDataManager.deletePackageUnits(connection, packageUnits, monitor);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    context.log("Adding new package units to system tables:");
    for (InternalCDOPackageUnit newPackageUnit : newPackageUnits)
    {
      context.log("   " + newPackageUnit.getID());
    }

    metaDataManager.writePackageUnits(accessor.getConnection(), newPackageUnits, monitor);

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    context.log("Committing migration results");
    connection.commit();

    context.log("Reinitializing package registry");
    LifecycleUtil.deactivate(repositoryPackageRegistry);
    LifecycleUtil.activate(repositoryPackageRegistry);
    Repository.readPackageUnits(accessor, repositoryPackageRegistry);

    context.log("Resetting metadata manager");
    metaDataManager.clearMetaIDMappings();

    context.log("Recomputing class mappings");
    mappingStrategy.clearClassMappings();
    mappingStrategy.getClassMappings(true);

  }

  /**
   * Build bidirectional mappings between repository and previous release classifiers.
   */
  private void mapRepositoryAndOldElements()
  {
    if (oldRelease != null)
    {
      for (EPackage oldPackage : oldRelease.getAllPackages())
      {
        EPackage repositoryPackage = repositoryPackageRegistry.getEPackage(oldPackage.getNsURI());
        if (repositoryPackage != null)
        {
          repositoryToOldElements.put(repositoryPackage, oldPackage);
          oldToRepositoryElements.put(oldPackage, repositoryPackage);

          for (EClassifier oldClassifier : oldPackage.getEClassifiers())
          {
            EClassifier repositoryClassifier = repositoryPackage.getEClassifier(oldClassifier.getName());
            if (repositoryClassifier != null)
            {
              repositoryToOldElements.put(repositoryClassifier, oldClassifier);
              oldToRepositoryElements.put(oldClassifier, repositoryClassifier);

              if (oldClassifier instanceof EClass)
              {
                EClass oldClass = (EClass)oldClassifier;
                EClass repositoryClass = (EClass)repositoryClassifier;

                for (EStructuralFeature oldFeature : oldClass.getEStructuralFeatures())
                {
                  EStructuralFeature repositoryFeature = repositoryClass.getEStructuralFeature(oldFeature.getName());
                  if (repositoryFeature != null)
                  {
                    repositoryToOldElements.put(repositoryFeature, oldFeature);
                    oldToRepositoryElements.put(oldFeature, repositoryFeature);
                  }
                }
              }
              else if (oldClassifier instanceof EEnum)
              {
                EEnum oldEnum = (EEnum)oldClassifier;
                EEnum repositoryEnum = (EEnum)repositoryClassifier;

                for (EEnumLiteral oldLiteral : oldEnum.getELiterals())
                {
                  EEnumLiteral repositoryLiteral = repositoryEnum.getEEnumLiteral(oldLiteral.getName());
                  if (repositoryLiteral != null)
                  {
                    repositoryToOldElements.put(repositoryLiteral, oldLiteral);
                    oldToRepositoryElements.put(oldLiteral, repositoryLiteral);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void createAllRepositoryClassMappings()
  {
    Map<String, String> mappingStrategyProperties = mappingStrategy.getProperties();
    String oldEagerTableCreation = mappingStrategyProperties.put(Props.EAGER_TABLE_CREATION, String.valueOf(Boolean.FALSE));

    try
    {
      mappingStrategy.getClassMappings(true);
    }
    finally
    {
      mappingStrategyProperties.put(Props.EAGER_TABLE_CREATION, oldEagerTableCreation);
    }
  }

  private ColumnRenamer getColumnRenamer(IDBTable table)
  {
    ColumnRenamer columnRenamer = columnRenamers.get(table);
    if (columnRenamer == null)
    {
      columnRenamer = new ColumnRenamer(connection, table);
      columnRenamers.put(table, columnRenamer);
    }

    return columnRenamer;
  }

  private ColumnRenamer runColumnRenamer(IDBTable table)
  {
    ColumnRenamer columnRenamer = columnRenamers.get(table);
    if (columnRenamer != null)
    {
      columnRenamer.run();
    }

    return columnRenamer;
  }

  private void renameChangedColumn(EClass oldClass, EStructuralFeature oldFeature, IClassMapping2 newClassMapping, IFeatureMapping newFeatureMapping,
      IDBTable table, List<IDBField> columnsToDelete)
  {
    EClass repositoryClass = (EClass)oldToRepositoryElements.get(oldClass);
    IClassMapping2 repositoryClassMapping = (IClassMapping2)mappingStrategy.getClassMapping(repositoryClass);

    EStructuralFeature repositoryFeature = (EStructuralFeature)oldToRepositoryElements.get(oldFeature);
    IFeatureMapping repositoryFeatureMapping = repositoryClassMapping.getFeatureMapping(repositoryFeature);

    IDBField repositoryField = AbstractFeatureMapping.getField(repositoryClassMapping, repositoryFeatureMapping);
    IDBField newField = AbstractFeatureMapping.getField(newClassMapping, newFeatureMapping);

    if (!newField.isAssignableFrom(repositoryField))
    {
      ColumnRenamer columnRenamer = getColumnRenamer(table);
      columnRenamer.addName(repositoryField.getName());

      columnsToDelete.add(repositoryField);
    }
  }

  /**
   * @author Eike Stepper
   */
  public abstract class LoggingRenamer extends Renamer
  {
    private int count;

    private String header;

    public LoggingRenamer(String header)
    {
      this.header = header;
    }

    public int getCount()
    {
      return count;
    }

    @Override
    public void run()
    {
      try
      {
        super.run();

        if (count != 0)
        {
          doFinally(true);
        }
      }
      catch (RuntimeException ex)
      {
        if (count != 0)
        {
          doFinally(false);
        }

        throw ex;
      }
      catch (Error ex)
      {
        if (count != 0)
        {
          doFinally(false);
        }

        throw ex;
      }
    }

    protected void doInit()
    {
    }

    @Override
    protected final void doRename(String oldName, String newName)
    {
      if (++count == 1)
      {
        context.log(header);
        doInit();
      }

      context.log("   " + oldName + " --> " + newName);
      doRename(oldName, newName, count);
    }

    protected abstract void doRename(String oldName, String newName, int count);

    protected void doFinally(boolean success)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  public abstract class JDBCRenamer extends LoggingRenamer
  {
    private final IDBConnection connection;

    private Statement statement;

    public JDBCRenamer(IDBConnection connection, String header)
    {
      super(header);
      this.connection = connection;
    }

    @Override
    protected void doInit()
    {
      try
      {
        statement = connection.createStatement();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    @Override
    protected void doRename(String oldName, String newName, int count)
    {
      String sql = getSQL(oldName, newName);

      try
      {
        statement.execute(sql);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    protected abstract String getSQL(String oldName, String newName);
  }

  /**
   * @author Eike Stepper
   */
  public class TableRenamer extends JDBCRenamer
  {
    public TableRenamer(IDBConnection connection)
    {
      super(connection, "Renaming tables:");
    }

    @Override
    protected void initNames()
    {
      // Initialize the renamer with all existing table names.
      for (IDBTable table : accessor.getStore().getDBSchema().getTables())
      {
        addNames(table.getName(), null);
      }
    }

    @Override
    protected String getSQL(String oldName, String newName)
    {
      return adapter.sqlRenameTable(newName, oldName);
    }

    @Override
    protected void doRename(String oldName, String newName, int count)
    {
      super.doRename(oldName, newName, count);

      InternalDBSchema schema = (InternalDBSchema)accessor.getStore().getDBSchema();
      IDBTable table = schema.getTable(oldName);
      schema.unlock();

      try
      {
        table.rename(newName);
      }
      finally
      {
        schema.lock();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ColumnRenamer extends JDBCRenamer
  {
    private final IDBTable table;

    public ColumnRenamer(IDBConnection connection, IDBTable table)
    {
      super(connection, "Renaming columns of table " + table + ":");
      this.table = table;
    }

    @Override
    protected void initNames()
    {
      // Initialize the renamer with all existing column names.
      for (IDBField field : table.getFields())
      {
        addNames(field.getName(), null);
      }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected String getSQL(String oldName, String newName)
    {
      IDBField field = table.getField(oldName);
      String restoreName = field.getName();
      field.setName(newName);

      try
      {
        return adapter.sqlRenameField(field, oldName);
      }
      finally
      {
        field.setName(restoreName);
      }
    }

    @Override
    protected void doRename(String oldName, String newName, int count)
    {
      super.doRename(oldName, newName, count);

      InternalDBSchema schema = (InternalDBSchema)accessor.getStore().getDBSchema();
      IDBField field = table.getField(oldName);
      schema.unlock();

      try
      {
        field.rename(newName);
      }
      finally
      {
        schema.lock();
      }
    }
  }
}
