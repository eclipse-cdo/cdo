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
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport;
import org.eclipse.emf.cdo.server.db.evolution.phased.Context.Model;
import org.eclipse.emf.cdo.server.db.evolution.phased.Phase.Handler;
import org.eclipse.emf.cdo.server.db.evolution.phased.Phase.Transition;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageProcessor;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.LogListener;
import org.eclipse.net4j.util.event.PropertiesEvent;
import org.eclipse.net4j.util.factory.AnnotationFactory;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil.ReactivationTrigger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * A {@link Phase phased} implementation of {@link IModelEvolutionSupport model evolution support} for DB stores.
 * <p>
 * Model evolution support is responsible for evolving the models stored in a DB store to match the currently
 * registered EPackages.
 * <p>
 * This implementation is based on a phased approach. The following phases are supported:
 * <p>
 * <ol>
 * <li><b>Change detection</b>: Detects model changes between the stored models and the currently registered EPackages.
 * If no changes are detected, the model evolution process is aborted.</li>
 * <li><b>Repository export</b>: Exports the repository data to a temporary location to prepare for schema migration.
 * This phase is optional.</li>
 * <li><b>Schema migration</b>: Evolves the database schema to match the new models. This includes updating the database
 * schema, changing the container feature IDs in case of shifted features, and adjusting enum literals in case
 * of changed enums. This phase is mandatory and ensures that the database schema is in sync with the new models.</li>
 * <li><b>Store processing</b>: Performs any necessary post-processing on the DB store after schema migration.
 * This phase is optional.</li>
 * <li><b>Repository processing</b>: Performs any necessary post-processing on the repository after the DB store
 * has been processed. This phase is optional.</li>
 * </ol>
 * <p>
 * Each phase is handled by a dedicated {@link Phase.Handler phase handler}. Phase handlers can be
 * configured via dependency injection (see {@link AnnotationFactory}). The Change Detection and Schema Migration
 * phases must have a phase handler configured. If no phase handler is configured for these phases, a default implementation
 * is used:
 * <p>
 * <ul>
 * <li>Change Detection: {@link DefaultChangeDetector}</li>
 * <li>Schema Migration: {@link DefaultSchemaMigrator}</li>
 * </ul>
 * <p>
 * The Repository Export, Store Processing, and Repository Processing phases may be skipped if no phase handler
 * is configured for them. The Repository Export phase is the only optional phase for which a default implementation
 * exists: {@link DefaultRepositoryExporter}.
 * <p>
 * Each phase is triggered at a specific point during the activation of the DB store and the repository. There are
 * two {@link org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport.Trigger triggers}:
 * <p>
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport.Trigger#ActivatingStore ActivatingStore}:
 * Triggered when the DB store is being activated by the {@link org.eclipse.emf.cdo.server.IRepository IRepository}.
 * This is the main trigger for model evolution. It only occurs when the store is restarted, i.e., not
 * when the store is activated for the first time.</li>
 * <li>{@link org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport.Trigger#ActivatedRepository ActivatedRepository}:
 * Triggered when the {@link org.eclipse.emf.cdo.server.IRepository IRepository} has been fully activated.
 * This trigger exists mainly for special purposes, e.g., to export repository models before the schema
 * is upgraded.</li>
 * </ul>
 * <p>
 * Phases can request a store or repository restart by returning the appropriate {@link Transition}
 * from their {@link Phase#transitionTo(Phase) transition} method.
 * <p>
 * The model evolution process can be configured via the <code>mode</code> attribute, which
 * can be set to <code>migrate</code> (default), <code>prevent</code>, or <code>disabled</code>.
 * In <code>migrate</code> mode, model changes are automatically evolved. In <code>prevent</code> mode,
 * an exception is thrown if model changes are detected. In <code>disabled</code> mode,
 * model evolution is skipped entirely.
 * <p>
 * The model evolution process stores its state in a dedicated root folder, which can be configured
 * via the <code>rootFolder</code> attribute. The root folder contains an <code>evolution.properties</code> file
 * storing the current evolution ID and phase, as well as dedicated evolution folders for each
 * evolution process. Each evolution folder is named after the evolution ID and contains
 * the exported models and an optional evolution log. The {@link DefaultRepositoryExporter} stores
 * the repository export file in the evolution folder during the Repository Export phase.
 * <p>
 * The model evolution context, which contains the models to be evolved and other relevant information,
 * is persisted in the evolution folder after the Change Detection phase. If the model evolution process
 * is interrupted, it can be resumed from the persisted context. Manual inspection of the evolution folder
 * may help diagnosing issues during model evolution or recovering from failures. The context is managed
 * by a {@link Context.Manager context manager}, which can be configured via dependency injection.
 * <p>
 * The phases and their handlers fire various events during the model evolution process.
 * Clients can register {@link IListener listeners} with the model evolution support to be notified
 * about these events and to monitor or impact the evolution process. Registering a {@link LogListener}
 * will log all evolution events to the logging system and helps to understand the evolution process.
 * <p>
 * Example configuration snippet:
 * <pre>
 * &lt;store type="db">
 *   ...
 *   &lt;modelEvolutionSupport type="phased" rootFolder="@state/evolution" mode="migrate">
 *     &lt;<contextManager type="folder" saveNewModels="true"/>
 *     &lt;changeDetector/>
 *     &lt;repositoryExporter type="default" binary="false"/>
 *     &lt;schemaMigrator/>
 *     &lt;storeProcessor type="my-sql-processor" myExtraArg="data"/>
 *     &lt;repositoryProcessor type="my-eobject-handler" myExtraArg="data"/> -->
 *     &lt;!-- Optional listeners -->
 *     &lt;listener type="log"/>
 *     &lt;listener type="my-extra-checks"/>
 *   &lt;/modelEvolutionSupport>
 *   ...
 * &lt;/store>
 * </pre>
 *
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

  private static final Registry GLOBAL_PACKAGE_REGISTRY = EPackage.Registry.INSTANCE;

  private static final PackageLoader NO_PACKAGE_LOADING = packageUnit -> {
    throw new UnsupportedOperationException("Loading of package units is not supported during model evolution");
  };

  private static final PackageProcessor NO_PACKAGE_PROCESSING = null;

  private static final String SUPPORT_PROPERTIES_FILE = "support.properties";

  private static final String PROP_EVOLUTION_ID = "evolution.id";

  private static final String PROP_EVOLUTION_ONGOING = "evolution.ongoing";

  private static final String PROP_PHASE = "phase";

  private static final String PROP_PHASE_ONGOING = "phase.ongoing";

  private final Deque<Pair<String, Long>> logBuffer = new LinkedList<>();

  private final EnumMap<Phase, Handler> phaseHandlers = new EnumMap<>(Phase.class);

  private final PropertiesEvent event = new PropertiesEvent(this)
  {
    @Override
    public PropertiesEvent fire()
    {
      if (!EventUtil.fireEvent(PhasedModelEvolutionSupport.this, this))
      {
        fireEvent(this);
      }

      return this;
    }

    @Override
    protected String formatEventName()
    {
      return "ModelEvolutionEvent";
    }
  };

  private Context.Manager contextManager;

  private File rootFolder;

  private IDBStore store;

  private Mode mode;

  private int evolutionID;

  private boolean evolutionOngoing;

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
   * Returns the context manager used to create, load, and save evolution contexts.
   */
  public Context.Manager getContextManager()
  {
    return contextManager;
  }

  /**
   * Sets the context manager used to create, load, and save evolution contexts.
   */
  @InjectElement(name = "contextManager", productGroup = Context.Manager.PRODUCT_GROUP, defaultFactoryType = "folder")
  public void setContextManager(Context.Manager contextManager)
  {
    checkInactive();
    this.contextManager = contextManager;
  }

  /**
   * Returns the phase handler for the given phase, or <code>null</code> if none is set.
   */
  public Handler getPhaseHandler(Phase phase)
  {
    return phase == null ? null : phaseHandlers.get(phase);
  }

  /**
   * Sets the change detection phase handler.
   */
  @InjectElement(name = "changeDetector", productGroup = Handler.PRODUCT_GROUP, defaultFactoryType = "default", factoryTypeSuffix = "-change-detector")
  public void setChangeDetector(Handler changeDetector)
  {
    setPhaseHandler(Phase.ChangeDetection, changeDetector);
  }

  /**
   * Sets the repository export phase handler.
   */
  @InjectElement(name = "repositoryExporter", productGroup = Handler.PRODUCT_GROUP, defaultFactoryType = "default", factoryTypeSuffix = "-repository-exporter")
  public void setRepositoryExporter(Handler repositoryExporter)
  {
    setPhaseHandler(Phase.RepositoryExport, repositoryExporter);
  }

  /**
   * Sets the schema migration phase handler.
   */
  @InjectElement(name = "schemaMigrator", productGroup = Handler.PRODUCT_GROUP, defaultFactoryType = "default", factoryTypeSuffix = "-schema-migrator")
  public void setSchemaMigrator(Handler schemaMigrator)
  {
    setPhaseHandler(Phase.SchemaMigration, schemaMigrator);
  }

  /**
   * Sets the store post-processing phase handler.
   */
  @InjectElement(name = "storeProcessor", productGroup = Handler.PRODUCT_GROUP, factoryTypeSuffix = "-store-processor")
  public void setStorePostProcessor(Handler storeProcessor)
  {
    setPhaseHandler(Phase.StoreProcessing, storeProcessor);
  }

  /**
   * Sets the repository post-processing phase handler.
   */
  @InjectElement(name = "repositoryProcessor", productGroup = Handler.PRODUCT_GROUP, factoryTypeSuffix = "-repository-processor")
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
   * Returns whether the current phase is ongoing.
   */
  public boolean isPhaseOngoing()
  {
    return phaseOngoing;
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
    return evolutionID;
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
    return evolutionID < 1 ? null : new File(rootFolder, Integer.toString(evolutionID));
  }

  public File getEvolutionLog()
  {
    File folder = getEvolutionFolder();
    return folder == null ? null : new File(folder, "evolution.log");
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
   * Returns the old package registry used for recreating the old EPackages.
   */
  public InternalCDOPackageRegistry getOldPackageRegistry()
  {
    if (oldPackageRegistry == null)
    {
      oldPackageRegistry = createPackageRegistry(false);
    }

    return oldPackageRegistry;
  }

  /**
   * Returns the new package registry used for determining the new EPackages.
   */
  public InternalCDOPackageRegistry getNewPackageRegistry()
  {
    if (newPackageRegistry == null)
    {
      newPackageRegistry = createPackageRegistry(true);
    }

    return newPackageRegistry;
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

      logBuffer.add(Pair.create(string, System.currentTimeMillis()));

      File log = getEvolutionLog();
      if (log != null && log.getParentFile().isDirectory())
      {
        try (Writer writer = IOUtil.buffered(new FileWriter(log, true)))
        {
          while (!logBuffer.isEmpty())
          {
            Pair<String, Long> line = logBuffer.poll();

            writer.write("[");
            writer.write(CDOCommonUtil.formatTimeStamp(line.getElement2()));
            writer.write("] ");
            writer.write(line.getElement1());
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
   * If the evolution mode is set to {@link Mode#Migrate}, the evolution process is executed
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
        OM.LOG.info("Model evolution support is disabled");
      }

      // Skip model evolution.
      deactivate();
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

    log("Triggered by " + trigger);

    if (context == null)
    {
      log("Root folder: " + rootFolder);

      if (phase.initial())
      {
        // Starting a new model evolution process.
        log("Creating new model evolution context");
        context = contextManager.createContext();
      }
      else
      {
        // Resuming an ongoing model evolution process.
        log("Loading existing model evolution context");
        context = contextManager.loadContext();
      }
    }

    // Execute the phase loop.
    executePhaseLoop();
  }

  /**
   * Executes the phase loop until completion or until a restart is requested.
   */
  protected void executePhaseLoop() throws IOException, Exception, ReactivationTrigger
  {
    // Execute phases until completion or until a restart is requested.
    while (phase != null)
    {
      if (phaseOngoing)
      {
        // A previous attempt to execute a phase was interrupted.
        // The data may be inconsistent, so we cannot continue.
        throw new IllegalStateException("Model evolution was interrupted for repository " //
            + store.getRepository().getName() + " during phase " + phase //
            + "; manual intervention is required to recover");
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

      // Execute the phase handler.
      executePhaseHandler(phaseHandler);

      // The initial phase is Change Detection.
      // If no model changes were detected, the evolution process is aborted.
      // Otherwise, the evolution ID is incremented and the context is persisted.
      if (phase.initial())
      {
        if (context.getChangeInfos().isEmpty())
        {
          // No model changes detected during the initial phase.
          // The entire model evolution process is aborted in this case.
          log("No model changes detected; aborting model evolution process");
          deactivate();
          return;
        }

        if (mode == Mode.Prevent)
        {
          // Model changes were detected, but the mode is set to PREVENT.
          log("Model changes detected, but model evolution mode is set to PREVENT; aborting model evolution process");
          throw new IllegalStateException("Model changes detected, but model evolution mode is set to PREVENT");
        }

        // Increment the evolution ID before persisting the context.
        ++evolutionID;
        log("Model changes detected; assigned evolution ID " + evolutionID);

        // Persist the context after the initial phase.
        contextManager.saveContext(context);
      }

      log("Completed phase " + phase);

      // Determine the next phase to execute. Skip any phases without a handler.
      Phase nextPhase = determineNextPhase();

      // Let the current phase determine the transition to the next phase.
      // If the next phase is null, we are done. Then transition is null as well.
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
          // Move to the next trigger before proceeding to the next phase. Do not deactivate yet!
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
    evolutionOngoing = false;
    saveProperties();
    deactivate();
  }

  /**
   * Executes the given phase handler.
   */
  protected void executePhaseHandler(Handler phaseHandler) throws Exception
  {
    Phase phase = phaseHandler.getPhase();
    event.addProperty("phase", phase);

    event.setType("InitializingModelEvolutionPhase").fire();
    phase.init(context);

    event.setType("ExecutingModelEvolutionPhase").fire();
    phaseHandler.execute(context);
    event.setType("ExecutedModelEvolutionPhase").fire();

    phase.done(context);
    event.setType("FinishedModelEvolutionPhase").fire().removeProperty("phase");
  }

  /**
   * Determines the next phase to execute, skipping any phases without a handler.
   *
   * @return the next phase to execute, or <code>null</code> if there are no more phases left to execute.
   */
  protected Phase determineNextPhase()
  {
    // Start with the next phase, no matter if it has a handler or not.
    Phase nextPhase = phase.next();

    // Skip phases without a handler.
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

  /**
   * Called after a change info has been added to the evolution context.
   * <p>
   * Can be used by phases and handlers to notify listeners about added change infos.
   * <p>
   * Subclasses may override.
   */
  protected void addedChangeInfo(Model model, Object changeInfo)
  {
    event.setType("AddedChangeInfo").addProperty("changeInfo", changeInfo).fire().removeProperty("changeInfo");
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
      mode = Mode.Migrate;
    }

    if (contextManager == null)
    {
      contextManager = new FolderContextManager();
    }

    contextManager.setSupport(this);

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
    LifecycleUtil.activate(contextManager);

    for (Handler handler : phaseHandlers.values())
    {
      LifecycleUtil.activate(handler);
    }

    loadProperties();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (Handler handler : phaseHandlers.values())
    {
      LifecycleUtil.deactivate(handler);
    }

    LifecycleUtil.deactivate(contextManager);

    evolutionID = 0;
    evolutionOngoing = false;
    phase = null;
    phaseOngoing = false;
    logBuffer.clear();

    super.doDeactivate();
  }

  private void setPhaseHandler(Phase phase, Handler handler)
  {
    checkInactive();
    handler.setPhase(phase);
    phaseHandlers.put(phase, handler);
  }

  private void loadProperties() throws IOException
  {
    Properties properties = IOUtil.loadProperties(getSupportPropertiesFile());
    evolutionID = getProperty(properties, PROP_EVOLUTION_ID, 0);
    evolutionOngoing = getProperty(properties, PROP_EVOLUTION_ONGOING, false);
    phase = Phase.parse(properties.getProperty(PROP_PHASE));
    phaseOngoing = getProperty(properties, PROP_PHASE_ONGOING, false);
  }

  private void saveProperties() throws IOException
  {
    Properties properties = new Properties();

    if (evolutionID != 0)
    {
      properties.setProperty(PROP_EVOLUTION_ID, Integer.toString(evolutionID));
    }

    if (evolutionOngoing)
    {
      properties.setProperty(PROP_EVOLUTION_ONGOING, StringUtil.TRUE);
    }

    if (phase != null)
    {
      properties.setProperty(PROP_PHASE, phase.name());
    }

    if (phaseOngoing)
    {
      properties.setProperty(PROP_PHASE_ONGOING, StringUtil.TRUE);
    }

    IOUtil.saveProperties(getSupportPropertiesFile(), properties, null);
  }

  private File getSupportPropertiesFile()
  {
    return new File(rootFolder, SUPPORT_PROPERTIES_FILE);
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

  private static InternalCDOPackageRegistry createPackageRegistry(boolean delegateToGlobal)
  {
    EPackage.Registry delegateRegistry = delegateToGlobal ? GLOBAL_PACKAGE_REGISTRY : null;

    InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)CDOModelUtil.createPackageRegistry(delegateRegistry);
    packageRegistry.setPackageLoader(NO_PACKAGE_LOADING); // No loading during model evolution.
    packageRegistry.setPackageProcessor(NO_PACKAGE_PROCESSING); // No processing during model evolution.
    packageRegistry.activate();
    return packageRegistry;
  }

  /**
   * Indicates the model evolution mode.
   * <p>
   * There are three modes:
   * <ul>
   * <li><b>Disabled</b>: Model evolution is disabled. No model changes are detected or evolved.</li>
   * <li><b>Prevent</b>: Model evolution is prevented. If model changes are detected, an exception is thrown.</li>
   * <li><b>Migrate</b>: Model evolution is enabled. If model changes are detected, the database is automatically migrated.</li>
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
     * Model evolution is disabled. No model changes are detected or migrated.
     */
    Disabled,

    /**
     * Model evolution is prevented. If model changes are detected, an exception is thrown.
     */
    Prevent,

    /**
     * Model evolution is enabled. If model changes are detected, the database is automatically migrated.
     */
    Migrate;

    /**
     * Parses the given string into a Mode.
     */
    public static Mode parse(String str)
    {
      return StringUtil.parseEnum(Mode.class, str, true);
    }
  }
}
