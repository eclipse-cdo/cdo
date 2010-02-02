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
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersionAndBranch;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CDOIDAndVersionAndBranchImpl extends CDOIDAndVersionImpl implements CDOIDAndVersionAndBranch
{
  private int branchID;

  public CDOIDAndVersionAndBranchImpl(CDOID id, int version, int branchID)
  {
    super(id, version);
    this.branchID = branchID;
  }

  public CDOIDAndVersionAndBranchImpl(CDODataInput in) throws IOException
  {
    super(in);
    branchID = in.readInt();
  }

  @Override
  public void write(CDODataOutput out) throws IOException
  {
    super.write(out);
    out.writeInt(branchID);
  }

  public int getBranchID()
  {
    return branchID;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOIDAndVersionAndBranch)
    {
      CDOIDAndVersionAndBranch that = (CDOIDAndVersionAndBranch)obj;
      return getID().equals(that.getID()) && getVersion() == that.getVersion() && branchID == that.getBranchID();
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return getID().hashCode() ^ getVersion() ^ branchID;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}v{1}b{2}", getID(), getVersion(), branchID); //$NON-NLS-1$
  }
}
