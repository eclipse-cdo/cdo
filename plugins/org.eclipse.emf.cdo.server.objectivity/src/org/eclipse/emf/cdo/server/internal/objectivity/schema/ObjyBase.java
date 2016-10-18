/*
 * Copyright (c) 2010-2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.as.app.Proposed_Class;
import com.objy.as.app.Rel_Copy;
import com.objy.as.app.Rel_Propagation;
import com.objy.as.app.Rel_Versioning;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.as.app.ooBaseType;

/**
 * EMF Classes in Objectivity are enhanced with this base class. This class is use for the revision data as a base for
 * other classes created.
 *
 * @author ibrahim
 */
public class ObjyBase
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyBase.class);

  public static final String CLASS_NAME = "ObjyBase";

  public static final String ATT_CONTAINERID = "oo_containerId";

  public static final String ATT_CONTAINER_FEATUERID = "oo_containerFeatureId";

  public static final String ATT_RESOURCEID = "oo_resourceId";

  public static final String ATT_VERSION = "oo_version";

  public static final String ATT_REVISED_TIME = "oo_revisedTime";

  public static final String ATT_CREATION_TIME = "oo_creationTime";

  public static final String ATT_REVISIONS = "oo_revisions";

  public static final String ATT_BRANCHID = "oo_branchId";

  public static final String ATT_BASE = "oo_base";

  public static final String ATT_LAST_REVISION = "oo_lastRevision";

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(ObjyBase.CLASS_NAME) == null && top_mod.resolve_proposed_class(ObjyBase.CLASS_NAME) == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooBase. Adding it.");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      Proposed_Class propClass = top_mod.propose_new_class(ObjyBase.CLASS_NAME);

      propClass.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      propClass.add_bidirectional_relationship(d_Module.LAST, d_Access_Kind.d_PUBLIC, ObjyBase.ATT_REVISIONS, ObjyBase.CLASS_NAME, false, false, true,
          Rel_Copy.DELETE, Rel_Versioning.COPY, Rel_Propagation.LOCK_YES_DELETE_YES, ObjyBase.ATT_BASE, false);

      // propClass.add_bidirectional_relationship(position, visibility,
      // name, destinationClassName, isInline, isShort, isToMany,
      // copyMode, versioning, propagation, inverseName, inverseIsToMany)

      propClass.add_bidirectional_relationship(d_Module.LAST, d_Access_Kind.d_PUBLIC, ObjyBase.ATT_BASE, ObjyBase.CLASS_NAME, false, false, false,
          Rel_Copy.DELETE, Rel_Versioning.COPY, Rel_Propagation.LOCK_YES_DELETE_YES, ObjyBase.ATT_REVISIONS, true);

      propClass.add_unidirectional_relationship(d_Module.LAST, d_Access_Kind.d_PUBLIC, ObjyBase.ATT_LAST_REVISION, ObjyBase.CLASS_NAME, true, false, false,
          Rel_Copy.DELETE, Rel_Versioning.COPY, Rel_Propagation.LOCK_YES_DELETE_YES);

      propClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyBase.ATT_CONTAINER_FEATUERID, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT32 // Type of numeric data
      ); // Default value

      propClass.add_ref_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          ObjyBase.ATT_CONTAINERID, // Attribute name
          1, // # elements in fixed-size array
          "ooObj", false); // Default value // Default value

      propClass.add_ref_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          ObjyBase.ATT_RESOURCEID, // Attribute name
          1, // # elements in fixed-size array
          "ooObj", false); // Default value // Default value

      propClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyBase.ATT_VERSION, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT32 // Type of numeric data
      ); // Default value

      propClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyBase.ATT_BRANCHID, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT32 // Type of numeric data
      ); // Default value

      propClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyBase.ATT_CREATION_TIME, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT64 // Type of numeric data
      ); // Default value

      propClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyBase.ATT_REVISED_TIME, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT64 // Type of numeric data
      ); // Default value

      if (!inProcess)
      {
        top_mod.activate_proposals(true, true);
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("SCHEMA changed : ooBaseClass added");
      }
    }

  }

}
