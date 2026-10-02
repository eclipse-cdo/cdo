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
package org.eclipse.emf.cdo.doc.programmers;

import org.eclipse.emf.cdo.doc.programmers.client.Doc04_WorkingWithViews;
import org.eclipse.emf.cdo.doc.programmers.client.Doc08_BranchingAndVersioning;
import org.eclipse.emf.cdo.doc.programmers.client.Doc09_NotificationsAndEventHandling;

/**
 * Understanding the Key Concepts
 * <p>
 * CDO keeps EMF's object graph programming model while adding repository identity, concurrent
 * access, and (when configured) history. The distinctions below are useful when deciding what a
 * client is looking at and what a commit will change. Detailed client behavior is covered in the
 * {@link org.eclipse.emf.cdo.doc.programmers.client} guide; server-side ownership and capabilities
 * are covered in the {@link org.eclipse.emf.cdo.doc.programmers.server} guide.
 *
 * @author Eike Stepper
 * @number 2
 */
public class Doc02_KeyConcepts
{
  /**
   * Models
   * <p>
   * An EMF model is the runtime object graph that represents application data: instances connected
   * through attributes and references. The graph conforms to a metamodel, which describes the
   * available classes and features. CDO stores the graph's objects in repository resources; it does
   * not replace the application's domain model with a separate data model. A client loads objects
   * into an EMF {@code ResourceSet} and works with familiar EMF APIs, while CDO tracks their
   * repository identity and changes. Model preparation and generated-model choices are described in
   * {@link Doc04_PreparingModels}.
   */
  public class ConceptModels
  {
  }

  /**
   * Meta Models
   * <p>
   * A metamodel defines the types and features used by model instances. In EMF this is commonly an
   * Ecore model containing {@code EPackage}, {@code EClass}, {@code EAttribute}, and
   * {@code EReference} declarations. The metamodel determines how CDO serializes and interprets
   * object data, so clients that access the same repository data must have compatible package
   * definitions registered. A metamodel is distinct from the repository model data: registering a
   * package does not create instances of its classes.
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
   * {@code EObject} is the general EMF contract for model instances. In native CDO mode, an object
   * also implements {@code CDOObject}, which exposes CDO-specific information such as its
   * {@code CDOID}, current view, state, and revision. Legacy model mode can adapt ordinary generated
   * EMF objects to CDO, so application code should use {@code CDOUtil} when it needs to cross
   * between these contracts rather than assuming every generated Java object directly implements
   * both. A CDOID identifies the logical object across revisions; it is not a revision number and
   * does not by itself identify a historical state.
   */
  public class ConceptModelObjects
  {
  }

  /**
   * Resources
   * <p>
   * In EMF, a resource contains model objects and gives a resource set a URI-based way to find
   * them. A conventional resource may be backed by a file; a CDO resource is backed by a repository
   * and is read or changed through a view or transaction.
   * <p>
   * CDO resources form a repository namespace. {@code CDOResource} contains model contents;
   * {@code CDOResourceFolder} groups resource nodes, and binary and text resource nodes hold
   * content outside ordinary model features. Resource nodes are themselves repository objects, so
   * their names, paths, and other model features participate in repository changes. Concurrent
   * clients see committed changes according to their view/update configuration; an uncommitted
   * transaction change is local to that transaction.
   * <p>
   * CDOResourceFolders can also contain CDOBinaryResources
   * and CDOTextResources to store binary large objects (LOBs) and text files, respectively.
   * <p>
   * All these resource types are subtypes of CDOResourceNode.
   * CDOResourceNodes are regular CDOObjects and are, therefore, part of the versioned and shared model themselves.
   */
  public class ConceptResources
  {
  }

  /**
   * Branches
   * <p>
   * A branch is a line of development with its own sequence of committed states. A branch has an
   * identity and may inherit its initial state from a parent branch. Its name is a human-readable
   * lookup key, not its identity. Branching requires a repository/store configuration that supports
   * it. The detailed branch and history operations are
   * described in {@link Doc08_BranchingAndVersioning}.
   */
  public class ConceptBranches
  {
  }

