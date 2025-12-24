/*
 * Copyright (c) 2010-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchVersionImpl;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDORevisionKeyImpl extends CDOBranchVersionImpl implements CDORevisionKey
{
  private CDOID id;

  public CDORevisionKeyImpl(CDOID id, CDOBranch branch, int version)
  {
    super(branch, version);
    this.id = id;
  }

  @Override
  public CDOID getID()
  {
    return id;
  }

  @Override
  public int hashCode()
  {
    return id.hashCode() ^ super.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORevisionKey)
    {
      CDORevisionKey that = (CDORevisionKey)obj;
      return id == that.getID() && getVersion() == that.getVersion() && getBranch() == that.getBranch();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}:{1}v{2}", id, getBranch().getID(), getVersion());
  }
}
