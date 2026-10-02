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

import org.eclipse.emf.cdo.doc.programmers.client.Doc03_WorkingWithSessions;
import org.eclipse.emf.cdo.doc.programmers.client.Doc04_WorkingWithViews;
import org.eclipse.emf.cdo.doc.programmers.client.Doc05_WorkingWithTransactions;
import org.eclipse.emf.cdo.doc.programmers.server.Architecture;
import org.eclipse.emf.cdo.doc.programmers.server.Doc02_ServerApplicationAndStartup;

/**
 * Introduction
 * <p>
 * CDO is a repository and distributed shared-model framework for EMF. It stores model objects and
 * their relationships centrally so multiple clients can work with the same model while keeping
 * local object graphs in ordinary EMF {@code ResourceSet}s. A client session connects to a
 * repository; views read a repository state and transactions edit a branch head and publish a
 * change set as a commit. Repositories can also retain history and expose branches when their
 * storage configuration supports those capabilities.
 * <p>
 * This documentation is intended for developers who want to use CDO in their applications.
 * It assumes that you have a basic understanding of EMF, Net4j and Java development with Eclipse.
 * Here are some pointers to other documentation that may be helpful:
 * <ul>
 * <li>The <a href="https://help.eclipse.org/latest/topic/org.eclipse.pde.doc.user/guide/intro/pde_overview.htm">Plug-in Development Environment Guide</a> provides information about developing Eclipse plug-ins.
 * <li>The <a href="https://eclipse.dev/emf/docs.html">EMF Documentation</a> provides comprehensive information about EMF concepts and APIs.
 * <li>The {@link org.eclipse.net4j.doc.Overview Net4j Signaling Platform Documentation} explains the underlying communication framework used by CDO.
 * <li>The {@link org.eclipse.net4j.db.doc.Overview Net4j DB Framework Documentation} explains the database access framework used by CDO.
 * <li>The {@link org.eclipse.net4j.util.doc.Overview Net4j Utilities Documentation} explains various utility classes used by CDO.
 * </ul>
 * <p>
 * This guide separates three kinds of work. Client programming covers connecting, loading,
 * observing, and changing models. Server programming covers embedding or extending a repository
 * server. Workspace preparation and source installation cover development setup; operating a
 * deployed server, including its configuration and maintenance, belongs to the Operator's Guide.
 * <p>
 * The guide has two main programming parts:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.doc.programmers.client}: A guide for developing client applications that use CDO to store and manage EMF models in a distributed environment.
 * <li>{@link org.eclipse.emf.cdo.doc.programmers.server}: A guide for developing server applications that provide CDO repositories for client applications.
 * </ul>
 * <p>
 * Before diving into CDO programming, it is essential to understand some key concepts that form the foundation of CDO.
 * They are explained in {@link Doc02_KeyConcepts}.
 * <p>
 * Instructions for preparing your development environment are provided in {@link Doc03_PreparingWorkspace}.
 * This includes setting up your Eclipse workspace with the necessary dependencies.
 * <p>
 * Instructions for preparing your EMF models for use with CDO are provided in {@link Doc04_PreparingModels}.
 * This includes creating Ecore models and generating CDO-enabled code.
 * <p>
 * For a first client application, continue with the client {@link Doc03_WorkingWithSessions session} chapter,
 * then open a {@link Doc04_WorkingWithViews view} for reading or a transaction for changes. Follow
 * {@link Doc05_WorkingWithTransactions} for commit, rollback, and cleanup. The remaining client
 * chapters explain specific topics such as synchronization, locking, history, notifications, and
 * integration with other EMF-based frameworks.
 * <p>
 * For server development, start with {@link Architecture} to understand the components and their
 * ownership, then read {@link Doc02_ServerApplicationAndStartup} for the packaged server lifecycle.
 * Continue with managed containers, application extensions, and repository creation when building
 * an embedded or extended server. Use the Operator's Guide for deployment configuration.
 * <p>
 * At the end of this Programmer's Guide, you find the chapter {@link DocXX_InstallingSources} with
 * instructions for installing the CDO sources into your workspace. This is only necessary if you want to
 * work on CDO itself or want to analyze the commit history of CDO.
 *
 * @author Eike Stepper
 * @number 1
 */
public class Doc01_Introduction
{
}
