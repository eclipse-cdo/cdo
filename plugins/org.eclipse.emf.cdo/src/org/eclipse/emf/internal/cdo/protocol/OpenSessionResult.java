/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class OpenSessionResult
{
  private int sessionID;

  private String repositoryUUID;

  private Set<String> packageURIs;

  public OpenSessionResult(int sessionID, String repositoryUUID, Set<String> packageURIs)
  {
    this.sessionID = sessionID;
    this.repositoryUUID = repositoryUUID;
    this.packageURIs = packageURIs;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public String getRepositoryUUID()
  {
    return repositoryUUID;
  }

  public Set<String> getPackageURIs()
  {
    return packageURIs;
  }
}
