/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Stefan Winkler - bug 249610: [DB] Support external references (Implementation)
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOIDExternal;

/**
 * @author Stefan Winkler
 * @since 3.0
 */
public interface IExternalReferenceManager
{
  public long mapExternalReference(IDBStoreAccessor accessor, CDOIDExternal id, long commitTime);

  public CDOIDExternal unmapExternalReference(IDBStoreAccessor accessor, long mappedId);

  /**
   * @since 4.0
   */
  public long mapURI(IDBStoreAccessor accessor, String uri, long commitTime);

  /**
   * @since 4.0
   */
  public String unmapURI(IDBStoreAccessor accessor, long mappedId);

  /**
   * @author Eike Stepper
   */
  public interface Internal extends IExternalReferenceManager
  {
    public IDBStore getStore();

    public void setStore(IDBStore store);
  }
}
