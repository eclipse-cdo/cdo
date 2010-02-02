/**
builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("= ?  * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Interface which complements {@link IClassMappingAuditSupport} with methods to facilitate
 * branching support.
 *
 * @see {@link IMappingStrategy#hasAuditSupport()
 *
 * @author Eike Stepper
 * @author Stefan Winkler
 *
 * @since 3.0
 */
public interface IClassMappingBranchingSupport extends IClassMappingAuditSupport
{
  /**
   * Removes an object from the database. In the case of auditing support the object is just marked as revised, else it
   * it permanently deleted.
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @param id
   *          the ID of the object to remove.
   * @param branch
   *          the branch in which to work
   * @param revised
   *          the timeStamp when this object became detached.
   * @param monitor
   *          the monitor to indicate progress.
   */
  public void detachObject(IDBStoreAccessor dbStoreAccessor, CDOID id, long revised, CDOBranch branch, OMMonitor monitor);

  /**
   * Write a special placeholder revision which is used to indicate that a revision is detached in a branch.
   * 
   * @param accessor
   *          the accessor to use.
   * @param revision
   *          the old revision about to be detached
   * @param revised
   *          the timeStamp when this object became detached.
   * @param monitor
   *          the monitor to indicate progress.
   * @since 3.0
   */
  public void detachFirstVersion(IDBStoreAccessor accessor, CDORevision revision, long revised, OMMonitor monitor);

}
