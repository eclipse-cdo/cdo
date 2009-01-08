/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;

/**
 * @author Eike Stepper
 */
public interface IObjectTypeCache
{
  /**
   * @since 2.0
   */
  public CDOClassRef getObjectType(IDBStoreAccessor accessor, CDOID id);

  /**
   * @since 2.0
   */
  public void putObjectType(IDBStoreAccessor accessor, CDOID id, CDOClass type);

  /**
   * @since 2.0
   */
  public void removeObjectType(IDBStoreAccessor accessor, CDOID id);
}
