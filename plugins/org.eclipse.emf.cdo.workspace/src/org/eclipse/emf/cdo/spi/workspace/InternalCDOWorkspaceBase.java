/*
 * Copyright (c) 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.workspace;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase2;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOWorkspaceBase extends CDOWorkspaceBase2
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.workspace.bases";

  @Override
  public InternalCDOWorkspace getWorkspace();

  public void init(InternalCDOWorkspace workspace);

  /**
   * @since 4.1
   */
  public void registerChangedOrDetachedObject(InternalCDORevision revision);

  /**
   * @since 4.2
   */
  public void registerAddedAndDetachedObject(InternalCDORevision revision);

  /**
   * @since 4.1
   */
  public void registerAddedObject(CDOID id);

  /**
   * @since 4.1
   */
  public void deregisterObject(CDOID id);

  @Deprecated
  public void updateAfterCommit(CDOTransaction transaction);

  /**
   * @since 4.2
   */
  public void deleteAddedAndDetachedObjects(IStoreAccessor.Raw accessor, CDOBranch branch);

  public void clear();
}
