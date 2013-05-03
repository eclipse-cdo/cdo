/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.history;

/**
 * @author Eike Stepper
 */
public class Track extends SegmentList
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
