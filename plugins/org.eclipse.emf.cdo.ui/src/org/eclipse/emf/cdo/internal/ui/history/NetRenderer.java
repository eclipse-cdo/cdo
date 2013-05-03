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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory.TriggerLoadElement;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.Input;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite.LabelProvider;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Eike Stepper
 */
public class NetRenderer implements Listener
{
  private static final int ROUND_EDGE = 3;

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

  public NetRenderer(TableViewer tableViewer)
  {
    labelProvider = (LabelProvider)tableViewer.getLabelProvider();

    ResourceManager resourceManager = labelProvider.getResourceManager();
    colorDotFill = resourceManager.createColor(new RGB(220, 220, 220));
    colorDotOutline = resourceManager.createColor(new RGB(110, 110, 110));

    Table table = tableViewer.getTable();
    table.addListener(SWT.MeasureItem, this);
    table.addListener(SWT.PaintItem, this);
    table.addListener(SWT.EraseItem, this);
  }

  public final Net getNet()
  {
    return net;
  }

  public void addCommit(CDOCommitInfo commitInfo)
  {
    if (net != null && !(commitInfo instanceof TriggerLoadElement))
    {
      net.addCommit(commitInfo);
    }
  }

  public void setInput(Input input)
  {
    if (input != null)
    {
      CDOSession session = input.getSession();
      CDOObject object = input.getObject();
      CDOID objectID = object == null ? null : object.cdoID();

      ResourceManager resourceManager = labelProvider.getResourceManager();
      net = new Net(session, objectID, resourceManager);
    }
    else
    {
      net = null;
    }
  }

  public void handleEvent(Event event)
  {
    switch (event.type)
    {
    case SWT.MeasureItem:
      event.width = handlePaintEvent(event, true) + TRACK_OFFSET;
      break;

    case SWT.PaintItem:
      handlePaintEvent(event, false);
      break;

    case SWT.EraseItem:
      event.detail &= ~SWT.FOREGROUND;
      break;
    }
  }

  public int handlePaintEvent(Event event, boolean justMeasureWidth)
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
      return drawCell(commitInfo, event.index, justMeasureWidth);
    }
    catch (Throwable ex)
    {
      OM.LOG.error(ex);
      return 0;
    }
  }

  private int drawCell(CDOCommitInfo commitInfo, int columnIndex, boolean justMeasureWidth)
  {
    int x = TRACK_OFFSET;
    if (columnIndex == 1)
    {
      if (!(commitInfo instanceof TriggerLoadElement))
      {
        x += drawCommit(commitInfo, justMeasureWidth);
      }
    }
    else
    {
      Image image = labelProvider.getColumnImage(commitInfo, columnIndex);
      if (image != null)
      {
        Rectangle bounds = image.getBounds();
        x += 2;
        if (!justMeasureWidth)
        {
          gc.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, //
              cellX + x - TRACK_OFFSET, cellY + 1, bounds.width, bounds.height);
        }

        x += bounds.width;
      }
    }

    String text = labelProvider.getColumnText(commitInfo, columnIndex);
    int width = drawText(text, x, cellHeightHalf, justMeasureWidth);
    x += width;

    if (commitInfo instanceof TriggerLoadElement)
    {
      if (width != 0)
      {
        width += 2 * TRACK_OFFSET;
      }

      if (!justMeasureWidth)
      {
        int y = cellHeightHalf + 1;
        int x2 = gc.getClipping().width;
        drawLine(colorDotOutline, width, y, x2, y);
      }
    }

    return x;
  }

  private int drawCommit(CDOCommitInfo commitInfo, boolean justMeasureWidth)
  {
    Commit commit = net.getCommit(commitInfo);
    Segment[] segments = commit.getRowSegments();

    if (!justMeasureWidth)
    {
      Segment commitSegment = commit.getSegment();
      long commitTime = commit.getTime();

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

              int horizontal = (positionDelta - 1) * TRACK_WIDTH + 6 + ROUND_EDGE;
              if (i < commitTrackPosition)
              {
                horizontal = -horizontal;
              }

              LinePlotter plotter = new LinePlotter(color, commitTrackCenter, cellHeightHalf);
              plotter.relative(horizontal, 0);
              plotter.absolute(getTrackCenter(i), ROUND_EDGE);
              plotter.relative(0, -ROUND_EDGE);
            }
            else
            {
              // Full vertical line
              drawLine(color, trackCenter, 0, trackCenter, cellHeight);
            }
          }
        }

        Color color = commitSegment.getBranch().getColor();
        int position = commitSegment.getTrack().getPosition();
        int trackCenter = getTrackCenter(position);

        if (commitTime < commitSegment.getLastCommitTime())
        {
          // Half vertical line to top
          drawLine(color, trackCenter, 0, trackCenter, cellHeightHalf);
        }

        if (commitTime > commitSegment.getFirstVisualTime() || !commitSegment.isComplete())
        {
          if (!commitInfo.isInitialCommit())
          {
            // Half vertical line to bottom
            drawLine(color, trackCenter, cellHeightHalf, trackCenter, cellHeight);
          }
        }

        int dotX = trackCenter - dotSizeHalf - 1;
        int dotY = cellHeightHalf - dotSizeHalf;
        drawDot(dotX, dotY, dotSize, dotSize);
      }
    }

    return getTrackX(segments.length);
  }

  private void drawLine(Color color, int x1, int y1, int x2, int y2)
  {
    gc.setForeground(color);
    gc.setLineWidth(LINE_WIDTH);
    gc.drawLine(cellX + x1, cellY + y1, cellX + x2, cellY + y2);
  }

  private void drawDot(final int x, final int y, final int w, final int h)
  {
    int dotX = cellX + x + 2;
    int dotY = cellY + y + 1;
    int dotW = w - 2;
    int dotH = h - 2;
    gc.setBackground(colorDotFill);
    gc.fillOval(dotX, dotY, dotW, dotH);
    gc.setForeground(colorDotOutline);
    gc.setLineWidth(2);
    gc.drawOval(dotX, dotY, dotW, dotH);
  }

  private int drawText(final String msg, final int x, final int y, boolean justMeasureWidth)
  {
    Point extent = gc.textExtent(msg);
    if (!justMeasureWidth)
    {
      int textY = (y * 2 - extent.y) / 2;
      gc.setForeground(cellForeground);
      gc.setBackground(cellBackground);
      gc.setBackground(cellBackground);
      gc.drawString(msg, cellX + x, cellY + textY, true);
    }

    return extent.x;
  }

  private int getTrackX(int position)
  {
    return TRACK_OFFSET + TRACK_WIDTH * position;
  }

  private int getTrackCenter(int position)
  {
    return getTrackX(position) + TRACK_WIDTH / 2;
  }

  /**
   * @author Eike Stepper
   */
  private final class LinePlotter
  {
    private final Color color;

    private int x;

    private int y;

    public LinePlotter(Color color, int x, int y)
    {
      this.color = color;
      this.x = x;
      this.y = y;
    }

    public void relative(int width, int height)
    {
      int fromX = x;
      int fromY = y;
      x += width;
      y += height;
      drawLine(color, fromX, fromY, x, y);
    }

    public void absolute(int x, int y)
    {
      drawLine(color, this.x, this.y, x, y);
      this.x = x;
      this.y = y;
    }
  }
}
