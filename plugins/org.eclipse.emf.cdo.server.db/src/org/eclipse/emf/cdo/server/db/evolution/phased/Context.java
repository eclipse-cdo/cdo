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

import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.TreeMapping;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.server.db.evolution.IModelEvolutionSupport;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.StatementBatcher;
import org.eclipse.net4j.db.StatementBatcher.BatchEvent;
import org.eclipse.net4j.db.StatementBatcher.CloseEvent;
import org.eclipse.net4j.db.StatementBatcher.ResultEvent;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.properties.IPropertiesContainer;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The context for model evolution.
 * <p>
 * Provides access to the models being evolved, their old and new EPackages, element mappings, and utilities for
 * logging and statement batching.
 * <p>
 * Instances of this class are created per model evolution operation and passed to the various phases and handlers.
 * If model changes are detected, a Context instance is created and used throughout the evolution process. It is
 * saved to the {@link PhasedModelEvolutionSupport#getEvolutionFolder() evolution folder} and loaded from there when
 * resuming an evolution operation.
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public final class Context implements CDOTimeProvider, IPropertiesContainer
{
  private final IRegistry<String, Object> properties = new HashMapRegistry.AutoCommit<>();

  private final IModelEvolutionSupport support;

  private final long timeStamp;

  private final List<Model> models;

  private final List<Model> changedModels;

  private final Map<String, EPackage> oldPackages = new HashMap<>();

  private final TreeMapping<EObject> elementMappings = new TreeMapping<>(EObject.class);

  private final Map<EEnum, Map<Integer, Integer>> enumLiteralChanges = new HashMap<>();

  private final AtomicInteger totalUpdateCount = new AtomicInteger();

  /**
   * Creates a context for model evolution with the given models.
   */
  public Context(IModelEvolutionSupport support, List<Model> models)
  {
    this.support = support;
    this.models = models;

    changedModels = models.stream().filter(Model::isChanged).collect(Collectors.toList());
    timeStamp = support.getStore().getRepository().getTimeStamp();

    models.forEach(model -> {
      EPackage oldPackage = model.getOldPackage();
      EMFUtil.getAllPackages(oldPackage, ePackage -> oldPackages.put(ePackage.getNsURI(), ePackage));
      elementMappings.map(oldPackage, model.getNewPackage(), true);
    });
  }

  /**
   * Returns the properties container for this context.
   * <p>
   * Can be used by phases and handlers to store and retrieve arbitrary named objects.
   */
  @Override
  public IRegistry<String, Object> properties()
  {
    return properties;
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
      OM.LOG.info(message.toString());
    }
  }

  /**
   * Returns the model evolution support associated with this context.
   */
  public IModelEvolutionSupport getSupport()
  {
    return support;
  }

  /**
   * Returns the timestamp of this context.
   */
  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  /**
   * Returns the total update count accumulated during this evolution.
   */
  public int getTotalUpdateCount()
  {
    return totalUpdateCount.get();
  }

  /**
   * Returns the models being evolved in this context.
   * <p>
   * Includes both changed and unchanged models.
   */
  public List<Model> getModels()
  {
    return models;
  }

  /**
   * Returns the changed models being evolved in this context.
   */
  public List<Model> getChangedModels()
  {
    return changedModels;
  }

  /**
   * Returns the old EPackages mapped by their URIs as returned by {@link EcoreUtil#getURI(EObject)}.
   */
  public Map<String, EPackage> getOldPackages()
  {
    return oldPackages;
  }

  /**
   * Returns the element mappings from old to new EObjects.
   * <p>
   * Most of the mappings are between EModelElements, but there can also be mappings between arbitrary EObjects,
   * e.g., for contents in EAnnotations.
   */
  public TreeMapping<EObject> getElementMappings()
  {
    return elementMappings;
  }

  /**
   * Returns the new element corresponding to the given old element, or <code>null</code> if there is no such mapping.
   */
  public <T extends EObject> T getNewElement(T oldElement)
  {
    @SuppressWarnings("unchecked")
    T newElement = (T)elementMappings.get(oldElement);
    return newElement;
  }

  /**
   * Returns a map of old to new literal IDs for the given old enum.
   */
  public Map<Integer, Integer> getEnumLiteralChanges(EEnum oldEnum)
  {
    return enumLiteralChanges.computeIfAbsent(oldEnum, k -> {
      Map<Integer, Integer> changes = new HashMap<>();

      EEnum newEnum = (EEnum)elementMappings.get(oldEnum);
      if (newEnum != null)
      {
        oldEnum.getELiterals().forEach(oldLiteral -> {
          EEnumLiteral newLiteral = (EEnumLiteral)elementMappings.get(oldLiteral);
          if (newLiteral != null)
          {
            int oldLiteralID = oldLiteral.getValue();
            int newLiteralID = newLiteral.getValue();
            if (oldLiteralID != newLiteralID)
            {
              changes.put(oldLiteralID, newLiteralID);
            }
          }
        });
      }

      return changes;
    });
  }

  /**
   * Handles feature ID changes for the given old class by applying the given feature provider and invoking the given handler
   * for each detected feature ID change.
   */
  public <F extends EStructuralFeature> void handleFeatureIDChanges(EClass oldClass, Function<EClass, Collection<F>> featureProvider,
      BiConsumer<Integer, Integer> handler)
  {
    if (handler != null)
    {
      EClass newClass = (EClass)elementMappings.get(oldClass);
      if (newClass != null)
      {
        Collection<F> oldFeatures = featureProvider.apply(oldClass);
        if (oldFeatures != null)
        {
          oldFeatures.forEach(oldFeature -> {
            EStructuralFeature newFeature = (EStructuralFeature)elementMappings.get(oldFeature);
            if (newFeature != null)
            {
              int oldFeatureID = oldClass.getFeatureID(oldFeature);
              int newFeatureID = newClass.getFeatureID(newFeature);
              if (oldFeatureID != newFeatureID)
              {
                handler.accept(oldFeatureID, newFeatureID);
              }
            }
          });
        }
      }
    }
  }

  /**
   * Creates a statement batcher for the given connection that logs batch and result events to the evolution log
   * and accumulates the total update count.
   */
  public StatementBatcher createStatementBatcher(Connection connection) throws SQLException
  {
    StatementBatcher batcher = new StatementBatcher(connection);
    batcher.addListener(event -> {
      if (event instanceof BatchEvent || event instanceof ResultEvent)
      {
        log(event);
      }
      else if (event instanceof CloseEvent)
      {
        totalUpdateCount.addAndGet(batcher.getTotalUpdateCount());
      }
    });

    return batcher;
  }

  /**
   * Creates a cancelation error to abort model evolution.
   * <p>
   * Typically used by the Change Detection phase to abort evolution when no model changes are detected.
   */
  public Error cancelation()
  {
    return new Cancelation();
  }

  /**
   * Runs the given runnable and returns whether it was canceled.
   */
  public static boolean canceled(RunnableWithException runnable) throws Exception
  {
    try
    {
      runnable.run();
      return false;
    }
    catch (Cancelation ex)
    {
      return true;
    }
  }

  /**
   * Indicates the cancelation of model evolution.
   *
   * @author Eike Stepper
   */
  private static final class Cancelation extends Error
  {
    private static final long serialVersionUID = 1L;

    public Cancelation()
    {
    }
  }

  /**
   * A model stored in the DB store.
   * <p>
   * Includes its ID, original type, timestamp, old and new EPackages, and whether it has changed.
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public static final class Model
  {
    private final String id;

    private final CDOPackageUnit.Type originalType;

    private final long timeStamp;

    private final EPackage oldPackage;

    private final EPackage newPackage;

    private final boolean changed;

    /**
     * Creates a model with the given ID, original type, timestamp, old and new EPackages.
     */
    public Model(String id, Type originalType, long timeStamp, EPackage oldPackage, EPackage newPackage)
    {
      this.id = id;
      this.originalType = originalType;
      this.timeStamp = timeStamp;
      this.oldPackage = oldPackage;
      this.newPackage = newPackage;
      changed = newPackage != null && !EcoreUtil.equals(oldPackage, newPackage);
    }

    /**
     * Returns the ID of this model. The ID corresponds to the nsURI of the root EPackage.
     */
    public String getID()
    {
      return id;
    }

    /**
     * Returns the original type of this model.
     */
    public CDOPackageUnit.Type getOriginalType()
    {
      return originalType;
    }

    /**
     * Returns the timestamp of this model.
     */
    public long getTimeStamp()
    {
      return timeStamp;
    }

    /**
     * Returns the old root EPackage of this model.
     */
    public EPackage getOldPackage()
    {
      return oldPackage;
    }

    /**
     * Returns the new root EPackage of this model, or <code>null</code> if the model has been removed.
     */
    public EPackage getNewPackage()
    {
      return newPackage;
    }

    /**
     * Returns whether this model has changed.
     * <p>
     * A model is considered changed if its new EPackage exists and is not equal to its old EPackage, as determined by
     * {@link EcoreUtil#equals(EObject, EObject)}.
     */
    public boolean isChanged()
    {
      return changed;
    }

    /**
     * Returns whether this model has been removed.
     * <p>
     * A model is considered removed if its new EPackage is <code>null</code>.
     */
    public boolean isRemoved()
    {
      return newPackage == null;
    }

    /**
     * Returns a string representation of this model for logging purposes.
     */
    @Override
    public String toString()
    {
      return "Model[id=" + id + ", originalType=" + originalType + ", timeStamp=" + timeStamp + "]";
    }
  }
}
