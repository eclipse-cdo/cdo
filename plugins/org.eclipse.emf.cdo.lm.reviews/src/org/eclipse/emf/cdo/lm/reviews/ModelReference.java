/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import org.eclipse.net4j.util.StringUtil;

import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 1.2
 */
public final class ModelReference
{
  private static final char SEPARATOR = '.';

  private final CDOID objectID;

  private final String featureName;

  public ModelReference(CDOID objectID, String featureName)
  {
    this.objectID = objectID;
    this.featureName = featureName;
  }

  public ModelReference(CDOID objectID)
  {
    this(objectID, null);
  }

  public ModelReference(String str)
  {
    if (StringUtil.isEmpty(str))
    {
      objectID = null;
      featureName = null;
    }
    else
    {
      int sep = str.indexOf(SEPARATOR);
      if (sep != -1)
      {
        objectID = CDOIDUtil.read(str.substring(sep + 1));
        featureName = str.substring(0, sep);
      }
      else
      {
        objectID = CDOIDUtil.read(str);
        featureName = null;
      }
    }
  }

  public CDOID getObjectID()
  {
    return objectID;
  }

  public String getFeatureName()
  {
    return featureName;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(objectID, featureName);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    ModelReference other = (ModelReference)obj;
    return objectID == other.objectID && Objects.equals(featureName, other.featureName);
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();

    if (featureName != null)
    {
      builder.append(featureName);
      builder.append(SEPARATOR);
    }

    CDOIDUtil.write(builder, objectID);
    return builder.toString();
  }
}
