/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.revision;

import org.eclipse.emf.cdo.CDORevisionManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.emf.internal.cdo.CDORevisionManagerImpl;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CDOReferenceProxyImpl implements CDOReferenceProxy
{
  private int index;

  public CDOReferenceProxyImpl(int index)
  {
    this.index = index;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public CDOID resolve(CDORevisionManager revisionManager, CDORevision revision, CDOFeature feature, int index)
  {
    return ((CDORevisionManagerImpl)revisionManager).resolveReferenceProxy(revision, feature, index, getIndex());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOReferenceProxy[{0}]", index);
  }
}
