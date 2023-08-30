/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.util;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.AnnotationValidator;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.internal.cdo.bundle.Activator;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.util.EcoreValidator;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * @since 4.22
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.etypes.EtypesPackage
 * @generated
 */
public class EtypesValidator extends EObjectValidator
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final EtypesValidator INSTANCE = new EtypesValidator();

  /**
   * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.Diagnostic#getSource()
   * @see org.eclipse.emf.common.util.Diagnostic#getCode()
   * @generated
   */
  public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.cdo.etypes"; //$NON-NLS-1$

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EtypesValidator()
  {
    super();
  }

  /**
   * Returns the package of this validator switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EPackage getEPackage()
  {
    return EtypesPackage.eINSTANCE;
  }

  /**
   * Calls <code>validateXXX</code> for the corresponding classifier of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    switch (classifierID)
    {
    case EtypesPackage.MODEL_ELEMENT:
      return validateModelElement((ModelElement)value, diagnostics, context);
    case EtypesPackage.ANNOTATION:
      return validateAnnotation((Annotation)value, diagnostics, context);
    case EtypesPackage.STRING_TO_STRING_MAP_ENTRY:
      return validateStringToStringMapEntry((Map.Entry<?, ?>)value, diagnostics, context);
    case EtypesPackage.BLOB:
      return validateBlob((CDOBlob)value, diagnostics, context);
    case EtypesPackage.CLOB:
      return validateClob((CDOClob)value, diagnostics, context);
    case EtypesPackage.LOB:
      return validateLob((CDOLob<?>)value, diagnostics, context);
    case EtypesPackage.INPUT_STREAM:
      return validateInputStream((InputStream)value, diagnostics, context);
    case EtypesPackage.READER:
      return validateReader((Reader)value, diagnostics, context);
    case EtypesPackage.CLASSIFIER_REF:
      return validateClassifierRef((CDOClassifierRef)value, diagnostics, context);
    case EtypesPackage.BRANCH_REF:
      return validateBranchRef((CDOBranchRef)value, diagnostics, context);
    case EtypesPackage.BRANCH_POINT_REF:
      return validateBranchPointRef((CDOBranchPointRef)value, diagnostics, context);
    default:
      return true;
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateModelElement(ModelElement modelElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(modelElement, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateAnnotation(Annotation annotation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(annotation, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(annotation, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateAnnotation_WellFormed(annotation, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateAnnotation_WellFormedSourceURI(annotation, diagnostics, context);
    }
    return result;
  }

  /**
   * Validates the WellFormed constraint of '<em>Annotation</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateAnnotation_WellFormed(Annotation annotation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    AnnotationValidator annotationValidator = AnnotationValidator.Registry.INSTANCE.getAnnotationValidator(annotation.getSource());
    if (annotationValidator != null)
    {
      return annotationValidator.validate(annotation, diagnostics, context);
    }

    return true;
  }

  /**
   * Validates the WellFormedSourceURI constraint of '<em>Annotation</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean validateAnnotation_WellFormedSourceURI(Annotation annotation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    String source = annotation.getSource();
    boolean result = source == null || CDOCommonUtil.isWellFormedURI(source);
    if (!result && diagnostics != null)
    {
      diagnostics
          .add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, EcoreValidator.WELL_FORMED_SOURCE_URI, "_UI_AnnotationSourceURINotWellFormed_diagnostic",
              new Object[] { source }, new Object[] { annotation, EtypesPackage.Literals.ANNOTATION__SOURCE }, context));
    }

    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateStringToStringMapEntry(Map.Entry<?, ?> stringToStringMapEntry, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint((EObject)stringToStringMapEntry, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateBlob(CDOBlob blob, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateClob(CDOClob clob, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateLob(CDOLob<?> lob, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateInputStream(InputStream inputStream, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateReader(Reader reader, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateClassifierRef(CDOClassifierRef classifierRef, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateBranchRef(CDOBranchRef branchRef, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateBranchPointRef(CDOBranchPointRef branchPointRef, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return Activator.INSTANCE;
  }

} // EtypesValidator
