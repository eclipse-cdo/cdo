/*
 * Copyright (c) 2008-2012, 2014, 2017, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;

import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Simon McDuff
 */
public abstract class CDOFeatureDeltaImpl implements InternalCDOFeatureDelta
{
  private EStructuralFeature feature;

  protected CDOFeatureDeltaImpl(EStructuralFeature feature)
  {
    CheckUtil.checkArg(feature, "feature"); //$NON-NLS-1$
    this.feature = feature;
  }

  public CDOFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    int featureID = in.readXInt();
    feature = eClass.getEStructuralFeature(featureID);
    CheckUtil.checkState(feature, "feature"); //$NON-NLS-1$
  }

  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    out.writeXInt(getType().ordinal());
    out.writeXInt(eClass.getFeatureID(feature));
  }

  @Override
  public EStructuralFeature getFeature()
  {
    return feature;
  }

  @Override
  @Deprecated
  public void apply(CDORevision revision)
  {
    applyTo(revision);
  }

  @Override
  public boolean isStructurallyEqual(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOFeatureDelta)
    {
      CDOFeatureDelta that = (CDOFeatureDelta)obj;
      return feature.equals(that.getFeature()) && getType().equals(that.getType());
    }

    return false;
  }

  @Override
  public String toString()
  {
    String additional = toStringAdditional();
    if (additional == null)
    {
      return MessageFormat.format("CDOFeatureDelta[{0}, {1}]", feature.getName(), getType());
    }

    return MessageFormat.format("CDOFeatureDelta[{0}, {1}, {2}]", feature.getName(), getType(), additional);
  }

  public abstract boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster);

  protected abstract String toStringAdditional();

  /**
   * @author Eike Stepper
   */
  public static final class UnknownValue extends NotifierImpl
  {
    @Override
    public String toString()
    {
      return "UNKNOWN"; //$NON-NLS-1$
    }
  }
}
