package org.eclipse.net4j.internal.ui.wizards.steps.sample;

import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.StaticSelectionStep;
import org.eclipse.net4j.ui.wizards.StringStep;

/**
 * @author Eike Stepper
 */
public class AddressStep extends ParallelStep
{
  private static final String[] PETS = { "Elvis", "Tim", "Teufel", "Robbie", "Else", "Nino" };

  public AddressStep()
  {
    add(new StaticSelectionStep("Sex", Sex.class, 1, 1));
    add(new StringStep("Name"));
    add(new StringStep("Street"));
    add(new StringStep("Zip-Code"));
    add(new StringStep("City"));
    add(new StaticSelectionStep("Pets", PETS, 0, 1));
  }

  public static enum Sex
  {
    MALE, FEMALE
  }
}