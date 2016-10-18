/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.HashMap;

public class ObjyMapper
{
  public static ObjyMapper INSTANCE = new ObjyMapper();

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyMapper.class);

  protected HashMap<EClassifier, ITypeMapper> typeMap = new HashMap<EClassifier, ITypeMapper>();

  protected HashMap<EClassifier, ITypeMapper> manyTypeMap = new HashMap<EClassifier, ITypeMapper>();

  private ITypeMapper manyRef = ManyReferenceMapper.INSTANCE;

  // private AttributeBridge manyRef = new ManyReferenceMapperTreeListX();
  private ITypeMapper singleRef = new SingleReferenceMapper();

  // private ITypeMapper singleContRef = new SingleContainementReferenceMapper();

  public ObjyMapper()
  {
    // TODO - this is from the old code. Verify if we still need it?!!!!
    // MultipleTypeMapper multipleMapper = new MultipleTypeMapper();
    // multipleMapper.add((IManyTypeMapper)manyRef);
    // multipleMapper.add(new IndexesReferenceMapper());
    // manyRef = multipleMapper;

    initMap();
    initManyMap();
  }

  public ITypeMapper getTypeMapper(EStructuralFeature feature)
  {
    if (feature == null || !ObjyObject.isPersistent(feature))
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("feature is transient " + feature);
      }
      return null;
    }

    boolean isMany = false;

    if (feature.isMany())
    {
      isMany = true;
    }

    if (feature instanceof EAttribute)
    {
      // PROBLEM
      EObject type = feature.getEType();
      if (type.eIsProxy())
      {
        URI a = EcoreUtil.getURI(type);
        type = EcorePackage.eINSTANCE.eResource().getEObject(a.fragment());
      }
      ITypeMapper attrMapper = null;
      if (isMany)
      {
        attrMapper = manyTypeMap.get(type);
      }
      else
      {
        attrMapper = typeMap.get(type);
      }
      if (attrMapper != null)
      {
        return attrMapper;
      }

      if (type instanceof EEnum)
      {
        return EnumTypeMapper.INSTANCE;
      }

      /*** handle custom types... ***/
      if (type instanceof EDataType)
      {
        if (isMany)
        {
          return CustomDataManyTypeMapper.INSTANCE;
        }

        return CustomDataTypeMapper.INSTANCE;
      }
    }
    else if (feature instanceof EReference)
    {
      if (isMany)
      {
        return manyRef;
      }

      return singleRef;
    }

    TRACER_DEBUG
        .trace("ERROR: " + feature.getEType() + " not supported for feature " + ((EClassifier)feature.eContainer()).getName() + "." + feature.getName());
    throw new RuntimeException(feature.getEType() + " not supported for feature " + ((EClassifier)feature.eContainer()).getName() + "." + feature.getName());
  }

  protected void initMap()
  {
    // TODO Do not support these type yet
    typeMap.put(EcorePackage.eINSTANCE.getEBoolean(), NumericTypeMapper.TMBOOLEAN);
    typeMap.put(EcorePackage.eINSTANCE.getEByte(), NumericTypeMapper.TMBYTE);
    typeMap.put(EcorePackage.eINSTANCE.getEChar(), NumericTypeMapper.TMCHAR);
    typeMap.put(EcorePackage.eINSTANCE.getEDate(), NumericTypeMapper.TMDATE);
    typeMap.put(EcorePackage.eINSTANCE.getEDouble(), NumericTypeMapper.TMDOUBLE);
    typeMap.put(EcorePackage.eINSTANCE.getEFloat(), NumericTypeMapper.TMFLOAT);
    typeMap.put(EcorePackage.eINSTANCE.getEInt(), NumericTypeMapper.TMINTEGER);
    typeMap.put(EcorePackage.eINSTANCE.getELong(), NumericTypeMapper.TMLONG);
    typeMap.put(EcorePackage.eINSTANCE.getEShort(), NumericTypeMapper.TMSHORT);
    typeMap.put(EcorePackage.eINSTANCE.getEString(), StringTypeMapper.INSTANCE);

    typeMap.put(EcorePackage.eINSTANCE.getEBooleanObject(), NumericTypeMapper.TMBOOLEAN);
    typeMap.put(EcorePackage.eINSTANCE.getEByteObject(), NumericTypeMapper.TMBYTE);
    typeMap.put(EcorePackage.eINSTANCE.getECharacterObject(), NumericTypeMapper.TMCHAR);
    typeMap.put(EcorePackage.eINSTANCE.getEDoubleObject(), NumericTypeMapper.TMDOUBLE);
    typeMap.put(EcorePackage.eINSTANCE.getEIntegerObject(), NumericTypeMapper.TMINTEGER);
    typeMap.put(EcorePackage.eINSTANCE.getELongObject(), NumericTypeMapper.TMLONG);
    typeMap.put(EcorePackage.eINSTANCE.getEFloatObject(), NumericTypeMapper.TMFLOAT);

    // the EByteArray doesn't show us as isMany()== true?!!!!
    typeMap.put(EcorePackage.eINSTANCE.getEByteArray(), ByteArrayTypeMapper.INSTANCE);
    typeMap.put(EcorePackage.eINSTANCE.getEBigDecimal(), BigDecimalTypeMapper.INSTANCE);
    typeMap.put(EcorePackage.eINSTANCE.getEBigInteger(), BigIntegerTypeMapper.INSTANCE);

  }

  private void initManyMap()
  {
    // TODO Do not support these type yet
    // typeMap.put(EcorePackage.eINSTANCE.getEBigDecimal()
    // typeMap.put(EcorePackage.eINSTANCE.getEBigInteger()

    manyTypeMap.put(EcorePackage.eINSTANCE.getEBoolean(), NumericManyTypeMapper.TMBOOLEAN);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEByte(), NumericManyTypeMapper.TMBYTE);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEChar(), NumericManyTypeMapper.TMCHAR);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEDate(), NumericManyTypeMapper.TMDATE);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEDouble(), NumericManyTypeMapper.TMDOUBLE);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEFloat(), NumericManyTypeMapper.TMFLOAT);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEInt(), NumericManyTypeMapper.TMINTEGER);
    manyTypeMap.put(EcorePackage.eINSTANCE.getELong(), NumericManyTypeMapper.TMLONG);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEShort(), NumericManyTypeMapper.TMSHORT);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEString(), StringManyTypeMapper.INSTANCE);

    manyTypeMap.put(EcorePackage.eINSTANCE.getEBooleanObject(), NumericManyTypeMapper.TMBOOLEAN);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEByteObject(), NumericManyTypeMapper.TMBYTE);
    manyTypeMap.put(EcorePackage.eINSTANCE.getECharacterObject(), NumericManyTypeMapper.TMCHAR);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEDoubleObject(), NumericManyTypeMapper.TMDOUBLE);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEFloatObject(), NumericManyTypeMapper.TMFLOAT);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEIntegerObject(), NumericManyTypeMapper.TMINTEGER);
    manyTypeMap.put(EcorePackage.eINSTANCE.getELongObject(), NumericManyTypeMapper.TMLONG);

    manyTypeMap.put(EcorePackage.eINSTANCE.getEByteArray(), NumericManyTypeMapper.TMBYTE);
    manyTypeMap.put(EcorePackage.eINSTANCE.getEFeatureMapEntry(), FeatureMapTypeMapper.INSTANCE);
  }

}
