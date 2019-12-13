/*
 * Copyright (c) 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.examples.client.offline.FailoverMonitorView.OScope.Channel;
import org.eclipse.emf.cdo.server.net4j.FailoverMonitor;
import org.eclipse.emf.cdo.server.net4j.FailoverMonitor.AgentProtocol;

import org.eclipse.net4j.signal.SignalScheduledEvent;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import java.io.ByteArrayInputStream;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Eike Stepper
 */
public class FailoverMonitorView extends AbstractView<FailoverMonitor>
{
  public static final String ID = "org.eclipse.emf.cdo.examples.client.offline.FailoverMonitorView"; //$NON-NLS-1$

  public FailoverMonitorView()
  {
    super(FailoverMonitor.class);
  }

  @Override
  protected void createPane(Composite parent, FailoverMonitor monitor)
  {
    final OScope scope = new OScope(parent, SWT.NONE);
    monitor.addListener(new ContainerEventAdapter<AgentProtocol>()
    {
      @Override
      protected void notifyContainerEvent(IContainerEvent<AgentProtocol> event)
      {
        addEvent(event);
        super.notifyContainerEvent(event);
      }

      @Override
      protected void onAdded(IContainer<AgentProtocol> monitor, final AgentProtocol agent)
      {
        final Channel channel = scope.getChannel(agent.getConnectorDescription());
        agent.addListener(new IListener()
        {
          @Override
          public void notifyEvent(IEvent event)
          {
            if (event instanceof SignalScheduledEvent)
            {
              if (!scope.isDisposed())
              {
                scope.getDisplay().asyncExec(new Runnable()
                {
                  @Override
                  public void run()
                  {
                    if (!scope.isDisposed())
                    {
                      channel.peak();
                    }
                  }
                });
              }
            }
          }
        });
      }
    });
  }

  @Override
  public void setFocus()
  {
  }

  /**
   * @author Eike Stepper
   */
  public static class OScope extends Canvas implements Runnable, PaintListener, ControlListener
  {
    public static final int[] PEAK = { 10, 95, 80, 90, 50, 25, 10, 5 };

    private static final String INITIAL = "#INITIAL#";

    private static final Random RANDOM = new Random();

    private static final int[] BACKGROUND = { 255, 216, 255, 224, 0, 16, 74, 70, 73, 70, 0, 1, 1, 1, 0, 72, 0, 72, 0, 0, 255, 254, 0, 19, 67, 114, 101, 97, 116,
        101, 100, 32, 119, 105, 116, 104, 32, 71, 73, 77, 80, 255, 219, 0, 67, 0, 5, 3, 4, 4, 4, 3, 5, 4, 4, 4, 5, 5, 5, 6, 7, 12, 8, 7, 7, 7, 7, 15, 11, 11, 9,
        12, 17, 15, 18, 18, 17, 15, 17, 17, 19, 22, 28, 23, 19, 20, 26, 21, 17, 17, 24, 33, 24, 26, 29, 29, 31, 31, 31, 19, 23, 34, 36, 34, 30, 36, 28, 30, 31,
        30, 255, 219, 0, 67, 1, 5, 5, 5, 7, 6, 7, 14, 8, 8, 14, 30, 20, 17, 20, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 255, 192, 0, 17, 8, 0, 20, 0,
        20, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, 255, 196, 0, 23, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 5, 7, 255, 196, 0, 34, 16, 0, 2, 2, 0, 5,
        5, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 4, 2, 20, 33, 52, 84, 17, 115, 145, 178, 209, 97, 255, 196, 0, 23, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 2, 0, 1, 3, 255, 196, 0, 27, 17, 0, 2, 2, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 17, 33, 49, 81, 18, 255, 218, 0, 12, 3, 1, 0, 2, 17,
        3, 17, 0, 63, 0, 225, 37, 87, 118, 245, 59, 79, 217, 140, 212, 60, 24, 60, 226, 250, 83, 110, 196, 74, 10, 205, 211, 133, 245, 141, 180, 155, 122, 106,
        255, 0, 78, 77, 187, 88, 4, 164, 237, 96, 204, 5, 89, 168, 120, 48, 121, 197, 244, 10, 223, 5, 233, 240, 148, 170, 238, 222, 167, 105, 251, 48, 9, 237,
        20, 182, 137, 64, 6, 140, 255, 217 };

    private SortedMap<String, Channel> channels = new TreeMap<String, Channel>();

    private Channel[] channelArray;

    private int width;

    private int height;

    private int channelHeight;

    private boolean resizing;

    private Color black;

    private Color white;

    public OScope(Composite parent, int style)
    {
      super(parent, style | SWT.DOUBLE_BUFFERED);
      black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
      white = getDisplay().getSystemColor(SWT.COLOR_WHITE);

      setBackgroundImage(createBackgroundImage());
      addPaintListener(this);
      addControlListener(this);

      Channel channel = new Channel(INITIAL);
      channels.put(INITIAL, channel);
      channelArray = new Channel[] { channel };

      run();
    }

