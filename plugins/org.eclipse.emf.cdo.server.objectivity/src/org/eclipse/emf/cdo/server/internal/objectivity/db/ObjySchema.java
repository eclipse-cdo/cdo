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

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStore;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ITypeMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ObjyMapper;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyArrayListId;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyArrayListString;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBase;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyFeatureMapArrayList;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyProxy;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyResourceList;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Class;
import com.objy.as.app.d_Module;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for the AS schema code with caching of the wrapped classes. This class need to be reseted by the
 * ObjectivityStore doDeactivate().
 * 
 * @author ibrahim
 */
public class ObjySchema
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjySchema.class);

  // static HashMap<String, d_Class> mapOfCacheClasses = new HashMap<String, d_Class>();

  private static HashMap<String, ObjyClass> mapOfObjyClasses = new HashMap<String, ObjyClass>();

  private static HashMap<String, EClass> mapOfEClasses = new HashMap<String, EClass>();

  private static HashMap<EClass, EClass> visitedClasses = new HashMap<EClass, EClass>();

  private static HashMap<EClass, EClass> visitedStructureOnlyClasses = new HashMap<EClass, EClass>();

  private static Map<String, String> packageNameMapping = new HashMap<String, String>();

  private static d_Module topModule = null;

  public static void resetCache()
  {
    topModule = null;
    mapOfObjyClasses.clear();
    mapOfEClasses.clear();
    visitedClasses.clear();
    visitedStructureOnlyClasses.clear();
  }

  public static ObjyClass getObjyClass(String name)
  {
    ObjyClass classObject = mapOfObjyClasses.get(name);
    if (classObject == null)
    {
      d_Class newClass = getTopModule().resolve_class(name);
      // EClass eClass = getEClass(store, name);
      classObject = new ObjyClass(newClass);
      mapOfObjyClasses.put(name, classObject);
    }
    return classObject;
  }

  public static d_Module getTopModule()
  {
    if (topModule == null)
    {
      topModule = d_Module.top_level();
    }
    return topModule;
  }

  /**
   * Originally in EProposedManager.
   */
  static public String getObjectivityClassName(EClassifier eClassifier)
  {
    return formObjectivityClassName(eClassifier, false);
  }

  /**
   * Originally in EProposedManager
   */
  static String formObjectivityClassName(EClassifier eClassifier, boolean onlyStructure)
  {
    if (eClassifier == EcorePackage.eINSTANCE.getEObject())
    {
      return "ooObj";
    }

    // same class names might exist in different nsUri.
    String nsURI = eClassifier.getEPackage().getNsURI();
    // // get the hash string for uniqueness.
    // String nsURIHash = new Integer(Math.abs(nsURI.hashCode())).toString();
    String objyPackageName = getObjyPackageName(nsURI);

    if (onlyStructure)
    {
      // return "oo_" + eClassifier.getEPackage().getName() + "_" + eClassifier.getName() + "ST";
      // return "oo_" + nsURIHash + "_" + eClassifier.getEPackage().getNsPrefix() + "_" + eClassifier.getName() + "_ST";
      return objyPackageName + ":" + eClassifier.getName() + "_ST";
    }

    // return "oo_" + eClassifier.getEPackage().getName() + "_" + eClassifier.getName();
    // return "oo_" + nsURIHash + "_" + eClassifier.getEPackage().getNsPrefix() + "_" + eClassifier.getName();
    return objyPackageName + ":" + eClassifier.getName();
  }

  static public void setPackageNameMapping(String name1, String name2)
  {
    if (packageNameMapping.get(name1) == null)
    {
      packageNameMapping.put(name1, name2);
    }
  }

  static public String getPackageNameMapping(String key)
  {
    return packageNameMapping.get(key);
  }

  /**
   * Originally in EProposedManager
   * 
   * @param ePackage
   */
  static public void registerEPackage(EPackage ePackage)
  {
    for (EClassifier eClass : ePackage.getEClassifiers())
    {
      if (eClass instanceof EClass)
      {
        getOrCreate(eClass.eClass());
      }
    }
  }

  /**
   * @param eClass
   * @return ObjyClass
   */
  static public ObjyClass getOrCreate(EClass eClass)
  {
    String className = getObjectivityClassName(eClass);

    ObjyClass objyClass = mapOfObjyClasses.get(className);

    if (objyClass != null)
    {
      return objyClass;
    }

    // create the ObjyClass and hash it.
    synchronized (getTopModule())
    {
      // System.out.println("OBJY: finding class '" + className + "' in objy schema.");
      d_Class dClass = getTopModule().resolve_class(className);
      // System.out.println("OBJY:... got d_Class:" + dClass);

      // TODO - evolving classes is partially implemented, only adding attributes is
      // supported.
      if (dClass == null)
      {
        objyClass = createObjyClass(eClass);
      }
      else if (!isSameClass(dClass, eClass))
      {
        objyClass = evolveObjyClass(eClass);
      }
      else
      {
        objyClass = new ObjyClass(dClass/* , eClass */);
      }

      if (objyClass == null)
      {
        throw new RuntimeException("Cannot retrieved " + eClass.getName() + " class from Objy schema as:" + className);
      }
      String asClassName = objyClass.getASClassName();
      mapOfObjyClasses.put(asClassName, objyClass);
      mapOfEClasses.put(asClassName, eClass);
    }

    return objyClass;
  }

  /**
   * @param eClass
   * @return
   */
  private static ObjyClass createObjyClass(EClass eClass)
  {
    try
    {

      String className = getObjectivityClassName(eClass);

      // System.out.println("OBJY: calling createObjyClassSchema for class: " + className);
      createObjyClassSchema(eClass, false);

      getTopModule().activate_proposals(true, true);
      // getTopModule().activate_proposals(true);

      // System.out.println("OBJY: resolving class '" + className + "' in objy schema.");
      d_Class dClass = getTopModule().resolve_class(className);
      ObjyClass objyClass = new ObjyClass(dClass/* , eClass */);
      return objyClass;
    }
    catch (Throwable throwable)
    {
      throwable.printStackTrace();
    }
    return null;
  }

  public static ObjyClass evolveObjyClass(EClass eClass)
  {
    try
    {

      String className = getObjectivityClassName(eClass);

      evolveObjyClassSchema(eClass, false);

      getTopModule().activate_proposals(true, true);
      // getTopModule().activate_proposals(true);

      // System.out.println("OBJY: resolving class '" + className + "' in objy schema.");
      d_Class dClass = getTopModule().resolve_class(className);
      ObjyClass objyClass = new ObjyClass(dClass/* , eClass */);
      return objyClass;
    }
    catch (Throwable throwable)
    {
      throwable.printStackTrace();
    }
    return null;
  }

  /**
   * This function creates the schema in Objectivity, if the class is being proposed or already exist no action is
   * happening.
   */
  static void createObjyClassSchema(EClass eClass, boolean onlyStructure)
  {
    HashMap<EClass, EClass> hashMap = onlyStructure ? visitedStructureOnlyClasses : visitedClasses;
    if (hashMap.containsKey(eClass))
    {
      return;
    }

    hashMap.put(eClass, eClass);
    String className = formObjectivityClassName(eClass, onlyStructure);
    d_Class dClass = getTopModule().resolve_class(className);

    if (dClass != null)
    {
      return;
    }

    // check if the class has been proposed before
    if (getTopModule().resolve_proposed_class(className) == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Creating new class: " + className);
      }

      // start schema creation.
      // System.out.println("OBJY: starting schema creation for class: " + className);
      ObjyClassProposed proposedClass = new ObjyClassProposed(getTopModule(), eClass, onlyStructure);
      proposedClass.propose();
    }
  }

  /**
   * @param eClass
   * @param itrOnlyStructure
   */
  static void evolveObjyClassSchema(EClass eClass, boolean onlyStructure)
  {
    String className = formObjectivityClassName(eClass, onlyStructure);

    // check if the class has been proposed before
    if (getTopModule().resolve_proposed_class(className) == null)
    {
      d_Class dClass = getTopModule().resolve_class(className);

      TRACER_DEBUG.trace("Evolving class: " + className);

      // start schema evolution.
      // System.out.println("OBJY: starting schema creation for class: " + className);
      ObjyClassProposed proposedClass = new ObjyClassProposed(getTopModule(), eClass, onlyStructure);
      proposedClass.evolve(dClass);
    }
  }

  // From EProposedManager...
  // For now check only the name of the attribute
  // It only check from EMF to Objectivity... not the reverse
  // TODO - see if we can do full cycle schema changes.
  static boolean isSameClass(d_Class dClass, EClass eClass)
  {
    // Look at the hierarchy
    for (EClass superType : eClass.getESuperTypes())
    {
      getOrCreate(superType);
    }

    for (EStructuralFeature feature : eClass.getEStructuralFeatures())
    {
      if (!(feature instanceof EAttribute || feature instanceof EReference))
      {
        continue;
      }
      ITypeMapper mapper = ObjyMapper.INSTANCE.getTypeMapper(feature);
      if (mapper == null)
      {
        continue;
      }

      // identify any missing attribute.
      d_Attribute dAttr = dClass.resolve_attribute(feature.getName());
      if (dAttr == null)
      {
        return false;
      }
      /****
       * TODO - actiavte this code, once ITypeMapper.validate() is implemented. ITypeMapper attributeMapper =
       * ObjyMapper.INSTANCE.getTypeMapper(feature); if (attributeMapper.validate(dAttr, feature) == false) { if
       * (TRACER_DEBUG.isEnabled()) { TRACER_DEBUG.trace("Feature " + feature.getName() + " for object " +
       * eClass.getName() + " changed "); } attributeMapper.validate(dAttr, feature); return false; }
       ****/
    }
    return true;
  }

  // /**
  // * From EProposedManager.
  // */
  // void ensureEClassExist(EClass eClass, boolean onlyStructure)
  // {
  // HashMap<EClass, EClass> hashMap = onlyStructure ? visitedStructureOnlyClass : visitedClass;
  // if (hashMap.containsKey(eClass))
  // {
  // return;
  // }
  //
  // hashMap.put(eClass, eClass);
  // String nameClass = getObjectivityClassName(eClass, onlyStructure);
  // d_Class ooClass = getTopModule().resolve_class(nameClass);
  //
  // if (ooClass != null && isSameClass(ooClass, eClass))
  // {
  // return;
  // }
  //
  // if (module.resolve_proposed_class(nameClass) == null)
  // {
  // EProposedClass proposedClass = new EProposedClass(module, eClass, onlyStructure);
  // proposedClass.propose(this, ooClass);
  // }
  // }

  // /**
  // * From EProposedManager.
  // */
  // void ensureEClassExist(EClass eClass)
  // {
  // ensureEClassExist(eClass, false);
  // }

  public static EClass getEClass(ObjectivityStore store, ObjyClass objyClass)
  {
    String className = objyClass.getASClassName();
    return getEClass(store, className);
  }

  public static EClass getEClass(ObjectivityStore store, String className)
  {
    // String className = objyObject.getASClass().name();
    // System.out.println("OBJY: getEClass(store, " + className +")");
    EClass eClass = mapOfEClasses.get(className);
    if (eClass == null)
    {
      // the format is "<some_URI_name_used_as_package_name>:className"
      String[] splits = className.split(":");
      // get the mapping to the nsURI.
      CDOPackageRegistry registry = store.getRepository().getPackageRegistry();
      String nsURI = getPackageNameMapping(splits[0]);
      EPackage packageObject = registry.getEPackage(nsURI);

      if (packageObject == null)
      {
        throw new RuntimeException("Package not found " + splits[1] + " for class name " + className);
      }
      eClass = (EClass)packageObject.getEClassifier(splits[splits.length - 1]);
      mapOfEClasses.put(className, eClass);
    }
    // else
    // {
    // System.out.println("***OBJY: getEClass(cached): " + eClass);
    // }
    return eClass;
  }

  /***
   * identify if the class is of type Resource. TODO - why we need to pass the store, can't we keep the info we need for
   * the package mapping here?!!!
   */
  public static boolean isResource(ObjectivityStore store, ObjyClass objyClass)
  {
    EClass eClass = getEClass(store, objyClass);

    if (eClass == EresourcePackage.Literals.CDO_RESOURCE || eClass == EresourcePackage.Literals.CDO_RESOURCE_NODE
        || eClass == EresourcePackage.Literals.CDO_RESOURCE_FOLDER)
    {
      return true;
    }

    return false;
  }

  /***
   * Build initial schema for some collection classes.
   */
  public static void createBaseSchema()
  {
    ObjyArrayListId.buildSchema();
    ObjyFeatureMapArrayList.buildSchema();
    ObjyProxy.buildSchema();
    ObjyArrayListString.buildSchema();
    ObjyBase.buildSchema();
    ObjyResourceList.buildSchema();
  }

  public static String getObjyPackageName(String packageURI)
  {
    String name = "";
    boolean first = true;
    // parse the URI, remove "http://" and replace each "." with "_"
    String[] splits = packageURI.split("://");
    for (String strValue : splits)
    {
      if (strValue.equals("http"))
      {
        continue;
      }
      if (!first)
      {
        name = name.concat("_");
      }
      else
      {
        first = false;
      }

      name = name.concat(strValue);
    }
    name = name.replace("/", ".");
    name = name.replace(".", "_");
    return name;
  }

}
