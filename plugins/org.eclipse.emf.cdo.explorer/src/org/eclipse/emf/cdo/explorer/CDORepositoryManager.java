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
package org.eclipse.emf.cdo.explorer;

import org.eclipse.net4j.util.container.IContainer;

/**
 * Manages a set of {@link CDORepository repositories}.
 *
 * @author Eike Stepper
 * @since 4.4
 * @apiviz.landmark
 * @apiviz.composedOf {@link CDORepository}
 */
public interface CDORepositoryManager extends IContainer<CDORepository>
{
  public CDORepository[] getRepositories();

  public CDORepository addRemoteRepository(String label, String repositoryName, String connectorType,
      String connectorDescription);
}
