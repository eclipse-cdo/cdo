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
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.doc.programmers.Doc04_PreparingModels;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

/**
 * Anatomy of a Model Object
 * <p>
 * EMF applications manipulate models as object graphs consisting of instances of {@link EObject EObjects}. The EMF framework and
 * various EMF-based technologies provide a rich set of features for working with these object graphs, such as change
 * notification, persistence, and validation. EObjects are the core building blocks of EMF models, and they provide a common
 * interface for working with models.
 * <p>
 * <img src="AnatomyEObject.png"/>
 * <p>
 * CDO extends the capabilities of EMF by providing a distributed shared model framework that enables collaborative editing
 * of EMF models in a distributed environment. CDO achieves this by introducing the concept of a CDO repository, which
 * is a central server that manages the storage and retrieval of EMF models. Clients connect to the repository to
 * access and manipulate the shared model. In a CDO client application, model objects are represented as instances of
 * {@link CDOObject}, which is a subclass of {@link EObject}. This article provides an overview of the anatomy of a CDOObject
 * and its key features.
 * <p>
 * A CDOObject is a specialized EObject that is designed to work with CDO's distributed shared model framework.
 * CDOObjects have several key features that distinguish them from regular EObjects. They are:
 * <ul>
 * <li><b>Persistent:</b> CDOObjects are persistent, meaning that they can be stored and retrieved from a CDO repository.
 * <li><b>Transactional:</b> CDOObjects support transactions, which allow multiple changes to be made to the model as a single atomic operation.
 * <li><b>Scalable:</b> CDOObjects are designed to be scalable, allowing large models to be managed efficiently in a distributed environment.
 * <li><b>Thread-safe:</b> CDOObjects are designed to be thread-safe, allowing multiple threads to access and modify the model concurrently.
 * <li><b>Identifiable:</b> CDOObjects have a unique identifier that is used to identify them in the repository.
 * <li><b>Versioned:</b> CDOObjects support versioning, which allows multiple versions of the same object to exist in the repository.
 * <li><b>Stateful:</b> CDOObjects maintain state information, such as whether they are new, dirty, or deleted.
 * </ul>
 * <p>
 * For these unique features to work, CDOObjects have a fundamentally different anatomy than regular EObjects. Especially,
 * to achieve scalability, CDOObjects do not contain their attributes and references directly. Instead, they delegate
 * the storage and retrieval of their data to a {@link CDORevision}.
 * <p>
 * <p>
 * <p>
 * <img src="AnatomyCDOObject.png"/>
 * <p>
 * In summary, a CDOObject is a specialized EObject that is designed to work with CDO's distributed shared model framework.
 * To turn a regular EMF model into a CDO model, the model must be migrated to use CDO and regenerated to produce CDOObject
 * subclasses. This is explained in the article {@link Doc04_PreparingModels}.
 * <p>
 * Note that CDOObjects and CDOViews are client-side concepts. On the server side, CDO uses only CDORevisions to
 * represent model objects. CDORevisions are also used as the unit of storage and retrieval in the CDO repository,
 * as well as the unit of data transfer between clients and the server.
 * <p>
 * The internet provides a wealth of information about EMF and CDO, including tutorials, documentation, and forums.
 * Here are some useful links:
 * <ul>
 * <li>The blog article <a href="https://thegordian.blogspot.com/2008/11/how-scalable-are-my-models.html">How Scalable are my Models?</a> provides
 * an introduction to the concepts of scalability in EMF and CDO.
 * <li>The blog article <a href="https://thegordian.blogspot.com/2011/07/concurrent-access-to-models.html">Concurrent Access to Models</a> provides
 * an introduction to the concepts of concurrency in EMF and CDO.
 * </ul>
 *
 *
 * @author Eike Stepper
 */
public class Doc02_AnatomyModelObject
{
  /**
   * CDOID
   * <p>
   * A {@link CDOID} is a unique identifier for a CDOObject within a CDO repository. It is used to identify and reference
   * CDOObjects in a distributed environment. CDOIDs are immutable and are assigned to CDOObjects when they are
   * created. CDOIDs can be of different types, such as long, string, or UUID, depending on the configuration of the
   * CDO repository.
   * <p>
   * The identifier of a CDOObject can be accessed using the {@link CDOObject#cdoID()} method.
   */
  public class DocID
  {
  }

  /**
   * CDORevision
   * <p>
   * A CDORevision is a lightweight representation of an object's state at a specific point in time. It contains the
   * attributes and references of the object, as well as metadata such as the object's identifier, branch and version.
   * <p>
   * CDORevisions are immutable and are created when an object is first loaded from the repository or when an object is
   * modified and committed to the repository. Each time an object is modified and committed, a new CDORevision is
   * created, allowing CDO to maintain a history of changes to the object over time.
   * <p>
   * A CDORevision references other model objects by their
   * CDOID, not by direct object references. This indirection allows CDO to manage large object graphs efficiently,
   * as it can load and unload objects from memory as needed. Also, it allows CDO to change the revision of an object
   * without affecting other objects that reference it. This is essential for supporting versioning and branching.
   * <p>
   * <img src="AnatomyRevision.png"/>
   * <p>
   * The revision of a CDOObject can be accessed using the {@link CDOObject#cdoRevision()} method.
   */
  public class DocRevision
  {
  }

  /**
   * CDOView
   * <p>
   * CDOObjects are managed by a {@link CDOView}, which provides a context for accessing and manipulating the model.
   * CDOViews are associated with a specific branch and, optionally, a specific point in time. They provide methods for
   * loading and unloading objects, as well as for managing the state of objects (e.g., marking them as dirty or
   * new).
   * <p>
   * The view of a CDOObject can be accessed using the {@link CDOObject#cdoView()} method.
   */
  public class DocView
  {
  }

  /**
   * CDOState
   * <p>
   * CDOObjects maintain state information that indicates their current status within the CDO framework. This state
   * is managed by a state machine that is part of the CDOView. The state machine tracks various states, such as:
   * <p>
   * <img src="StateMachine.png"/>
   * <p>
   *
   */
  public class DocState
  {
  }
}
