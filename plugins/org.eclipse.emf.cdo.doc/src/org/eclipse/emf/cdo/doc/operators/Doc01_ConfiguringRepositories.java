/*
 * Copyright (c) 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.operators;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories.Element_repository.Property_supportingAudits;
import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories.Element_repository.Property_supportingBranches;
import org.eclipse.emf.cdo.doc.users.Doc09_TechnicalBackground.Doc_BackgroundModelElements.Doc_BackgroundLegacyModels;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.mapping.ColumnTypeModifier;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.view.CDOUnit;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.util.factory.IFactory;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import javax.sql.DataSource;

/**
 * Configuring Repositories
 * <p>
 * The repositories of a CDO Server are configured in the cdo-server.xml file. Here's an example:
 * {@link #cdoServerXML() cdo&#8209;server.xml}
 * <p>
 * The following sections describe the various elements and properties.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc01_ConfiguringRepositories
{
  /**
   * @snippet xml cdo-server-repository.xml
   */
  public void cdoServerXML()
  {
  }

  /**
   * Element repository
   * <p>
   * Defines an {@link IRepository} instance.
   * <p>
   * The <code>name</code> attribute uniquely identifies a repository in the scope of a repository configurator.
   * <p>
   * The <code>repository</code> element can contain several property elements (see below) and must contain exactly one {@link Element_store store} element.
   */
  public class Element_repository
  {
    /**
     * Property overrideUUID
     * <p>
     * Specifies a constant UUID for the repository. If omitted the repository will be created with a random UUID.
     * The format of an override UUID is not further specified but should respect the file naming conventions of the used operating system.
     * <p>
     * Overriding the default random UUID can be useful if you have scripts that operate on the file system folder
     * that is created on the server for each repository and named after the repository UUID.
     */
    public class Property_overrideUUID
    {
    }

    /**
     * Property supportingAudits
     * <p>
     * Specifies whether the repository will support audit views or not. Please note that a repository can only support audit views
     * if its {@link Element_store store} supports audit views, as well.
     * <p>
     * The shipped DBStore does support audit views.
     * Note also that it will not delete or update rows for modified objects if audits are supported.
     * All revised state of the repository will be kept in the DB which can result in databases growing very large!
     */
    public class Property_supportingAudits
    {
    }

    /**
     * Property supportingBranches
     * <p>
     * Specifies whether the repository will support the creation and usage of branches below the always existing main branch or not.
     * Please note that a repository can only support branches if its {@link Element_store store} supports branches, as well.
     * <p>
     * Also note that branching support always <b>requires</b> {@link Property_supportingAudits auditing support}, too.
     */
    public class Property_supportingBranches
    {
    }

    /**
     * Property supportingEcore
     * <p>
     * Specifies whether the repository will support the storage of instances of the Ecore (meta meta) model or not.
     * <p>
     * With the advent of the {@link Doc_BackgroundLegacyModels legacy mode} in CDO 3.0 you can store instances of any model in CDO repositories.
     * Whether these models have been generated for CDO or not only influences their characteristics (scalability, performance, etc.).
     * As a consequence you can also store instances of the Ecore (meta meta) model in CDO Repositories.
     * Since Ecore is always registered in all package registries the legacy mode would lead to the creation of mapped tables in many types of stores,
     * even if you never planned to store instances of Ecore.
     * <p>
     * Valid values: <code>false</code> (default) or <code>true</code>.
     * <p>
     * <b>This property is deprecated. As of 4.2 instances of Ecore are always supported (on demand).</b>
     */
    public class Property_supportingEcore
    {
    }

    /**
     * Property supportingUnits
     * <p>
     * Specifies whether the repository will support the creation and optimized loading of {@link CDOUnit units} or not.
     * <p>
     * Unit support is only available if the configured {@link IStore store} supports units.
     * <p>
     * Valid values: <code>false</code> (default) or <code>true</code>.
     */
    public class Property_supportingUnits
    {
    }

    /**
     * Property checkUnitMoves
     * <p>
     * Specifies whether the repository will apply extra validation to prevent moves of objects between {@link CDOUnit units} or not.
     * <p>
     * Valid values: <code>false</code> (default) or <code>true</code>.
     * <p>
     *
     * @see Property_supportingUnits
     */
    public class Property_checkUnitMoves
    {
    }

    /**
     * Property ensureReferentialIntegrity
     * <p>
     * Specifies whether the repository will detect and reject commits that would leave stale references in the object graph.
     * <p>
     * Valid values: <code>false</code> (default) or <code>true</code>.
     */
    public class Property_ensureReferentialIntegrity
    {
    }

    /**
     * Property serializeCommits
     * <p>
     * Specifies whether the repository will serialize commit operations by utilizing a lock or not.
     * <p>
     * Some stores, such as the LissomeStore, require commit operations to be serialized.
     * <p>
     * Valid values: <code>false</code> (default) or <code>true</code>.
     */
    public class Property_serializeCommits
    {
    }

    /**
     * Property_allowInterruptRunningQueries
     * <p>
     * Specifies whether the repository will cancel a scheduled query job if it is already running.
     * Some underlying stores (e.g. DBStore with a Derby database) might not be able to deal with this cleanly.
     * For such stores, this parameter can be set to <code>false</code>.
     * <p>
     * Valid values: <code>false</code> (default) or <code>true</code>.
     */
    public class Property_allowInterruptRunningQueries
    {
    }

    /**
     * Property idGenerationLocation
     * <p>
     * Specifies whether the repository will expect clients to generate IDs for new objects or whether it will ask the backend store to generate them.
     * <p>
     * Valid values: <code>STORE</code> (default) or <code>CLIENT</code>.
     */
    public class Property_idGenerationLocation
    {
    }
  }

  /**
   * Element securityManager
   * <p>
   * Example: &lt;securityManager type="default" description="/security:annotation:home(/home)"/>
   * <p>
   * See also: <a href="http://wiki.eclipse.org/CDO/Security_Manager">http://wiki.eclipse.org/CDO/Security_Manager</a>
   */
  public class Element_securityManager
  {
  }

  /**
   * Element authenticator
   * <p>
   * Example: &lt;authenticator type="file" description="_database/repo1.users"/>
   * <p>
   * See also: <a href="http://bugs.eclipse.org/302775">http://bugs.eclipse.org/302775</a>
   */
  public class Element_authenticator
  {
  }

  /**
   * Element initialPackage
   * <p>
   * Example: &lt;initialPackage nsURI="http://www.eclipse.org/emf/CDO/examples/company/1.0.0"/>
   * <p>
   * See also: <a href="http://bugs.eclipse.org/345431">http://bugs.eclipse.org/345431</a>
   */
  public class Element_initialPackage
  {
  }

  /**
   * Element store
   * <p>
   * Defines an {@link IStore} instance.
   * <p>
   * The <code>type</code> attribute corresponds to the type of a store factory that is contributed via the
   * <code>org.eclipse.emf.cdo.server.storeFactory</code> extension point.
   * The remaining attributes depend on the specified <code>type</code> attribute value.
   * The following values are possible with the shipped distribution (subject to user-supplied extension):
   * <ul>
   * <li> <b>mem</b>: Store without real persistence. A repository with a MEMStore can function properly as long as the the server is not restarted.
   *      No additional attributes are recognized.
   * <li> <b>db</b>: Store that connects via JDBC to a relational database and manages persistent revisions and models
   *      through a built-in O/R mapper, see [[CDO/DB Store]]. A DBStore element can contain the following nested elements:
   *      <ul>
   *        <li> {@link Element_mappingStrategy}
   *        <li> {@link Element_dbAdapter}
   *        <li> {@link Element_dataSource}
   *      </ul>
   * <li> <b>hibernate:</b> Store that uses Teneo/Hibernate, see [[CDO/Hibernate Store]].
   * <li> <b>objectivity:</b> Store that uses Objectivity/DB, see [[CDO/Objectivity Store]].
   * <li> <b>mongodb:</b> Store that uses MongoDB, see [[CDO/MongoDB Store]].
   * <li> <b>db4o:</b> Store that uses DB4O, see [[CDO/DB4O Store]].
   * </ul>
   */
  public class Element_store
  {
    /**
     * Property connectionKeepAlivePeriod
     * <p>
     * Specifies, if the store is a DBStore, at what interval in minutes the store will issue an SQL statement to keep the connection to the database alive.
     */
    public class Property_connectionKeepAlivePeriod
    {
    }

    /**
     * Property connectionRetryCount
     * <p>
     * Specifies, if the store is a DBStore, the number of additional attempts to connect to the DB after initial connection failure.
     */
    public class Property_connectionRetryCount
    {
    }

    /**
     * Property connectionRetrySeconds
     * <p>
     * Specifies, if the store is a DBStore, the number of seconds to wait before additional attempts to connect to the DB after initial connection failure.
     */
    public class Property_connectionRetrySeconds
    {
    }

    /**
     * Property readerPoolCapacity
     * <p>
     * Specifies, if the store is a DBStore, the maximum number of store accessors (JDBC connections) to keep in the reader pool.
     * <p>
     * The default value is 15.
     */
    public class Property_readerPoolCapacity
    {
    }

    /**
     * Property writerPoolCapacity
     * <p>
     * Specifies, if the store is a DBStore, the maximum number of store accessors (JDBC connections) to keep in the writer pool.
     * <p>
     * The default value is 15.
     */
    public class Property_writerPoolCapacity
    {
    }

    /**
     * Property dropAllDataOnActivate
     * <p>
     * If set to <code>true</code> and the store is a DBStore, drops all database tables of the configured
     * {@link Element_dataSource schema} at the beginning of the store activation.
     * <p>
     * The default value is <code>false</code>.
     */
    public class Property_dropAllDataOnActivate
    {
    }
  }

  /**
   * Element mappingStrategy
   * <p>
   * This element is recognized by DBStores and defines the overall {@link IMappingStrategy mapping strategy} of the built-in O/R mapper.
   * <p>
   * The <code>type</code> attribute corresponds to the type of a mapping strategy factory that is contributed via the
   * <code>org.eclipse.emf.cdo.server.db.mappingStrategies</code> extension point.
   * The following values are possible with the shipped distribution (subject to user-supplied extension):
   * <ul>
   * <li> <b>horizontal</b>: Mapping strategy that creates one DB table per concrete model class.
   *      The following nested property elements are recognized:
   *      <ul>
   *        <li> {@link Property_toManyReferences}
   *        <li> {@link Property_maxTableNameLength}
   *        <li> {@link Property_maxFieldNameLength}
   *        <li> {@link Property_tableNamePrefix}
   *        <li> {@link Property_qualifiedNames}
   *        <li> {@link Property_forceNamesWithID}
   *      </ul>
   * </ul>
   */
  public class Element_mappingStrategy
  {
    /**
     * Property toManyReferences
     * <p>
     * Specifies how the built-in O/R mapper will handle to-many references (collections). The following values are recognized:
     * <ul>
     * <li> <b>ONE_TABLE_PER_REFERENCE</b>: Each to-many reference of the model will get its own DB table.
     * <li> <b>ONE_TABLE_PER_CLASS</b>: All to-many references of a model class will share a single DB table.
     * <li> <b>ONE_TABLE_PER_PACKAGE</b>: All to-many references of a model package will share a single DB table.
     * <li> <b>ONE_TABLE_PER_REPOSITORY</b>: All to-many references of all model classes i the repository will share a single DB table.
     * </ul>
     */
    public class Property_toManyReferences
    {
    }

    /**
     * Property maxTableNameLength
     * <p>
     * Enables you to override the default value of the chosen DB adapter for the maximum length of table names.
     */
    public class Property_maxTableNameLength
    {
    }

    /**
     * Property maxFieldNameLength
     * <p>
     * Enables you to override the default value of the chosen DB adapter for the maximum length of column names.
     */
    public class Property_maxFieldNameLength
    {
    }

    /**
     * Property tableNamePrefix
     * <p>
     * Specifies a common fixed prefix for all table names generated by this mapping strategy.
     */
    public class Property_tableNamePrefix
    {
    }

    /**
     * Property qualifiedNames
     * <p>
     * Specifies whether generated package or class table names are qualified or not.
     */
    public class Property_qualifiedNames
    {
    }

    /**
     * Property forceNamesWithID
     * <p>
     * Specifies whether generated names are always suffixed with an internal ID or only in cases where the generated name absolutely needs mangling.
     */
    public class Property_forceNamesWithID
    {
    }

    /**
     * Property fieldConstructionTracking
     * <p>
     * Specifies whether you want {@link IDBField} construction stacktrace on schema update to have the origin of the nullable index field.
     */
    public class Property_fieldConstructionTracking
    {
    }

    /**
     * Property objectTypeCacheSize
     * <p>
     * Specifies the size of the object type in-memory cache. Possible configuration values are:
     * <ul>
     * <li> 0 (zero): Don't use memory caching.
     * <li> &gt;0: Use memory caching with the cache size given.
     * </ul>
     * The default is a memory cache size of 10,000,000.
     */
    public class Property_objectTypeCacheSize
    {
    }

    /**
     * Property columnTypeModifier
     * <p>
     * Specifies the name of a {@link ColumnTypeModifier}.
     */
    public class Property_columnTypeModifier
    {
    }

    /**
     * Property typeMappingProvider
     * <p>
     * Specifies the type name of the {@link IFactory factory} for a custom type mapping provider. Possible values:
     * <ul>
     * <li> "registry" (default)
     * <li> the type name of a custom {@link IFactory factory}, which is contributed to the product group "org.eclipse.emf.cdo.server.db.typeMappingProviders".
     * </ul>
     * <p>
     * This property is optional.
     */
    public class Property_typeMappingProvider
    {
    }

    /**
     * Property forceIndexes
     * <p>
     * Specifies on what types of structural features additional indexes are to be created.
     * The value is either empty or a &#124; (pipe) separated list of the following tokens:
     * <ul>
     * <li> NONE (default)
     * <li> ALL (equal to ATTRIBUTE&#124;REFERENCE)
     * <li> ATTRIBUTE
     * <li> REFERENCE (equal to CONTAINER&#124;CONTAINMENT&#124;XREF)
     * <li> CONTAINER
     * <li> CONTAINMENT
     * <li> XREF
     * </ul>
     */
    public class Property_forceIndexes
    {
    }

    /**
     * Property eagerTableCreation
     * <p>
     * Specifies whether all tables for a {@link EPackage package} are created eagerly.
     * <p>
     * Possible configuration values are:
     * <ul>
     * <li> <code>false</code> (creates tables lazily, i.e., when they are actually needed for writing the first object; default value)
     * <li> <code>true</code> (creates tables eagerly, i.e., when a new {@link EPackage package} is committed)
     * </ul>
     */
    public class Property_eagerTableCreation
    {
    }

    /**
     * Property withRanges
     * <p>
     * Specifies whether new {@link CDORevision revisions} create entire new copies of all their list {@link EStructuralFeature features}
     * or whether just the list deltas are stored.
     * <p>
     * Possible configuration values are:
     * <ul>
     * <li> <code>false</code> (store new copies of all lists of a revision; default value)
     * <li> <code>true</code> (store only list deltas/ranges of a revision)
     * </ul>
     * <p>
     * This property is only applicable to horizontal mapping strategies in {@link Property_supportingAudits auditing}
     * or {@link Property_supportingBranches branching} repositories.
     */
    public class Property_withRanges
    {
    }

    /**
     * Property copyOnBranch
     * <p>
     * Specifies whether <b>the first</b> new {@link CDORevision revisions} in a {@link CDOBranch branch} create entire new copies
     * of all their list {@link EStructuralFeature features} or whether just the list deltas (relative to the base revisions in the parent branch) are stored.
     * <p>
     * Possible configuration values are:
     * <ul>
     * <li> <code>false</code> (store only list deltas/ranges of the first revision in a branch; default value)
     * <li> <code>true</code> (store new copies of all lists of the first revision in a branch)
     * </ul>
     * <p>
     * This property is only applicable to {@link Property_withRanges range-based} horizontal mapping strategies in
     * {@link Property_supportingBranches branching} repositories.
     */
    public class Property_copyOnBranch
    {
    }

    /**
     * Property forceZeroBasedIndex
     * <p>
     * Specifies whether element removals from the beginning of list {@link EStructuralFeature features} adjust the
     * list indexes of all following elements or whether the first element is allowed to have a non-zero list index.
     * <p>
     * Possible configuration values are:
     * <ul>
     * <li> <code>false</code> (allow non-zero list index for the first list elements; default value)
     * <li> <code>true</code> (force zero list indexes for the first list elements)
     * </ul>
     * <p>
     * This property is only applicable to {@link Property_withRanges range-based} horizontal mapping strategies in
     * {@link Property_supportingAudits auditing} or {@link Property_supportingBranches branching} repositories.
     */
    public class Property_forceZeroBasedIndex
    {
    }
  }

  /**
   * Element dbAdapter
   * <p>
   * Defines the {@link IDBAdapter} instance of the store that interprets the SQL dialect of the used database.
   * <p>
   * The <code>type</code> attribute corresponds to the name of a DB adapter factory that is contributed via the
   * <code>org.eclipse.net4j.db.dbAdapters</code> extension point. No additional attributes are recognized.
   * <p>
   * The DB adapter must match the database specified in the {@link Element_dataSource dataSource} element.
   */
  public class Element_dbAdapter
  {
  }

  /**
   * Element dataSource
   * <p>
   * Defines the {@link DataSource} instance of the store.
   * <p>
   * The <code>class</code> attribute corresponds to the fully qualified name of the data source class.
   * Please refer to your DB manual for details about the supported data sources and their attributes.
   */
  public class Element_dataSource
  {
  }
}
