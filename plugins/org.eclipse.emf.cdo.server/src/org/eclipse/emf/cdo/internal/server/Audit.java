/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.server.IAudit;

import java.text.MessageFormat;

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
    this.timeStamp = timeStamp;
  }

  @Override
  public Type getViewType()
  {
    return CDOProtocolView.Type.AUDIT;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Audit[{0}, {1,date} {1,time}]", getViewID(), timeStamp);
  }
}
