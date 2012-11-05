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
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.server.net4j.FailoverMonitor;
import org.eclipse.emf.cdo.server.net4j.FailoverMonitor.AgentProtocol;

import org.eclipse.net4j.signal.SignalScheduledEvent;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeDispatcher;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Eike Stepper
 */
public class FailoverMonitorView extends AbstractView<FailoverMonitor>
{
  public static final String ID = "org.eclipse.emf.cdo.examples.client.offline.FailoverMonitorView"; //$NON-NLS-1$

  private static final int AMPLITUDE = 100;

  private static final int LENGTH = 5;

  private static final int[] ZERO = new int[LENGTH];

  private static final int[] PEAK = createPeakValues();

  public FailoverMonitorView()
  {
    super(FailoverMonitor.class);
  }

  @Override
  protected void createPane(Composite parent, FailoverMonitor monitor, ItemProvider<FailoverMonitor> itemProvider)
  {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new FillLayout(SWT.VERTICAL));

    monitor.addListener(new ContainerEventAdapter<AgentProtocol>()
    {
      private Map<String, AgentOscilloscope> oscilloscopes = new HashMap<String, AgentOscilloscope>();

      @Override
      protected void onAdded(IContainer<AgentProtocol> monitor, final AgentProtocol agent)
      {
        final AgentOscilloscope oscilloscope = getOscilloscope(composite, agent);
        agent.addListener(new IListener()
        {
          public void notifyEvent(IEvent event)
          {
            if (event instanceof SignalScheduledEvent)
            {
              oscilloscope.scheduleValues(PEAK);
            }
          }
        });
      }

      @Override
      protected void onRemoved(IContainer<AgentProtocol> container, AgentProtocol agent)
      {
        AgentOscilloscope oscilloscope = getOscilloscope(composite, agent);
        oscilloscope.scheduleValues(ZERO);
      }

      protected AgentOscilloscope getOscilloscope(final Composite parent, AgentProtocol agent)
      {
        String key = agent.toString();

        final AgentOscilloscope[] oscilloscope = { oscilloscopes.get(key) };
        if (oscilloscope[0] == null)
        {
          if (!parent.isDisposed())
          {
            parent.getDisplay().syncExec(new Runnable()
            {
              public void run()
              {
                oscilloscope[0] = new AgentOscilloscope(parent);
                parent.layout();
              }
            });

            oscilloscopes.put(key, oscilloscope[0]);
          }
        }

        return oscilloscope[0];
      }
    });
  }

  @Override
  public void setFocus()
  {
  }

  private static int[] createPeakValues()
  {
    Random random = new Random(3);

    int length = 2 * LENGTH;
    int[] values = new int[length];
    for (int i = 0; i < length; i++)
    {
      values[i] = random.nextInt(2 * AMPLITUDE) - AMPLITUDE;
      values[++i] = -values[i - 1];
    }

    return values;
  }

  /**
   * @author Eike Stepper
   */
  private static class AgentOscilloscope extends Oscilloscope
  {
    private int[] values = ZERO;

    public AgentOscilloscope(Composite parent)
    {
      super(parent, SWT.NONE);
      setConnect(0, true);
      setSteady(0, true, -1);
      setFade(0, false);
      setTailSize(0, Oscilloscope.TAILSIZE_MAX);
      setTailFade(0, 0);

      addStackListener(0, new OscilloscopeStackAdapter()
      {
        @Override
        public void stackEmpty(Oscilloscope scope, int channel)
        {
          setValues(0, values);
          values = ZERO;
        }
      });

      OscilloscopeDispatcher dispatcher = new OscilloscopeDispatcher(0, this);
      dispatcher.dispatch();
    }

    public void scheduleValues(int[] values)
    {
      this.values = values;
    }
  }
}
