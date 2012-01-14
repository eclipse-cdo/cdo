/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

import java.util.Set;

/**
 * Represents the local state of a {@link CDOWorkspace workspace} directly after the initial
 * {@link CDOWorkspaceConfiguration#checkout() checkout} operation or any subsequent
 * {@link CDOWorkspace#update(org.eclipse.emf.cdo.transaction.CDOMerger) update} and
 * {@link CDOWorkspace#replace(String, long) replace} operations.
 * 
 * @author Eike Stepper
 */
public interface CDOWorkspaceBase extends CDORevisionProvider
{
  public CDOWorkspace getWorkspace();

  public Set<CDOID> getIDs();
}
