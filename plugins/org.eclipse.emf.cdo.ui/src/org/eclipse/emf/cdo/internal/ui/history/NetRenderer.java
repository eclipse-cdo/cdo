/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.history;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.Input;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.LabelProvider;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Eike Stepper
 */
public class NetRenderer implements Listener
{
  private static final int TRACK_OFFSET = 4;

  private static final int TRACK_WIDTH = 14;

  private static final int LINE_WIDTH = 2;

  private static final int UNKNOWN = -1;

  private final Color colorDotFill;

  private final Color colorDotOutline;

  private Net net;

  private GC gc;

  private boolean antiAliasing = true;

  private int dotSize = UNKNOWN;

  private int dotSizeHalf;

  private int textHeight = UNKNOWN;

  private int cellHeight = UNKNOWN;

  private int cellHeightHalf;

  private int cellX;

  private int cellY;

  private Color cellForeground;

  private Color cellBackground;

  private LabelProvider labelProvider;

  public NetRenderer(LabelProvider labelProvider)
  {
    this.labelProvider = labelProvider;

    ResourceManager resourceManager = labelProvider.getResourceManager();
    colorDotFill = resourceManager.createColor(new RGB(220, 220, 220));
    colorDotOutline = resourceManager.createColor(new RGB(110, 110, 110));
  }

  public void setInput(Input input)
  {
    CDOSession session = input.getSession();
    CDOObject object = input.getObject();
    CDOID objectID = object == null ? null : object.cdoID();

    ResourceManager resourceManager = labelProvider.getResourceManager();
    net = new Net(session, objectID, resourceManager);
  }

  public void handleEvent(Event event)
  {
    try
    {
      gc = event.gc;

      if (antiAliasing)
      {
        try
        {
          gc.setAntialias(SWT.ON);
        }
        catch (SWTException ex)
        {
          antiAliasing = false;
        }
      }

      if (textHeight == UNKNOWN)
      {
        textHeight = gc.stringExtent("/").y; //$NON-NLS-1$
        cellHeight = event.height;
        cellHeightHalf = cellHeight / 2;

        dotSize = (int)(Math.min(cellHeight, TRACK_WIDTH) * 0.5f);
        dotSize += dotSize & 1;
        dotSizeHalf = dotSize / 2;
      }

      cellX = event.x;
      cellY = event.y;
      cellForeground = gc.getForeground();
      cellBackground = gc.getBackground();

      CDOCommitInfo commitInfo = (CDOCommitInfo)((TableItem)event.item).getData();
      drawCell(commitInfo, event.index);
    }
    catch (Throwable ex)
    {
      OM.LOG.error(ex);
    }
  }

  private void drawCell(CDOCommitInfo commitInfo, int columnIndex)
  {
    int textX = TRACK_OFFSET;
    if (columnIndex == 1)
    {
      Commit commit = net.getCommit(commitInfo);
      Segment commitSegment = commit.getSegment();
      long commitTime = commit.getTime();

      Segment[] segments = commit.getRowSegments();
      for (int i = 0; i < segments.length; i++)
      {
        Segment segment = segments[i];
        if (segment != null)
        {
          Branch branch = segment.getBranch();
          Color color = branch.getColor();

          int trackCenter = getTrackCenter(i);
          if (segment != commitSegment)
          {
            if (commitTime == segment.getFirstVisualTime() && segment.isComplete())
            {
              Track commitTrack = commitSegment.getTrack();
              int commitTrackPosition = commitTrack.getPosition();
              int commitTrackCenter = getTrackCenter(commitTrackPosition);
              int positionDelta = Math.abs(i - commitTrackPosition);

              int x2 = commitTrackCenter;
              if (i < commitTrackPosition)
              {
                // Horizontal line to left
                x2 -= (positionDelta - 1) * TRACK_WIDTH + 7;
                drawLine(color, commitTrackCenter, cellHeightHalf, x2, cellHeightHalf, LINE_WIDTH);

                // Diagonal line to upper left
                drawLine(color, x2, cellHeightHalf, getTrackCenter(i), 0, LINE_WIDTH);
              }
              else
              {
                // Horizontal line to right
                x2 += (positionDelta - 1) * TRACK_WIDTH + 7;
                drawLine(color, commitTrackCenter, cellHeightHalf, x2, cellHeightHalf, LINE_WIDTH);

                // Diagonal line to upper right
                drawLine(color, x2, cellHeightHalf, getTrackCenter(i), 0, LINE_WIDTH);
              }
            }
            else
            {
              // Full vertical line
              drawLine(color, trackCenter, 0, trackCenter, cellHeight, LINE_WIDTH);
            }
          }
        }

        Color color = commitSegment.getBranch().getColor();
        int position = commitSegment.getTrack().getPosition();
        int trackCenter = getTrackCenter(position);

        if (commitTime < commitSegment.getLastCommitTime())
        {
          // Half vertical line to top
          drawLine(color, trackCenter, 0, trackCenter, cellHeightHalf, LINE_WIDTH);
        }

        if (commitTime > commitSegment.getFirstVisualTime() || !commitSegment.isComplete())
        {
          // Half vertical line to bottom
          drawLine(color, trackCenter, cellHeightHalf, trackCenter, cellHeight, LINE_WIDTH);
        }

        int dotX = trackCenter - dotSizeHalf - 1;
        int dotY = cellHeightHalf - dotSizeHalf;
        drawCommitDot(dotX, dotY, dotSize, dotSize);
      }

      textX += getTrackX(segments.length) + TRACK_WIDTH;
    }
    else
    {
      Image image = labelProvider.getColumnImage(commitInfo, columnIndex);
      if (image != null)
      {
        Rectangle bounds = image.getBounds();
        gc.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, //
            cellX + textX, cellY + 1, bounds.width, bounds.height);

        textX += bounds.width + TRACK_OFFSET;
      }
    }

    String text = labelProvider.getColumnText(commitInfo, columnIndex);
    drawText(text, textX, cellHeightHalf);
  }

  private void drawLine(final Color color, final int x1, final int y1, final int x2, final int y2, final int width)
  {
    gc.setForeground(color);
    gc.setLineWidth(width);
    gc.drawLine(cellX + x1, cellY + y1, cellX + x2, cellY + y2);
  }

  private void drawDot(final Color outline, final Color fill, final int x, final int y, final int w, final int h)
  {
    int dotX = cellX + x + 2;
    int dotY = cellY + y + 1;
    int dotW = w - 2;
    int dotH = h - 2;
    gc.setBackground(fill);
    gc.fillOval(dotX, dotY, dotW, dotH);
    gc.setForeground(outline);
    gc.setLineWidth(2);
    gc.drawOval(dotX, dotY, dotW, dotH);
  }

  private void drawCommitDot(final int x, final int y, final int w, final int h)
  {
    drawDot(colorDotOutline, colorDotFill, x, y, w, h);
  }

  private void drawText(final String msg, final int x, final int y)
  {
    final Point textsz = gc.textExtent(msg);
    final int texty = (y * 2 - textsz.y) / 2;
    gc.setForeground(cellForeground);
    gc.setBackground(cellBackground);
    gc.drawString(msg, cellX + x, cellY + texty, true);
  }

  private int getTrackX(int position)
  {
    return TRACK_OFFSET + TRACK_WIDTH * position;
  }

  private int getTrackCenter(int position)
  {
    return getTrackX(position) + TRACK_WIDTH / 2;
  }
}
