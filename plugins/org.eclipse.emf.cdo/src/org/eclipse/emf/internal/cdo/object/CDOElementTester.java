/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOElement;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public abstract class CDOElementTester extends DefaultPropertyTester<EObject>
{
  public CDOElementTester(String namespace, IProperties<EObject> properties)
  {
    super(namespace, properties);
  }

  @Override
  protected EObject convertReceiver(Object receiver)
  {
    if (receiver instanceof CDOElement)
    {
      CDOElement element = (CDOElement)receiver;
      Object delegate = element.getDelegate();
      if (delegate instanceof EObject)
      {
        return (EObject)delegate;
      }
    }

    return super.convertReceiver(receiver);
  }

  @Override
  protected String getReceiverTypeName()
  {
    return CDOElement.class.getName();
  }
}
