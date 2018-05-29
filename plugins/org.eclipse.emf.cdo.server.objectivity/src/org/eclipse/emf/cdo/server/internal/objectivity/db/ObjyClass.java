/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import com.objy.as.app.Class_Position;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Class;

import java.util.HashMap;

/**
 * Wrapper around the AS class to be able to cache attributes.
 *
 * @author ibrahim
 */
public class ObjyClass
{

  protected d_Class asClass;

  protected String asClassName;

  protected HashMap<String, d_Attribute> attributeMap = new HashMap<String, d_Attribute>();

  protected HashMap<String, Class_Position> classPositionMap = new HashMap<String, Class_Position>();

  public ObjyClass(d_Class asClass/* , EClass eClass */)
  {
    this.asClass = asClass;
    if (asClass.namespace_name() != null)
    {
      asClassName = asClass.namespace_name() + ":" + asClass.name();
    }
    else
    {
      asClassName = asClass.name();
    }
  }

  public d_Attribute resolve_attribute(String attribute_name)
  {
    d_Attribute attr = attributeMap.get(attribute_name);
    if (attr == null)
    {
      attr = asClass.resolve_attribute(attribute_name);
      // we might get (attr == null) if the attribute is from a base class.
      // so we'll try to get the attribute through the position.
      if (attr == null)
      {
        Class_Position position = resolve_position(attribute_name);
        if (position != null)
        {
          attr = asClass.attribute_at_position(position);
        }
      }
      attributeMap.put(attribute_name, attr);
    }
    return attr;
  }

  public Class_Position resolve_position(String attribute_name)
  {
    Class_Position attr = classPositionMap.get(attribute_name);
    if (attr == null)
    {
      attr = asClass.position_in_class(attribute_name);
      classPositionMap.put(attribute_name, attr);
    }
    return attr;
  }

  public d_Class getASClass()
  {
    return asClass;
  }

  public String getASClassName()
  {
    return asClassName;
  }

  // public List<Class_Position> getListOfRefAttributes()
  // {
  // List<Class_Position> positions = new ArrayList<Class_Position>();
  //
  // System.out.println(">>> Class: " + asClassName);
  //
  // //
  // @SuppressWarnings("rawtypes")
  // Iterator itr = asClass.attributes_plus_inherited();
  // while (itr.hasNext())
  // {
  // d_Attribute attribute = (d_Attribute)itr.next();
  // if (attribute.is_type() && attribute.type_of() instanceof d_Ref_Type)
  // {
  // d_Class dClass = attribute.class_type_of();
  // if (dClass.name().equals(ObjyFeatureMapArrayList.ClassName))
  // {
  // // we'll need to copy this one.
  // positions.add(resolve_position(attribute.name()));
  // System.out.println("\t attr: " + attribute.name());
  // }
  // else if (dClass.name().equals(ObjyArrayListString.ClassName))
  // {
  // // we'll need to copy this one.
  // positions.add(resolve_position(attribute.name()));
  // System.out.println("\t attr: " + attribute.name());
  // }
  // else if (dClass.name().equals(ObjyArrayListId.className))
  // {
  // // we'll need to copy this one.
  // positions.add(resolve_position(attribute.name()));
  // System.out.println("\t attr: " + attribute.name());
  // }
  // else if (dClass.name().equals(ObjyProxy.className))
  // {
  // // we'll need to copy this one.
  // positions.add(resolve_position(attribute.name()));
  // System.out.println("\t attr: " + attribute.name());
  // }
  // }
  // }
  //
  // return positions;
  // }

}
