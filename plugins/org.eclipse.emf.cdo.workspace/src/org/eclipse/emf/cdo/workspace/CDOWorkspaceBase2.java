/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

  /**
   * @since 4.1
   */
  public boolean isAddedObject(CDOID id);
}
