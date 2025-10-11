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
package org.eclipse.emf.cdo.doc.programmers;

/**
 * Key Concepts of CDO
 * <p>
 * Before diving into CDO programming, it is essential to understand some key concepts that form the foundation of CDO.
 * They are explained in the following sections:
 *
 * @author Eike Stepper
 * @number 2
 */
public class Doc02_KeyConcepts
{
  /**
   * Models
   * <p>
   * CDO is designed to work with EMF models. An EMF model is a structured representation of data that conforms to a specific
   * metamodel. A model consists of a graph of interconnected model objects (see below). Models can be large and complex.
   * CDO allows you to store and manage these models in a distributed environment.
   */
  public class ConceptModels
  {
  }

  /**
   * Meta Models
   * <p>
   * A metamodel defines the structure and constraints of a model. In EMF, metamodels are typically defined using Ecore, which is a
   * modeling language for defining models. CDO works with any EMF metamodel, allowing you to store and manage models based on different metamodels.
   * In an Ecore metamodel, you define EPackages, EClasses, EAttributes, and EReferences to represent the concepts and relationships in your domain.
   */
  public class ConceptMetaModels
  {
  }

  /**
   * Model Objects
   * <p>
   * In EMF, model objects are instances of EObject, which is the base class for all EMF model elements. Each EObject
   * corresponds to an instance of an EClass defined in the metamodel and, hence, has a set of
   * structural features (attributes and references) that define its properties and relationships with other model objects.
   * In CDO, model objects are instances of CDOObject, which extends EObject and adds additional functionality for working in a distributed environment,
   * most notably a technical unique identifier, the CDOID.
   */
  public class ConceptModelObjects
  {
  }

  /**
   * Resources
   * <p>
   * In EMF, a resource is a container for model objects. It provides a way to group related model objects together and manage their
   * persistence. Resources are typically stored in files, such as XMI or XML files.
   * <p>
   * CDO extends the concept of resources by providing CDOResource,
   * which is a specialized resource that is designed to work with CDO's distributed shared model framework. CDOResources are stored in a CDO repository
   * and can be accessed and manipulated by multiple clients concurrently. CDOResources can be organized in a hierarchical structure using CDOResourceFolder.
   * This allows you to create a tree-like structure of resources, similar to a file system.
   * <p>
   * CDOResourceFolders can also contain CDOBinaryResources
   * and CDOTextResources to store binary large objects (LOBs) and text files, respectively.
   * <p>
   * All these resource types are subtypes of CDOResourceNode.
   * CDOResourceNodes are regular CDOObjects and can, therefore, be part of the versioned and shared model themselves.
   */
  public class ConceptResources
  {
  }

  /**
   * Branches
   * <p>
   * CDO supports branching, which allows you to create multiple versions of a model. Each branch represents a separate line of development,
   * allowing you to work on different features or bug fixes without affecting the mainline version of the model.
   */
  public class ConceptBranches
  {
  }

  /**
   * Branch Points
   * <p>
   * A branch point is a specific point in time on a branch. It represents a snapshot of the model at that point in time.
   * Branch points are used to manage versions of the model and to perform operations such as merging and comparing different versions.
   * In each branch there are two special branch points: <i>BASE</i> and <i>HEAD</i>. BASE represents the fixed point where the branch was created,
   * HEAD represents the latest point in time on the branch, a floating point that moves forward on the branch as time progresses.
   */
  public class ConceptBranchPoints
  {
  }

  /**
   * Commits
   * <p>
   * A commit represents a set of changes made to a model. Each commit occurs at a specific time and
   * is associated with a specific branch. A commit is a branch point, but not every branch point is a commit.
   * Although CDO processes commits in parallel, it ensures that each commit is associated with a unique timestamp.
   * This timestamp is the Commit ID.
   */
  public class ConceptCommits
  {
  }

  /**
   * Revisions
   * <p>
   * A revision represents a specific version of a specific model object. Each time a model object is modified and committed,
   * a new revision is created. Revisions are identified by a combination of the object's CDOID, the branch, and the version number. The version number
   * of a revision is specific to and unique within the revision's branch. In addition, each revision has a creation time, which is the
   * timestamp of the commit that created it. Unless the revision is the latest one of the object on the branch, it also has a revised time,
   * which is the creation time (minus one millisecond) of the next revision of the same object on the same branch. Revisions are immutable,
   * except for when they represent the dirty state of an object within a client transaction. Revisions are the data transfer objects that CDO uses
   * to send model object data between clients and the server.
   */
  public class ConceptRevisions
  {
  }

  /**
   * Revision Deltas
   * <p>
   * A revision delta represents a set of changes made to a specific revision of a model object.
   * It is used to efficiently transmit only the changes made to a model object, rather than the entire object. It consists of the CDOID of the object,
   * the version number of the revision being modified, and a set of changes to the object's attributes and references, the so-called feature deltas.
   */
  public class ConceptRevisionDeltas
  {
  }

  /**
   * Repositories
   * <p>
   * A CDO repository is a central storage location for EMF models and metamodels. It provides a way to store, version,
   * retrieve, and manage models in a distributed environment. A repository can contain multiple models and metamodels. The models
   * are stored in the form of revisions, which are created each time a model object is modified and committed. Whether historical revisions
   * are retained or not depends on the repository's configuration. Also, whether branching is supported or not depends on the repository's configuration.
   * Although metamodels are models themselves, they can not be versioned or branched. Instead, they are managed separately from regular models.
   * A repository is hosted by a CDO server and accessed by CDO clients.
   */
  public class ConceptRepositories
  {
  }

  /**
   * Sessions
   * <p>
   * A CDO session represents a connection to a CDO repository. It is used to perform basic operations on the
   * repository, such as loading metamodels, branches, and revisions. A session can be associated with a specific user,
   * allowing for authentication and authorization. It is the main entry point for interacting with a CDO repository.
   * It does not provide access to model objects. For that, you need to open a view or transaction (see below).
   */
  public class ConceptSessions
  {
  }

  /**
   * Views
   * <p>
   * A CDO view provides read-only access to a consistent graph of model objects in a repository.
   * It represents the model state at a specific branch point in the repository, allowing you to navigate and query the model objects
   * without modifying them. Views are opened from a session and are associated with an EMF ResourceSet, which is used to manage the
   * loaded model objects. Multiple views can be opened from a single session, each, for example, representing a different branch point.
   * It is an important aspect of CDO that views, together with model versions they provide, are thread-safe. This means that multiple threads can
   * use the same view concurrently without causing data corruption or inconsistencies.
   */
  public class ConceptViews
  {
  }

  /**
   * Transactions
   * <p>
   * A CDO transaction is a special type of view that provides read-write access to a consistent graph of model objects in a repository.
   * It allows you to modify model objects and commit the changes back to the repository. Like views, transactions are opened from a session
   * and are associated with an EMF ResourceSet. A transaction is always associated with the HEAD of a specific branch in the repository.
   */
  public class ConceptTransactions
  {
  }

  /**
   * Change Notifications
   * <p>
   * All views that target the HEAD of a branch (which includes, by definition, all transactions) receive change notifications
   * about changes that other clients commit to the same branch. These notifications are used to keep the model objects in the view up to date
   * and to notify listeners (and EMF Adapters) about changes.
   */
  public class ConceptChangeNotifications
  {
  }
}
