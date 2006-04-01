package org.eclipse.net4j.util.fsm;


public interface IStateMachine<SUBJECT>
{
  public int getStateCount();

  public int getEventCount();

  public String getStateName(int state);

  public String getEventName(int event);

  public ITransition<SUBJECT> getIdentityTransition();

  public void process(SUBJECT subject, int event, Object data) throws Exception;

  public void handle(int state, int event, ITransition<SUBJECT> transition);

  public void handle(int state, int event, int newState);

  public void ignore(int state, int event);

  public void cancel(int state, int event);


  public interface ITransition<SUBJECT>
  {
    public void process(SUBJECT subject, int event, Object data) throws Exception;
  }
}