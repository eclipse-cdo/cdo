package org.eclipse.net4j.internal.ui.wizards.steps;

import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.ui.wizards.DecisionStep;

/**
 * @author Eike Stepper
 */
public class ProvideConnectorStep extends DecisionStep
{
  private ITransportContainer container;

  private SelectConnectorStep selectConnectorStep;

  private NewConnectorStep newConnectorStep;

  public ProvideConnectorStep(ITransportContainer container)
  {
    this.container = container;
    add(selectConnectorStep = new SelectConnectorStep(container));
    add(newConnectorStep = new NewConnectorStep(container));
  }

  public ProvideConnectorStep()
  {
    this(IPluginTransportContainer.INSTANCE);
  }

  public IConnector getConnector()
  {
    int decision = getDecisionIndex();
    switch (decision)
    {
    case 0:
      return selectConnectorStep.getConnector();

    case 1:
      String type = newConnectorStep.getConnectorType();
      String description = newConnectorStep.getConnectorDescription();
      return container.getConnector(type, description);

    default:
      throw new IllegalStateException("decision: " + decision);
    }
  }
}