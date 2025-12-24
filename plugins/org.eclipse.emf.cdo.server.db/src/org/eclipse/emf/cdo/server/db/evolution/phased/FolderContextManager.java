/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.evolution.phased;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.evolution.phased.Context.Model;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageUnitsTable;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A context manager that saves and loads the evolution contexts to and from a folder on disk.
 * <p>
 * The folder is determined by {@link PhasedModelEvolutionSupport#getEvolutionFolder()}.
 * <p>
 * Each model is saved as an individual .ecore file. The file name is derived from the model ID by encoding it
 * to a safe file name using {@link IOUtil#encodeFileName(String)}. Additionally, an
 * <code>evolution.properties</code> file is created to map the model file names to their original types and
 * timestamps.
 * <p>
 * If the {@link #setSaveNewModels(boolean) saveNewModels} property is set to <code>true</code>, the new
 * models are also saved to disk during evolution. The new model files have the same name as the original
 * model files, but with a <code>_new</code> suffix before the <code>.ecore</code> extension.
 * These extra files can be useful for debugging and verification purposes.
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public class FolderContextManager extends Lifecycle implements Context.Manager
{
  /**
   * The factory type of the folder context manager implementation.
   */
  public static final String FACTORY_TYPE = "folder"; //$NON-NLS-1$

  private static final String EVOLUTION_PROPERTIES_FILE = "evolution.properties"; //$NON-NLS-1$

  private static final String PROP_MODEL_PREFIX = "model."; //$NON-NLS-1$

  private PhasedModelEvolutionSupport support;

  private boolean saveNewModels;

  /**
   * Creates a new folder context manager.
   */
  public FolderContextManager()
  {
  }

  /**
   * Returns the model evolution support.
   */
  @Override
  public PhasedModelEvolutionSupport getSupport()
  {
    return support;
  }

  /**
   * Sets the model evolution support.
   *
   * @noreference This method is not intended to be called by clients.
   */
  @Override
  public void setSupport(PhasedModelEvolutionSupport support)
  {
    this.support = support;
  }

  /**
   * Returns whether to save the new models to disk during evolution.
   */
  public boolean isSaveNewModels()
  {
    return saveNewModels;
  }

  /**
   * Sets whether to save the new models to disk during evolution.
   */
  @InjectAttribute(name = "saveNewModels")
  public void setSaveNewModels(boolean saveNewModels)
  {
    checkInactive();
    this.saveNewModels = saveNewModels;
  }

  /**
   * Creates a new context by loading the models from the database.
   */
  @Override
  public Context createContext() throws Exception
  {
    List<Model> models = loadModelsFromDatabase(support.getStore());
    return new Context(support, models);
  }

  /**
   * Loads the context from disk.
   */
  @Override
  public Context loadContext() throws Exception
  {
    File folder = support.getEvolutionFolder();
    if (folder == null || !folder.isDirectory())
    {
      throw new IllegalStateException("Evolution folder not found for evolution ID " + support.getEvolutionID());
    }

    List<Model> models = loadModelsFromDisk(folder);
    return new Context(support, models);
  }

  /**
   * Saves the context to disk.
   */
  @Override
  public void saveContext(Context context) throws Exception
  {
    File folder = support.getEvolutionFolder();
    if (folder != null)
    {
      folder.mkdirs();
    }

    Collection<Model> models = context.getChangeInfos().keySet();
    saveModelsToDisk(folder, models);
  }

  /**
   * Loads the models from the database.
   */
  protected List<Model> loadModelsFromDatabase(IDBStore store) throws SQLException
  {
    // Prepare a list to hold the loaded models.
    List<Model> models = new ArrayList<>();

    // Prepare the old package registry and resource set for recreating the old EPackages.
    InternalCDOPackageRegistry oldPackageRegistry = support.getOldPackageRegistry();
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(oldPackageRegistry);

    // Prepare the new package registry for determining the new EPackages.
    CDOPackageRegistry newPackageRegistry = support.getNewPackageRegistry();

    // Define a row handler to process each row in the cdo_package_units table.
    IDBRowHandler modelRowHandler = (row, values) -> {
      // Read ID, original type, timestamp, and (optionally) package data from the current row.
      String id = (String)values[0];
      CDOPackageUnit.Type originalType = CDOPackageUnit.Type.values()[DBUtil.asInt(values[1])];
      long timeStamp = DBUtil.asLong(values[2]);

      // Determine the old EPackage.
      EPackage oldPackage;
      if (EcorePackage.eNS_URI.equals(id))
      {
        // The Ecore package is always available and MUST NOT be recreated dynamically from stored package data!
        oldPackage = EcorePackage.eINSTANCE;
      }
      else
      {
        // Recreate the old EPackage from the stored package data.
        byte[] packageData = (byte[])values[3];
        oldPackage = EMFUtil.createEPackage(id, packageData, resourceSet, false);
      }

      // Determine the new EPackage from the new package registry.
      EPackage newPackage = newPackageRegistry.getEPackage(id);

      // Create the model and add it to the list.
      Model model = new Model(id, originalType, timeStamp, oldPackage, newPackage);
      models.add(model);

      // Create the old package unit and register it in the old package registry.
      CDOModelUtil.createPackageUnit(oldPackage, originalType, timeStamp, oldPackageRegistry);
      return true;
    };

    // Load the models from the cdo_package_units table using the row handler.
    try (Connection connection = store.getConnection())
    {
      // Get the cdo_package_units table.
      PackageUnitsTable packageUnitsTable = ((DBStore)store).tables().packageUnits();

      // Execute the select statement to load the models.
      DBUtil.select(connection, modelRowHandler, StringUtil.EMPTY, //
          packageUnitsTable.id(), //
          packageUnitsTable.originalType(), //
          packageUnitsTable.timeStamp(), //
          packageUnitsTable.packageData());
    }

    // Return the loaded models.
    return models;
  }

  /**
   * Loads the models from disk.
   */
  protected List<Model> loadModelsFromDisk(File folder) throws IOException
  {
    List<Model> models = new ArrayList<>();

    InternalCDOPackageRegistry oldPackageRegistry = support.getOldPackageRegistry();
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(oldPackageRegistry);

    Properties properties = IOUtil.loadProperties(getEvolutionPropertiesFile(folder));

    for (Map.Entry<Object, Object> entry : properties.entrySet())
    {
      String fileName = (String)entry.getKey();
      if (fileName.startsWith(PROP_MODEL_PREFIX))
      {
        fileName = fileName.substring(PROP_MODEL_PREFIX.length());

        String[] parts = ((String)entry.getValue()).split(",");
        CDOPackageUnit.Type originalType = CDOPackageUnit.Type.valueOf(parts[0]);
        long timeStamp = Long.parseLong(parts[1]);

        URI uri = URI.createFileURI(new File(folder, fileName).getAbsolutePath());
        Resource resource = resourceSet.getResource(uri, true);
        EPackage oldPackage = (EPackage)resource.getContents().get(0);
        String id = oldPackage.getNsURI();
        EPackage newPackage = support.getNewPackageRegistry().getEPackage(id);

        Model model = new Model(id, originalType, timeStamp, oldPackage, newPackage);
        models.add(model);

        InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit(oldPackage, originalType, timeStamp, oldPackageRegistry);
        oldPackageRegistry.putPackageUnit(packageUnit);
      }
    }

    return models;
  }

  /**
   * Saves the models to disk.
   */
  protected void saveModelsToDisk(File folder, Collection<Model> models) throws IOException
  {
    Properties properties = new Properties();

    for (Model model : models)
    {
      String encodedFileName = IOUtil.encodeFileName(model.getID());
      String fileName = encodedFileName + ".ecore";
      properties.setProperty(PROP_MODEL_PREFIX + fileName, model.getOriginalType().name() + "," + model.getTimeStamp());

      IOUtil.writeText(new File(folder, fileName), false, model.getOldXMI());

      if (saveNewModels)
      {
        IOUtil.writeText(new File(folder, encodedFileName + "_new.ecore"), false, model.getNewXMI());
      }
    }

    IOUtil.saveProperties(getEvolutionPropertiesFile(folder), properties, null);
  }

  /**
   * Returns the evolution properties file in the given folder.
   */
  private static File getEvolutionPropertiesFile(File folder)
  {
    return new File(folder, EVOLUTION_PROPERTIES_FILE);
  }
}
