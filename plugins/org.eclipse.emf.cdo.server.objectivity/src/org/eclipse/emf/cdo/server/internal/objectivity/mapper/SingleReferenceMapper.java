/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.mapper;

import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyProxy;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.TypeConvert;

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
// TODO - we ignore the boolean value for null references....
public class SingleReferenceMapper extends BasicTypeMapper implements ISingleTypeMapper
{
  static public SingleReferenceMapper INSTANCE = new SingleReferenceMapper();

  public boolean createSchema(Proposed_Class proposedClasses, EStructuralFeature feature)
  {
    EClassifier destination = feature.getEType();

    String destinationClassName = ObjySchema.getObjectivityClassName(destination);

    proposedClasses.add_ref_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        feature.getName(), // Attribute name
        1, // # elements in fixed-size array
        destinationClassName, // Type of numeric data
        false); // Short reference
    return false;
  }

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    /***
     * ooId id2 = internal.ooClassObject().get_ooId(position); if (id2 == null || id2.isNull()) { return null; }
     ***/
    Class_Position position = getAttributePosition(objyObject, feature);

    return getValue(objyObject, position);
  }

  // called by ObjyObject to get some resource elements.
  public Object getValue(ObjyObject objyObject, Class_Position position)
  {
    ooId childObject = objyObject.get_ooId(position);

    if (!childObject.isNull())
    {
      // check for external reference.
      // TODO -
      // we can optimize this by making sure the object is flagged by having external
      // references, and only do the following if true.
      // System.out.println("-->> IS: getting Class_Object from OID: " + childObject.getStoreString());
      Class_Object refClassObject = Class_Object.class_object_from_oid(childObject);

      if (refClassObject.type_of().name().equals(ObjyProxy.className))
      {
        ObjyProxy proxyObject = new ObjyProxy(refClassObject);
        return OBJYCDOIDUtil.createCDIDExternal(proxyObject);
      }
    }
    // convert to CDOID, revese of the setValue()
    return OBJYCDOIDUtil.getCDOID(childObject);
  }

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    Class_Position position = getAttributePosition(objyObject, feature);
    setValue(objyObject, position, newValue);
  }

  // called by ObjyObject to set resource elements.
  public void setValue(ObjyObject objyObject, Class_Position position, Object newValue)
  {
    ooId ooid = null;

    if (newValue instanceof CDOIDExternal)
    {
      // create an ObjyProxy object to hold the the value.
      ObjyProxy proxyObject = ObjyProxy.createObject(objyObject.ooId());
      proxyObject.setUri(((CDOIDExternal)newValue).getURI());
      ooid = proxyObject.ooId();
    }
    else
    {
      ooid = TypeConvert.toOoId(newValue);
    }

    objyObject.set_ooId(position, ooid);
  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    System.out.println(">>>OBJYIMPL: SingleRefernceMapper.validate() - not implemented.");
    return true;
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    Class_Position position = getAttributePosition(objyObject, feature);
    ooId childOid = objyObject.get_ooId(position);
    if (!childOid.isNull())
    {
      // This is a single reference, so we shouldn't be deleting the reference
      // object, just set the reference to NULL.
      // ooObj childObject = ooObj.create_ooObj(childOid);
      // childObject.delete();
      setValue(objyObject, feature, null);
    }
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    // TODO Implement the initialization, if any....
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
