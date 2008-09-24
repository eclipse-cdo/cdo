/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class RepositoryTimeResult
{
  private long requested;

  private long indicated;

  private long responded;

  private long confirmed;

  public RepositoryTimeResult()
  {
  }

  public long getRequested()
  {
    return requested;
  }

  public void setRequested(long requested)
  {
    this.requested = requested;
  }

  public long getIndicated()
  {
    return indicated;
  }

  public void setIndicated(long indicated)
  {
    this.indicated = indicated;
  }

  public long getResponded()
  {
    return responded;
  }

  public void setResponded(long responded)
  {
    this.responded = responded;
  }

  public long getConfirmed()
  {
    return confirmed;
  }

  public void setConfirmed(long confirmed)
  {
    this.confirmed = confirmed;
  }

  public long getAproximateRepositoryOffset()
  {
    long latency = confirmed - requested >> 1;
    long shift = confirmed - responded;
    return shift - latency;
  }

  public long getAproximateRepositoryTime()
  {
    long offset = getAproximateRepositoryOffset();
    return System.currentTimeMillis() + offset;
  }

  @Override
  public String toString()
  {
    return MessageFormat
        .format(
            "RepositoryTime[requested={0,date} {0,time}, indicated={1,date} {1,time}, responded={2,date} {2,time}, confirmed={3,date} {3,time}]",
            requested, indicated, responded, confirmed);
  }
}
