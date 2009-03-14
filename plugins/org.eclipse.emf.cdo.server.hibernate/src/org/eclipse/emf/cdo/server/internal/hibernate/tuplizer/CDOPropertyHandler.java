/**
 * Copyright (c) 2004 - 2009 Martin Taal and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Martin Taal
 */
public abstract class CDOPropertyHandler
{
  private final ContextTracer tracer = new ContextTracer(OM.DEBUG, this.getClass());

  private CDORevisionTuplizer tuplizer;

  private EStructuralFeature feature;

  private boolean virtualProperty = false;

  public CDOPropertyHandler(CDORevisionTuplizer tuplizer, String propertyName)
  {
    this.tuplizer = tuplizer;
    feature = tuplizer.getEClass().getEStructuralFeature(propertyName);
    if (getTracer().isEnabled())
    {
      getTracer().trace(
          "Created " + this.getClass().getName() + " for eClass/feature: " + tuplizer.getEClass().getName() + "."
              + propertyName);
    }

    if (feature == null)
    {
      if (isVirtualPropertyAllowed())
      {
        virtualProperty = true;
        if (getTracer().isEnabled())
        {
          getTracer().trace("This is a virtual property");
        }
      }
      else
      {
        throw new IllegalStateException("Feature not found: " + propertyName);
      }
    }
  }

  protected ContextTracer getTracer()
  {
    return tracer;
  }

  public CDORevisionTuplizer getTuplizer()
  {
    return tuplizer;
  }

  public EStructuralFeature getEStructuralFeature()
  {
    return feature;
  }

  protected boolean isVirtualPropertyAllowed()
  {
    return false;
  }

  /**
   * @return the virtualProperty
   */
  public boolean isVirtualProperty()
  {
    return virtualProperty;
  }
}
