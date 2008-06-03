/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;

import org.eclipse.net4j.util.io.ExtendedDataInput;

import java.io.IOException;

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

  public static CDORevisionDelta create(CDORevision originRevision, CDORevision dirtyRevision)
  {
    return new CDORevisionDeltaImpl(originRevision, dirtyRevision);
  }

  public static CDORevisionDelta read(ExtendedDataInput in, CDOPackageManager packageManager) throws IOException
  {
    return new CDORevisionDeltaImpl(in, packageManager);
  }
}
