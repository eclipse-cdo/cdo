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
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class BuildContext
{
  private Builder<?> builder;

  private boolean fullBuild;

  public BuildContext(Builder<?> builder, int kind, Map<String, String> args)
  {
    this.builder = builder;
    fullBuild = kind == IncrementalProjectBuilder.FULL_BUILD || kind == IncrementalProjectBuilder.CLEAN_BUILD;
  }

  public IProject getProject()
  {
    return builder.getProject();
  }

  public IResourceDelta getDelta()
  {
    return builder.getDelta(getProject());
  }

  public boolean isFullBuild()
  {
    return fullBuild;
  }

  public void setFullBuild()
  {
    fullBuild = true;
  }
}
