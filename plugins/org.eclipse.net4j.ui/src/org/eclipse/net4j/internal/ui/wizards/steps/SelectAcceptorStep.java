package org.eclipse.net4j.internal.ui.wizards.steps;

import org.eclipse.net4j.internal.ui.AcceptorContentProvider;
import org.eclipse.net4j.transport.IAcceptor;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.SelectionStep;

/**
 * @author Eike Stepper
 */
public class SelectAcceptorStep extends ParallelStep
{
  public static final String KEY_ACCEPTOR = "Acceptor";

  public SelectAcceptorStep(ITransportContainer container)
  {
    add(new SelectionStep<String>(KEY_ACCEPTOR, IAcceptor.class, container, new AcceptorContentProvider(), null, 1, 1));
  }

  public SelectAcceptorStep()
  {
    this(IPluginTransportContainer.INSTANCE);
  }

  public IAcceptor getAcceptor()
  {
    Object value = getWizard().getSingleContextValue(KEY_ACCEPTOR);
    return value instanceof IAcceptor ? (IAcceptor)value : null;
  }
}