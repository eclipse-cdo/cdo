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

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport.Trigger;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.StringUtil;

/**
 * Phases in the model evolution process.
 * <p>
 * Each phase is associated with a specific {@link Trigger trigger} that causes its execution. Furthermore, each
 * phase defines the possible transitions to subsequent phases via the {@link #transitionTo(Phase) transitionTo()}
 * method.
 * <p>
 * The phases are executed in the following order:
 * <ol>
 * <li>{@link #ChangeDetection}</li>
 * <li>{@link #RepositoryExport}</li>
 * <li>{@link #SchemaMigration}</li>
 * <li>{@link #StoreProcessing}</li>
 * <li>{@link #RepositoryProcessing}</li>
 * </ol>
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public enum Phase
{
  /**
   * The change detection phase identifies model changes between the stored models and the currently registered
   * EPackages.
   * <p>
   * This phase is triggered by the {@link Trigger#ActivatingStore activating store} event.
   * <p>
   * From this phase, transitions are possible to:
   * <ul>
   * <li>{@link #RepositoryExport} via the {@link Transition#NextTrigger next trigger} transition</li>
   * <li>{@link #SchemaMigration} via the {@link Transition#SameTrigger same trigger} transition</li>
   * </ul>
   * <p>
   * The change detection phase is always the initial phase in the model evolution process.
   * It is also the only phase that can be cancelled, for example, if no model changes are detected.
   */
  ChangeDetection(Trigger.ActivatingStore)
  {
    @Override
    protected Transition getTransitionTo(Phase nextPhase)
    {
      switch (nextPhase)
      {
      case RepositoryExport:
        return Transition.NextTrigger;
      case SchemaMigration:
        return Transition.SameTrigger;
      default:
        return null;
      }
    }
  },

  /**
   * The repository export phase exports the current repository data to the {@link PhasedModelEvolutionSupport#getEvolutionFolder()
   * evolution folder} before any schema migration takes place.
   * <p>
   * This phase is triggered by the {@link Trigger#ActivatedRepository activated repository} event. At this point in time,
   * the repository is fully configured and active. The repository's package registry is filled with the old models.
   * <p>
   * From this phase, a transition is possible to:
   * <ul>
   * <li>{@link #SchemaMigration} via the {@link Transition#RepositoryRestart repository restart} transition</li>
   * </ul>
   * <p>
   * The repository export phase, if applicable, is always the second phase in the model evolution process. It is only executed
   * if model changes were detected during the {@link #ChangeDetection change detection} phase and a {@link Phase.Handler phase handler}
   * was registered for this phase.
   */
  RepositoryExport(Trigger.ActivatedRepository)
  {
    @Override
    protected Transition getTransitionTo(Phase nextPhase)
    {
      switch (nextPhase)
      {
      case SchemaMigration:
        return Transition.RepositoryRestart;
      default:
        return null;
      }
    }

    /**
     * Installs the old package registry in the repository before executing the phase handler.
     */
    @Override
    public void init(Context context)
    {
      PhasedModelEvolutionSupport support = context.getSupport();
      InternalRepository repository = (InternalRepository)support.getStore().getRepository();
      InternalCDOPackageRegistry originalPackageRegistry = repository.getPackageRegistry(false);
      context.properties().put(ORIGINAL_PACKAGE_REGISTRY_KEY, originalPackageRegistry);
      repository.setPackageRegistry(support.getOldPackageRegistry());
    }

    /**
     * Restores the original package registry in the repository after executing the phase handler.
     */
    @Override
    public void done(Context context)
    {
      PhasedModelEvolutionSupport support = context.getSupport();
      InternalRepository repository = (InternalRepository)support.getStore().getRepository();
      InternalCDOPackageRegistry originalPackageRegistry = (InternalCDOPackageRegistry)context.properties().remove(ORIGINAL_PACKAGE_REGISTRY_KEY);
      if (originalPackageRegistry != null)
      {
        repository.setPackageRegistry(originalPackageRegistry);
      }
    }
  },

  /**
   * The schema migration phase migrates the database schema to match the currently registered EPackages.
   * It also performs any necessary data migrations, such as updating the <code>cdo_feature</code> columns
   * when feature IDs have changed or updating enum literal values when enum literals have changed.
   * <p>
   * This phase is triggered by the {@link Trigger#ActivatingStore activating store} event.
   * <p>
   * From this phase, transitions are possible to:
   * <ul>
   * <li>{@link #StoreProcessing} via the {@link Transition#StoreRestart store restart} transition</li>
   * <li>{@link #RepositoryProcessing} via the {@link Transition#StoreRestart store restart} transition</li>
   * </ul>
   * <p>
   * The schema migration phase is always executed (i.e., a {@link Phase.Handler phase handler} must be registered for it)
   * when model changes were detected during the {@link #ChangeDetection change detection} phase.
   */
  SchemaMigration(Trigger.ActivatingStore)
  {
    @Override
    protected Transition getTransitionTo(Phase nextPhase)
    {
      switch (nextPhase)
      {
      case StoreProcessing:
      case RepositoryProcessing:
        return Transition.StoreRestart;
      default:
        return null;
      }
    }

    /**
     * Installs the new package registry in the repository and forces remapping before executing the phase handler.
     */
    @Override
    public void done(Context context)
    {
      PhasedModelEvolutionSupport support = context.getSupport();
      IDBStore store = support.getStore();

      // Install the new package registry in the repository.
      InternalRepository repository = (InternalRepository)store.getRepository();
      repository.setPackageRegistry(support.getNewPackageRegistry());

      // Force remapping with new package registry.
      IMappingStrategy mappingStrategy = store.getMappingStrategy();
      mappingStrategy.clearClassMappings();
    }
  },

  /**
   * The store processing phase processes the DB store after the schema migration has taken place.
   * This phase is typically used to perform any necessary adjustments to the stored data
   * to ensure consistency with the new schema.
   * <p>
   * This phase is triggered by the {@link Trigger#ActivatingStore activating store} event. At this point in time,
   * the DB store is fully configured, but not yet active. The repository's package registry is filled with the new models.
   * The store's mapping registry has been updated to match the new models.
   * <p>
   * From this phase, a transition is possible to:
   * <ul>
   * <li>{@link #RepositoryProcessing} via the {@link Transition#NextTrigger next trigger} transition</li>
   * </ul>
   * <p>
   * The store processing phase is only executed if a {@link Phase.Handler phase handler} was registered for it.
   */
  StoreProcessing(Trigger.ActivatingStore)
  {
    @Override
    protected Transition getTransitionTo(Phase nextPhase)
    {
      switch (nextPhase)
      {
      case RepositoryProcessing:
        return Transition.NextTrigger;
      default:
        return null;
      }
    }
  },

  /**
   * The repository processing phase processes the repository after the schema migration has taken place.
   * This phase is typically used to perform any necessary adjustments to the repository data.
   * <p>
   * This phase is triggered by the {@link Trigger#ActivatedRepository activated repository} event. At this point in time,
   * the repository is fully configured and active. The repository's package registry is filled with the new models.
   * <p>
   * This is the terminal phase in the model evolution process. It is only executed if a {@link Phase.Handler phase handler}
   * was registered for it.
   */
  RepositoryProcessing(Trigger.ActivatedRepository)
  {
    @Override
    protected Transition getTransitionTo(Phase nextPhase)
    {
      return null;
    }
  };

  private static final String ORIGINAL_PACKAGE_REGISTRY_KEY = CDOPackageRegistry.class.getName();

  private final Trigger trigger;

  private Phase(Trigger trigger)
  {
    this.trigger = trigger;
  }

  /**
   * Returns true if this is the initial phase in the model evolution process.
   * The initial phase is always {@link #ChangeDetection}.
   */
  public boolean initial()
  {
    return ordinal() == 0;
  }

  /**
   * Returns the next phase in the model evolution process, or <code>null</code> if this is the terminal phase.
   * The terminal phase is always {@link #RepositoryProcessing}.
   */
  public Phase next()
  {
    Phase[] values = values();

    int nextOrdinal = ordinal() + 1;
    if (nextOrdinal < values.length)
    {
      return values[nextOrdinal];
    }

    return null;
  }

  /**
   * Returns the {@link Trigger trigger} that causes the execution of this phase.
   */
  public Trigger trigger()
  {
    return trigger;
  }

  /**
   * Returns the {@link Transition transition} to the given <code>nextPhase</code>, or <code>null</code>
   * if no such transition exists.
   */
  public Transition transitionTo(Phase nextPhase)
  {
    return nextPhase == null ? null : getTransitionTo(nextPhase);
  }

  protected abstract Transition getTransitionTo(Phase nextPhase);

  /**
   * Called before executing a phase handler.
   */
  public void init(Context context)
  {
    // Default implementation does nothing.
  }

  /**
   * Called after executing a phase handler.
   */
  public void done(Context context)
  {
    // Default implementation does nothing.
  }

  /**
   * Parses the given string into a Phase.
   */
  public static Phase parse(String str)
  {
    return StringUtil.parseEnum(Phase.class, str, true);
  }

  /**
   * A handler for a specific phase in the model evolution process.
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public interface Handler
  {
    /**
     * The product group for phase handlers.
     */
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.evolution.phaseHandlers"; //$NON-NLS-1$

    /**
     * Returns the phase this handler is responsible for.
     */
    public Phase getPhase();

    /**
     * Sets the phase this handler is responsible for.
     *
     * @noreference This method is not intended to be called by clients.
     */
    public void setPhase(Phase phase);

    /**
     * Executes the phase handler.
     */
    public void execute(Context context) throws Exception;
  }

  /**
   * The possible transitions between phases.
   * <p>
   * There are four possible transitions:
   * <ul>
   * <li>{@link #SameTrigger}: The next phase is triggered by the same trigger as the current phase.</li>
   * <li>{@link #NextTrigger}: The next phase is triggered by the next trigger in the sequence.</li>
   * <li>{@link #StoreRestart}: The next phase requires a restart of the DB store.</li>
   * <li>{@link #RepositoryRestart}: The next phase requires a restart of the repository.</li>
   * </ul>
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public enum Transition
  {
    /**
     * The next phase is triggered by the same trigger as the current phase.
     */
    SameTrigger,

    /**
     * The next phase is triggered by the next trigger in the sequence.
     */
    NextTrigger,

    /**
     * The next phase requires a restart of the DB store.
     */
    StoreRestart,

    /**
     * The next phase requires a restart of the repository.
     */
    RepositoryRestart;
  }
}
