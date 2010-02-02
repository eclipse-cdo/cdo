/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision.cache;

import org.eclipse.emf.cdo.common.revision.CDORevision;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDORevisionCacheFactory
{
  public CDORevisionCache createRevisionCache(CDORevision revision);

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class PrototypeInstantiator implements CDORevisionCacheFactory
  {
    private CDORevisionCache prototype;

    public PrototypeInstantiator(CDORevisionCache prototype)
    {
      this.prototype = prototype;
    }

    public CDORevisionCache getPrototype()
    {
      return prototype;
    }

    public CDORevisionCache createRevisionCache(CDORevision revision)
    {
      if (revision.getBranch().isMainBranch())
      {
        return prototype;
      }

      return ((InternalCDORevisionCache)prototype).instantiate(revision);
    }
  }
}
