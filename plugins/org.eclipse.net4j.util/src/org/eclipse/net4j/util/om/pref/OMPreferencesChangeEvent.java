package org.eclipse.net4j.util.om.pref;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface OMPreferencesChangeEvent<T> extends IEvent
{
  public OMPreference<T> getPreference();

  public T getOldValue();

  public T getNewValue();
}