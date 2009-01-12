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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.team.core.RepositoryProvider;

/**
 * @author Eike Stepper
 */
public class RepositoryTeamProvider extends RepositoryProvider
{
  public static final String PROVIDER_ID = "org.eclipse.emf.cdo.team.TeamProvider";

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
}
