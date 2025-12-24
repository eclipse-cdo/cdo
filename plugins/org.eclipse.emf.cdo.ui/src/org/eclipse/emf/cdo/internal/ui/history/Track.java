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
public final class Track extends SegmentList
{
  private final int position;

  public Track(Net net, int position)
  {
    super(net);
    this.position = position;
  }

  public int getPosition()
  {
    return position;
  }

  public boolean hasSegment(long fromTime, long toTime)
  {
    Segment segment = getLastSegment();

    while (segment != null)
    {
      if (segment.getLastCommitTime() < fromTime)
      {
        return false;
      }

      if (segment.getFirstVisualTime() < toTime)
      {
        return true;
      }

      segment = segment.getPreviousInTrack();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return "Track[" + position + "]";
  }

  @Override
  protected Segment getNextSegment(Segment segment)
  {
    return segment.getNextInTrack();
  }

  @Override
  protected void setNextSegment(Segment segment, Segment nextSegment)
  {
    segment.setNextInTrack(nextSegment);
  }

  @Override
  protected Segment getPreviousSegment(Segment segment)
  {
    return segment.getPreviousInTrack();
  }

  @Override
  protected void setPreviousSegment(Segment segment, Segment previousSegment)
  {
    segment.setPreviousInTrack(previousSegment);
  }
}
