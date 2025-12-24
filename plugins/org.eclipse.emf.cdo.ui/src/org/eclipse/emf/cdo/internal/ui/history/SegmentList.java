/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.history;

/**
 * @author Eike Stepper
 */
public abstract class SegmentList
{
  private final Net net;

  private Segment firstSegment;

  private Segment lastSegment;

  public SegmentList(Net net)
  {
    this.net = net;
  }

  public final Net getNet()
  {
    return net;
  }

  public final Segment getFirstSegment()
  {
    return firstSegment;
  }

  public final Segment getLastSegment()
  {
    return lastSegment;
  }

  public final Segment getSegment(long time)
  {
    Segment segment = lastSegment;
    while (segment != null)
    {
      if (segment.containsVisualTime(time))
      {
        return segment;
      }

      segment = getPreviousSegment(segment);
    }

    return null;
  }

  void addSegment(Segment segment, boolean afterLast)
  {
    if (lastSegment == null)
    {
      lastSegment = segment;
      firstSegment = segment;
    }
    else if (afterLast)
    {
      setPreviousSegment(segment, lastSegment);
      setNextSegment(lastSegment, segment);
      lastSegment = segment;
    }
    else
    {
      setNextSegment(segment, firstSegment);
      setPreviousSegment(firstSegment, segment);
      firstSegment = segment;
    }
  }

  void removeSegment(Segment segment)
  {
    Segment nextSegment = getNextSegment(segment);
    Segment previousSegment = getPreviousSegment(segment);

    if (nextSegment != null)
    {
      setPreviousSegment(nextSegment, previousSegment);
    }
    else
    {
      lastSegment = previousSegment;
    }

    if (previousSegment != null)
    {
      setNextSegment(previousSegment, nextSegment);
    }
    else
    {
      firstSegment = nextSegment;
    }
  }

  protected abstract Segment getNextSegment(Segment segment);

  protected abstract void setNextSegment(Segment segment, Segment nextSegment);

  protected abstract Segment getPreviousSegment(Segment segment);

  protected abstract void setPreviousSegment(Segment segment, Segment previousSegment);
}
