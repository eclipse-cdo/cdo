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

import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.explorer.CDORepositoryManager;

import org.eclipse.net4j.util.container.SetContainer;

/**
 * @author Eike Stepper
 */
public class CDORepositoryManagerImpl extends SetContainer<CDORepository> implements CDORepositoryManager
{
  public CDORepositoryManagerImpl()
  {
    super(CDORepository.class);
  }

  public CDORepository[] getRepositories()
  {
    return getElements();
  }

  public CDORepository addRemoteRepository(String label, String repositoryName, String connectorType,
      String connectorDescription)
  {
    RemoteCDORepository repository = new RemoteCDORepository(this, label, repositoryName, connectorType,
        connectorDescription);
    addElement(repository);
    return repository;
  }
}
