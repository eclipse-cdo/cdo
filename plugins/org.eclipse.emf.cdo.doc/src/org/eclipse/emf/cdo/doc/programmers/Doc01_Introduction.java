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
 * Introduction
 * <p>
 * CDO is a model repository and distributed shared model framework for EMF models. It enables
 * collaborative editing of EMF models in a distributed environment, providing features such as
 * transactions, versioning, and branching.
 * <p>
 * This documentation is intended for developers who want to use CDO in their applications.
 * It assumes that you have a basic understanding of EMF, Net4j and Java development with Eclipse.
 * Here are some pointers to other documentation that may be helpful:
 * <ul>
 * <li>The <a href="https://help.eclipse.org/latest/topic/org.eclipse.pde.doc.user/guide/intro/pde_overview.htm">Plug-in Development Environment Guide</a> provides information about developing Eclipse plug-ins.
 * <li>The <a href="https://eclipse.dev/emf/docs.html">EMF Documentation</a> provides comprehensive information about EMF concepts and APIs.
 * <li>The {@link org.eclipse.net4j.doc.Overview Net4j Signalling Platform Documentation} explains the underlying communication framework used by CDO.
 * <li>The {@link org.eclipse.net4j.db.doc.Overview Net4j DB Framework Documentation} explains the database access framework used by CDO.
 * <li>The {@link org.eclipse.net4j.util.doc.Overview Net4j Utilities Documentation} explains various utility classes used by CDO.
 * </ul>
 * <p>
 * This documentation about CDO programming is split into two main parts:
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
