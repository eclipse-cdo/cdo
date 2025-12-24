/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

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
              String result = session.authorizeOperations(AuthorizableOperation.build((String)operationID))[0];
              if (result != null)
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
