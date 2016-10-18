/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStoreAccessor;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObjectManager;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooTreeListX;

/***
 * OoResouceList is a specialized ooArrayListId, where all elements are of type Resourc(Node|Folder). The class will
 * allow adding, validating and removing resources From the list.
 *
 * @author ibrahim
 */
public class ObjyResourceList
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyProxy.class);

  static public String className = "ooResourceList";

  static public String Attribute_arrayName = "oo_array";

  private ObjySession objySession;

  protected Class_Object classObject;

  protected ObjyArrayListId list = null;

  protected ooId objectId;

  private static final EStructuralFeature featureName = EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME;

  // we could use this "EresourcePackage.eINSTANCE.getCDOResourceNode_Name()" instead of above.

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(ObjyResourceList.className) == null && top_mod.resolve_proposed_class(ObjyResourceList.className) == null)
    {

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooArrayListId. Adding ooArrayListId");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      // Proposed_Class A = new Proposed_Class(ooArrayListId.ClassName);
      Proposed_Class propClass = top_mod.propose_new_class(ObjyResourceList.className);

      propClass.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, ObjyBase.CLASS_NAME /* "ooObj" */);

      propClass.add_ref_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          ObjyResourceList.Attribute_arrayName, // Attribute name
          1, // # elements in fixed-size array
          ObjyArrayListId.className, false); // Default value // Default value

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

  public ObjyResourceList(ObjySession objySession, ObjyObject objyObject)
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

  private ObjyArrayListId getList()
  {
    if (list != null)
    {
      return list;
    }

    try
    {
      // Class_Position position = classObject.position_in_class(ObjyResourceList.Attribute_arrayName);
      ooId oid = classObject.nget_ooId(ObjyResourceList.Attribute_arrayName);
      if (!oid.isNull())
      {
        list = new ObjyArrayListId(Class_Object.class_object_from_oid(oid));
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

  // public void checkDuplicateResources(ObjectivityStoreAccessor storeAccessor, CDORevision revision)
  // throws IllegalStateException
  // {
  // CDOID folderID = (CDOID)revision.data().getContainerID();
  // String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);
  // CDOID existingID = storeAccessor.readResourceID(folderID, name, revision.getBranch().getHead());
  // if (existingID != CDOID.NULL && !existingID.equals(revision.getID()))
  // {
  // throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder " + folderID); //$NON-NLS-1$
  // //$NON-NLS-2$
  // }
  // }

  public void checkDuplicateResources(ObjectivityStoreAccessor storeAccessor, InternalCDORevision revision) throws IllegalStateException
  {
    // CDOID folderID = (CDOID)revision.data().getContainerID();
    CDOID folderId = (CDOID)revision.data().getContainerID();
    String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);

    // ooId folderId = objyObject.getEContainerAsOid();
    // String name = OoResourceList.getResourceName(objyObject);

    // iterate over all resource in the list, and verify if we have both name and folderID.
    int size = (int)getList().size();
    ObjyObjectManager objyObjectManager = storeAccessor.getObjySession().getObjectManager();
    for (int i = 0; i < size; i++)
    {
      ObjyObject resource = getResource(i);
      ObjyObject resourceRevision = resource;
      // get the proper revision of the resource (might need to refactor this code, see readRevision())
      if (storeAccessor.getStore().isRequiredToSupportBranches())
      {
        try
        {
          resourceRevision = resource.getRevision(revision.getTimeStamp(), revision.getBranch().getID(), objyObjectManager);
        }
        catch (RuntimeException ex)
        {
          ex.printStackTrace();
        }
      }
      else if (storeAccessor.getStore().isRequiredToSupportAudits())
      {
        try
        {
          resourceRevision = resource.getRevision(revision.getTimeStamp(), CDOBranch.MAIN_BRANCH_ID, objyObjectManager);
        }
        catch (RuntimeException ex)
        {
          ex.printStackTrace();
        }
      }

      if (resourceRevision == null || resourceRevision.getVersion() < 0)
      {
        continue;
      }

      // int v = resource.getVersion();
      CDOID resourceFolderId = (CDOID)resourceRevision.getEContainer();
      String resourceName = ObjyResourceList.getResourceName(resourceRevision);
      if (resourceFolderId != null && resourceFolderId.equals(folderId) && resourceName != null && resourceName.equals(name))
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
    Class_Object classObject = Class_Object.new_persistent_object(ObjySchema.getObjyClass(ObjyResourceList.className).getASClass(), nearOid, false);
    // ObjyObjectManager.newInternalObjCount++;
    // Class_Position position = classObject.position_in_class(ObjyResourceList.Attribute_arrayName);
    // Class_Object arrayClassObject = Class_Object.new_persistent_object(
    // ObjySchema.getTopModule().resolve_class(ObjyArrayListId.className), classObject.objectID(), false);
    // // ooId arrayOid = arrayClassObject.objectID();
    ooTreeListX list = new ooTreeListX(10, false);
    // ObjyObjectManager.newInternalObjCount++;
    ooObj anObj = ooObj.create_ooObj(classObject.objectID());
    anObj.cluster(list);
    // System.out.println("initObject: " + anObj.getOid().getStoreString() + " treeListX: "
    // // + list.getOid().getStoreString());
    classObject.nset_ooId(ObjyResourceList.Attribute_arrayName, list.getOid());

    // classObject.set_ooId(position, arrayClassObject.objectID());
    // ObjyArrayListId.initObject(arrayClassObject);
    ObjyObject objyObject = null;
    try
    {
      objyObject = new ObjyObject(classObject);
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
    }

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
