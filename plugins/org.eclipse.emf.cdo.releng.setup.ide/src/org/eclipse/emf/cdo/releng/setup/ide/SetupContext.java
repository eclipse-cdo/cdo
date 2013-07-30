/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.GitClone;
import org.eclipse.emf.cdo.releng.setup.Setup;

import java.io.File;

/**
 * @author Eike Stepper
 */
public interface SetupContext
{
  public File getLogFile();

  public File getProjectDir();

  public File getBaselineDir();

  public File getBranchDir();

  public File getEclipseDir();

  public File getGitDir();

  public File getWorkDir(GitClone clone);

  public File getWorkspaceDir();

  public File getTargetPlatformDir();

  public Setup getSetup();
}