  /**
   * Branch Points
   * <p>
   * A branch point is a coordinate: a branch together with a time on that branch. It identifies a
   * model state, while a branch alone identifies the history in which states occur. A child
   * branch's {@code BASE} is the fixed point from which it was created; {@code HEAD} means the
   * latest state and moves as commits arrive. A fixed timestamp or commit point remains stable.
   * Branch points are therefore suitable for opening historical views and comparing states; they
   * are not interchangeable with an object version number.
   */
  public class ConceptBranchPoints
  {
  }

  /**
   * Commits
   * <p>
   * A commit publishes a transaction's accepted changes to one branch. Its commit timestamp
   * identifies the committed change set and also defines a branch point on that branch. A branch
   * point can instead denote a boundary such as a branch base, so not every branch point represents
   * a commit. A commit timestamp is not the version of any particular object: one commit can create
   * revisions for many objects, and an object may not change in a given commit. See
   * {@link org.eclipse.emf.cdo.doc.programmers.client.Doc05_WorkingWithTransactions} for client
   * commit behavior.
   */
  public class ConceptCommits
  {
  }

  /**
   * Revisions
   * <p>
   * A revision is the repository representation of one object's data at a point in its history.
   * Object identity (the CDOID) remains stable while successive commits can create new revisions.
   * Revision versions are scoped to an object and branch; version 3 is not a globally unique
   * historical coordinate. A revision's time associates it with the commit that created it, while
   * the branch and time together locate the complete model state. Committed revisions are
   * historical records; a transaction may also have a mutable local revision representing its
   * uncommitted data. CDO transfers revision data, not the application's Java object instance.
   */
  public class ConceptRevisions
  {
  }

  /**
   * Revision Deltas
   * <p>
   * A revision delta describes changes relative to an object's base revision, including changed
   * features and detached objects as appropriate. It lets CDO communicate transaction changes
   * without treating the in-memory {@code EObject} as the repository record. A delta is meaningful
   * in the context of its object identity and base state; it is not a complete standalone snapshot.
   */
  public class ConceptRevisionDeltas
  {
  }

  /**
   * Repositories
   * <p>
   * A repository is the server-side boundary for model data, branches, commits, and repository
   * services. Objects are stored as revisions. Whether old revisions can be queried and whether
   * branches can be created depends on store and repository capabilities such as auditing and
   * branching; applications should check the configured repository rather than assume every
   * deployment supports every history operation. Package metadata is registered and managed
   * separately from ordinary versioned model instances. Server architecture and these capabilities
   * are described in {@link org.eclipse.emf.cdo.doc.programmers.server.Architecture}.
   */
  public class ConceptRepositories
  {
  }

  /**
   * Sessions
   * <p>
   * A session is the client connection and identity context for repository services. It owns the
   * views and transactions opened through it and provides access to managers such as branch and
   * package registries. A session is not itself a model snapshot: open one or more views to access
   * model objects. Closing the session closes its open views and releases connection resources.
   */
  public class ConceptSessions
  {
  }

  /**
   * Views
   * <p>
   * A view gives access to model objects at a repository coordinate. A read-only view observes a
   * state without publishing changes; a transaction is also a view but overlays local edits on its
   * base state. A session can own several views at different branches or times. Each view manages
   * its own resource set and loaded object instances, so an object obtained from one view should
   * not be treated as an object owned by another view. Concurrent access to the same view has a
   * defined synchronization contract: individual accesses and multi-step critical sections differ;
   * see {@link Doc04_WorkingWithViews}.
   */
  public class ConceptViews
  {
  }

  /**
   * Transactions
   * <p>
   * A transaction is a writable view based on a branch head. Its edits, new objects, and deletions
   * are local until commit succeeds; other clients continue to see committed repository state until
   * publication. A commit creates a repository change set and advances the branch head. A failed
   * commit does not mean the application operation should be blindly replayed: the transaction may
   * need conflict resolution or rollback. Savepoints and scopes organize local transaction state
   * but do not themselves create repository commits.
   */
  public class ConceptTransactions
  {
  }

  /**
   * Change Notifications
   * <p>
   * Passive updates let a live view learn that another client has committed changes relevant to
   * the view's branch. Depending on the session update mode and subscriptions, the client may
   * receive invalidation information or more detailed change notifications; this does not turn the
   * remote commit into a local transaction edit. Applications that cache derived state should use
   * the appropriate event/update mechanism and refresh when required. See
   * {@link Doc09_NotificationsAndEventHandling} for the notification contract.
   */
  public class ConceptChangeNotifications
  {
  }
}
