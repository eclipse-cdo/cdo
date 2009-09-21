/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 *    
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.db.IDBStore;

/**
 * @author Victor Roldan Betancort
 */
public interface InternalIDBStore extends IDBStore
{
  /**
   * Get the manager for external references.
   * 
   * @author Eike Stepper
   */
  public IExternalReferenceManager getExternalReferenceManager();
}
