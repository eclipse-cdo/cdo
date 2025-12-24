/*
 * Copyright (c) 2008-2012, 2014-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.ListTargetAdding;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Simon McDuff
 */
public class CDOSetFeatureDeltaImpl extends CDOSingleValueFeatureDeltaImpl implements CDOSetFeatureDelta, ListTargetAdding
{
  private static final ThreadLocal<Boolean> TRANSFER_OLD_VALUE = new ThreadLocal<>();

  private Object oldValue = UNKNOWN_VALUE;

  public CDOSetFeatureDeltaImpl(EStructuralFeature feature, int index, Object value)
  {
    super(feature, index, value);
  }

  public CDOSetFeatureDeltaImpl(EStructuralFeature feature, int index, Object value, Object oldValue)
  {
    super(feature, index, value);
    this.oldValue = oldValue;
  }

  public CDOSetFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);

    if (TRANSFER_OLD_VALUE.get() == Boolean.TRUE)
    {
      oldValue = readValue(in, eClass);
    }
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);

    if (TRANSFER_OLD_VALUE.get() == Boolean.TRUE)
    {
      writeValue(out, eClass, oldValue);
    }
  }

  @Override
  public Type getType()
  {
    return Type.SET;
  }

  @Override
  public CDOFeatureDelta copy()
  {
    return new CDOSetFeatureDeltaImpl(getFeature(), getIndex(), getValue(), getOldValue());
  }

  @Override
  public Object applyTo(CDORevision revision)
  {
    EStructuralFeature feature = getFeature();
    int index = getIndex();
    Object value = getValue();

    InternalCDORevision internalRevision = (InternalCDORevision)revision;
    return internalRevision.set(feature, index, value);
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public Object getOldValue()
  {
    return oldValue;
  }

  public void setOldValue(Object oldValue)
  {
    this.oldValue = oldValue;
  }

  @Override
  protected String toStringAdditional()
  {
    return super.toStringAdditional() + MessageFormat.format(", oldValue={0}", oldValue); //$NON-NLS-1$
  }

  public static void transferOldValue(boolean on)
  {
    if (on)
    {
      TRANSFER_OLD_VALUE.set(Boolean.TRUE);
    }
    else
    {
      TRANSFER_OLD_VALUE.remove();
    }
  }
}
