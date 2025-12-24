/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

/**
 * Adds {@link #isEmpty()} and {@link #containsID(CDOID)} methods.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public interface CDOWorkspaceBase2 extends CDOWorkspaceBase
{
  public boolean isEmpty();

  public boolean containsID(CDOID id);

  public boolean isAddedObject(CDOID id);
}
