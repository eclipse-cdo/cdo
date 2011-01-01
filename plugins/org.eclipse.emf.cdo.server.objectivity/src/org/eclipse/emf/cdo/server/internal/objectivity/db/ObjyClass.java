/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
    asClassName = asClass.name();
  }

  public d_Attribute resolve_attribute(String attribute_name)
  {
    d_Attribute attr = attributeMap.get(attribute_name);
    if (attr == null)
    {
      attr = asClass.resolve_attribute(attribute_name);
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

}
