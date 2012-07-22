/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.IModel;

/**
 * @author Eike Stepper
 */
public abstract class VersionValidator
{
  public VersionValidator()
  {
  }

  public void abort(BuildState buildState, IProject project, Exception exception, IProgressMonitor monitor)
      throws Exception
  {
    buildState.setValidatorState(null);
  }

  public abstract void updateBuildState(BuildState buildState, String releasePath, Release release, IProject project,
      IResourceDelta delta, IModel componentModel, IProgressMonitor monitor) throws Exception;
}
