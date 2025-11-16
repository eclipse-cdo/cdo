/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.evolution.phased;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.TreeMapping;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.evolution.phased.Context.Model;
import org.eclipse.emf.cdo.server.db.evolution.phased.ISchemaMigration.SchemaMigrationNotSupportedException;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.MetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageInfosTable;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageUnitsTable;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Evolves the models with the given mapping strategy, context, and store accessor.
 * <p>
 * This method performs the following steps:
 * <ol>
 * <li>It delegates the model evolution to the mapping strategy, which is responsible for updating the database schema,
 * migrating the existing data, and changing the container feature IDs in case of shifted features.</li>
 * <li>It updates the system tables, including the <code>cdo_package_units</code> table, <code>cdo_package_infos</code> table, and
 * <code>cdo_external_refs</code> table.</li>
 * <li>It commits the transaction.</li>
 * </ol>
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public class DefaultSchemaMigrator extends BasicPhaseHandler
{
  /**
   * The factory type of the default schema migrator.
   */
  public static final String FACTORY_TYPE = "default-schema-migrator"; //$NON-NLS-1$

  private boolean deleteObsoleteMetaIDs;

  /**
   * Creates a schema migrator.
   */
  public DefaultSchemaMigrator()
  {
  }

  /**
   * Returns whether to delete obsolete meta ID mappings from the cdo_external_refs table.
   */
  public boolean isDeleteObsoleteMetaIDs()
  {
    return deleteObsoleteMetaIDs;
  }

  /**
   * Sets whether to delete obsolete meta ID mappings from the cdo_external_refs table.
   */
  @InjectAttribute(name = "deleteObsoleteMetaIDs")
  public void setDeleteObsoleteMetaIDs(boolean deleteObsoleteMetaIDs)
  {
    checkInactive();
    this.deleteObsoleteMetaIDs = deleteObsoleteMetaIDs;
  }

  /**
   * Evolves the models with the current mapping strategy.
   */
  @Override
  public void execute(Context context) throws Exception
  {
    IDBStore store = context.getSupport().getStore();

    // Check whether the mapping strategy supports model evolution.
    ISchemaMigration schemaMigration = ObjectUtil.tryCast(store.getMappingStrategy(), ISchemaMigration.class, //
        () -> new SchemaMigrationNotSupportedException());

    // Obtain a store accessor and set it in the thread local.
    IDBStoreAccessor accessor = store.getWriter(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      // Let the mapping strategy migrate the database. This includes:
      // - updating the database schema,
      // - migrating the existing data,
      // - changing the container feature IDs, in case of shifted features,
      migrateSchema(context, accessor, schemaMigration);

      // Update the system tables. This includes:
      // - updating the cdo_package_units table,
      // - updating the cdo_package_infos table,
      // - updating the model elements' meta IDs in the cdo_external_refs table.
      updateSystemTables(context, accessor);

      context.log("Schema migration completed with " + context.getTotalUpdateCount() + " row updates");
      commitChanges(context, accessor);
    }
    finally
    {
      // Release the store accessor and clear the thread local.
      StoreThreadLocal.release();
    }
  }

  /**
   * Delegates the schema migration to the mapping strategy.
   */
  protected void migrateSchema(Context context, IDBStoreAccessor accessor, ISchemaMigration schemaMigration) throws SQLException
  {
    context.log("Migrating schema with mapping strategy " + ReflectUtil.getSimpleClassName(schemaMigration));
    schemaMigration.migrateSchema(context, accessor);
  }

  /**
   * Updates the system tables with the changed models.
   */
  protected void updateSystemTables(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    context.log("Updating system tables with changed models");

    IDBConnection connection = accessor.getDBConnection();
    DBStore store = (DBStore)accessor.getStore();
    MetaDataManager metaDataManager = (MetaDataManager)store.getMetaDataManager();
    PackageUnitsTable packageUnits = store.tables().packageUnits();
    PackageInfosTable packageInfos = store.tables().packageInfos();
    InternalCDOPackageRegistry packageRegistry = context.getSupport().getNewPackageRegistry();

    // Update the changed models in the database.
    try (Statement statement = connection.createStatement())
    {
      // Replace the changed package units and package infos.
      for (Model model : context.getChangeInfos().keySet())
      {
        String unitID = model.getID();

        // Delete the old package unit and package info(s).
        context.log("Deleting old package unit " + unitID);
        int count1 = statement.executeUpdate("DELETE from " + packageUnits + " WHERE " + packageUnits.id() + "='" + unitID + "'");
        context.incrementTotalUpdateCount(count1);

        // Delete the old package info(s).
        context.log("Deleting old package infos for " + unitID);
        int count2 = statement.executeUpdate("DELETE from " + packageInfos + " WHERE " + packageInfos.unit() + "='" + unitID + "'");
        context.incrementTotalUpdateCount(count2);

        // Get the registered package bytes.
        EPackage newPackage = model.getNewPackage();
        byte[] packageBytes = EMFUtil.getEPackageBytes(newPackage, metaDataManager.isZipPackageBytes(), packageRegistry);

        // Write the new package unit.
        context.log("Writing new package unit " + unitID);
        metaDataManager.writePackageUnit(connection, unitID, model.getOriginalType().ordinal(), context.getTimeStamp(), packageBytes, null);
        context.incrementTotalUpdateCount();

        // Write the new package info(s).
        for (EPackage ePackage : EMFUtil.getAllPackages(newPackage))
        {
          context.log("Writing new package info for " + ePackage.getNsURI());
          metaDataManager.writePackageInfo(connection, ePackage.getNsURI(), EMFUtil.getParentURI(ePackage), unitID, null);
          context.incrementTotalUpdateCount();
        }
      }

      if (deleteObsoleteMetaIDs)
      {
        // Clean up obsolete meta ID mappings.
        TreeMapping<EObject> elementMappings = context.getElementMappings();
        Set<String> newURIs = elementMappings.getToObjectsByURI().keySet();

        elementMappings.getFromObjectsByURI().forEach((oldURI, oldModelElement) -> {
          if (oldModelElement instanceof EModelElement && !newURIs.contains(oldURI))
          {
            context.log("Deleting obsolete meta ID mapping for " + oldURI);
            metaDataManager.deleteMetaIDMapping(statement, (EModelElement)oldModelElement);
            context.incrementTotalUpdateCount();
          }
        });
      }
    }
  }

  /**
   * Commits the model evolution changes to the database.
   */
  protected boolean commitChanges(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    context.log("Committing model evolution changes to the database");
    accessor.getConnection().commit();
    return false;
  }
}