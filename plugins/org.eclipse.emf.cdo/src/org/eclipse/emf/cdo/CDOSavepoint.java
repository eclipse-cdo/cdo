/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **************************************************************************/
package org.eclipse.emf.cdo;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOSavepoint
{
  public CDOTransaction getTransaction();

  public CDOSavepoint getNextSavepoint();

  public CDOSavepoint getPreviousSavepoint();

  public boolean isValid();

  public void rollback(boolean remote);
}
