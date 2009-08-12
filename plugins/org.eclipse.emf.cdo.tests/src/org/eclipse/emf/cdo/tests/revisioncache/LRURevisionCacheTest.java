/**
 * Copyright (c) 2004 - 2009 Andre Dietisheim (Bern, Switzerland) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheUtil;
import org.eclipse.emf.cdo.session.CDOSession;

/**
 * @author Andre Dietisheim
 */
public class LRURevisionCacheTest extends AbstractCDORevisionCacheTest
{
  public static final int DEFAULT_CAPACITY_CURRENT = 1000;

  public static final int DEFAULT_CAPACITY_REVISED = 1000;

  @Override
  protected CDORevisionCache createRevisionCache(CDOSession session) throws Exception
  {
    return CDORevisionCacheUtil.createLRUCache(DEFAULT_CAPACITY_CURRENT, DEFAULT_CAPACITY_REVISED);
  }
}
