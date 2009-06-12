/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - further implementations
 */
package org.eclipse.emf.cdo.common.db;

import org.eclipse.emf.cdo.common.internal.db.cache.DBRevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOCommonDBUtil
{
  private CDOCommonDBUtil()
  {
  }

  /**
   * Creates and returns a new JDBC-based revision cache.
   * <p>
   * TODO Add all config parameters!
   */
  public static CDORevisionCache createDBCache()
  {
    DBRevisionCache cache = new DBRevisionCache();
    return cache;
  }
}
