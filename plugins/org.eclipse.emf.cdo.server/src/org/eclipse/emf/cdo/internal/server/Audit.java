/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IAudit;
import org.eclipse.emf.cdo.server.IRepository;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Audit extends View implements IAudit
{
  private long timeStamp;

  public Audit(Session session, int viewID, long timeStamp)
  {
    super(session, viewID);
    IRepository repository = getSession().getSessionManager().getRepository();
    setTimeStamp(repository, timeStamp);
  }

  @Override
  public Type getViewType()
  {
    return CDOCommonView.Type.AUDIT;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  public List<CDORevision> setTimeStamp(long timeStamp, List<CDOID> invalidObjects)
  {
    checkOpen();
    IRepository repository = getSession().getSessionManager().getRepository();
    setTimeStamp(repository, timeStamp);
    return repository.getRevisionManager().getRevisionsByTime(invalidObjects, 0, timeStamp, false);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Audit[{0}, {1,date} {1,time}]", getViewID(), timeStamp);
  }

  private void setTimeStamp(IRepository repository, long timeStamp)
  {
    repository.validateTimeStamp(timeStamp);
    this.timeStamp = timeStamp;
  }

  private void checkOpen()
  {
    if (isClosed())
    {
      throw new IllegalStateException("View closed");
    }
  }
}
