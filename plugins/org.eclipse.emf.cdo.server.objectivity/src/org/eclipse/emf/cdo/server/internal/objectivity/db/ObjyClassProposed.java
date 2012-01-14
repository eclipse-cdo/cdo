/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ITypeMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ObjyMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBase;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Class;
import com.objy.as.app.d_Inheritance;
import com.objy.as.app.d_Module;

import java.util.ArrayList;
import java.util.Iterator;

public class ObjyClassProposed
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyClassProposed.class);

  private EClass eClass;

  private d_Module module;

  private boolean onlyStructure = false; // TODO - I'm not sure why we need this TBV.

  public ObjyClassProposed(d_Module module, EClass eClass, boolean onlyStructure)
  {
    this.eClass = eClass;
    this.module = module;
    this.onlyStructure = onlyStructure;

  }

  public void propose()
  {
    String className = ObjySchema.formObjectivityClassName(eClass, onlyStructure);

    Proposed_Class proposedooClass = null;

    {
      proposedooClass = ObjySchema.getTopModule().propose_new_class(className);

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("ECLASS " + eClass.getName() + " -> " + className + " = ADD - START");
      }

      int countIndex = 0;

      for (EClass eSuperObject : eClass.getESuperTypes())
      {
        if (TRACER_DEBUG.isEnabled())
        {
          TRACER_DEBUG.trace("ECLASS " + className + " ADDING SUPER CLASS " + eSuperObject.getName());
        }
        // This is used to only allow one base class with persistence inheritance.
        boolean itrOnlyStructure = countIndex == 0 ? false || onlyStructure : true;

        ObjySchema.createObjyClassSchema(eSuperObject, itrOnlyStructure);

        String superClassName = ObjySchema.formObjectivityClassName(eSuperObject, itrOnlyStructure);
        proposedooClass.add_base_class(com.objy.as.app.d_Module.LAST, // Position
            d_Access_Kind.d_PUBLIC, // Access kind
            superClassName); // Base class name*/
        countIndex++;
      }

      if (eClass.getESuperTypes().size() == 0 && !onlyStructure)
      {
        // this is done in ObjySchema.buildSchema()...
        // ooBaseClass.buildSchema();
        proposedooClass.add_base_class(com.objy.as.app.d_Module.LAST, // Position
            d_Access_Kind.d_PUBLIC, // Access kind
            ObjyBase.CLASS_NAME); // Base class name

      }
    }
    EList<EStructuralFeature> listFeatures = eClass.getEStructuralFeatures();
    ArrayList<EClass> toBeProcessed = new ArrayList<EClass>();
    for (EStructuralFeature feature : listFeatures)
    {
      if (!(feature instanceof EAttribute || feature instanceof EReference) || !EMFUtil.isPersistent(feature))
      {
        continue;
      }

      EClassifier destination = feature.getEType();

      ITypeMapper bridge = ObjyMapper.INSTANCE.getTypeMapper(feature);

      if (bridge == null)
      {
        continue;
      }

      // new field
      bridge.createSchema(proposedooClass, feature);

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("New Attribut for " + eClass.getName() + " name : " + feature.getName());
      }

      if (feature instanceof EReference)
      {
        // ObjySchema.createObjyClassSchema((EClass)destination, false);
        toBeProcessed.add((EClass)destination);
      }
    }

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("ECLASS " + eClass.getName() + " -> " + className + " -- DONE");
    }

    for (EClass classifier : toBeProcessed)
    {
      // ObjySchema.getOrCreate(classifier);
      ObjySchema.createObjyClassSchema(classifier, false);
    }

  }

  public void evolve(d_Class dClass)
  {
    String className = ObjySchema.formObjectivityClassName(eClass, onlyStructure);

    Proposed_Class proposedooClass = null;

    // boolean evolution = false;

    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("ECLASS " + eClass.getName() + " -> " + className + " EVOLVE - START");
      }

      Iterator<?> subClasses = dClass.sub_class_list();
      while (subClasses.hasNext())
      {
        d_Inheritance subClass = (d_Inheritance)subClasses.next();
        module.propose_evolved_class(subClass.inherits_to().name());
      }
      proposedooClass = module.propose_evolved_class(className);
    }

    EList<EStructuralFeature> listFeatures = eClass.getEStructuralFeatures();
    ArrayList<EClass> toBeEvolve = new ArrayList<EClass>();
    for (EStructuralFeature feature : listFeatures)
    {
      if (!(feature instanceof EAttribute || feature instanceof EReference) || !EMFUtil.isPersistent(feature))
      {
        continue;
      }

      EClassifier destination = feature.getEType();

      ITypeMapper bridge = ObjyMapper.INSTANCE.getTypeMapper(feature);

      if (bridge == null)
      {
        continue;
      }

      d_Attribute attr = null;
      attr = dClass.resolve_attribute(feature.getName());

      if (attr == null)
      {
        if (TRACER_DEBUG.isEnabled())
        {
          TRACER_DEBUG.trace("New Attribut for " + eClass.getName() + " name : " + feature.getName());
        }

        // new field
        bridge.createSchema(proposedooClass, feature);
      }
      // TODO - enable the rest of attribute type evolution...
      // else if (bridge.validate(attr, feature))
      // {
      // continue;
      // }
      else
      {
        if (TRACER_DEBUG.isEnabled())
        {
          TRACER_DEBUG.trace("Attribut Changed for " + eClass.getName() + " name : " + feature.getName());
          TRACER_DEBUG.trace("... attribute change is not supported in this release... for " + eClass.getName()
              + " name : " + feature.getName());
        }

        // TODO - enable the rest of attribute type evolution...
        // bridge.modifySchema(proposedooClass, feature);
      }

      if (feature instanceof EReference)
      {
        // String destinationClassName = getObjectivityClass(destination);
        // d_Class dClass = module.resolve_class(destinationClassName);
        toBeEvolve.add((EClass)destination);
      }
    }

    TRACER_DEBUG.trace("evolve ECLASS " + eClass.getName() + " -> " + className + " -- DONE");

    for (EClass classifier : toBeEvolve)
    {
      String localName = ObjySchema.formObjectivityClassName(classifier, false);
      d_Class localDClass = ObjySchema.getTopModule().resolve_class(localName);
      if (localDClass == null)
      {
        ObjySchema.createObjyClassSchema(classifier, false);
      }
      else
      {
        ObjySchema.evolveObjyClassSchema(classifier, false);
      }
    }
  }
}
