/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.model;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface InternalCDOFeature extends CDOFeature, InternalCDOModelElement
{
  public CDOClassProxy getReferenceTypeProxy();

  public void setContainingClass(CDOClass containingClass);

  public void setFeatureIndex(int featureIndex);

  public void setReferenceType(CDOClassRef cdoClassRef);

  public void setDefaultValue(Object defaultValue);

  public void writeValue(CDODataOutput out, Object value) throws IOException;

  public Object readValue(CDODataInput in) throws IOException;
}
