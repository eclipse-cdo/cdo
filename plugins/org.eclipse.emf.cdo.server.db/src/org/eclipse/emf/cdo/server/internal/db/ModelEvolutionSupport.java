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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.TreeMapping;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IModelEvolutionSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy.ModelEvolution;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy.ModelEvolution.ModelEvolutionNotSupportedException;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageInfosTable;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageUnitsTable;

import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class ModelEvolutionSupport implements IModelEvolutionSupport
{
  public ModelEvolutionSupport()
  {
  }

  @Override
  public boolean evolveModels(Context context) throws SQLException
  {
    IDBStore store = context.getStore();

    // Check whether the mapping strategy supports model evolution.
    ModelEvolution mappingStrategy = ObjectUtil.tryCast(store.getMappingStrategy(), ModelEvolution.class, //
        () -> new ModelEvolutionNotSupportedException());

    context.log("Starting model evolution for repository " + store.getRepository().getName() + (context.isDry() ? " (dry run)" : ""));

    // Obtain a store accessor and set it in the thread local.
    IDBStoreAccessor accessor = store.getWriter(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      // Evolve the models with the model evolution context and the store accessor.
      return evolveModels(context, accessor, mappingStrategy);
    }
    finally
    {
      // Release the store accessor and clear the thread local.
      StoreThreadLocal.release();
    }
  }

  /**
   * Evolves the models with the given mapping strategy, context, and store accessor.
   * <p>
   * This method performs the following steps:
   * <ol>
   * <li>It delegates the model evolution to the mapping strategy, which is responsible for updating the database schema,
   * migrating the existing data, and changing the container feature IDs in case of shifted features.</li>
   * <li>It updates the system tables, including the cdo_package_units table, cdo_package_infos table, and
   * cdo_external_refs table.</li>
   * <li>It commits the transaction unless it's a dry run, in which case it rolls back the changes.</li>
   * </ol>
   * @return
   */
  protected boolean evolveModels(Context context, IDBStoreAccessor accessor, ModelEvolution mappingStrategy) throws SQLException
  {
    // Let the mapping strategy evolve the models. This includes:
    // - updating the database schema,
    // - migrating the existing data,
    // - changing the container feature IDs, in case of shifted features,
    boolean triggerCrashRecovery = delegateToMappingStrategy(context, accessor, mappingStrategy);

    // Update the system tables. This includes:
    // - updating the cdo_package_units table,
    // - updating the cdo_package_infos table,
    // - updating the model elements' meta IDs in the cdo_external_refs table.
    triggerCrashRecovery |= updateSystemTables(context, accessor);

    context.log("Model evolution completed with " + context.getTotalUpdateCount() + " row updates");

    // Commit the transaction unless it's a dry run.
    if (context.isDry())
    {
      rollback(context, accessor);
      return false;
    }

    triggerCrashRecovery |= commit(context, accessor);
    return triggerCrashRecovery;
  }

  protected boolean delegateToMappingStrategy(Context context, IDBStoreAccessor accessor, ModelEvolution mappingStrategy) throws SQLException
  {
    return mappingStrategy.evolveModels(context, accessor);
  }

  protected boolean updateSystemTables(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    IDBConnection connection = accessor.getDBConnection();
    DBStore store = (DBStore)accessor.getStore();
    MetaDataManager metaDataManager = (MetaDataManager)store.getMetaDataManager();
    PackageUnitsTable packageUnits = store.tables().packageUnits();
    PackageInfosTable packageInfos = store.tables().packageInfos();

    // Update the changed models in the database.
    try (Statement statement = connection.createStatement())
    {
      // Replace the changed package units and package infos.
      for (Model model : context.getChangedModels())
      {
        String unitID = model.getID();

        // Delete the old package unit and package info(s).
        statement.execute("DELETE from " + packageUnits + " WHERE " + packageUnits.id() + "='" + unitID + "'");
        statement.execute("DELETE from " + packageInfos + " WHERE " + packageInfos.unit() + "='" + unitID + "'");

        // Get the registered package bytes.
        EPackage registeredPackage = model.getRegisteredPackage();
        byte[] packageBytes = MetaDataManager.getEPackageBytes(registeredPackage, EPackage.Registry.INSTANCE);

        // Write the new package unit.
        metaDataManager.writePackageUnit(connection, unitID, model.getOriginalType().ordinal(), context.getTimeStamp(), packageBytes, null);

        // Write the new package info(s).
        for (EPackage ePackage : EMFUtil.getAllPackages(registeredPackage))
        {
          metaDataManager.writePackageInfo(connection, ePackage.getNsURI(), EMFUtil.getParentURI(ePackage), unitID, null);
        }
      }

      // Clean up obsolete meta ID mappings.
      TreeMapping<EObject> elementMappings = context.getElementMappings();
      Set<String> registeredURIs = elementMappings.getToObjectsByURI().keySet();

      elementMappings.getFromObjectsByURI().forEach((uri, modelElement) -> {
        if (modelElement instanceof EModelElement && !registeredURIs.contains(uri))
        {
          metaDataManager.deleteMetaIDMapping(statement, (EModelElement)modelElement);
        }
      });
    }

    return false;
  }

  protected void rollback(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    context.log("Reverting model evolution changes (dry run)");
    accessor.getConnection().rollback();
  }

  protected boolean commit(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    context.log("Committing model evolution changes to the database");
    accessor.getConnection().commit();
    return false;
  }
}
