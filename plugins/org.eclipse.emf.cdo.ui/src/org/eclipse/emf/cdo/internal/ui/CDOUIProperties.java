/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.UIOperations;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class CDOUIProperties extends Properties<Object>
{
  public static final IProperties<Object> INSTANCE = new CDOUIProperties();

  private CDOUIProperties()
  {
    super(Object.class);

    add(new Property.WithArguments<>("authorizedOperation")
    {
      @Override
      protected Object eval(Object receiver, Object[] args)
      {
        if (args != null && args.length != 0)
        {
          CDOSession session = CDOUtil.getSession(receiver);
          if (session != null)
          {
            for (Object operationID : args)
            {
              if (!UIOperations.isAuthorized(session, (String)operationID))
              {
                return false;
              }
            }

            return true;
          }
        }

        return false;
      }
    });
  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<Object>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.ui";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
