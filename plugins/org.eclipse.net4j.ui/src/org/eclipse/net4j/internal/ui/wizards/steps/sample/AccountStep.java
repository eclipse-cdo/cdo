package org.eclipse.net4j.internal.ui.wizards.steps.sample;

import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.StringStep;

/**
 * @author Eike Stepper
 */
public class AccountStep extends ParallelStep
{
  public AccountStep()
  {
    add(new StringStep("Account-ID"));
    add(new StringStep("Account-Owner"));
    add(new StringStep("Bank-Code"));
    add(new StringStep("Bank-Name"));
  }
}