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

import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
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
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Eike Stepper
 */
public class ModelEvolutionSupport extends Lifecycle implements IModelEvolutionSupport
{
  public static final String FACTORY_TYPE = "default"; //$NON-NLS-1$

  private IDBStore store;

  private Model.Loader modelLoader;

  private ModelEvolutionMode mode;

  public ModelEvolutionSupport()
  {
  }

  @Override
  public IDBStore getStore()
  {
    return store;
  }

  @Override
  public void setStore(IDBStore store)
  {
    checkInactive();
    this.store = store;
  }

  public Model.Loader getModelLoader()
  {
    return modelLoader;
  }

  @InjectElement(name = "modelLoader", productGroup = Model.Loader.PRODUCT_GROUP)
  public void setModelLoader(Model.Loader modelLoader)
  {
    checkInactive();
    this.modelLoader = modelLoader;
  }

  public ModelEvolutionMode getMode()
  {
    return mode;
  }

  @InjectAttribute(name = "mode")
  public void setMode(ModelEvolutionMode mode)
  {
    checkInactive();
    this.mode = mode;
  }

  @Override
  public void evolveModels() throws SQLException
  {
    checkActive();

    if (mode == ModelEvolutionMode.DISABLED)
    {
      // Model evolution is disabled.
      OM.LOG.info("Model evolution is disabled for repository " + store.getRepository().getName());
      return;
    }

    List<Model> models = modelLoader.loadModels(store);

    List<Model> changedModels = models.stream().filter(Model::isChanged).collect(Collectors.toList());
    if (changedModels.isEmpty())
    {
      // No changed models.
      OM.LOG.info("No model evolution needed for repository " + store.getRepository().getName());
      return;
    }

    if (mode == ModelEvolutionMode.PREVENT)
    {
      throw new IllegalStateException(
          "Model evolution needed for repository " + store.getRepository().getName() + ", but model evolution mode is set to PREVENT");
    }

    // Check whether the mapping strategy supports model evolution.
    ModelEvolution mappingStrategy = ObjectUtil.tryCast(store.getMappingStrategy(), ModelEvolution.class, //
        () -> new ModelEvolutionNotSupportedException());

    ModelEvolutionContext context = createModelEvolutionContext(store, models, changedModels);
    context.log("Starting model evolution for repository " + store.getRepository().getName());

    // Evolve the models with the model evolution context and the mapping strategy.
    boolean triggerCrashRecovery = doEvolveModels(context, mappingStrategy);

    // Finally, trigger a restart of the store to pick up the evolved models and to
    // recover from potential crashes during model evolution.
    context.log("Restarting store to pick up evolved models (crash recovery: " + triggerCrashRecovery + ")");
    store.triggerRestart(triggerCrashRecovery);
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
  protected boolean doEvolveModels(Context context, ModelEvolution mappingStrategy) throws SQLException
  {
    // Obtain a store accessor and set it in the thread local.
    IDBStoreAccessor accessor = store.getWriter(null);
    StoreThreadLocal.setAccessor(accessor);

    try
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

      triggerCrashRecovery |= commitChanges(context, accessor);
      return triggerCrashRecovery;
    }
    finally
    {
      // Release the store accessor and clear the thread local.
      StoreThreadLocal.release();
    }
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

  protected boolean commitChanges(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    context.log("Committing model evolution changes to the database");
    accessor.getConnection().commit();
    return false;
  }

  protected ModelEvolutionContext createModelEvolutionContext(IDBStore store, List<Model> models, List<Model> changedModels)
  {
    return new ModelEvolutionContext(this, models, changedModels);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    CheckUtil.checkState(store, "store");

    if (modelLoader == null)
    {
      modelLoader = new DefaultModelLoader();
    }

    if (mode == null)
    {
      mode = ModelEvolutionMode.EVOLVE;
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(modelLoader);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(modelLoader);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   * @since 4.14
   */
  public enum ModelEvolutionMode
  {
    DISABLED, PREVENT, EVOLVE;

    public static ModelEvolutionMode parse(String str)
    {
      if (str != null)
      {
        for (ModelEvolutionMode mode : values())
        {
          if (mode.name().equalsIgnoreCase(str))
          {
            return mode;
          }
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class DefaultModelLoader implements Model.Loader
  {
    public DefaultModelLoader()
    {
    }

    @Override
    public List<Model> loadModels(IDBStore store) throws SQLException
    {
      List<Model> models = new ArrayList<>();

      EPackage.Registry packageRegistry = getPackageRegistry();
      ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(packageRegistry);

      IDBRowHandler modelRowHandler = (row, values) -> {
        String id = (String)values[0];
        CDOPackageUnit.Type originalType = CDOPackageUnit.Type.values()[DBUtil.asInt(values[1])];
        long timeStamp = DBUtil.asLong(values[2]);
        byte[] packageData = (byte[])values[3];

        EPackage storedPackage = MetaDataManager.createEPackage(id, packageData, resourceSet);
        EPackage registeredPackage = packageRegistry.getEPackage(id);

        Model model = new Model(id, originalType, timeStamp, storedPackage, registeredPackage);
        models.add(model);
        return true;
      };

      try (Connection connection = store.getConnection())
      {
        PackageUnitsTable packageUnitsTable = ((DBStore)store).tables().packageUnits();

        DBUtil.select(connection, modelRowHandler, StringUtil.EMPTY, //
            packageUnitsTable.id(), //
            packageUnitsTable.originalType(), //
            packageUnitsTable.timeStamp(), //
            packageUnitsTable.packageData());
      }

      return models;
    }

    protected EPackage.Registry getPackageRegistry()
    {
      return EPackage.Registry.INSTANCE;
    }
  }
}
