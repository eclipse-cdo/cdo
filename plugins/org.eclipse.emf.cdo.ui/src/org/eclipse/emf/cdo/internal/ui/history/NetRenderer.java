/*
 * Copyright (c) 2012, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory.TriggerLoadElement;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
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

/**
 * @author Eike Stepper
 */
public final class NetRenderer implements Listener
{
  private static final int ROUND_EDGE = 3;

  private static final int ARC_SIZE = 8;

  private static final int TRACK_OFFSET = 4;

  private static final int TRACK_WIDTH = 14;

  private static final int LINE_WIDTH = 2;

  private static final int UNKNOWN = -1;

  private final Color colorDotFill;

  private final Color colorDotOutline;

  private final Color colorBadgeFill;

  private final Color colorBadgeOutline;

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
    colorBadgeFill = resourceManager.createColor(new RGB(188, 220, 188));
    colorBadgeOutline = resourceManager.createColor(new RGB(0, 128, 0));

    Table table = tableViewer.getTable();
    table.addListener(SWT.MeasureItem, this);
    table.addListener(SWT.PaintItem, this);
    table.addListener(SWT.EraseItem, this);
  }

  public Net getNet()
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

  @Override
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

      CDOCommitInfo commitInfo = (CDOCommitInfo)event.item.getData();
      return drawCell(commitInfo, event.index, justMeasureWidth);
    }
    catch (Throwable ex)
    {
      if (!net.isHideExceptions())
      {
        OM.LOG.error(ex);
        net.hideExceptions();
      }

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

    if (columnIndex == 1)
    {
      CDOBranchPoint mergeSource = commitInfo.getMergeSource();
      if (mergeSource != null && !text.startsWith(CDOTransactionCommentator.MERGE_PREFIX))
      {
        StringBuilder builder = new StringBuilder();
        CDOTransactionCommentator.appendMerge(builder, mergeSource);

        if (text.length() != 0)
        {
          builder.append(", ");
          builder.append(text);
        }

        text = builder.toString();
      }
    }

    int width = drawText(text, x, cellHeightHalf, false, justMeasureWidth);
    x += width;

    if (commitInfo instanceof TriggerLoadElement)
    {
      if (width != 0)
      {
        width += 4 * TRACK_OFFSET;
      }

      if (!justMeasureWidth)
      {
        int y = cellHeightHalf + 1;
        int x2 = gc.getClipping().width;
        drawLine(width, y, x2, y, colorDotOutline);
      }
    }

    return x;
  }

  private int drawCommit(CDOCommitInfo commitInfo, boolean justMeasureWidth)
  {
    Commit commit = net.getOrAddCommit(commitInfo);
    Branch commitBranch = commit.getBranch();
    Segment[] segments = commit.getRowSegments();
    Segment commitSegment = commit.getSegment();
    long commitTime = commit.getTime();
    boolean commitLastInBranch = commit.isLastInBranch(); // commitTime == commitBranch.getLastCommitTime();

    if (!justMeasureWidth)
    {
      Track commitTrack = commitSegment.getTrack();
      Color commitColor = commitBranch.getColor();
      int commitTrackPosition = commitTrack.getPosition();
      int commitTrackCenter = getTrackCenter(commitTrackPosition);

      for (int i = 0; i < segments.length; i++)
      {
        Segment segment = segments[i];
        if (segment != null && segment != commitSegment)
        {
          Branch branch = segment.getBranch();
          boolean merge = segment.isMerge();

          Color color = branch.getColor();
          int lineStyle = merge ? SWT.LINE_DOT : SWT.LINE_SOLID;
          int trackCenter = getTrackCenter(i);

          int positionDelta = Math.abs(i - commitTrackPosition);

          int horizontal = (positionDelta - 1) * TRACK_WIDTH + 6 + ROUND_EDGE;
          if (i < commitTrackPosition)
          {
            horizontal = -horizontal;
          }

          if (merge ? commitTime == segment.getLastCommitTime() || commitTime == segment.getFirstCommitTime()
              : commitTime == segment.getFirstVisualTime() && segment.isComplete())
          {
            // Plot horizontal line to left or right and a round edge up or down.
            boolean down = merge && commitTime == segment.getLastCommitTime();
            drawHorizontalLineWithRoundEdge(color, lineStyle, commitTrackCenter, trackCenter, horizontal, down);
          }
          else
          {
            Commit mergeSource = commit.getMergeSource();
            if (mergeSource != null && mergeSource == segment.getMergeSource())
            {
              drawHorizontalLineWithRoundEdge(color, lineStyle, commitTrackCenter, trackCenter, horizontal, true);
            }

            // Draw full vertical line.
            drawLine(trackCenter, 0, trackCenter, cellHeight, color, lineStyle);
          }
        }
      }

      if (!commitLastInBranch)
      {
        // Draw half vertical line up (solid).
        drawLine(commitTrackCenter, 0, commitTrackCenter, cellHeightHalf, commitColor);
      }
      else if (commit.getMergeTargets() != null)
      {
        Segment mergeSegment = commit.getMergeSegment();
        if (mergeSegment != null && mergeSegment.getTrack() == commitTrack)
        {
          // Draw half vertical line up (dotted).
          drawLine(commitTrackCenter, 0, commitTrackCenter, cellHeightHalf, commitColor, SWT.LINE_DOT);
        }
      }

      if (commitTime > commitSegment.getFirstVisualTime() || !commitSegment.isComplete())
      {
        if (!commitInfo.isInitialCommit())
        {
          // Draw half vertical line down (solid).
          drawLine(commitTrackCenter, cellHeightHalf, commitTrackCenter, cellHeight, commitColor);
        }
      }

      // Draw center dot.
      int dotX = commitTrackCenter - dotSizeHalf - 1;
      int dotY = cellHeightHalf - dotSizeHalf;
      drawDot(dotX, dotY, dotSize, dotSize);
    }

    int x = getTrackX(segments.length);

    if (commitLastInBranch)
    {
      String branchLabel = labelProvider.getBranchString(commit.getBranch().getCDOBranch());

      x += TRACK_OFFSET;
      x += drawText(branchLabel, x + TRACK_OFFSET, cellHeightHalf, true, justMeasureWidth);
    }

    return x;
  }

  private void drawHorizontalLineWithRoundEdge(Color color, int lineStyle, int commitTrackCenter, int trackCenter, int horizontal, boolean down)
  {
    LinePlotter plotter = new LinePlotter(color, lineStyle, commitTrackCenter, cellHeightHalf);
    plotter.relative(horizontal, 0);
    plotter.absolute(trackCenter, down ? cellHeight - ROUND_EDGE : ROUND_EDGE);
    plotter.relative(0, down ? ROUND_EDGE : -ROUND_EDGE);
  }

  private void drawLine(int x1, int y1, int x2, int y2, Color color)
  {
    drawLine(x1, y1, x2, y2, color, SWT.LINE_SOLID);
  }

  private void drawLine(int x1, int y1, int x2, int y2, Color color, int lineStyle)
  {
    gc.setForeground(color);
    gc.setLineWidth(LINE_WIDTH);
    gc.setLineStyle(lineStyle);
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
    gc.setLineStyle(SWT.LINE_SOLID);
    gc.drawOval(dotX, dotY, dotW, dotH);
  }

  private int drawText(String msg, int x, int y, boolean badge, boolean justMeasureWidth)
  {
    Point extent = gc.textExtent(msg);
    int width = extent.x;

    if (badge)
    {
      width += 2 * TRACK_OFFSET;
    }

    if (!justMeasureWidth)
    {
      x += cellX;
      y = (y * 2 - extent.y) / 2 + cellY - 1;
      int height = extent.y + 1;

      if (badge)
      {
        gc.setBackground(colorBadgeFill);
        gc.fillRoundRectangle(x + 2, y + 2, width - 3, height - 3, ARC_SIZE, ARC_SIZE);

        gc.setLineWidth(1);
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setForeground(colorBadgeOutline);
        gc.drawRoundRectangle(x, y, width, height, ARC_SIZE, ARC_SIZE);
      }
      else
      {
        gc.setBackground(cellBackground);
      }

      gc.setForeground(cellForeground);
      gc.drawString(msg, x + TRACK_OFFSET + 1, y + 1, true);
    }

    return width;
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

    private final int lineStyle;

    private int x;

    private int y;

    public LinePlotter(Color color, int lineStyle, int x, int y)
    {
      this.color = color;
      this.lineStyle = lineStyle;
      this.x = x;
      this.y = y;
    }

    public void relative(int width, int height)
    {
      int fromX = x;
      int fromY = y;
      x += width;
      y += height;
      drawLine(fromX, fromY, x, y, color, lineStyle);
    }

    public void absolute(int x, int y)
    {
      drawLine(this.x, this.y, x, y, color, lineStyle);
      this.x = x;
      this.y = y;
    }
  }
}
