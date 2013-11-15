/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.cdo.releng.setup.util.OS;
import org.eclipse.emf.cdo.releng.setup.util.StringExpander;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;

import org.eclipse.emf.common.util.URI;

import java.io.File;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface SetupTaskContext extends Map<Object, Object>, ProgressLog, StringExpander
{
  public Trigger getTrigger();

  public boolean isRestartNeeded();

  public void setRestartNeeded(String reason);

  public OS getOS();

  public String getP2ProfileName();

  public File getP2ProfileDir();

  public File getP2AgentDir();

  public File getP2PoolDir();

  public File getInstallDir();

  public File getProjectDir();

  public File getBranchDir();

  public File getEclipseDir();

  public File getTargetPlatformDir();

  public File getWorkspaceDir();

  public Setup getSetup();

  public void redirect(URI sourceURI, URI targetURI);

  public URI redirect(URI uri);
}
