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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalCDORevisionCache extends CDORevisionCache, ILifecycle
{
  public InternalCDORevisionCache instantiate(CDORevision revision);

  public boolean addRevision(CDORevision revision);

  public CDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion);

  public void clear();

  public Map<CDOBranch, List<CDORevision>> getAllRevisions();
}
