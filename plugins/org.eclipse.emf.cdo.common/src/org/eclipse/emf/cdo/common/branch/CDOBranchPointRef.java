/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

import org.eclipse.net4j.util.ObjectUtil;

import java.io.IOException;
import java.io.Serializable;

/**
 * References a {@link CDOBranchPoint}.
 *
 * @author Eike Stepper
 * @since 4.10
 */
public final class CDOBranchPointRef implements Serializable
{
  private static final long serialVersionUID = 1L;

  /**
   * @since 4.12
   */
  public static final CDOBranchPointRef MAIN_HEAD = CDOBranchRef.MAIN.getHeadRef();

  public static final String URI_SEPARATOR = "#"; //$NON-NLS-1$

  public static final String BASE = "BASE";

  public static final String HEAD = "HEAD";

  private final String branchPath;

  private final long timeStamp;

  public CDOBranchPointRef(CDOBranchPoint branchPoint)
  {
    this(branchPoint.getBranch().getPathName(), branchPoint.getTimeStamp());
  }

  /**
   * @since 4.12
   */
  public CDOBranchPointRef(CDOBranchRef branchRef, long timeStamp)
  {
    this(branchRef.getBranchPath(), timeStamp);
  }

  public CDOBranchPointRef(String branchPath, long timeStamp)
  {
    this.branchPath = CDOBranchUtil.sanitizePathName(branchPath).intern();
    this.timeStamp = timeStamp;
  }

  public CDOBranchPointRef(String uri)
  {
    if (uri == null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOClassifierRef.1") + uri); //$NON-NLS-1$
    }

    int fragmentSeparator = uri.lastIndexOf(URI_SEPARATOR);
    if (fragmentSeparator == -1)
    {
      branchPath = CDOBranchUtil.sanitizePathName(uri).intern();
      timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
      return;
    }

    branchPath = CDOBranchUtil.sanitizePathName(uri.substring(0, fragmentSeparator)).intern();
    String timeStampSpec = uri.substring(fragmentSeparator + 1);

    if (HEAD.equals(timeStampSpec))
    {
      timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    }
    else if (BASE.equals(timeStampSpec))
    {
      timeStamp = CDOBranchPoint.INVALID_DATE;
    }
    else
    {
      timeStamp = Long.valueOf(timeStampSpec);
    }
  }

  public CDOBranchPointRef(CDODataInput in) throws IOException
  {
    this(in.readCDOPackageURI(), in.readLong());
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOPackageURI(branchPath);
    out.writeLong(timeStamp);
  }

  public String getURI()
  {
    return branchPath + URI_SEPARATOR + getTimeStampSpec();
  }

  /**
   * @since 4.12
   */
  public CDOBranchRef getBranchRef()
  {
    return new CDOBranchRef(branchPath);
  }

  public String getBranchPath()
  {
    return branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public String getTimeStampSpec()
  {
    if (isHead())
    {
      return HEAD;
    }

    if (isBase())
    {
      return BASE;
    }

    return Long.toString(timeStamp);
  }

  public boolean isBase()
  {
    return timeStamp == CDOBranchPoint.INVALID_DATE;
  }

  public boolean isHead()
  {
    return timeStamp == CDOBranchPoint.UNSPECIFIED_DATE;
  }

  public CDOBranchPoint resolve(CDOBranchManager branchManager)
  {
    CDOBranch branch = branchManager.getBranch(branchPath);
    if (branch == null)
    {
      return null;
    }

    return branch.getPoint(timeStamp);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == CDOBranchPointRef.class)
    {
      CDOBranchPointRef that = (CDOBranchPointRef)obj;
      return ObjectUtil.equals(branchPath, that.branchPath) && timeStamp == that.timeStamp;
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return branchPath.hashCode() ^ Long.hashCode(timeStamp);
  }

  @Override
  public String toString()
  {
    return getURI();
  }

  /**
   * Provides {@link CDOBranchPointRef branch point references}.
   *
   * @author Eike Stepper
   */
  public interface Provider
  {
    public CDOBranchPointRef getBranchPointRef();
  }
}
