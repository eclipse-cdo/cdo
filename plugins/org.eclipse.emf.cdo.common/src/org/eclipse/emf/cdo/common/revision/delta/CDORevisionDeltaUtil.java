/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;

/**
 * @author Eike Stepper
 */
public final class CDORevisionDeltaUtil
{
  private CDORevisionDeltaUtil()
  {
  }

  public static CDORevisionDelta create(CDORevision revision)
  {
    return new CDORevisionDeltaImpl(revision);
  }

  /**
   * @since 2.0
   */
  public static CDORevisionDelta copy(CDORevisionDelta revisionDelta)
  {
    return new CDORevisionDeltaImpl(revisionDelta);
  }

  public static CDORevisionDelta create(CDORevision originRevision, CDORevision dirtyRevision)
  {
    return new CDORevisionDeltaImpl(originRevision, dirtyRevision);
  }
}
