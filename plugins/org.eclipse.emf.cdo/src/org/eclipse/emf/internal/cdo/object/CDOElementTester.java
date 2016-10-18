/*
 * Copyright (c) 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
