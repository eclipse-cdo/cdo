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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.lm.reviews.util.ModelReferenceExtractorRegistry;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.PrioritizingElementList.Prioritized;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.FSMUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 1.2
 */
public final class ModelReference
{
  public static final char SEPARATOR = ':';

  private static final List<String> NO_PROPERTIES = Collections.emptyList();

  private final String data;

  public ModelReference(String data)
  {
    CheckUtil.checkArg(!StringUtil.isEmpty(data), "data");
    this.data = data;

  }

  public ModelReference(String type, List<String> properties)
  {
    CheckUtil.checkArg(!StringUtil.isEmpty(type) && type.indexOf(SEPARATOR) == -1, "type");

    StringBuilder builder = new StringBuilder(type);

    if (properties != null)
    {
      for (String property : properties)
      {
        builder.append(SEPARATOR);
        builder.append(StringUtil.escape(property, SEPARATOR));
      }
    }

    data = builder.toString();
  }

  public String getType()
  {
    int pos = data.indexOf(SEPARATOR);
    if (pos == -1)
    {
      return data;
    }

    return data.substring(0, pos);
  }

  public List<String> getProperties()
  {
    int pos = data.indexOf(SEPARATOR);
    if (pos == -1)
    {
      return NO_PROPERTIES;
    }

    List<String> properties = new ArrayList<>();

    for (;;)
    {
      ++pos;

      int nextPos = data.indexOf(SEPARATOR, pos);
      if (nextPos == -1)
      {
        String field = data.substring(pos);
        properties.add(StringUtil.unescape(field, SEPARATOR));
        break;
      }

      String field = data.substring(pos, nextPos);
      properties.add(StringUtil.unescape(field, SEPARATOR));
      pos = nextPos;
    }

    return properties;
  }

  @Override
  public int hashCode()
  {
    return data.hashCode();
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
    return data.equals(other.data);
  }

  @Override
  public String toString()
  {
    return data;
  }

  public static Builder builder(String type)
  {
    return new Builder(type);
  }

  public static Builder builder(String type, String... properties)
  {
    Builder builder = new Builder(type);
    builder.addAll(Arrays.asList(properties));
    return builder;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Builder extends ArrayList<String>
  {
    private static final long serialVersionUID = 1L;

    private final String type;

    public Builder(String type)
    {
      this.type = type;
    }

    public String type()
    {
      return type;
    }

    public Builder property(Object property)
    {
      if (property != null)
      {
        add(property.toString());
      }

      return this;
    }

    public Builder property(CDOID objectID)
    {
      StringBuilder builder = new StringBuilder();
      CDOIDUtil.write(builder, objectID);
      return property(builder.toString());
    }

    public Builder property(EObject object)
    {
      CDOObject cdoObject = CDOUtil.getCDOObject(object);
      if (cdoObject == null || FSMUtil.isTransient(cdoObject))
      {
        return this;
      }

      CDOID objectID = cdoObject.cdoID();
      return property(objectID);
    }

    public ModelReference build()
    {
      return new ModelReference(type, this);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Extractor extends Prioritized
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.lm.reviews.modelReferenceExtractors";

    public ModelReference extractModelReference(Object object);

    /**
     * @author Eike Stepper
     */
    public interface Registry extends Extractor
    {
      public static final Registry INSTANCE = ModelReferenceExtractorRegistry.INSTANCE;

      public Extractor[] getExtractors();
    }
  }
}
