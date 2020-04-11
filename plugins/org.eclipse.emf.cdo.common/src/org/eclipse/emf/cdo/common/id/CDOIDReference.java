/*
 * Copyright (c) 2011, 2012, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * Represents a {@link CDOID} typed reference from one object to another object.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOIDReference implements CDOReference<CDOID>
{
  private CDOID targetID;

  private CDOID sourceID;

  private EReference sourceReference;

  private int sourceIndex;

  public CDOIDReference(CDOID targetID, CDOID sourceID, EStructuralFeature sourceReference, int sourceIndex)
  {
    this.targetID = targetID;
    this.sourceID = sourceID;
    this.sourceReference = (EReference)sourceReference;
    this.sourceIndex = sourceIndex;
  }

  public CDOIDReference(CDODataInput in) throws IOException
  {
    targetID = in.readCDOID();
    sourceID = in.readCDOID();

    EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
    String featureName = in.readString();
    sourceReference = (EReference)eClass.getEStructuralFeature(featureName);

    sourceIndex = in.readXInt();
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOID(targetID);
    out.writeCDOID(sourceID);
    out.writeCDOClassifierRef(sourceReference.getEContainingClass());
    out.writeString(sourceReference.getName());
    out.writeXInt(sourceIndex);
  }

  @Override
  public CDOID getTargetObject()
  {
    return targetID;
  }

  @Override
  public CDOID getSourceObject()
  {
    return sourceID;
  }

  @Deprecated
  @Override
  public EStructuralFeature getSourceFeature()
  {
    return sourceReference;
  }

  @Override
  public EReference getSourceReference()
  {
    return sourceReference;
  }

  @Override
  public int getSourceIndex()
  {
    return sourceIndex;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(sourceID);
    builder.append(".");
    builder.append(sourceReference.getName());
    if (sourceIndex != NO_INDEX)
    {
      builder.append("[");
      builder.append(sourceIndex);
      builder.append("]");
    }

    builder.append(" --> ");
    builder.append(targetID);
    return builder.toString();
  }
}
