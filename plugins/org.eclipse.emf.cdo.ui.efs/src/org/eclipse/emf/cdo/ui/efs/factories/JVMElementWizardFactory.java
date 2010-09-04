package org.eclipse.emf.cdo.ui.efs.factories;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.container.ElementWizardFactory;
import org.eclipse.net4j.util.ui.container.IElementWizard;

/**
 * @author Martin Fluegge
 */
public class JVMElementWizardFactory extends ElementWizardFactory
{
  public JVMElementWizardFactory(String elementProductGroup, String elementFactoryType)
  {
    super(elementProductGroup, elementFactoryType);
  }

  @Override
  public String getDescriptionFor(Object product)
  {
    return null;
  }

  @Override
  public IElementWizard create(String description) throws ProductCreationException
  {
    return null;
  }
}
