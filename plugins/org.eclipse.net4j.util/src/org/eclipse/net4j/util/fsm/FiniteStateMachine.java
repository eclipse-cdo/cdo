package org.eclipse.net4j.util.fsm;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public abstract class FiniteStateMachine<STATE extends Enum<?>, EVENT extends Enum<?>, SUBJECT>
{
  @SuppressWarnings("unchecked")
  public static final ITransition IGNORE = new IgnoreTransition();

  @SuppressWarnings("unchecked")
  public static final ITransition FAIL = new FailTransition();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, FiniteStateMachine.class);

  private static final String MSG_PROCESS = "Processing event {0} in state {1} for {2} (data={3})";

  private static final String MSG_IGNORE = "Ignoring event {0} in state {1} for {2} (data={3})";

  private static final String MSG_FAIL = "Failing event {0} in state {1} for {2} (data={3})";

  private STATE[] states;

  private EVENT[] events;

  private ITransition<STATE, EVENT, SUBJECT, ?>[][] transitions;

  public FiniteStateMachine(Class<STATE> stateEnum, Class<EVENT> eventEnum,
      ITransition<STATE, EVENT, SUBJECT, ?> defaultTransition)
  {
    states = stateEnum.getEnumConstants();
    events = eventEnum.getEnumConstants();
    transitions = new ITransition[states.length][events.length];
    transitAll(defaultTransition);
  }

  public FiniteStateMachine(Class<STATE> stateEnum, Class<EVENT> eventEnum)
  {
    this(stateEnum, eventEnum, IGNORE);
  }

  public final STATE[] getStates()
  {
    return states;
  }

  public final EVENT[] getEvents()
  {
    return events;
  }

  public final ITransition<STATE, EVENT, SUBJECT, ?> getTransition(STATE state, EVENT event)
  {
    int s = state.ordinal();
    int e = event.ordinal();
    return transitions[s][e];
  }

  public final void transit(STATE state, EVENT event, ITransition<STATE, EVENT, SUBJECT, ?> transition)
  {
    checkTransition(transition);
    int s = state.ordinal();
    int e = event.ordinal();
    transitions[s][e] = transition;
  }

  public final void transitEvents(STATE state, ITransition<STATE, EVENT, SUBJECT, ?> transition)
  {
    checkTransition(transition);
    int s = state.ordinal();
    for (int e = 0; e < events.length; e++)
    {
      transitions[s][e] = transition;
    }
  }

  public final void transitStates(EVENT event, ITransition<STATE, EVENT, SUBJECT, ?> transition)
  {
    checkTransition(transition);
    int e = event.ordinal();
    for (int s = 0; s < states.length; s++)
    {
      transitions[s][e] = transition;
    }
  }

  public final void transitAll(ITransition<STATE, EVENT, SUBJECT, ?> transition)
  {
    checkTransition(transition);
    for (int s = 0; s < states.length; s++)
    {
      for (int e = 0; e < events.length; e++)
      {
        transitions[s][e] = transition;
      }
    }
  }

  public final <DATA> void process(SUBJECT subject, EVENT event, DATA data)
  {
    STATE state = getState(subject);
    int s = state.ordinal();
    int e = event.ordinal();
    ITransition<STATE, EVENT, SUBJECT, DATA> transition = (ITransition<STATE, EVENT, SUBJECT, DATA>)transitions[s][e];
    if (transition == IGNORE)
    {
      // TODO if (TRACER.isEnabled())
      // {
      // TRACER.trace(formatIgnoreMessage(subject, state, event, data));
      // }
    }
    else if (transition == FAIL)
    {
      throw new IllegalStateException(formatFailMessage(subject, state, event, data));
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(formatProcessMessage(subject, state, event, data));
      }

      transition.execute(subject, state, event, data);
    }
  }

  protected ITransition<STATE, EVENT, SUBJECT, ?> createIgnoreTransition(STATE state, EVENT event)
  {
    return IGNORE;
  }

  protected ITransition<STATE, EVENT, SUBJECT, ?> createFailTransition(STATE state, EVENT event)
  {
    return FAIL;
  }

  protected String formatProcessMessage(SUBJECT subject, STATE state, EVENT event, Object data)
  {
    return MessageFormat.format(MSG_PROCESS, event, state, subject, data);
  }

  protected String formatIgnoreMessage(SUBJECT subject, STATE state, EVENT event, Object data)
  {
    return MessageFormat.format(MSG_IGNORE, event, state, subject, data);
  }

  protected String formatFailMessage(SUBJECT subject, STATE state, EVENT event, Object data)
  {
    return MessageFormat.format(MSG_FAIL, event, state, subject, data);
  }

  protected abstract STATE getState(SUBJECT subject);

  private void checkTransition(ITransition<STATE, EVENT, SUBJECT, ?> transition)
  {
    if (transition == null)
    {
      throw new IllegalArgumentException("transition == null");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class IgnoreTransition implements ITransition<Enum<?>, Enum<?>, Object, Object>
  {
    public void execute(Object subject, Enum<?> state, Enum<?> event, Object data)
    {
      // Do nothing
    }

    @Override
    public String toString()
    {
      return "IGNORE";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FailTransition implements ITransition<Enum<?>, Enum<?>, Object, Object>
  {
    public void execute(Object subject, Enum<?> state, Enum<?> event, Object data)
    {
      // Do nothing
    }

    @Override
    public String toString()
    {
      return "FAIL";
    }
  }
}