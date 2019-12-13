/*
 * Copyright (c) 2009-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOUserSavepoint extends CDOUserSavepoint
{
  @Override
  public InternalCDOUserTransaction getTransaction();

  public InternalCDOUserSavepoint getFirstSavePoint();

  @Override
  public InternalCDOUserSavepoint getPreviousSavepoint();

  @Override
  public InternalCDOUserSavepoint getNextSavepoint();

  public void setPreviousSavepoint(InternalCDOUserSavepoint previousSavepoint);

  public void setNextSavepoint(InternalCDOUserSavepoint nextSavepoint);
}
