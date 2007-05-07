package org.eclipse.net4j.internal.ui.wizards.steps;

import org.eclipse.net4j.internal.ui.FactoryTypeContentProvider;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.SelectionStep;
import org.eclipse.net4j.ui.wizards.StringStep;

import org.eclipse.internal.net4j.transport.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class NewConnectorStep extends ParallelStep
{
  public static final String PRODUCT_GROUP = ConnectorFactory.CONNECTOR_GROUP;

  public static final String KEY_TYPE = "Connector type";

  public static final String KEY_DESCRIPTION = "Connector description";

  public NewConnectorStep(ITransportContainer container)
  {
    add(new SelectionStep<String>(KEY_TYPE, String.class, container, new FactoryTypeContentProvider(PRODUCT_GROUP),
        null, 1, 1));
    add(new StringStep("Description", KEY_DESCRIPTION));
  }

  public NewConnectorStep()
  {
    this(IPluginTransportContainer.INSTANCE);
  }

  public String getConnectorType()
  {
    Object value = getWizard().getSingleContextValue(KEY_TYPE);
    return value instanceof String ? (String)value : null;
  }

  public String getConnectorDescription()
  {
    Object value = getWizard().getSingleContextValue(KEY_DESCRIPTION);
    return value instanceof String ? (String)value : null;
  }
}