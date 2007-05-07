package org.eclipse.net4j.internal.ui.wizards.steps.sample;

import org.eclipse.net4j.internal.ui.wizards.steps.NewAcceptorStep;
import org.eclipse.net4j.ui.wizards.SequentialStep;

/**
 * @author Eike Stepper
 */
public class CustomerStep extends SequentialStep
{
  public CustomerStep()
  {
    add(new AddressStep());
    add(new NewAcceptorStep());
    add(new AccountStep());
  }
}