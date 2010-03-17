/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

public class OoCommitInfo extends ooObj
{
  protected int branchId;

  protected long timeStamp;

  protected String userId;

  protected String comment;

  /***
   * OoCommitInfo will be a readonly object.
   */
  public OoCommitInfo(int branchId, long timeStamp, String userId, String comment)
  {
    this.branchId = branchId;
    this.timeStamp = timeStamp;
    this.userId = userId;
    this.comment = comment;
  }

  public int getBranchId()
  {
    fetch();
    return branchId;
  }

  public long getTimeStamp()
  {
    fetch();
    return timeStamp;
  }

  public String getUserId()
  {
    fetch();
    return userId;
  }

  public String getComment()
  {
    fetch();
    return comment;
  }
}
