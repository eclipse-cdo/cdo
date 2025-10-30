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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IModelEvolutionSupport.Model;
import org.eclipse.emf.cdo.server.internal.db.DBStore;

import org.eclipse.net4j.db.StatementBatcher;
import org.eclipse.net4j.util.event.INotifier.INotifier2;
import org.eclipse.net4j.util.factory.IFactory;
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
 * DB store support for model evolution.
 * <p>
 * Model evolution support is responsible for evolving the models stored in a DB store to match the currently
 * registered EPackages.
 * <p>
 * Model evolution support implementations are registered via {@link IFactory factories} contributing to the
 * <code>org.eclipse.emf.cdo.server.db.modelEvolutionSupports</code> {@link #PRODUCT_GROUP product group}.
 * Here is an example contribution in plugin.xml:
 * <pre>
 * &lt;extension point="org.eclipse.net4j.util.factories">
 *    &lt;annotationFactory
 *       productGroup="org.eclipse.emf.cdo.server.db.modelEvolutionSupports"
 *       type="default"
 *       productClass="org.eclipse.emf.cdo.server.internal.db.ModelEvolutionSupport"/>
 * &lt;/extension>
 * </pre>
 * If model evolution support is desired, an instance must be created and set on the DB store via
 * {@link DBStore#setModelEvolutionSupport(IModelEvolutionSupport) setModelEvolutionSupport()} before the store
 * is activated by the {@link IRepository}. If the store is configured via the <code>CDOServerApplication</code> and
 * the <code>cdo-server.xml</code> configuration file, this can be achieved by specifying the
 * <code>modelEvolutionSupport</code> element as follows:
 * <pre>
 * &lt;store type="db">
 *   ...
 *   &lt;modelEvolutionSupport type="default" mode="evolve">
 *     &lt;modelLoader type="default"/>
 *     &lt;listener type="log"/>
 *     &lt;listener type="myCustomModelEvolutionExtraChecks"/>
 *   &lt;/modelEvolutionSupport>
 *   ...
 * &lt;/store>
 * </pre>
 * The optional <code>mode</code> attribute can be used to specify whether model evolution should be
 * performed automatically (<code>evolve</code>), only prevented with an exception (<code>prevent</code>),
 * or skipped entirely (<code>disabled</code>) when model changes are detected.
 * <p>
 * The optional <code>modelLoader</code> element specifies the model loader to be used for loading the models
 * stored in the DB store. If omitted, the default model loader is used.
 * <p>
 * The optional <code>listener</code> elements specify additional model evolution listeners to be
 * notified during model evolution. They can be used to implement logging, custom checks, or additional
 * evolution steps.
 * <p>
 * Note that model evolution happens very late during the activation of a DB store, but very early
 * during the activation of the {@link IRepository}. Therefore, model loaders must not rely on
 * any services of the repository. In particular, they must not access the repository's
 * {@link IRepository#getPackageRegistry() package registry} because it is not yet available!
 *
 * @author Eike Stepper
 * @since 4.14
 */
public interface IModelEvolutionSupport extends INotifier2
{
  /**
   * The product group for model evolution support implementations.
   */
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.modelEvolutionSupports"; //$NON-NLS-1$

  /**
   * Returns the DB store this model evolution support is associated with.
   */
  public IDBStore getStore();

  /**
   * Sets the DB store this model evolution support is associated with.
   * <p>
   * This method is called by the DB store when the model evolution support is set on the store.
   * After this method is called, the store is activating the model evolution support and calling
   * {@link #evolveModels()}.
   */
  public void setStore(IDBStore store);

  /**
   * Evolves the models stored in the given DB store to match the currently registered <code>EPackages</code>.
   */
  public void evolveModels() throws SQLException;

  /**
   * Context information for model evolution.
   * <p>
   * The context is used internally during model evolution to share information and provide services.
   *
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
   * A model stored in the DB store.
   * <p>
   * Models are loaded via {@link Model.Loader model loaders} when model evolution is performed.
   * They provide access to the stored and registered (if available) <code>EPackages</code> as well as metadata
   * like the model ID, original type, and time stamp.
   *
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
     * Loads models stored in a DB store.
     * <p>
     * Model loaders are registered via {@link IFactory factories} contributing to the
     * <code>org.eclipse.emf.cdo.server.db.modelLoaders</code> {@link #PRODUCT_GROUP product group}.
     * Here is an example contribution in plugin.xml:
     * <pre>
     * &lt;extension point="org.eclipse.net4j.util.factories">
     *   &lt;annotationFactory
     *     productGroup="org.eclipse.emf.cdo.server.db.modelLoaders"
     *     type="default"
     *     productClass="org.eclipse.emf.cdo.server.internal.db.DefaultModelLoader"/>
     * &lt;/extension>
     * </pre>
     * Model loaders are used by model evolution support implementations to load the models
     * stored in a DB store and to provide the corresponding registered <code>EPackages</code> (if available).
     * <p>
     * Note that model evolution happens very late during the activation of a DB store, but very early
     * during the activation of the {@link IRepository}. Therefore, model loaders must not rely on
     * any services of the repository. In particular, they must not access the repository's
     * {@link IRepository#getPackageRegistry() package registry} because it is not yet available!
     *
     * @author Eike Stepper
     */
    public interface Loader
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.modelLoaders"; //$NON-NLS-1$

      public List<Model> loadModels(IDBStore store) throws SQLException;
    }
  }
}
