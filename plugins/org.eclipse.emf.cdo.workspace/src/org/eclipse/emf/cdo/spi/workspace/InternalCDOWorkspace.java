/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.workspace;

import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public interface InternalCDOWorkspace extends CDOWorkspace
{
  public InternalCDOWorkspaceBase getBase();

  public InternalRepository getLocalRepository();

  public InternalCDOSession getLocalSession();
}
