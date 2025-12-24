/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
