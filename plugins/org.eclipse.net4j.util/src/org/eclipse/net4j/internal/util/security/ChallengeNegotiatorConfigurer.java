/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.security;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.security.IRandomizer;
import org.eclipse.net4j.util.security.IUserManager;

/**
 * @author Eike Stepper
 */
public class ChallengeNegotiatorConfigurer implements IElementProcessor
{
  public ChallengeNegotiatorConfigurer()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof ChallengeNegotiator)
    {
      ChallengeNegotiator negotiator = (ChallengeNegotiator)element;
      if (negotiator.getRandomizer() == null)
      {
        IRandomizer randomizer = getRandomizer(container);
        negotiator.setRandomizer(randomizer);
      }

      if (negotiator.getUserManager() == null)
      {
        IUserManager userManager = getUserManager(container, description);
        negotiator.setUserManager(userManager);
      }
    }

    return element;
  }

  protected IRandomizer getRandomizer(IManagedContainer container)
  {
    String productGroup = RandomizerFactory.PRODUCT_GROUP;
    String type = RandomizerFactory.TYPE;
    return (IRandomizer)container.getElement(productGroup, type, null);
  }

  protected IUserManager getUserManager(IManagedContainer container, String fileName)
  {
    String productGroup = FileUserManagerFactory.PRODUCT_GROUP;
    String type = FileUserManagerFactory.TYPE;
    return (IUserManager)container.getElement(productGroup, type, fileName);
  }
}
