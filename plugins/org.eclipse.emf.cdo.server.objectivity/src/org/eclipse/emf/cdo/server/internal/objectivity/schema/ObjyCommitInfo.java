/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import com.objy.db.app.ooObj;

public class ObjyCommitInfo extends ooObj
{
  protected int branchId;

  protected long timeStamp;

  protected long previousTimeStamp;

  protected String userId;

  protected String comment;

  /***
   * OoCommitInfo will be a readonly object.
   */
  public ObjyCommitInfo(int branchId, long timeStamp, long previousTimeStamp, String userId, String comment)
  {
    this.branchId = branchId;
    this.timeStamp = timeStamp;
    this.previousTimeStamp = previousTimeStamp;
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

  public long getPreviousTimeStamp()
  {
    fetch();
    return previousTimeStamp;
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
