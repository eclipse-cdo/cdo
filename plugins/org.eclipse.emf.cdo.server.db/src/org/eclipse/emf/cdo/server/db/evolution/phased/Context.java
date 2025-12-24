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

import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.TreeMapping;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;

import org.eclipse.net4j.db.StatementBatcher;
import org.eclipse.net4j.db.StatementBatcher.BatchEvent;
import org.eclipse.net4j.db.StatementBatcher.CloseEvent;
import org.eclipse.net4j.db.StatementBatcher.ResultEvent;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

  private final PhasedModelEvolutionSupport support;

  private final long timeStamp;

  private final Map<String, Model> models = new HashMap<>();

  private final Map<Model, Object> changeInfos = new HashMap<>();

  private final Map<String, EPackage> oldPackages = new HashMap<>();

  private final TreeMapping<EObject> elementMappings = new TreeMapping<>(EObject.class);

  private final Map<EEnum, Map<Integer, Integer>> enumLiteralChanges = new HashMap<>();

  private final AtomicInteger totalUpdateCount = new AtomicInteger();

  /**
   * Creates a context for model evolution with the given models.
   */
  public Context(PhasedModelEvolutionSupport support, Collection<Model> models)
  {
    this.support = support;
    timeStamp = support.getStore().getRepository().getTimeStamp();

    models.forEach(model -> {
      this.models.put(model.getID(), model);

      EPackage oldPackage = model.getOldPackage();
      EMFUtil.getAllPackages(oldPackage, ePackage -> oldPackages.put(ePackage.getNsURI(), ePackage));
      elementMappings.map(oldPackage, model.getNewPackage(), true);
    });
  }

  /**
   * Returns the model evolution support associated with this context.
   */
  public PhasedModelEvolutionSupport getSupport()
  {
    return support;
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
    support.log(message);
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
   * Returns the models being evolved in this context.
   * <p>
   * Includes both changed and unchanged models.
   */
  public Map<String, Model> getModels()
  {
    return Collections.unmodifiableMap(models);
  }

  /**
   * Returns the changed models along with their change infos.
   */
  public Map<Model, Object> getChangeInfos()
  {
    return Collections.unmodifiableMap(changeInfos);
  }

  /**
   * Adds the given changed model along with its change info.
   */
  public void addChangeInfo(Model model, Object changeInfo)
  {
    changeInfos.put(model, Objects.requireNonNull(changeInfo));
    support.addedChangeInfo(model, changeInfo);
  }

  /**
   * Returns the old EPackages mapped by their URIs as returned by {@link EcoreUtil#getURI(EObject)}.
   */
  public Map<String, EPackage> getOldPackages()
  {
    return Collections.unmodifiableMap(oldPackages);
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
   * Returns the total update count accumulated during this evolution.
   */
  public int getTotalUpdateCount()
  {
    return totalUpdateCount.get();
  }

  public int incrementTotalUpdateCount()
  {
    return totalUpdateCount.incrementAndGet();
  }

  public int incrementTotalUpdateCount(int delta)
  {
    return totalUpdateCount.addAndGet(delta);
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

    private String oldXMI;

    private String newXMI;

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
     * Returns the XMI serialization of the old EPackage of this model.
     */
    public String getOldXMI()
    {
      if (oldXMI == null)
      {
        oldXMI = EMFUtil.getXMI(oldPackage);
      }

      return oldXMI;
    }

    /**
     * Returns the XMI serialization of the new EPackage of this model, or <code>null</code> if the model has been removed.
     */
    public String getNewXMI()
    {
      if (newXMI == null && newPackage != null)
      {
        newXMI = EMFUtil.getXMI(newPackage);
      }

      return newXMI;
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
     * Returns the hash code of this model based on its ID.
     */
    @Override
    public int hashCode()
    {
      return Objects.hash(id);
    }

    /**
     * Returns whether this model is equal to the given object based on its ID.
     */
    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      Model other = (Model)obj;
      return Objects.equals(id, other.id);
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

  /**
   * A manager for creating, loading, and saving contexts.
   *
   * @author Eike Stepper
   * @since 4.14
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public interface Manager
  {
    /**
     * The product group for context manager implementations.
     */
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.evolution.contextManagers"; //$NON-NLS-1$

    /**
     * Returns the model evolution support associated with this context manager.
     */
    public PhasedModelEvolutionSupport getSupport();

    /**
     * Sets the model evolution support associated with this context manager.
     *
     * @noreference This method is not intended to be called by clients.
     */
    public void setSupport(PhasedModelEvolutionSupport support);

    /**
     * Creates a new context for model evolution.
     */
    public Context createContext() throws Exception;

    /**
     * Loads an existing context for model evolution.
     */
    public Context loadContext() throws Exception;

    /**
     * Saves the given context for model evolution.
     */
    public void saveContext(Context context) throws Exception;
  }
}
