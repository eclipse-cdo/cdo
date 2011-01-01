/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring comments for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.mapper;

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Class_Position;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.db.app.ooId;

/**
 * @author Simon McDuff
 */
// 100202:IS - this is leftover from the refactoring... verify if we need it.
@Deprecated
public class SingleContainementReferenceMapper extends BasicTypeMapper implements ISingleTypeMapper
{

  public boolean createSchema(Proposed_Class proposedClasses, EStructuralFeature feature)
  {
    EClassifier destination = feature.getEType();

    String destinationClassName = ObjySchema.getObjectivityClassName(destination);
    // Containment relationship
    // We do not detect recursive embedded relationship
    proposedClasses.add_embedded_class_attribute(com.objy.as.app.d_Module.LAST, // Access kind
        d_Access_Kind.d_PUBLIC, // Access kind
        feature.getName(), // Attribute name
        1, // # elements in fixed-size array
        destinationClassName); // Default value
    return false;
  }

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    Class_Position position = getAttributePosition(objyObject, feature);

    // TODO Auto-generated method stub
    ooId id2 = objyObject.get_ooId(position);
    if (id2 == null || id2.isNull())
    {
      return null;
    }
    Class_Object childObject = objyObject.get_class_obj(position);
    return childObject;
  }

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    Class_Position position = getAttributePosition(objyObject, feature);
    objyObject.set_ooId(position, (ooId)newValue);
  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void delete(ObjyObject class_Object, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public Object remove(ObjyObject objyObject, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

}
