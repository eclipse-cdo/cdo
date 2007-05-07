package org.eclipse.net4j.internal.ui.wizards.steps;

import org.eclipse.net4j.internal.ui.FactoryTypeContentProvider;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.SelectionStep;
import org.eclipse.net4j.ui.wizards.StringStep;

import org.eclipse.internal.net4j.transport.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class NewAcceptorStep extends ParallelStep
{
  public static final String PRODUCT_GROUP = AcceptorFactory.ACCEPTOR_GROUP;

  public static final String KEY_TYPE = "Acceptor type";

  public static final String KEY_DESCRIPTION = "Acceptor description";

  public NewAcceptorStep(ITransportContainer container)
  {
    add(new SelectionStep<String>(KEY_TYPE, String.class, container, new FactoryTypeContentProvider(PRODUCT_GROUP),
        null, 1, 1));
    add(new StringStep("Description", KEY_DESCRIPTION));
  }

  public NewAcceptorStep()
  {
    this(IPluginTransportContainer.INSTANCE);
  }

  public String getAcceptorType()
  {
    Object value = getWizard().getSingleContextValue(KEY_TYPE);
    return value instanceof String ? (String)value : null;
  }

  public String getAcceptorDescription()
  {
    Object value = getWizard().getSingleContextValue(KEY_DESCRIPTION);
    return value instanceof String ? (String)value : null;
  }
}