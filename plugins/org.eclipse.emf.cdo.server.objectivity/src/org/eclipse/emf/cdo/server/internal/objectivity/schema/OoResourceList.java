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
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Class_Position;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.ooId;

/***
 * OoResouceList is a specialized ooArrayListId, where all elements are of type Resourc(Node|Folder). The class will
 * allow adding, validating and removing resources From the list.
 * 
 * @author ibrahim
 */
public class OoResourceList
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ooProxy.class);

  static public String className = "ooResourceList";

  static public String Attribute_arrayName = "oo_array";

  private ObjySession objySession;

  protected Class_Object classObject;

  protected ooArrayListId list = null;

  protected ooId objectId;

  private static final EStructuralFeature featureName = EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME;

  // we could use this "EresourcePackage.eINSTANCE.getCDOResourceNode_Name()" instead of above.

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(OoResourceList.className) == null
        && top_mod.resolve_proposed_class(OoResourceList.className) == null)
    {

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooArrayListId. Adding ooArrayListId");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      // Proposed_Class A = new Proposed_Class(ooArrayListId.ClassName);
      Proposed_Class propClass = top_mod.propose_new_class(OoResourceList.className);

      propClass
          .add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, ooBase.ClassName /* "ooObj" */);

      propClass.add_ref_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          OoResourceList.Attribute_arrayName, // Attribute name
          1, // # elements in fixed-size array
          ooArrayListId.className, false); // Default value // Default value

      // top_mod.propose_new_class(A);
      if (!inProcess)
      {
        top_mod.activate_proposals(true, true);
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("SCHEMA changed : ooArrayListId added");
      }
    }

  }

  public OoResourceList(ObjySession objySession, ObjyObject objyObject)
  {
    classObject = objyObject.ooClassObject();
    this.objySession = objySession;
    objectId = objyObject.ooId();
  }

  // public OoResourceList(ObjySession objySession, Class_Object classObject)
  // {
  // this.classObject = classObject;
  // this.objySession = objySession;
  // }

  private ooArrayListId getList()
  {
    if (list != null)
    {
      return list;
    }

    try
    {
      Class_Position position = classObject.position_in_class(OoResourceList.Attribute_arrayName);
      ooId oid = classObject.get_ooId(position);
      if (!oid.isNull())
      {
        list = new ooArrayListId(Class_Object.class_object_from_oid(oid));
      }
    }
    catch (ObjyRuntimeException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return list;
  }

  public void remove(ObjyObject objyObject)
  {
    int size = (int)getList().size();
    for (int i = 0; i < size; i++)
    {
      if (objyObject.ooId().equals(getList().get(i)))
      {
        getList().remove(i);
        break;
      }
    }
  }

  public void add(ObjyObject objyObject)
  {
    // TODO - we need to make sure that objyObject is a resource!!!
    getList().add(objyObject.ooId());
  }

  public void checkDuplicateResources(InternalCDORevision revision) throws IllegalStateException
  {
    // CDOID folderID = (CDOID)revision.data().getContainerID();
    CDOID folderId = (CDOID)revision.data().getContainerID();
    String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);

    // ooId folderId = objyObject.getEContainerAsOid();
    // String name = OoResourceList.getResourceName(objyObject);

    // iterate over all resource in the list, and verify if we have both name and folderID.
    int size = (int)getList().size();
    for (int i = 0; i < size; i++)
    {
      ObjyObject resource = getResource(i);
      CDOID resourceFolderId = (CDOID)resource.getEContainer();
      String resourceName = OoResourceList.getResourceName(resource);
      if (resourceFolderId != null && resourceFolderId.equals(folderId) && resourceName != null
          && resourceName.equals(name))
      {
        throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder: " + folderId); //$NON-NLS-1$
      }
    }
  }

  public ObjyObject getResource(int index)
  {
    return objySession.getObjectManager().getObject(getList().get(index));
  }

  public static String getResourceName(ObjyObject objyObject)
  {
    String name = (String)objyObject.get(featureName);
    return name;
  }

  public static ObjyObject create(ooId nearOid)
  {
    Class_Object classObject = Class_Object.new_persistent_object(ObjySchema.getObjyClass(OoResourceList.className)
        .getASClass(), nearOid, false);
    Class_Position position = classObject.position_in_class(OoResourceList.Attribute_arrayName);
    Class_Object arrayClassObject = Class_Object.new_persistent_object(ObjySchema.getTopModule().resolve_class(
        ooArrayListId.className), classObject.objectID(), false);
    ooId arrayOid = arrayClassObject.objectID();

    classObject.set_ooId(position, arrayClassObject.objectID());
    ooArrayListId.initObject(arrayClassObject);
    ObjyObject objyObject = new ObjyObject(classObject);
    return objyObject;
  }

  public ooId ooId()
  {
    return objectId;
  }

  public int size()
  {
    return (int)getList().size();
  }

}
