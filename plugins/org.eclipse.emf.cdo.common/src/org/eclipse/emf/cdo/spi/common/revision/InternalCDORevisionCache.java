/*
 * Copyright (c) 2010-2013, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionInterner;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.List;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDORevisionCache extends CDORevisionCache, CDORevisionInterner, CDOAllRevisionsProvider, ILifecycle
{
  public InternalCDORevisionCache instantiate(CDORevision revision);

  public CDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion);

  /**
   * @since 4.15
   */
  public void removeRevisions(CDOBranch... branches);

  public void clear();

  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint);

  /**
   * Fills a list with all {@link CDORevision revisions}.
   *
   * @since 4.3
   * @see #getAllRevisions()
   */
  public void getAllRevisions(List<InternalCDORevision> result);
}
