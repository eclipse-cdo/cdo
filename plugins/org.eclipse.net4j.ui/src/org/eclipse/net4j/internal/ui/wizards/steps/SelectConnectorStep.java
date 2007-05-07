package org.eclipse.net4j.internal.ui.wizards.steps;

import org.eclipse.net4j.internal.ui.ConnectorContentProvider;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.SelectionStep;

/**
 * @author Eike Stepper
 */
public class SelectConnectorStep extends ParallelStep
{
  public static final String KEY_CONNECTOR = "Connector";

  public SelectConnectorStep(ITransportContainer container)
  {
    add(new SelectionStep<String>(KEY_CONNECTOR, IConnector.class, container, new ConnectorContentProvider(), null, 1,
        1));
  }

  public SelectConnectorStep()
  {
    this(IPluginTransportContainer.INSTANCE);
  }

  public IConnector getConnector()
  {
    Object value = getWizard().getSingleContextValue(KEY_CONNECTOR);
    return value instanceof IConnector ? (IConnector)value : null;
  }
}