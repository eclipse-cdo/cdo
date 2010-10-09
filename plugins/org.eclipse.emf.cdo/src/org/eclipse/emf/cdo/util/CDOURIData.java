/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOURIData
{
  public static final String BRANCH_PARAMETER = "branch";

  public static final String TIME_PARAMETER = "time";

  public static final String TRANSACTIONAL_PARAMETER = "transactional";

  private String scheme;

  private String userName;

  private String passWord;

  private String authority;

  private String repositoryName;

  private IPath resourcePath;

  private IPath branchPath = new Path(CDOBranch.MAIN_BRANCH_NAME);

  private long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

  private boolean transactional;

  public CDOURIData()
  {
  }

  public CDOURIData(URI uri) throws InvalidURIException
  {
    try
    {
      scheme = uri.scheme();
      authority = uri.authority();
      String userInfo = uri.userInfo();
      if (userInfo != null)
      {
        authority = authority.substring(userInfo.length());
        int colon = userInfo.indexOf(':');
        if (colon != -1)
        {
          userName = userInfo.substring(0, colon);
          passWord = userInfo.substring(colon + 1);
        }
        else
        {
          userName = userInfo;
        }
      }

      IPath path = new Path(uri.path()).makeAbsolute();
      repositoryName = path.segment(0);
      resourcePath = path.removeFirstSegments(1);

      String query = uri.query();
      if (query != null)
      {
        Map<String, String> parameters = CDOURIUtil.getParameters(query);
        String branch = parameters.get(BRANCH_PARAMETER);
        if (branch != null)
        {
          branchPath = new Path(branch).makeAbsolute();
        }

        String time = parameters.get(TIME_PARAMETER);
        if (time != null)
        {
          if (!"HEAD".equalsIgnoreCase(time))
          {
            timeStamp = new SimpleDateFormat().parse(time).getTime();
          }
        }

        String transactional = parameters.get(TRANSACTIONAL_PARAMETER);
        if (transactional != null)
        {
          this.transactional = Boolean.parseBoolean(transactional);
        }
      }

      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE && transactional)
      {
        throw new IllegalArgumentException("Only HEAD can be transactional");
      }
    }
    catch (Throwable t)
    {
      throw new InvalidURIException(uri, t);
    }

    // branchPath = Path.EMPTY.makeAbsolute();
    // timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    //
    // while (resourcePath.segmentCount() != 0)
    // {
    // String segment = resourcePath.segment(0);
    // resourcePath = resourcePath.removeFirstSegments(1);
    //
    // if (segment.startsWith("@"))
    // {
    // if (segment.length() != 1)
    // {
    // if (!segment.equals("@HEAD"))
    // {
    // timeStamp = Long.parseLong(segment.substring(1));
    // }
    // }
    //
    // break;
    // }
    //
    // branchPath = branchPath.append(segment);
    // }
    //
    // int segments = branchPath.segmentCount();
    // if (segments == 0 || segments == 1 && !branchPath.segment(0).equals(CDOBranch.MAIN_BRANCH_NAME))
    // {
    // branchPath = new Path(CDOBranch.MAIN_BRANCH_NAME).append(branchPath);
    // }
  }

  public String getScheme()
  {
    return scheme;
  }

  public void setScheme(String scheme)
  {
    this.scheme = scheme;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getPassWord()
  {
    return passWord;
  }

  public void setPassWord(String passWord)
  {
    this.passWord = passWord;
  }

  public String getAuthority()
  {
    return authority;
  }

  public void setAuthority(String authority)
  {
    this.authority = authority;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public IPath getResourcePath()
  {
    return resourcePath;
  }

  public void setResourcePath(IPath resourcePath)
  {
    this.resourcePath = resourcePath;
  }

  public IPath getBranchPath()
  {
    return branchPath;
  }

  public void setBranchPath(IPath branchPath)
  {
    this.branchPath = branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public boolean isTransactional()
  {
    return transactional;
  }

  public void setTransactional(boolean transactional)
  {
    this.transactional = transactional;
  }

  public URI toURI()
  {
    return URI.createURI(toString());
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(scheme);
    builder.append("://");
    builder.append(authority);
    builder.append("/");
    builder.append(repositoryName);
    builder.append("/");
    builder.append(branchPath.toPortableString());
    builder.append("/@");
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      builder.append(timeStamp);
    }

    builder.append("/");
    builder.append(resourcePath);
    return builder.toString();
  }
}
