/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444  
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

/**
 * Interface which complements {@link IClassMapping} with methods to facilitate 
 * audit support.
 * 
 * @see {@link IMappingStrategy#hasAuditSupport()
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * 
 * @since 2.0
 */
public interface IClassMappingAuditSupport
{

  /**
   * Read a specific version of a revision. If this method returns <code>true</code> it is guaranteed that
   * <code>revision.getVersion() == version</code>
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @param revision
   *          the revision object into which the data should be read. The revision has to be have its ID set to the
   *          requested object's ID. The version is ignored, as the version parameter is used to determine the version
   *          to be read.
   * @param version
   *          the version which should be read.
   * @param listChunk
   *          the chunk size to read attribute lists.
   * @return <code>true</code>, if the revision has been found and read correctly. <code>false</code> if the revision
   *         could not be found. In this case, the content of <code>revision</code> is undefined.
   */
  public boolean readRevisionByVersion(IDBStoreAccessor dbStoreAccessor, InternalCDORevision revision, int version,
      int listChunk);

  /**
   * Read a specific past version of a revision. If this method returns <code>true</code> it is guaranteed that
   * <code>revision.getCreated() <= timeStamp && (getRevised() == 0 || getRevised() >= timeStamp))</code>
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @param revision
   *          the revision object into which the data should be read. The revision has to be have its ID set to the
   *          requested object's ID. The version is ignored, as the version parameter is used to determine the version
   *          to be read.
   * @param timeStamp
   *          the timeStamp which should be used to determine the revision's version.
   * @param listChunk
   *          the chunk size to read attribute lists.
   * @return <code>true</code>, if the revision has been found and read correctly. <code>false</code> if the revision
   *         could not be found. In this case, the content of <code>revision</code> is undefined.
   */
  public boolean readRevisionByTime(IDBStoreAccessor dbStoreAccessor, InternalCDORevision revision, long timeStamp,
      int listChunk);

}
