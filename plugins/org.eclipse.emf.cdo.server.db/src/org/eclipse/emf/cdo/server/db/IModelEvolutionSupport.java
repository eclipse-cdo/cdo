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
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil.TreeMapping;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;

import org.eclipse.net4j.db.StatementBatcher;
import org.eclipse.net4j.util.event.INotifier.INotifier2;
import org.eclipse.net4j.util.properties.IPropertiesContainer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Eike Stepper
 * @since 4.14
 */
public interface IModelEvolutionSupport extends INotifier2
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.modelEvolutionSupports"; //$NON-NLS-1$

  public IDBStore getStore();

  public void setStore(IDBStore store);

  /**
   * Evolves the models stored in the given DB store to match the currently registered EPackages.
   */
  public void evolveModels() throws SQLException;

  /**
   * @author Eike Stepper
   */
  public interface Context extends CDOTimeProvider, IPropertiesContainer
  {
    public IModelEvolutionSupport getModelEvolutionSupport();

    public IDBStore getStore();

    public List<Model> getModels();

    public List<Model> getChangedModels();

    public Map<String, EPackage> getStoredPackages();

    public TreeMapping<EObject> getElementMappings();

    public <T extends EObject> T getRegisteredElement(T storedElement);

    public Map<Integer, Integer> getEnumLiteralChanges(EEnum storedEnum);

    public <F extends EStructuralFeature> void handleFeatureIDChanges(EClass storedClass, Function<EClass, Collection<F>> featureProvider,
        BiConsumer<Integer, Integer> handler);

    public StatementBatcher createStatementBatcher(Connection connection) throws SQLException;

    public void log(Object message);

    public int getTotalUpdateCount();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Model
  {
    private final String id;

    private final CDOPackageUnit.Type originalType;

    private final long timeStamp;

    private final EPackage storedPackage;

    private final EPackage registeredPackage;

    private final boolean changed;

    public Model(String id, Type originalType, long timeStamp, EPackage storedPackage, EPackage registeredPackage)
    {
      this.id = id;
      this.originalType = originalType;
      this.timeStamp = timeStamp;
      this.storedPackage = storedPackage;
      this.registeredPackage = registeredPackage;
      changed = registeredPackage != null && !EcoreUtil.equals(storedPackage, registeredPackage);
    }

    public String getID()
    {
      return id;
    }

    public CDOPackageUnit.Type getOriginalType()
    {
      return originalType;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public EPackage getStoredPackage()
    {
      return storedPackage;
    }

    public EPackage getRegisteredPackage()
    {
      return registeredPackage;
    }

    public boolean isChanged()
    {
      return changed;
    }

    public boolean isRemoved()
    {
      return registeredPackage == null;
    }

    @Override
    public String toString()
    {
      return "Model[id=" + id + ", originalType=" + originalType + ", timeStamp=" + timeStamp + "]";
    }

    /**
     * @author Eike Stepper
     */
    public interface Loader
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.modelLoaders"; //$NON-NLS-1$

      public List<Model> loadModels(IDBStore store) throws SQLException;
    }
  }
}
