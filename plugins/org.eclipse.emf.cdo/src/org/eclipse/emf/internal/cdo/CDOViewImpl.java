/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionResolver;

import java.text.MessageFormat;
import java.util.Date;

/**
 * @author Eike Stepper
 */
public class CDOViewImpl implements CDOView
{
  private CDOAdapterImpl adapter;

  private long timeStamp;

  public CDOViewImpl(CDOAdapterImpl adapter, long timeStamp)
  {
    this.adapter = adapter;
    this.timeStamp = timeStamp;
  }

  public CDOAdapterImpl getAdapter()
  {
    return adapter;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public boolean isActual()
  {
    return timeStamp == UNSPECIFIED_DATE;
  }

  public boolean isHistorical()
  {
    return !isActual();
  }

  public CDORevisionImpl resolve(CDOID id)
  {
    CDORevisionResolver revisionManager = adapter.getSession().getRevisionManager();
    if (isActual())
    {
      return (CDORevisionImpl)revisionManager.getActualRevision(id);
    }

    return (CDORevisionImpl)revisionManager.getHistoricalRevision(id, timeStamp);
  }

  @Override
  public String toString()
  {
    if (isActual())
    {
      return "ActualView";
    }

    return MessageFormat.format("HistoricalView({0})", new Date(timeStamp));
  }
}
