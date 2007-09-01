package org.eclipse.net4j.util.om.trace;

/**
 * @author Eike Stepper
 */
public interface OMTraceHandlerEvent
{
  public long getTimeStamp();

  public OMTracer getTracer();

  public Class<?> getContext();

  public String getMessage();

  public Throwable getThrowable();
}