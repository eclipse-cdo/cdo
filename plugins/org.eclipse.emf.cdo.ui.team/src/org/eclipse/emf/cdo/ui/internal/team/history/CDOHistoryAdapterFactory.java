package org.eclipse.emf.cdo.ui.internal.team.history;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.team.ui.history.IHistoryPageSource;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("rawtypes")
public class CDOHistoryAdapterFactory implements IAdapterFactory
{
  private static final Class[] ADAPTER_TYPES = { IHistoryPageSource.class };

  public CDOHistoryAdapterFactory()
  {
  }

  public Class[] getAdapterList()
  {
    return ADAPTER_TYPES;
  }

  public Object getAdapter(Object adaptableObject, Class adapterType)
  {
    if (adapterType == IHistoryPageSource.class)
    {
      return CDOHistoryPageSource.INSTANCE;
    }

    return null;
  }
}
