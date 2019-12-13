/*
 * Copyright (c) 2009-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;

import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOSavepoint extends CDOSavepoint, InternalCDOUserSavepoint
{
  @Override
  public InternalCDOTransaction getTransaction();

  @Override
  public InternalCDOSavepoint getFirstSavePoint();

  @Override
  public InternalCDOSavepoint getPreviousSavepoint();

  @Override
  public InternalCDOSavepoint getNextSavepoint();

  public void clear();

  @Deprecated
  public Set<CDOID> getSharedDetachedObjects();

  @Deprecated
  public void recalculateSharedDetachedObjects();

  /**
   * @since 4.1
   */
  public boolean isNewObject(CDOID id);

  /**
   * @since 4.6
   */
  public CDOObject getDirtyObject(CDOID id);

  /**
   * @since 4.6
   */
  public CDOObject getDetachedObject(CDOID id);
}
