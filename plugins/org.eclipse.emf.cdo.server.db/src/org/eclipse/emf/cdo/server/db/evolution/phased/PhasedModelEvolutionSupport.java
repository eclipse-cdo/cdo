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

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.TreeMapping;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport;
import org.eclipse.emf.cdo.server.db.evolution.phased.Context.Model;
import org.eclipse.emf.cdo.server.db.evolution.phased.ISchemaMigration.SchemaMigrationNotSupportedException;
import org.eclipse.emf.cdo.server.db.evolution.phased.Phase.Handler;
import org.eclipse.emf.cdo.server.db.evolution.phased.Phase.Transition;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageInfosTable;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageUnitsTable;
import org.eclipse.emf.cdo.server.internal.db.MetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageProcessor;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil.ReactivationTrigger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A default implementation of {@link IModelEvolutionSupport model evolution support} for DB stores.
 * <p>
 * Model evolution support is responsible for evolving the models stored in a DB store to match the currently
 * registered EPackages.
 * <p>
 * This implementation is based on a phased approach. Each phase is handled by a dedicated {@link Phase.Handler phase handler}.
 * The following phases are supported:
 * <ul>
 * <li>Change detection: Detects model changes between the stored models and the currently registered EPackages.
 * If no changes are detected, the model evolution process is aborted.</li>
 * <li>Repository export: Exports the repository data to a temporary location to prepare for schema migration.
 * This phase is optional.</li>
 * <li>Schema migration: Evolves the database schema to match the new models. This includes updating the database
 * schema, changing the container feature IDs in case of shifted features, and adjusting enum literals in case
 * of changed enums. This phase is mandatory and ensures that the database schema is in sync with the new models.</li>
 * <li>Store processing: Performs any necessary post-processing on the DB store after schema migration.
 * This phase is optional.</li>
 * <li>Repository processing: Performs any necessary post-processing on the repository after the DB store
 * has been processed. This phase is optional.</li>
 * </ul>
 * Each phase is triggered at a specific point during the activation of the DB store and the repository.
 * <p>
 * The model evolution process can be configured via the <code>mode</code> attribute, which
 * can be set to <code>evolve</code> (default), <code>prevent</code>, or <code>disabled</code>.
 * In <code>evolve</code> mode, model changes are automatically evolved. In <code>prevent</code> mode,
 * an exception is thrown if model changes are detected. In <code>disabled</code> mode,
 * model evolution is skipped entirely.
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public class PhasedModelEvolutionSupport extends Lifecycle implements IModelEvolutionSupport
{
  /**
   * The factory type of the default model evolution support implementation.
   */
  public static final String FACTORY_TYPE = "phased"; //$NON-NLS-1$

  private static final String PROPERTIES_FILE = "evolution.properties";

  private static final String PROP_ID = "id";

  private static final String PROP_PHASE = "phase";

  private static final String PROP_PHASE_ONGOING = "phase.ongoing";

  private static final String PROP_MODEL_PREFIX = "model.";

  private final Deque<String> logBuffer = new LinkedList<>();

  private final EnumMap<Phase, Handler> phaseHandlers = new EnumMap<>(Phase.class);

  private File rootFolder;

  private IDBStore store;

  private Mode mode;

  private int id;

  private Phase phase;

  private boolean phaseOngoing;

  private Context context;

  private InternalCDOPackageRegistry oldPackageRegistry;

  private InternalCDOPackageRegistry newPackageRegistry;

  /**
   * Creates a model evolution support.
   */
  public PhasedModelEvolutionSupport()
  {
  }

  /**
   * Returns the root folder where evolution data is stored.
   * <p>
   * The root folder contains the evolution properties file and dedicated evolution folders
   * for each evolution process.
   *
   * @see #getEvolutionFolder()
   */
  public File getRootFolder()
  {
    return rootFolder;
  }

  /**
   * Sets the root folder where evolution data is stored.
   */
  @InjectAttribute(name = "rootFolder")
  public void setRootFolder(File rootFolder)
  {
    checkInactive();
    this.rootFolder = rootFolder;
  }

  /**
   * Returns the DB store this model evolution support is associated with.
   */
  @Override
  public IDBStore getStore()
  {
    return store;
  }

  /**
   * Sets the DB store this model evolution support is associated with.
   *
   * @noreference This method is not intended to be called by clients.
   */
  @Override
  public void setStore(IDBStore store)
  {
    checkInactive();
    this.store = store;
  }

  /**
   * Returns the model evolution mode.
   */
  public Mode getMode()
  {
    return mode;
  }

  /**
   * Sets the model evolution mode.
   */
  @InjectAttribute(name = "mode")
  public void setMode(Mode mode)
  {
    checkInactive();
    this.mode = mode;
  }

  /**
   * Returns the phase handler for the given phase, or <code>null</code> if none is set.
   */
  public Handler getPhaseHandler(Phase phase)
  {
    return phase == null ? null : phaseHandlers.get(phase);
  }

  private void setPhaseHandler(Phase phase, Handler handler)
  {
    checkInactive();
    handler.setPhase(phase);
    phaseHandlers.put(phase, handler);
  }

  /**
   * Sets the change detection phase handler.
   */
  @InjectElement(name = "changeDetector", productGroup = Handler.PRODUCT_GROUP)
  public void setChangeDetector(Handler changeDetector)
  {
    setPhaseHandler(Phase.ChangeDetection, changeDetector);
  }

  /**
   * Sets the repository export phase handler.
   */
  @InjectElement(name = "repositoryExporter", productGroup = Handler.PRODUCT_GROUP)
  public void setRepositoryExporter(Handler repositoryExporter)
  {
    setPhaseHandler(Phase.RepositoryExport, repositoryExporter);
  }

  /**
   * Sets the schema migration phase handler.
   */
  @InjectElement(name = "schemaMigrator", productGroup = Handler.PRODUCT_GROUP)
  public void setSchemaMigrator(Handler schemaMigrator)
  {
    setPhaseHandler(Phase.SchemaMigration, schemaMigrator);
  }

  /**
   * Sets the store post-processing phase handler.
   */
  @InjectElement(name = "storeProcessor", productGroup = Handler.PRODUCT_GROUP)
  public void setStorePostProcessor(Handler storeProcessor)
  {
    setPhaseHandler(Phase.StoreProcessing, storeProcessor);
  }

  /**
   * Sets the repository post-processing phase handler.
   */
  @InjectElement(name = "repositoryProcessor", productGroup = Handler.PRODUCT_GROUP)
  public void setRepositoryPostProcessor(Handler repositoryProcessor)
  {
    setPhaseHandler(Phase.RepositoryProcessing, repositoryProcessor);
  }

  /**
   * Returns the current evolution phase.
   */
  public Phase getPhase()
  {
    return phase;
  }

  /**
   * Returns the current evolution ID.
   * <p>
   * The evolution ID is incremented each time a model evolution process is successfully completed.
   * It starts at 0 for a new repository and is stored in the evolution properties file.
   * <p>
   * The evolution ID is used to create a dedicated evolution folder under the root folder
   * for each evolution process.
   *
   * @see #getEvolutionFolder()
   */
  public int getEvolutionID()
  {
    return id;
  }

  /**
   * Returns the evolution folder for the current evolution ID, or <code>null</code> if the evolution ID is less than 1.
   * <p>
   * The evolution folder is a subfolder of the root folder named after the evolution ID.
   * It contains the models properties file and the exported models
   * for the corresponding evolution process.
   *
   * @see #getRootFolder()
   * @see #getEvolutionID()
   */
  public File getEvolutionFolder()
  {
    return id < 1 ? null : new File(rootFolder, Integer.toString(id));
  }

  public File getEvolutionLog()
  {
    File folder = getEvolutionFolder();
    return folder == null ? null : new File(folder, "evolution.log");
  }

  /**
   * Logs the given message to the evolution log.
   * <p>
   * Can be used by phases and handlers to log messages during model evolution.
   * <p>
   * The evolution log is typically located in the {@link PhasedModelEvolutionSupport#getEvolutionFolder() evolution folder}.
   */
  public void log(Object message)
  {
    if (message != null)
    {
      String string = message.toString();

      try
      {
        OM.LOG.info("[Model evolution for repository " + store.getRepository().getName() + "] " + string);
      }
      catch (Exception ex)
      {
        OM.LOG.error("Failed to log message: " + message.getClass().getName(), ex);
      }

      logBuffer.add(string);

      File log = getEvolutionLog();
      if (log != null)
      {
        try (Writer writer = IOUtil.buffered(new FileWriter(log, true)))
        {
          while (!logBuffer.isEmpty())
          {
            String line = logBuffer.poll();
            writer.write(line);
            writer.write(StringUtil.NL);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error("Failed to write to evolution log: " + log, ex);
        }
      }
    }
  }

  /**
   * Returns the current evolution context.
   * <p>
   * The evolution context contains the models to be evolved and other relevant information
   * for the model evolution process.
   */
  public Context getContext()
  {
    return context;
  }

  /**
   * Evolves the models stored in the given DB store to match the currently registered <code>EPackages</code>.
   * <p>
   * The evolution process is triggered by the given {@link org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport.Trigger trigger}.
   * Depending on the current phase of the evolution process, the appropriate phase handler is executed.
   * <p>
   * If the evolution mode is set to {@link Mode#Disabled}, the evolution process is skipped.
   * <p>
   * If the evolution mode is set to {@link Mode#Prevent} and model changes are detected,
   * an exception is thrown.
   * <p>
   * If the evolution mode is set to {@link Mode#Evolve}, the evolution process is executed
   * phase by phase until completion.
   * <p>
   * If a phase handler requests a store or repository restart, a {@link ReactivationTrigger}
   * is thrown to signal the need for a restart.
   */
  @Override
  public void trigger(Trigger trigger) throws Exception
  {
    checkActive();

    if (mode == Mode.Disabled)
    {
      // Model evolution is disabled.
      if (trigger == Trigger.ActivatingStore)
      {
        OM.LOG.info("Model evolution is disabled");
      }

      // Skip model evolution.
      return;
    }

    if (phase == null)
    {
      // Start from the beginning.
      phase = Phase.ChangeDetection;
    }

    if (phase.trigger() != trigger)
    {
      // Not the right trigger for the current phase. Try the next trigger.
      return;
    }

    log("Model evolution triggered by " + trigger);

    if (context == null)
    {
      OM.LOG.info("Model evolution root folder: " + rootFolder);

      if (phase.initial())
      {
        // Starting a new model evolution process.
        log("Creating new model evolution context");
        context = createContext();
      }
      else
      {
        // Resuming an ongoing model evolution process.
        log("Loading existing model evolution context");
        context = loadContext();
      }
    }

    // Execute phases until completion or until a restart is requested.
    while (phase != null)
    {
      if (phaseOngoing)
      {
        // A previous attempt to execute a phase was interrupted.
        // The data may be inconsistent, so we cannot continue.
        throw new IllegalStateException("Model evolution was interrupted for repository " + store.getRepository().getName());
      }

      // Record that we are executing a phase.
      phaseOngoing = true;

      // The initial phase is Change Detection, which does not modify the database.
      // No need to save the properties before executing it.
      if (!phase.initial())
      {
        // Persist the current phase before executing it.
        saveProperties();
      }

      // At this point, the current phase is set, persisted, and guaranteed to have a handler.
      Handler phaseHandler = getCurrentPhaseHandler();
      log("Starting phase " + phase + " (handler: " + phaseHandler + ")");

      // Execute the phase handler with cancelation detection.
      if (Context.canceled(() -> phaseHandler.execute(context)))
      {
        // Cancelation was requested during phase execution.
        // This can happen e.g. during change detection if no model changes are detected.
        // The entire model evolution process is aborted in this case.
        return;
      }

      if (phase.initial())
      {
        // Increment the evolution ID and persist the context after the initial phase.
        ++id;
        saveContext(context);
      }

      log("Completed phase " + phase);

      // Determine the next phase to execute.
      Phase nextPhase = determineNextPhase();

      // Let the current phase determine the transition to the next phase.
      // If the next phase is null, we are done. Transition is null as well.
      Transition transition = phase.transitionTo(nextPhase);

      // Persist the new phase and mark it as not (yet) ongoing.
      phase = nextPhase;
      phaseOngoing = false;
      saveProperties();

      // Finally, handle the transition, if any.
      if (transition != null)
      {
        switch (transition)
        {
        case SameTrigger:
          // Stay in the same trigger for the next phase.
          break;

        case NextTrigger:
          // Move to the next trigger before proceeding to the next phase.
          log("Moving to next trigger before proceeding to phase " + phase);
          return;

        case StoreRestart:
          // Restart the store before proceeding to the next phase.
          log("Restarting store before proceeding to phase " + phase);
          throw ReactivationTrigger.createFor(store);

        case RepositoryRestart:
          // Restart the repository before proceeding to the next phase.
          log("Restarting repository before proceeding to phase " + phase);
          throw ReactivationTrigger.createFor(store.getRepository());
        }
      }
    }

    // Model evolution process completed successfully. No more phases to execute.
    // Deactivate model evolution support, so that a potential next trigger does not try to continue.
    log("Model evolution process completed successfully");
    log("Total row updates: " + context.getTotalUpdateCount());
    deactivate();
  }

  /**
   * Determines the next phase to execute, skipping any phases without a handler.
   *
   * @return the next phase to execute, or <code>null</code> if there are no more phases left to execute.
   */
  protected Phase determineNextPhase()
  {
    Phase nextPhase = phase.next();

    while (nextPhase != null && getPhaseHandler(nextPhase) == null)
    {
      nextPhase = nextPhase.next();
    }

    return nextPhase;
  }

  /**
   * Returns the handler for the current phase, throwing an exception if none is set.
   */
  protected Handler getCurrentPhaseHandler()
  {
    Handler phaseHandler = getPhaseHandler(phase);
    if (phaseHandler == null)
    {
      throw new IllegalStateException("No phase handler for phase " + phase);
    }

    return phaseHandler;
  }

  protected InternalCDOPackageRegistry getOldPackageRegistry()
  {
    int xxx;
    if (oldPackageRegistry == null)
    {
      oldPackageRegistry = createPackageRegistry();
    }

    return oldPackageRegistry;
  }

  protected InternalCDOPackageRegistry getNewPackageRegistry()
  {
    int xxx;
    if (newPackageRegistry == null)
    {
      newPackageRegistry = createPackageRegistry();
    }

    return newPackageRegistry;
  }

  /**
   * Loads the models from the database.
   */
  protected List<Model> loadModelsFromDatabase(IDBStore store) throws SQLException
  {
    List<Model> models = new ArrayList<>();

    InternalCDOPackageRegistry oldPackageRegistry = getOldPackageRegistry();
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(EPackage.Registry.INSTANCE);

    IDBRowHandler modelRowHandler = (row, values) -> {
      String id = (String)values[0];
      CDOPackageUnit.Type originalType = CDOPackageUnit.Type.values()[DBUtil.asInt(values[1])];
      long timeStamp = DBUtil.asLong(values[2]);
      byte[] packageData = (byte[])values[3];

      EPackage oldPackage = MetaDataManager.createEPackage(id, packageData, resourceSet);
      EPackage newPackage = EPackage.Registry.INSTANCE.getEPackage(id);

      Model model = new Model(id, originalType, timeStamp, oldPackage, newPackage);
      models.add(model);

      InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit(oldPackage, originalType, timeStamp, oldPackageRegistry);
      oldPackageRegistry.putPackageUnit(packageUnit);

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

  /**
   * Loads the models from disk.
   */
  protected List<Model> loadModelsFromDisk(File folder) throws IOException
  {
    List<Model> models = new ArrayList<>();

    InternalCDOPackageRegistry oldPackageRegistry = getOldPackageRegistry();
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(oldPackageRegistry);

    Properties properties = IOUtil.loadProperties(getPropertiesFile(folder));

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
        EPackage newPackage = getNewPackageRegistry().getEPackage(id);

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
  protected void saveModelsToDisk(File folder, List<Model> models) throws IOException
  {
    Properties properties = new Properties();

    for (Model model : models)
    {
      String fileName = IOUtil.encodeFileName(model.getID()) + ".ecore";
      properties.setProperty(PROP_MODEL_PREFIX + fileName, model.getOriginalType().name() + "," + model.getTimeStamp());

      Resource resource = model.getOldPackage().eResource();
      resource.setURI(URI.createFileURI(new File(folder, fileName).getAbsolutePath()));
    }

    IOUtil.saveProperties(getPropertiesFile(folder), properties, null);

    for (Model model : models)
    {
      Resource resource = model.getOldPackage().eResource();
      resource.save(null);
    }
  }

  /**
   * Creates a new evolution context by loading the models from the database.
   */
  protected Context createContext() throws Exception
  {
    List<Model> models = loadModelsFromDatabase(store);
    return new Context(this, models);
  }

  /**
   * Loads the evolution context by loading the models from disk.
   */
  protected Context loadContext() throws Exception
  {
    File folder = getEvolutionFolder();
    if (folder == null || !folder.isDirectory())
    {
      throw new IllegalStateException("Evolution folder not found for evolution ID " + id);
    }

    List<Model> models = loadModelsFromDisk(folder);
    return new Context(this, models);
  }

  /**
   * Saves the evolution context by saving the models to disk.
   */
  protected void saveContext(Context context) throws Exception
  {
    File folder = getEvolutionFolder();
    if (folder != null)
    {
      folder.mkdirs();
    }

    List<Model> models = context.getModels();
    saveModelsToDisk(folder, models);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    CheckUtil.checkState(store, "store");

    if (rootFolder == null)
    {
      rootFolder = new File(OM.BUNDLE.getStateLocation(), "evolution");
    }

    if (mode == null)
    {
      mode = Mode.Evolve;
    }

    if (getPhaseHandler(Phase.ChangeDetection) == null)
    {
      setPhaseHandler(Phase.ChangeDetection, new DefaultChangeDetector());
    }

    if (getPhaseHandler(Phase.SchemaMigration) == null)
    {
      setPhaseHandler(Phase.SchemaMigration, new DefaultSchemaMigrator());
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    loadProperties();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    phase = null;
    id = 0;
    logBuffer.clear();
    super.doDeactivate();
  }

  private InternalCDOPackageRegistry createPackageRegistry()
  {
    InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)CDOModelUtil.createPackageRegistry(EPackage.Registry.INSTANCE);
    packageRegistry.setPackageProcessor((PackageProcessor)store.getRepository());
    packageRegistry.setPackageLoader(packageUnit -> {
      throw new UnsupportedOperationException("Loading of package units is not supported during model evolution");
    });

    packageRegistry.activate();
    return packageRegistry;
  }

  private void loadProperties() throws IOException
  {
    Properties properties = IOUtil.loadProperties(getRootPropertiesFile());
    id = getProperty(properties, PROP_ID, 0);
    phase = Phase.parse(properties.getProperty(PROP_PHASE));
    phaseOngoing = getProperty(properties, PROP_PHASE_ONGOING, false);
  }

  private void saveProperties() throws IOException
  {
    Properties properties = new Properties();

    if (id != 0)
    {
      properties.setProperty(PROP_ID, Integer.toString(id));
    }

    if (phase != null)
    {
      properties.setProperty(PROP_PHASE, phase.name());
    }

    if (phaseOngoing)
    {
      properties.setProperty(PROP_PHASE_ONGOING, StringUtil.TRUE);
    }

    IOUtil.saveProperties(getRootPropertiesFile(), properties, null);
  }

  private File getRootPropertiesFile()
  {
    return getPropertiesFile(rootFolder);
  }

  private File getPropertiesFile(File folder)
  {
    return new File(folder, PROPERTIES_FILE);
  }

  private static boolean getProperty(Properties properties, String key, boolean defaultValue)
  {
    String value = properties.getProperty(key);
    if (value != null)
    {
      return Boolean.parseBoolean(value);
    }

    return defaultValue;
  }

  private static int getProperty(Properties properties, String key, int defaultValue)
  {
    String value = properties.getProperty(key);
    if (value != null)
    {
      return Integer.parseInt(value);
    }

    return defaultValue;
  }

  /**
   * Indicates the model evolution mode.
   * <p>
   * There are three modes:
   * <ul>
   * <li><b>Disabled</b>: Model evolution is disabled. No model changes are detected or evolved.</li>
   * <li><b>Prevent</b>: Model evolution is prevented. If model changes are detected, an exception is thrown.</li>
   * <li><b>Evolve</b>: Model evolution is enabled. If model changes are detected, they are automatically evolved.</li>
   * </ul>
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public enum Mode
  {
    /**
     * Model evolution is disabled. No model changes are detected or evolved.
     */
    Disabled,

    /**
     * Model evolution is prevented. If model changes are detected, an exception is thrown.
     */
    Prevent,

    /**
     * Model evolution is enabled. If model changes are detected, they are automatically evolved.
     */
    Evolve;

    /**
     * Parses the given string into a Mode.
     */
    public static Mode parse(String str)
    {
      return StringUtil.parseEnum(Mode.class, str, true);
    }
  }

  /**
   * Detects model changes between the stored models and the currently registered EPackages.
   * <p>
   * If no model changes are detected, the model evolution process is aborted.
   * If model changes are detected and the mode is set to {@link Mode#Prevent},
   * an exception is thrown.
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public static class DefaultChangeDetector extends BasicPhaseHandler
  {
    /**
     * The factory type of the default change detector.
     */
    public static final String FACTORY_TYPE = "default-change-detector"; //$NON-NLS-1$

    /**
     * Creates a change detector.
     */
    public DefaultChangeDetector()
    {
    }

    /**
     * Detects model changes and handles them according to the configured mode.
     */
    @Override
    public void execute(Context context) throws Exception
    {
      List<Model> changedModels = context.getChangedModels();
      if (changedModels.isEmpty())
      {
        // No changed models.
        context.log("No model evolution needed");
        throw context.cancelation();
      }

      changedModels.forEach(model -> context.log("Model changed: " + model.getID()));

      Mode mode = context.getSupport().getMode();
      if (mode == Mode.Prevent)
      {
        context.log("Model evolution prevented");
        throw new IllegalStateException("Model evolution needed, but model evolution mode is set to PREVENT");
      }
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
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public static class DefaultSchemaMigrator extends BasicPhaseHandler
  {
    /**
     * The factory type of the default schema migrator.
     */
    public static final String FACTORY_TYPE = "default-schema-migrator"; //$NON-NLS-1$

    /**
     * Creates a schema migrator.
     */
    public DefaultSchemaMigrator()
    {
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
        // Let the mapping strategy evolve the models. This includes:
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
    protected boolean migrateSchema(Context context, IDBStoreAccessor accessor, ISchemaMigration schemaMigration) throws SQLException
    {
      context.log("Migrating schema with mapping strategy " + schemaMigration.getClass().getName());
      return schemaMigration.migrateSchema(context, accessor);
    }

    /**
     * Updates the system tables with the changed models.
     */
    protected boolean updateSystemTables(Context context, IDBStoreAccessor accessor) throws SQLException
    {
      context.log("Updating system tables with changed models");

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
          context.log("Deleting old package unit " + unitID);
          int count1 = statement.executeUpdate("DELETE from " + packageUnits + " WHERE " + packageUnits.id() + "='" + unitID + "'");
          context.incrementTotalUpdateCount(count1);

          // Delete the old package info(s).
          context.log("Deleting old package infos for " + unitID);
          int count2 = statement.executeUpdate("DELETE from " + packageInfos + " WHERE " + packageInfos.unit() + "='" + unitID + "'");
          context.incrementTotalUpdateCount(count2);

          // Get the registered package bytes.
          EPackage newPackage = model.getNewPackage();
          byte[] packageBytes = MetaDataManager.getEPackageBytes(newPackage, EPackage.Registry.INSTANCE);

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

      return false;
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
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public static class DefaultRepositoryExporter extends BasicPhaseHandler
  {
    /**
     * The factory type of the default repository exporter.
     */
    public static final String FACTORY_TYPE = "default-repository-exporter"; //$NON-NLS-1$

    private boolean binary;

    /**
     * Creates an XML repository exporter.
     */
    public DefaultRepositoryExporter()
    {
      this(false);
    }

    /**
     * Creates a repository exporter that is either binary or XML.
     */
    public DefaultRepositoryExporter(boolean binary)
    {
      setBinary(binary);
    }

    public boolean isBinary()
    {
      return binary;
    }

    @InjectAttribute(name = "binary")
    public void setBinary(boolean binary)
    {
      checkInactive();
      this.binary = binary;
    }

    @Override
    public void execute(Context context) throws Exception
    {
      PhasedModelEvolutionSupport support = context.getSupport();
      IRepository repository = support.getStore().getRepository();

      CDOServerExporter<?> exporter;
      String fileName;

      if (binary)
      {
        exporter = new CDOServerExporter.Binary(repository);
        fileName = "export.bin";
      }
      else
      {
        exporter = new CDOServerExporter.XML(repository);
        fileName = "export.xml";
      }

      File evolutionFolder = support.getEvolutionFolder();
      File exportFile = new File(evolutionFolder, fileName);
      context.log("Exporting repository to " + exportFile.getAbsolutePath());

      try (OutputStream out = IOUtil.buffered(new FileOutputStream(exportFile)))
      {
        exporter.exportRepository(out);
      }
    }
  }
}
