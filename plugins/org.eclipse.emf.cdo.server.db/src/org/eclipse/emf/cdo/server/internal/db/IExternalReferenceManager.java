/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Eike Stepper - maintenance
 *    Victor Roldan - 289237: [DB] [maintenance] Support external references
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * @author Stefan Winkler
 */
public interface IExternalReferenceManager
{
  public long mapExternalReference(IDBStoreAccessor accessor, CDOIDExternal id);

  public CDOIDExternal unmapExternalReference(IDBStoreAccessor accessor, long mappedId);

  /**
   * @author Eike Stepper
   */
  public interface Internal extends IExternalReferenceManager
  {
    public IDBStore getStore();

    public void setStore(IDBStore store);
  }
}
