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
package org.eclipse.emf.cdo.releng.internal.version.proto;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Builder<DEPENDENCY> extends IncrementalProjectBuilder
{
  private List<DependencyProvider> dependencyProviders = new ArrayList<DependencyProvider>();

  public Builder()
  {
  }

  @Override
  protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException
  {
    BuildContext buildContext = new BuildContext(this, kind, args);

    initDependencyProviders(buildContext);
    boolean dependenciesMayHaveChanged = dependenciesMayHaveChanged();

    List<DEPENDENCY> dependencies;
    List<DependencyDelta> dependencyDeltas;
    if (dependenciesMayHaveChanged)
    {
      for (DependencyProvider dependencyProvider : dependencyProviders)
      {
        // List<DEPENDENCY> dependencies;
        // dependencyProvider.getDependencies();
      }
    }

    return null;
  }

  private void initDependencyProviders(BuildContext buildContext)
  {
    for (DependencyProvider dependencyProvider : dependencyProviders)
    {
      dependencyProvider.setBuildContext(buildContext);
    }
  }

  private boolean dependenciesMayHaveChanged()
  {
    for (DependencyProvider dependencyProvider : dependencyProviders)
    {
      if (dependencyProvider.dependenciesMayHaveChanged())
      {
        return true;
      }
    }

    return false;
  }
}
