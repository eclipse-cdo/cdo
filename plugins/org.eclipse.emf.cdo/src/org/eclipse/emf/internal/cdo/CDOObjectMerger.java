/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

/**
 * @author Simon McDuff
 */
public class CDOObjectMerger extends CDORevisionMerger
{
  public CDOObjectMerger()
  {
  }

  public void merge(InternalCDOObject object, CDORevisionDelta delta)
  {
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.copy(object.cdoRevision());
    revision.setTransactional();
    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetState(CDOState.DIRTY);
    merge(revision, delta);
  }
}
