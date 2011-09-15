/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.team;

import org.eclipse.emf.cdo.internal.team.bundle.OM;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;

/**
 * @author Eike Stepper
 */
public class RepositoryTeamProvider extends RepositoryProvider
{
  public static final String PROVIDER_ID = "org.eclipse.emf.cdo.team.TeamProvider"; //$NON-NLS-1$

  private static final QualifiedName SESSION_DESCRIPTION_KEY = new QualifiedName(OM.BUNDLE_ID, "sessionDescription"); //$NON-NLS-1$

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, RepositoryTeamProvider.class);

  public RepositoryTeamProvider()
  {
  }

  @Override
  public String getID()
  {
    return PROVIDER_ID;
  }

  @Override
  public void configureProject() throws CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Configuring project {0}", getProject()); //$NON-NLS-1$
    }
  }

  public void deconfigure() throws CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deconfiguring project {0}", getProject()); //$NON-NLS-1$
    }
  }

  public static String getSessionDescription(IProject project)
  {
    try
    {
      return project.getPersistentProperty(SESSION_DESCRIPTION_KEY);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void setSessionDescription(IProject project, String value)
  {
    try
    {
      project.setPersistentProperty(SESSION_DESCRIPTION_KEY, value);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static boolean isMapped(IProject project)
  {
    return RepositoryProvider.getProvider(project, PROVIDER_ID) != null;
  }

  public static void mapProject(IProject project, String sessionDescription) throws TeamException
  {
    map(project, PROVIDER_ID);
    setSessionDescription(project, sessionDescription);
  }

  public static void unmapProject(IProject project) throws TeamException
  {
    unmap(project);
    setSessionDescription(project, null);
  }
}
