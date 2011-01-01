/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

/**
 * @author Andre Dietisheim
 */
public class DefaultRevisionCacheTest extends AbstractCDORevisionCacheTest
{
  @Override
  protected InternalCDORevisionCache createRevisionCache(CDOSession session) throws Exception
  {
    return (InternalCDORevisionCache)CDORevisionUtil.createRevisionCache();
  }
}
