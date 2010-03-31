/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.server.internal.objectivity.schema.OoProperty;

import com.objy.db.app.Session;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class OoPropertyMapHandler
{
  protected ooId propertyMapId;

  public OoPropertyMapHandler(ooId oid)
  {
    propertyMapId = oid;
  }

  /***
   * Factory method to create the PropertyMap, which is an ooMap
   */
  public static ooId create(ooId scopeContOid)
  {
    ooMap map = new ooMap();
    ooObj clusterObject = ooObj.create_ooObj(scopeContOid);
    clusterObject.cluster(map);
    return map.getOid();
  }

  /***
   * This function assume we are in an Objy trnasaction.
   */
  public void setPropertyValues(Map<String, String> properties)
  {
    // get the map.
    ooMap propertyMap = getMap();
    String key = null;
    String value = null;
    OoProperty property = null;
    for (Entry<String, String> entry : properties.entrySet())
    {
      key = entry.getKey();
      value = entry.getValue();

      // check if we have the property
      if (propertyMap.isMember(key))
      {
        property = (OoProperty)propertyMap.lookup(key);
        property.setValue(value);
      }
      else
      {
        property = new OoProperty(key, value);
        propertyMap.add(property, key);
      }
    }
  }

  /***
   * This function assume we are in an Objy trnasaction.
   */
  public void removePropertyValues(Set<String> names)
  {
    // get the map.
    ooMap propertyMap = getMap();
    OoProperty property = null;
    for (String key : names)
    {
      if (propertyMap.isMember(key))
      {
        property = (OoProperty)propertyMap.lookup(key);
        // although removing the object will remove it from the map
        // it's cleaner to do it explicitly.
        propertyMap.remove(key);
        property.delete();
      }
    }
  }

  /***
   * This function assume we are in an Objy trnasaction.
   */
  public Map<String, String> getPropertyValues(Set<String> names)
  {
    Map<String, String> properties = new HashMap<String, String>();
    // get the map.
    ooMap propertyMap = getMap();
    OoProperty property = null;

    for (String key : names)
    {
      if (propertyMap.isMember(key))
      {
        property = (OoProperty)propertyMap.lookup(key);
        properties.put(property.getKey(), property.getValue());
      }
    }
    return properties;
  }

  /***
   * This function assume we are in an Objy trnasaction.
   */
  private ooMap getMap()
  {
    ooMap map = null;
    map = (ooMap)Session.getCurrent().getFD().objectFrom(propertyMapId);
    return map;
  }
}