    public int getTimerExecMillis()
    {
      return 25;
    }

    @Override
    public void dispose()
    {
      super.dispose();
    }

    public Channel getChannel(String name)
    {
      synchronized (channels)
      {
        Channel channel = channels.get(name);
        if (channel == null)
        {
          boolean resize = false;

          channel = channels.remove(INITIAL);
          if (channel == null)
          {
            channel = new Channel(name);
            resize = true;
          }
          else
          {
            channel.setName(name);
          }

          channels.put(name, channel);
          channelArray = channels.values().toArray(new Channel[channels.size()]);

          if (resize)
          {
            getDisplay().syncExec(new Runnable()
            {
              @Override
              public void run()
              {
                controlResized(null);
              }
            });
          }
        }

        return channel;
      }
    }

    @Override
    public void controlMoved(ControlEvent e)
    {
      // Do nothing
    }

    @Override
    public void controlResized(ControlEvent e)
    {
      try
      {
        resizing = true;
        Point size = getSize();
        width = size.x;
        height = size.y;

        int count = channelArray.length;
        if (count == 0)
        {
          return;
        }

        int oldChannelHeight = channelHeight;
        if (oldChannelHeight == 0)
        {
          oldChannelHeight = 1;
        }

        channelHeight = height / (2 * count);

        int y = channelHeight;
        for (Channel channel : channelArray)
        {
          channel.resize(y, oldChannelHeight);
          y += channelHeight + channelHeight;
        }
      }
      finally
      {
        resizing = false;
      }
    }

    @Override
    public void paintControl(PaintEvent e)
    {
      if (!resizing)
      {
        GC gc = e.gc;
        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);

        for (Channel channel : channelArray)
        {
          channel.paint(e.gc);
        }
      }
    }

    @Override
    public void run()
    {
      if (isDisposed())
      {
        return;
      }

      redraw();

      getDisplay().timerExec(getTimerExecMillis(), this);
    }

    protected Image createBackgroundImage()
    {
      byte[] bytes = new byte[BACKGROUND.length];
      for (int i = 0; i < BACKGROUND.length; i++)
      {
        bytes[i] = (byte)BACKGROUND[i];
      }

      return new Image(getDisplay(), new ByteArrayInputStream(bytes));
    }

    /**
     * @author Eike Stepper
     */
    public class Channel
    {
      private String name;

      private int[] values;

      private int[] head;

      private int headIndex;

      private boolean headNegate;

      private int x;

      private int y;

      public Channel(String name)
      {
        this.name = name;
      }

      public String getName()
      {
        return name;
      }

      public void setHead(int[] head)
      {
        headIndex = 0;
        headNegate = false;
        this.head = head;
      }

      public void peak()
      {
        setHead(PEAK);
      }

      @Override
      public String toString()
      {
        return "Channel[" + name + "]";
      }

      public void resize(int y, int oldChannelHeight)
      {
        int max = 0;
        int[] newValues = new int[2 * width];
        if (values != null)
        {
          System.arraycopy(values, 0, newValues, 0, Math.min(values.length, newValues.length));
          max = Math.min(values.length / 2, width);
        }

        int x = 0;
        for (; x < max; x++)
        {
          int i = 2 * x;
          newValues[i] = x;
          newValues[i + 1] = (newValues[i + 1] - this.y) * channelHeight / oldChannelHeight + y;
        }

        for (; x < width; x++)
        {
          int i = 2 * x;
          newValues[i] = x;
          newValues[i + 1] = y;
        }

        values = newValues;
        x = Math.min(x, width);
        this.y = y;
      }

      public void paint(GC gc)
      {
        int i = 2 * x + 1;
        if (values == null || i >= values.length)
        {
          return;
        }

        int value = getNextValue() * (90 + RANDOM.nextInt(20)) / 100 + RANDOM.nextInt(4) - 2;
        if (value > 100)
        {
          value = 100;
        }
        else if (value < -100)
        {
          value = -100;
        }

        int fx = y + value * channelHeight / 100;
        values[i] = fx;

        gc.setForeground(white);
        gc.setLineWidth(1);
        gc.drawPolyline(values);

        gc.setForeground(black);
        gc.setLineWidth(2);
        gc.drawRectangle(x, fx, 2, 2);

        if (++x >= width)
        {
          x = 0;
        }
      }

      void setName(String name)
      {
        this.name = name;
      }

      private int getNextValue()
      {
        if (head == null)
        {
          return 0;
        }

        int value;
        if (headNegate)
        {
          value = -head[headIndex];
          if (++headIndex >= head.length)
          {
            setHead(null);
            headNegate = !headNegate;
          }
        }
        else
        {
          value = head[headIndex];
        }

        headNegate = !headNegate;
        return value;
      }
    }
  }
}
