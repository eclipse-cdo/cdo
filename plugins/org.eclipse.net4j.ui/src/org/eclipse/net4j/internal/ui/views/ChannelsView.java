/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.signal.Signal;
import org.eclipse.net4j.signal.SignalFinishedEvent;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.ui.Net4jItemProvider;
import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.spi.net4j.ChannelContainer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ChannelsView extends ContainerView implements IElementFilter
{
  public static final String ID = "org.eclipse.net4j.ChannelsView"; //$NON-NLS-1$

  private static final Styler ERROR_STYLER = StyledString.createColorRegistryStyler(JFacePreferences.ERROR_COLOR, null);

  private final Map<IChannel, List<LogEntry>> logs = new HashMap<>();

  private final ChannelContainer channelContainer = new ChannelContainer(IPluginContainer.INSTANCE)
  {
    @Override
    public boolean removeElement(IChannel channel)
    {
      synchronized (logs)
      {
        logs.remove(channel);
      }

      return super.removeElement(channel);
    }

    @Override
    protected void notifyProtocolEvent(IEvent event)
    {
      if (event instanceof SignalFinishedEvent)
      {
        SignalFinishedEvent<?> e = (SignalFinishedEvent<?>)event;

        Exception exception = e.getException();
        String error = exception == null ? null : exception.getMessage();

        notifySignal(e.getSignal(), error, e.getDuration());
      }
    }
  };

  public ChannelsView()
  {
  }

  @Override
  protected ChannelContainer getContainer()
  {
    return channelContainer;
  }

  @Override
  public boolean filter(Object element)
  {
    return element instanceof IChannel;
  }

  @Override
  protected Control createUI(Composite parent)
  {
    channelContainer.activate();
    return super.createUI(parent);
  }

  @Override
  public void dispose()
  {
    channelContainer.deactivate();
    super.dispose();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new Net4jItemProvider(this)
    {
      @Override
      public void sort(Viewer viewer, Object[] elements)
      {
        // Do nothing.
      }

      @Override
      public boolean hasChildren(Object element)
      {
        if (element instanceof IChannel)
        {
          IChannel channel = (IChannel)element;

          List<LogEntry> log;
          synchronized (logs)
          {
            log = logs.get(channel);
          }

          return log != null;
        }

        return super.hasChildren(element);
      }

      @Override
      public Object[] getChildren(Object element)
      {
        if (element instanceof IChannel)
        {
          IChannel channel = (IChannel)element;

          List<LogEntry> log;
          synchronized (logs)
          {
            log = logs.get(channel);
          }

          if (log != null)
          {
            return log.toArray(new LogEntry[log.size()]);
          }
        }

        return super.getChildren(element);
      }

      @Override
      protected void handleElementEvent(IEvent event)
      {
        super.handleElementEvent(event);

        INotifier source = event.getSource();
        if (source instanceof IProtocol)
        {
          IProtocol<?> protocol = (IProtocol<?>)source;
          updateLabels(protocol.getChannel());
        }
      }

      @Override
      public Image getImage(Object obj)
      {
        if (obj instanceof LogEntry)
        {
          return SharedIcons.getImage(SharedIcons.OBJ_SIGNAL);
        }

        return super.getImage(obj);
      }

      @Override
      public StyledString getStyledText(Object obj)
      {
        StyledString styledText = super.getStyledText(obj);

        if (obj instanceof IChannel)
        {
          IChannel channel = (IChannel)obj;
          decorateChannel(styledText, channel);
        }
        else if (obj instanceof LogEntry)
        {
          LogEntry entry = (LogEntry)obj;
          decorateLogEntry(styledText, entry);
        }

        return styledText;
      }

      private void decorateChannel(StyledString styledText, IChannel channel)
      {
        AbstractTransportView.decorateChannelInfraStructure(styledText, channel);
      }

      private void decorateLogEntry(StyledString styledText, LogEntry entry)
      {
        String error = entry.getError();
        if (error != null)
        {
          styledText.append("  " + error, ERROR_STYLER);
        }
        else
        {
          long duration = entry.getDuration();
          if (duration != -1L)
          {
            styledText.append("  " + duration + " millis", StyledString.COUNTER_STYLER);
          }
        }
      }
    };
  }

  protected void notifySignal(Signal signal, String error, long duration)
  {
    SignalProtocol<?> protocol = signal.getProtocol();
    if (protocol != null)
    {
      IChannel channel = protocol.getChannel();
      if (channel != null)
      {
        List<LogEntry> log;
        synchronized (logs)
        {
          log = logs.get(channel);
          if (log == null)
          {
            log = new ArrayList<>();
            logs.put(channel, log);
          }
        }

        log.add(0, new LogEntry(signal.toString(true), error, duration));
        refreshElement(channel, false);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LogEntry
  {
    private final String text;

    private final String error;

    private final long duration;

    public LogEntry(String text, String error, long duration)
    {
      this.text = text;
      this.error = error;
      this.duration = duration;
    }

    public String getText()
    {
      return text;
    }

    public String getError()
    {
      return error;
    }

    public long getDuration()
    {
      return duration;
    }

    @Override
    public String toString()
    {
      return getText();
    }
  }
}
