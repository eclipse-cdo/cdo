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
package org.eclipse.emf.cdo.server.db.evolution;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.evolution.phased.PhasedModelEvolutionSupport;
import org.eclipse.emf.cdo.server.internal.db.DBStore;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * DB store support for model evolution.
 * <p>
 * Model evolution support is responsible for evolving the models stored in a DB store to match the currently
 * registered EPackages.
 * <p>
 * Model evolution support implementations are registered via {@link IFactory factories} contributing to the
 * <code>org.eclipse.emf.cdo.server.db.evolution.supports</code> {@link #PRODUCT_GROUP product group}.
 * Here is an example contribution in plugin.xml:
 * <pre>
 * &lt;extension point="org.eclipse.net4j.util.factories">
 *    &lt;annotationFactory
 *       productGroup="org.eclipse.emf.cdo.server.db.evolution.supports"
 *       type="phased"
 *       productClass="org.eclipse.emf.cdo.server.db.evolution.phased.PhasedModelEvolutionSupport"/>
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
 *   &lt;modelEvolutionSupport type="phased" rootFolder="@config/evolution" mode="migrate">
 *     &lt;changeDetector type="default-change-detector"/>
 *     &lt;repositoryExporter type="default-repository-exporter" binary="false"/>
 *     &lt;schemaMigrator type="default-schema-migrator"/>
 *     &lt;storeProcessor type="custom-store-processor" myExtraArg="data"/>
 *     &lt;repositoryProcessor type="custom-repository-processor" myExtraArg="data"/> -->
 *     &lt;listener type="log"/>
 *     &lt;listener type="myCustomModelEvolutionExtraChecks"/>
 *   &lt;/modelEvolutionSupport>
 *   ...
 * &lt;/store>
 * </pre>
 * The optional <code>mode</code> attribute can be used to specify whether model evolution should be
 * performed automatically (<code>migrate</code>), only prevented with an exception (<code>prevent</code>),
 * or skipped entirely (<code>disabled</code>) when model changes are detected.
 * <p>
 * The optional <code>listener</code> elements specify additional model evolution listeners to be
 * notified during model evolution. They can be used to implement logging, custom checks, or additional
 * evolution steps.
 * <p>
 * Note that model evolution happens very late during the activation of a DB store, but very early
 * during the activation of the {@link IRepository}. Therefore, phase handlers must not rely on
 * any services of the repository. In particular, they must not access the repository's
 * {@link IRepository#getPackageRegistry() package registry} because it is not yet available!
 *
 * @see PhasedModelEvolutionSupport
 *
 * @author Eike Stepper
 * @since 4.14
 */
public interface IModelEvolutionSupport extends ILifecycle
{
  /**
   * The product group for model evolution support implementations.
   */
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.evolution.supports"; //$NON-NLS-1$

  /**
   * Returns the DB store this model evolution support is associated with.
   */
  public IDBStore getStore();

  /**
   * Sets the DB store this model evolution support is associated with.
   *
   * @noreference This method is not intended to be called by clients.
   */
  public void setStore(IDBStore store);

  /**
   * Evolves the models stored in the given DB store to match the currently registered <code>EPackages</code>.
   */
  public void trigger(Trigger trigger) throws Exception;

  /**
   * The triggers for model evolution.
   * <p>
   * There are two triggers:
   * <ul>
   * <li>{@link #ActivatingStore}: Triggered when the DB store is being activated by the {@link IRepository}.
   *    This is the main trigger for model evolution. It only occurs when the store is restarted, i.e., not
   *    when the store is activated for the first time.</li>
   * <li>{@link #ActivatedRepository}: Triggered when the {@link IRepository} has been fully activated.
   *   This trigger exists mainly for special purposes, e.g., to export repository models before the schema
   *   is upgraded.</li>
   * </ul>
   *
   * @author Eike Stepper
   */
  public enum Trigger
  {
    /**
     * Trigger when the DB store is being activated by the {@link IRepository}.
     * This is the main trigger for model evolution. It only occurs when the store is restarted,
     * i.e., not when the store is activated for the first time.
     * <p>
     * When this trigger occurs, the DB store is fully initialized, but not yet activated.
     * The repository is in the process of being activated, but not yet activated. It is mostly
     * unavailable at this point.
     */
    ActivatingStore,

    /**
     * Trigger when the {@link IRepository} has been fully activated. This trigger exists mainly for special purposes,
     * e.g., to export repository models before the schema is upgraded.
     * <p>
     * When this trigger occurs, both the DB store and the repository are fully activated and available.
     */
    ActivatedRepository;
  }
}
