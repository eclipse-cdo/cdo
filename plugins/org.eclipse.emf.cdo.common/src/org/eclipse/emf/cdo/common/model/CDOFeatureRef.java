/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * A symbolic reference to an {@link EStructuralFeature}, identified by its
 * declaring classifier and feature name.
 *
 * @author Eike Stepper
 * @since 4.29
 */
public final class CDOFeatureRef implements CDOModelElementRef<EStructuralFeature>
{
  private static final long serialVersionUID = 1L;

  private final CDOClassifierRef classifierRef;

  private final String featureName;

  /**
   * Creates a reference using the feature's declaring class. In particular,
   * inherited features are identified by {@link EStructuralFeature#getEContainingClass()}.
   *
   * @param feature the feature to reference
   */
  public CDOFeatureRef(EStructuralFeature feature)
  {
    this(feature == null ? null : new CDOClassifierRef(feature.getEContainingClass()), feature == null ? null : feature.getName());
  }

  /**
   * Creates a reference from a symbolic declaring classifier and feature name.
   *
   * @param classifierRef the symbolic declaring classifier
   * @param featureName the feature name
   */
  public CDOFeatureRef(CDOClassifierRef classifierRef, String featureName)
  {
    if (classifierRef == null)
    {
      throw new IllegalArgumentException("Classifier reference must not be null"); //$NON-NLS-1$
    }

    if (featureName == null)
    {
      throw new IllegalArgumentException("Feature name must not be null"); //$NON-NLS-1$
    }

    this.classifierRef = classifierRef;
    this.featureName = featureName;
  }

  /**
   * Reads a symbolic feature reference without resolving its classifier.
   *
   * @param in the data input
   * @throws IOException if the reference cannot be read
   */
  public CDOFeatureRef(CDODataInput in) throws IOException
  {
    this(new CDOClassifierRef(in), in.readString());
  }

  /**
   * Writes the classifier reference followed by the feature name.
   *
   * @param out the data output
   * @throws IOException if the reference cannot be written
   */
  public void write(CDODataOutput out) throws IOException
  {
    classifierRef.write(out);
    out.writeString(featureName);
  }

  /**
   * Returns the symbolic declaring classifier.
   *
   * @return the declaring classifier reference
   */
  public CDOClassifierRef getClassifierRef()
  {
    return classifierRef;
  }

  /**
   * Returns the symbolic feature name.
   *
   * @return the feature name
   */
  public String getFeatureName()
  {
    return featureName;
  }

  @Override
  public EStructuralFeature resolve(EPackage.Registry packageRegistry)
  {
    EClassifier classifier = classifierRef.resolve(packageRegistry);
    return classifier instanceof EClass ? resolve((EClass)classifier) : null;
  }

  /**
   * Resolves this reference against a declaring class. An inherited feature is
   * not accepted when the supplied class is not its declaring class.
   *
   * @param eClass the explicitly resolved declaring class
   * @return the declared feature, or {@code null} if it is absent
   */
  public EStructuralFeature resolve(EClass eClass)
  {
    if (eClass == null)
    {
      return null;
    }

    EStructuralFeature feature = eClass.getEStructuralFeature(featureName);
    return feature != null && feature.getEContainingClass() == eClass ? feature : null;
  }

  @Override
  public int hashCode()
  {
    return classifierRef.hashCode() ^ featureName.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == CDOFeatureRef.class)
    {
      CDOFeatureRef that = (CDOFeatureRef)obj;
      return classifierRef.equals(that.classifierRef) && featureName.equals(that.featureName);
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOFeatureRef[{0}, {1}]", classifierRef, featureName); //$NON-NLS-1$
  }
}
