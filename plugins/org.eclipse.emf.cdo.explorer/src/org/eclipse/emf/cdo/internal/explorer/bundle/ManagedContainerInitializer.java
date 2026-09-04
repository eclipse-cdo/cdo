package org.eclipse.emf.cdo.internal.explorer.bundle;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerInitializer;

/**
 * @author Eike Stepper
 */
public final class ManagedContainerInitializer implements IManagedContainerInitializer
{
  @Override
  public void initialize(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }
}
