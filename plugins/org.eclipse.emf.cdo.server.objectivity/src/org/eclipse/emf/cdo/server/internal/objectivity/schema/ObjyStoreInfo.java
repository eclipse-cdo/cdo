/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
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

public class ObjyStoreInfo extends ooObj
{
  protected long creationTime;

  protected String comment;

  /***
   * ObjyStoreInfo is a read only object.
   */
  public ObjyStoreInfo(long creationTime, String comment)
  {
    this.creationTime = creationTime;
    this.comment = comment;
  }

  public long getCreationTime()
  {
    fetch();
    return creationTime;
  }

  public String getComment()
  {
    fetch();
    return comment;
  }
}
