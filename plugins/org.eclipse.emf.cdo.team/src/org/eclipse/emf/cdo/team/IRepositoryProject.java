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

import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Represents a {@link org.eclipse.core.resources.IProject project} that is mapped
 * to a CDO repository by the CDO {@link org.eclipse.team.core.RepositoryProvider team provider}.
 *
 * @author Eike Stepper
 */
public interface IRepositoryProject extends IAdaptable
{
  public IProject getProject();

  public CDOView getView();
}
