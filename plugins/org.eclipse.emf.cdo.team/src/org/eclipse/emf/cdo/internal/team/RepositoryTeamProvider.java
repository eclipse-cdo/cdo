/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.team.core.RepositoryProvider;

/**
 * @author Eike Stepper
 */
public class RepositoryTeamProvider extends RepositoryProvider
{
  public static final String PROVIDER_ID = "org.eclipse.emf.cdo.team.TeamProvider";

  private static final QualifiedName CONNECTOR_DESCRIPTION_KEY = new QualifiedName(OM.BUNDLE_ID, "connectorDescription");

  private static final QualifiedName REPOSITORY_NAME_KEY = new QualifiedName(OM.BUNDLE_ID, "repositoryName");

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
  }

  public void deconfigure() throws CoreException
  {
  }

  public static String getConnectorDescription(IProject project)
  {
    try
    {
      return project.getPersistentProperty(CONNECTOR_DESCRIPTION_KEY);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void setConnectorDescription(IProject project, String value)
  {
    try
    {
      project.setPersistentProperty(CONNECTOR_DESCRIPTION_KEY, value);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static String getRepositoryName(IProject project)
  {
    try
    {
      return project.getPersistentProperty(REPOSITORY_NAME_KEY);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void setRepositoryName(IProject project, String value)
  {
    try
    {
      project.setPersistentProperty(REPOSITORY_NAME_KEY, value);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
