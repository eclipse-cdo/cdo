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
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IModelEvolutionSupport.Context;
import org.eclipse.emf.cdo.server.db.IModelEvolutionSupport.Model;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.StatementBatcher;
import org.eclipse.net4j.db.StatementBatcher.BatchEvent;
import org.eclipse.net4j.db.StatementBatcher.CloseEvent;
import org.eclipse.net4j.db.StatementBatcher.ResultEvent;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

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
 * @author Eike Stepper
 */
public final class ModelEvolutionContext extends Notifier implements Context
{
  private final IRegistry<String, Object> properties = new HashMapRegistry.AutoCommit<>();

  private final IDBStore store;

  private final long timeStamp;

  private final boolean dry;

  private final List<Model> models;

  private final List<Model> changedModels;

  private final Map<String, EPackage> storedPackages = new HashMap<>();

  private final TreeMapping<EObject> elementMappings = new TreeMapping<>(EObject.class);

  private final Map<EEnum, Map<Integer, Integer>> enumLiteralChanges = new HashMap<>();

  private final AtomicInteger totalUpdateCount = new AtomicInteger();

  public ModelEvolutionContext(IDBStore store, List<Model> models, boolean dry)
  {
    timeStamp = store.getRepository().getTimeStamp();

    this.store = store;
    this.dry = dry;
    this.models = models;

    changedModels = models.stream().filter(Model::isChanged).collect(Collectors.toList());

    models.forEach(model -> {
      EPackage storedPackage = model.getStoredPackage();
      EMFUtil.getAllPackages(storedPackage, ePackage -> storedPackages.put(ePackage.getNsURI(), ePackage));
      elementMappings.map(storedPackage, model.getRegisteredPackage(), true);
    });
  }

  @Override
  public IRegistry<String, Object> properties()
  {
    return properties;
  }

  @Override
  public void log(Object message)
  {
    if (message != null)
    {
      OM.LOG.info(message.toString());
    }
  }

  @Override
  public IDBStore getStore()
  {
    return store;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public int getTotalUpdateCount()
  {
    return totalUpdateCount.get();
  }

  @Override
  public boolean isDry()
  {
    return dry;
  }

  @Override
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

  @Override
  public List<Model> getModels()
  {
    return models;
  }

  @Override
  public List<Model> getChangedModels()
  {
    return changedModels;
  }

  @Override
  public Map<String, EPackage> getStoredPackages()
  {
    return storedPackages;
  }

  @Override
  public TreeMapping<EObject> getElementMappings()
  {
    return elementMappings;
  }

  @Override
  public <T extends EObject> T getRegisteredElement(T storedElement)
  {
    @SuppressWarnings("unchecked")
    T registeredElement = (T)elementMappings.get(storedElement);
    return registeredElement;
  }

  @Override
  public Map<Integer, Integer> getEnumLiteralChanges(EEnum storedEnum)
  {
    return enumLiteralChanges.computeIfAbsent(storedEnum, k -> {
      Map<Integer, Integer> changes = new HashMap<>();

      EEnum registeredEnum = (EEnum)elementMappings.get(storedEnum);
      if (registeredEnum != null)
      {
        storedEnum.getELiterals().forEach(storedLiteral -> {
          EEnumLiteral registeredLiteral = (EEnumLiteral)elementMappings.get(storedLiteral);
          if (registeredLiteral != null)
          {
            int storedLiteralID = storedLiteral.getValue();
            int registeredLiteralID = registeredLiteral.getValue();
            if (storedLiteralID != registeredLiteralID)
            {
              changes.put(storedLiteralID, registeredLiteralID);
            }
          }
        });
      }

      return changes;
    });
  }

  @Override
  public <F extends EStructuralFeature> void handleFeatureIDChanges(EClass storedClass, Function<EClass, Collection<F>> featureProvider,
      BiConsumer<Integer, Integer> handler)
  {
    if (handler != null)
    {
      EClass registeredClass = (EClass)elementMappings.get(storedClass);
      if (registeredClass != null)
      {
        Collection<F> features = featureProvider.apply(storedClass);
        if (features != null)
        {
          features.forEach(storedFeature -> {
            EStructuralFeature registeredFeature = (EStructuralFeature)elementMappings.get(storedFeature);
            if (registeredFeature != null)
            {
              int storedFeatureID = storedClass.getFeatureID(storedFeature);
              int registeredFeatureID = registeredClass.getFeatureID(registeredFeature);
              if (storedFeatureID != registeredFeatureID)
              {
                handler.accept(storedFeatureID, registeredFeatureID);
              }
            }
          });
        }
      }
    }
  }
}
