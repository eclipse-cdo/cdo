/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.team;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.core.resources.IProject;

/**
 * Manages {@link org.eclipse.core.resources.IProject projects} that are mapped
 * to CDO repositories by the CDO {@link org.eclipse.team.core.RepositoryProvider team provider}.
 *
 * @author Eike Stepper
 * @apiviz.uses {@link IRepositoryProject} - - manages
 */
public interface IRepositoryManager extends IContainer<IRepositoryProject>
{
  public static final IRepositoryManager INSTANCE = org.eclipse.emf.cdo.internal.team.RepositoryManager.INSTANCE;

  public IRepositoryProject getElement(IProject project);
}
