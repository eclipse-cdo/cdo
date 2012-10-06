/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.compare;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;

/**
 * An {@link CDOIDFunction ID function} that considers the {@link CDOID}s of {@link CDOObject objects}.
 *
 * @author Eike Stepper
 */
public class CDOIDFunction implements Function<EObject, String>
{
  public String apply(EObject o)
  {
    CDOObject object = CDOUtil.getCDOObject(o);
    CDOID id = object.cdoID();

    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, id);
    return builder.toString();
  }
}