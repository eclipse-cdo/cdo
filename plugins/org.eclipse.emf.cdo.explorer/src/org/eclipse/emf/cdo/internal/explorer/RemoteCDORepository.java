/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.explorer.CDORepositoryManager;

/**
 * @author Eike Stepper
 */
public class RemoteCDORepository extends CDORepositoryImpl
{
  private String connectorType;

  private String connectorDescription;

  public RemoteCDORepository(CDORepositoryManager repositoryManager, String label, String repositoryName,
      String connectorType, String connectorDescription)
  {
    super(repositoryManager, label, repositoryName);
    this.connectorType = connectorType;
    this.connectorDescription = connectorDescription;
  }

  public final String getConnectorType()
  {
    return connectorType;
  }

  public final String getConnectorDescription()
  {
    return connectorDescription;
  }
}
