/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.fsm;


import org.apache.log4j.Logger;


public abstract class StateMachine<SUBJECT> implements IStateMachine<SUBJECT>
{
  private static final Logger logger = Logger.getLogger(StateMachine.class.getName());

  private String[] stateNames;

  private String[] eventNames;

  private ITransition<SUBJECT>[][] matrix;

  public StateMachine(String[] stateNames, String[] eventNames,
      ITransition<SUBJECT> defaultTransition)
  {
    this.stateNames = stateNames;
    this.eventNames = eventNames;

    matrix = new ITransition[stateNames.length][eventNames.length];

    if (defaultTransition != null)
    {
      for (int state = 0; state < stateNames.length; state++)
      {
        for (int event = 0; event < eventNames.length; event++)
        {
          handle(state, event, defaultTransition);
        }
      }
    }
  }

  public StateMachine(String[] stateNames, String[] eventNames)
  {
    this(stateNames, eventNames, null);
  }

  public int getStateCount()
  {
    return stateNames.length;
  }

  public int getEventCount()
  {
    return eventNames.length;
  }

  public String getStateName(int state)
  {
    return stateNames[state];
  }

  public String getEventName(int event)
  {
    return eventNames[event];
  }

  public ITransition<SUBJECT> getIdentityTransition()
  {
    return new ITransition<SUBJECT>()
    {
      public void process(SUBJECT subject, int event, Object data) throws Exception
      {
        if (getLogger().isDebugEnabled())
        {
          int state = getState(subject);
          getLogger().debug("Ignoring   " + makeLabel(subject, state, event));
        }
      }
    };
  }

  public void handle(int state, int event, ITransition<SUBJECT> transition)
  {
    matrix[state][event] = transition;
  }

  public void handle(int state, int event, final int newState)
  {
    handle(state, event, new ITransition<SUBJECT>()
    {
      public void process(SUBJECT subject, int event, Object data) throws Exception
      {
        setState(subject, newState);
      }
    });
  }

  public void ignore(int state, int event)
  {
    handle(state, event, getIdentityTransition());
  }

  public void cancel(int state, int event)
  {
    handle(state, event, null);
  }

  public void process(SUBJECT subject, int event, Object data) throws Exception
  {
    int state = getState(subject);
    ITransition<SUBJECT> transition = matrix[state][event];

    if (transition == null)
    {
      if (getLogger().isDebugEnabled())
      {
        getLogger().warn("Ignoring   " + makeLabel(subject, state, event));
      }

      return;
    }

    if (getLogger().isDebugEnabled())
    {
      getLogger().debug("Processing " + makeLabel(subject, state, event));
    }

    transition.process(subject, event, data);

    if (getLogger().isDebugEnabled())
    {
      int newState = getState(subject);
      getLogger()
          .debug(
              "Finished   " + makeLabel(subject, state, event) + ", newState="
                  + getStateName(newState));
    }
  }

  protected Logger getLogger()
  {
    return logger;
  }

  protected String makeLabel(SUBJECT subject, int state, int event)
  {
    return "event " + eventNames[event] + " in state " + stateNames[state] + " for subject "
        + subject;
  }

  protected abstract int getState(SUBJECT subject);

  protected abstract void setState(SUBJECT subject, int state);
}
