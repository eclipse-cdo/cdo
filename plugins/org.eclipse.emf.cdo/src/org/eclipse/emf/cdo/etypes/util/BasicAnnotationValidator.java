/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.util;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.AnnotationValidator;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;

/**
 * A basic implementation of an {@link AnnotationValidator annotation validator}.
 * <p>
 * An implementation must specialize the {@link #getResourceLocator()} method in order for the {@link #getValidLocationDescription()} method to function correctly.
 * The most straight-forward way to implement an annotation validator is to model the supported keys,
 * specializing {@link #getPropertyClasses(ModelElement)} with one or more {@link EClass classes} that
 * can be {@link #createInstance(EClass, Annotation) instantiated} to represent the information in the annotation.
 * These classes are used to induce {@link #getProperties(ModelElement) a mapping} of keys onto the underlying annotation model's features.
 * If the annotation model includes references,
 * {@link #validateReferenceDetailValueLiteral(Annotation, ModelElement, Map.Entry, EReference, String, List, DiagnosticChain, Map) validateReferenceDetailValueLiteral}
 * and {@link #convertPropertyReferenceValueToLiteralItem(EObject, EReference, Object)} must also be specialized.
 * Alternatively an implementation can specialize {@link #validateDetail(Annotation, ModelElement, Map.Entry, DiagnosticChain, Map)} without providing a modeled representation.
 * The annotation validator's {@link #getAssistant() assistant} is especially useful for inducing a user interface based on the modeled annotation representation.
 * </p>
 *
 * @see AnnotationValidator
 * @see Assistant
 * @since 4.22
 */
public abstract class BasicAnnotationValidator implements AnnotationValidator
{
  /**
   * @see #reportInvalidLocation(Annotation, DiagnosticChain, Map)
   */
  public static final int INVALID_LOCATION = 1;

  /**
   * @see #reportDuplicate(Annotation, Annotation, ModelElement, DiagnosticChain, Map)
   */
  public static final int INVALID_DUPLICATE = 2;

  /**
   * @see #reportInvalidReferenceLiteral(Annotation, ModelElement, Map.Entry, EReference, String, DiagnosticChain, Map)
   */
  public static final int INVALID_REFERENCE_LITERAL = 3;

  /**
   * @see #createValueDiagnostic(Annotation, ModelElement, Map.Entry, EStructuralFeature)
   */
  public static final int INVALID_DETAIL_VALUE = 4;

  /**
   * @see #reportInvalidValueLiteral(Annotation, ModelElement, Map.Entry, EAttribute, String, EDataType, DiagnosticChain, RuntimeException, Map)
   */
  public static final int INVALID_VALUE_LITERAL = 5;

  /**
   * @see #reportIgnoredAnnotations(Annotation, ModelElement, Collection, DiagnosticChain, Map)
   */
  public static final int IGNORED_ANNOTATIONS = 6;

  /**
   * @see #reportIgnoredContents(Annotation, ModelElement, Collection, DiagnosticChain, Map)
   */
  public static final int IGNORED_CONTENTS = 7;

  /**
   * @see #reportIgnoredReferences(Annotation, ModelElement, Collection, DiagnosticChain, Map)
   */
  public static final int IGNORED_REFERENCES = 8;

  /**
   * @see #reportInvalidReference(Annotation, ModelElement, EObject, DiagnosticChain, Map)
   */
  public static final int INVALID_REFERENCE = 9;

  /**
   * @see #reportInvalidAnnotation(Annotation, ModelElement, Annotation, DiagnosticChain, Map)
   */
  public static final int INVALID_ANNOTATION = 10;

  /**
   * @see #reportInvalidContent(Annotation, ModelElement, EObject, DiagnosticChain, Map)
   */
  public static final int INVALID_CONTENT = 11;

  /**
   * @see #reportIgnoredEntry(Annotation, ModelElement, Map.Entry, DiagnosticChain, Map)
   */
  public static final int IGNORED_ENTRY = 12;

  /**
   * @see #reportMissingEntry(Annotation, ModelElement, String, EStructuralFeature, DiagnosticChain, Map)
   */
  public static final int MISSING_ENTRY = 13;

  /**
   * @see #reportMissingRequiredEntryValue(Annotation, ModelElement, EStructuralFeature, List, DiagnosticChain, Map)
   */
  public static final int MISSING_REQUIRED_ENTRY_VALUE = 14;

  /**
   * @see #reportTooFewValues(Annotation, ModelElement, Map.Entry, EStructuralFeature, List, int, int, DiagnosticChain, Map)
   */
  public static final int TOO_FEW_VALUES = 15;

  /**
   * @see #reportTooManyValues(Annotation, ModelElement, Map.Entry, EStructuralFeature, List, int, int, DiagnosticChain, Map)
   */
  public static final int TOO_MANY_VALUES = 16;

  /**
   * The {@link Annotation#getSource() annotation source} validated by this annotation validator.
   */
  protected final String annotationSource;

  /**
   * The name used in messages for this validator's annotations.
   */
  protected final String annotationName;

  /**
   * The {@link Diagnostic#getSource() source} used in this validator's diagnostics.
   */
  protected final String diagnosticSource;

  /**
   * The {@link Assistant assistant} used by the framework to induce a user interface.
   */
  protected final Assistant assistant;

  /**
   * Creates an instance for the given {@link Annotation#getSource() annotation source} validated by this annotation validator.
   *
   * @param annotationSource the annotation source validated by this annotation validator.
   * @param annotationName the name used in this validator's diagnostics
   * @param diagnosticSource the {@link Diagnostic#getSource() diagnostic source} used in this validator's diagnostics.
   */
  public BasicAnnotationValidator(String annotationSource, String annotationName, String diagnosticSource)
  {
    this.annotationSource = annotationSource;
    this.annotationName = annotationName;
    this.diagnosticSource = diagnosticSource;
    assistant = createAssistant();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAnnotationSource()
  {
    return annotationSource;
  }

  /**
   * Returns the resource locator for fetching implementation-specific messages.
   * @return the resource locator for fetching model-specific messages.
   */
  protected abstract ResourceLocator getResourceLocator();

  /**
   * Returns the model classes used to represent annotations for the given model element.
   * <p>
   * Typically an annotation validator implementation will return a single class.
   * An induced user interface will generally require the ability to {@link #createInstance(EClass, Annotation) create instances} of the classes returned by this method.
   * The annotation validator implementation itself does not require that ability.
   * </p>
   * @param modelElement the model element in question.
   * @return the model classes used to represent annotations for the given model element.
   */
  protected abstract List<EClass> getPropertyClasses(ModelElement modelElement);

  /**
   * Returns the assistant provided by this annotation validator
   * which is generally useful to provide access to protected methods that are needed primarily for inducing a user interface that represents the annotations in a more structured form.
   * @return the assistant provided by this annotation validator.
   */
  public Assistant getAssistant()
  {
    return assistant;
  }

  /**
   * Creates the assistant.
   * <p>
   * Generally derived classes will not need to specialize this method because all methods of the assistant delegate back to the annotation validator.
   * </p>
   * @return the assistant.
   */
  protected Assistant createAssistant()
  {
    return new Assistant(this)
    {
    };
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation returns <code>false</code> if the containing {@link Annotation#getModelElement()} is <code>null</code>,
   * if the {@link #isValidLocation(Annotation, ModelElement)} returns <code>false</code> for the containing model element,
   * or if this is not the first annotation with this annotation source in the model element and {@link #isDuplicateValid(ModelElement, Annotation, Annotation) isDuplicateValue} returns <code>false</code>.
   * </p>
   */
  @Override
  public boolean isValidLocation(Annotation annotation)
  {
    ModelElement modelElement = annotation.getModelElement();
    if (modelElement == null || !isValidLocation(annotation, modelElement))
    {
      return false;
    }
    Annotation otherAnnotation = modelElement.getAnnotation(annotationSource);
    return otherAnnotation == null || otherAnnotation == annotation || isDuplicateValid(modelElement, otherAnnotation, annotation);
  }

  /**
   * Returns whether this annotation {@link Annotation#getModelElement() contained} by this model element is valid at this location.
   * <p>
   * This implementation returns <code>true</code>.
   * It's typically the case that annotations on annotations aren't meaningful and valid.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @return whether this annotation contained by this model element is valid at this location.
   *
   * @see #isValidLocation(Annotation)
   * @see #validate(Annotation, DiagnosticChain, Map)
   */
  protected boolean isValidLocation(Annotation annotation, ModelElement modelElement)
  {
    return true;
  }

  /**
   * Returns whether the given two annotations, both with the annotation validator's annotation source, both {@link ModelElement#getAnnotation(String) contained} by the given model element, are valid.
   * <p>
   * This implementation returns <code>false</code> because it's typically the case that only the primary annotation is meaningful and valid.
   * </p>
   * @param modelElement the model element that contains both annotations in its {@link ModelElement#getAnnotations() annotations} feature.
   * @param primaryAnnotation the first annotation in the model element's details.
   * @param secondaryAnnotation a subsequent annotation in the model element's details.
   * @return whether these two annotations, both of which have this annotation validator's annotation source, are valid.
   * @see #isValidLocation(Annotation)
   * @see #validate(Annotation, DiagnosticChain, Map)
   */
  protected boolean isDuplicateValid(ModelElement modelElement, Annotation primaryAnnotation, Annotation secondaryAnnotation)
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation checks if the {@link #isValidLocation(Annotation, ModelElement) location is valid}
   * and {@link #reportInvalidLocation(Annotation, DiagnosticChain, Map) reports an invalid location} if it is not.
   * Then checks for {@link #isDuplicateValid(ModelElement, Annotation, Annotation) invalid duplicates}
   * and {@link #reportDuplicate(Annotation, Annotation, ModelElement, DiagnosticChain, Map) reports a duplicate} if it is an invalid duplicate.
   * Then it {@link #validateReferences(Annotation, ModelElement, DiagnosticChain, Map) validates the references} and {@link #validateDetails(Annotation, ModelElement, DiagnosticChain, Map) validates the details}.
   * </p>
   */
  @Override
  public boolean validate(Annotation annotation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result;
    ModelElement modelElement = annotation.getModelElement();
    if (modelElement != null && isValidLocation(annotation, modelElement))
    {
      Annotation otherAnnotation = modelElement.getAnnotation(annotationSource);
      if (otherAnnotation == annotation || isDuplicateValid(modelElement, otherAnnotation, annotation))
      {
        result = validateReferences(annotation, modelElement, diagnostics, context);
        if (result || diagnostics != null)
        {
          result &= validateContents(annotation, modelElement, diagnostics, context);
        }

        if (result || diagnostics != null)
        {
          result &= validateAnnotations(annotation, modelElement, diagnostics, context);
        }

        if (result || diagnostics != null)
        {
          result &= validateDetails(annotation, modelElement, diagnostics, context);
        }
      }
      else
      {
        result = false;
        if (diagnostics != null)
        {
          reportDuplicate(otherAnnotation, annotation, modelElement, diagnostics, context);
        }
      }
    }
    else
    {
      result = false;
      if (diagnostics != null)
      {
        reportInvalidLocation(annotation, diagnostics, context);
      }
    }

    return result;
  }

  /**
   * Returns whether this annotation's {@link Annotation#getReferences() references} are valid.
   * <p>
   * This implementation checks whether {@link #isReferencesSupported(Annotation, ModelElement) references are supported}.
   * If not, it checks whether the references are empty and if not {@link #reportIgnoredReferences(Annotation, ModelElement, Collection, DiagnosticChain, Map) reports ignored references}.
   * If references are supported, then for each reference, it tests whether that reference is among the {@link #getValidReferences(Annotation, ModelElement, Collection) valid references},
   * passing in this annotation's references to determine the valid references,
   * and {@link #reportInvalidReference(Annotation, ModelElement, EObject, DiagnosticChain, Map) reports an invalid reference} for each not present in the valid references.
   * </p>
   * <p>
   * It's typically the case that annotations ignore references.
   * If that's not the case, specialize {@link #isReferencesSupported(Annotation, ModelElement)}
   * and {@link #getValidReferences(Annotation, ModelElement, Collection)}.
   * An implementation may override this method to report missing required references.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether this annotation's references are valid.
   * @see #isReferencesSupported(Annotation, ModelElement)
   */
  protected boolean validateReferences(Annotation annotation, ModelElement modelElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    EList<EObject> references = annotation.getReferences();
    if (!isReferencesSupported(annotation, modelElement))
    {
      boolean result = references.isEmpty();
      if (!result && diagnostics != null)
      {
        reportIgnoredReferences(annotation, modelElement, references, diagnostics, context);
      }

      return result;
    }

    boolean result = true;
    Collection<?> validReferences = getValidReferences(annotation, modelElement, references);
    for (EObject reference : references)
    {
      if (!validReferences.contains(reference))
      {
        result = false;
        if (diagnostics == null)
        {
          break;
        }

        reportInvalidReference(annotation, modelElement, reference, diagnostics, context);
      }
    }

    return result;
  }

  /**
   * Returns whether {@link Annotation#getReferences() references} are meaningful for this annotation.
   * <p>
   * This method used to determine how references should be {@link #validateReferences(Annotation, ModelElement, DiagnosticChain, Map) validated}.
   * Also, an induced user interface should avoid providing the ability to specify references when this returns <code>false</code>.
   * Implementations that override this to ever return <code>true</code> should also override {@link #getValidReferences(Annotation, ModelElement, Collection)} to control the valid choices.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @return whether references are meaningful for this annotation.
   * @see Assistant#isReferencesSupported(Annotation)
   * @see #validateReferences(Annotation, ModelElement, DiagnosticChain, Map)
   */
  protected boolean isReferencesSupported(Annotation annotation, ModelElement modelElement)
  {
    return false;
  }

  /**
   * Returns the filtered collection of references that are valid for this annotation.
   * <p>
   * An induced user interface should provide the ability to specify only the references returned by this method.
   * The references argument may contain all reachable objects, some subset there of, or none at all;
   * an implementation may choose to filter from this collection or to provide its own result, including objects not in this collection.
   * This implementation returns the references argument if {@link #isReferencesSupported(Annotation, ModelElement) references are supported}, or an empty list otherwise.
   * It is also used to {@link #validateReferences(Annotation, ModelElement, DiagnosticChain, Map) determine} which references are valid.
   * An implementation that overrides this should also override {@link #isReferencesSupported(Annotation, ModelElement)}.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param references all reachable objects, some subset there of, or none at all.
   * @return the objects that are valid as references for this annotation.
   * @see Assistant#getValidReferences(Annotation, Collection)
   */
  protected Collection<?> getValidReferences(Annotation annotation, ModelElement modelElement, Collection<?> references)
  {
    return isReferencesSupported(annotation, modelElement) ? references : Collections.emptyList();
  }

  /**
   * Returns whether this annotation's {@link Annotation#getContents() contents} are valid.
   * <p>
   * This implementation checks whether {@link #isContentsSupported(Annotation, ModelElement) contents are supported}.
   * If not, it checks whether the contents are empty and if not {@link #reportIgnoredContents(Annotation, ModelElement, Collection, DiagnosticChain, Map) reports ignored contents}.
   * If contents are supported, then for each content, it tests whether that content is among the {@link #getValidContents(Annotation, ModelElement, Collection) valid contents},
   * passing in this annotation's contents to determine the valid contents,
   * and {@link #reportInvalidContent(Annotation, ModelElement, EObject, DiagnosticChain, Map) reports an invalid content} for each not present in the valid contents.
   * </p>
   * <p>
   * It's typically the case that annotations ignore contents.
   * If that's not the case, specialize {@link #isContentsSupported(Annotation, ModelElement)}
   * and {@link #getValidContents(Annotation, ModelElement, Collection)}.
   * An implementation may override this method to report missing required contents.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether this annotation's contents are valid.
   * @see #isContentsSupported(Annotation, ModelElement)
   * @see #getValidContents(Annotation, ModelElement, Collection)
   */
  protected boolean validateContents(Annotation annotation, ModelElement modelElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    EList<EObject> contents = annotation.getContents();
    if (!isContentsSupported(annotation, modelElement))
    {
      boolean result = contents.isEmpty();
      if (!result && diagnostics != null)
      {
        reportIgnoredContents(annotation, modelElement, contents, diagnostics, context);
      }

      return result;
    }

    boolean result = true;
    Collection<?> validContents = getValidContents(annotation, modelElement, contents);
    for (EObject content : contents)
    {
      if (!validContents.contains(content))
      {
        result = false;
        if (diagnostics == null)
        {
          break;
        }

        reportInvalidContent(annotation, modelElement, content, diagnostics, context);
      }
    }

    return result;
  }

  /**
   * Returns whether {@link Annotation#getContents() contents} are meaningful for this annotation.
   * <p>
   * This method used to determine how contents should be {@link #validateContents(Annotation, ModelElement, DiagnosticChain, Map) validated}.
   * Also, an induced user interface should avoid providing the ability to specify contents when this returns <code>false</code>.
   * Implementations that override this to ever return <code>true</code> should also override {@link #getValidContents(Annotation, ModelElement, Collection)} to control the valid choices.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @return whether contents are meaningful for this annotation.
   * @see Assistant#isContentsSupported(Annotation)
   * @see #validateContents(Annotation, ModelElement, DiagnosticChain, Map)
   */
  protected boolean isContentsSupported(Annotation annotation, ModelElement modelElement)
  {
    return false;
  }

  /**
   * Returns the filtered collection of contents that are valid for this annotation.
   * <p>
   * An induced user interface should provide the ability to specify only the contents returned by this method.
   * The contents argument may contain nothing at all, or the {@link Annotation#getContents() current contents} of the annotation;
   * an implementation may choose to filter from this collection or to provide its own result, including objects not in this collection
   * but it should not remove objects currently contained by the annotation that are valid.
   * This implementation returns the contents argument if {@link #isContentsSupported(Annotation, ModelElement) contents are supported}, or an empty list otherwise.
   * It is also used to {@link #validateContents(Annotation, ModelElement, DiagnosticChain, Map) determine} which contents are valid
   * and should therefore <b>not</b> remove values from the provided contents argument if they are valid.
   * An implementation that overrides this should also override {@link #isContentsSupported(Annotation, ModelElement)}.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param contents nothing at all, or the {@link Annotation#getContents() current or potential contents} of the annotation.
   * @return the objects that are valid as contents for this annotation.
   * @see Assistant#getValidContents(Annotation, Collection)
   */
  protected Collection<? extends EObject> getValidContents(Annotation annotation, ModelElement modelElement, Collection<? extends EObject> contents)
  {
    return isContentsSupported(annotation, modelElement) ? contents : Collections.<EObject> emptyList();
  }

  /**
   * Returns whether this annotation's {@link Annotation#getAnnotations() nested annotations} are valid.
   * <p>
   * This implementation iterates over the nested annotations, and if there is at least one for which there is no {@link org.eclipse.emf.cdo.etypes.AnnotationValidator.Registry#getAnnotationValidator(String) registered annotation validator}
   * or for which the registered annotation validator does not consider this nested annotation {@link #isValidLocation(Annotation) valid at this location},
   * it {@link #reportIgnoredAnnotations(Annotation, ModelElement, Collection, DiagnosticChain, Map) reports ignored annotations}.
   * It's typically the case that annotations ignore nested annotations.
   * If that's not the case, you should override this method and specialize {@link #isAnnotationsSupported(Annotation, ModelElement)}
   * and consider specializing {@link #getValidAnnotations(Annotation, ModelElement, Collection)}
   * </p>
   * <p>
   * This implementation checks whether {@link #isAnnotationsSupported(Annotation, ModelElement) nested annotations are supported}.
   * If not, it checks whether the {@link #getValidAnnotations(Annotation, ModelElement, Collection) valid annotations},
   * passing in this annotation's nested annotations to determine the valid annotations,
   * contain all the nested annotations; if not it {@link #reportIgnoredAnnotations(Annotation, ModelElement, Collection, DiagnosticChain, Map) reports ignored annotations}.
   * If nested annotations are supported, then for each nested annotation, it tests whether that annotation is among the {@link #getValidAnnotations(Annotation, ModelElement, Collection) valid annotations}
   * and {@link #reportInvalidAnnotation(Annotation, ModelElement, Annotation, DiagnosticChain, Map) reports an invalid annotation} for each not present in the valid annotations.
   * </p>
   * <p>
   * It's typically the case that annotations ignore nested annotations.
   * If that's not the case, specialize {@link #isAnnotationsSupported(Annotation, ModelElement)}
   * and {@link #getValidAnnotations(Annotation, ModelElement, Collection)}.
   * An implementation may override this method to report missing required nested annotations.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether this annotation's nested annotations are valid.
   * @see #isAnnotationsSupported(Annotation, ModelElement)
   */
  protected boolean validateAnnotations(Annotation annotation, ModelElement modelElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    EList<Annotation> annotations = annotation.getAnnotations();
    if (!annotations.isEmpty())
    {
      Collection<? extends Annotation> validAnnotations = getValidAnnotations(annotation, modelElement, annotations);
      if (!isAnnotationsSupported(annotation, modelElement))
      {
        if (!validAnnotations.containsAll(annotations))
        {
          if (diagnostics != null)
          {
            List<Annotation> ignoredAnnotations = new ArrayList<>(annotations);
            ignoredAnnotations.removeAll(validAnnotations);
            reportIgnoredAnnotations(annotation, modelElement, ignoredAnnotations, diagnostics, context);
          }

          return false;
        }

        return true;
      }

      boolean result = true;
      for (Annotation nestedAnnotation : annotations)
      {
        if (!validAnnotations.contains(nestedAnnotation))
        {
          result = false;
          if (diagnostics == null)
          {
            break;
          }

          reportInvalidAnnotation(annotation, modelElement, nestedAnnotation, diagnostics, context);
        }
      }

      return result;
    }

    return true;
  }

  /**
   * Returns whether {@link Annotation#getAnnotations() nested annotations} are meaningful for this annotation.
   * <p>
   * This method used to determine how nested annotations should be {@link #validateAnnotations(Annotation, ModelElement, DiagnosticChain, Map) validated}.
   * Also, an induced user interface should avoid providing the ability to specify nested annotations when this returns <code>false</code>.
   * Implementations that override this to ever return <code>true</code> should also override {@link #getValidAnnotations(Annotation, ModelElement, Collection)} to control the valid choices.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @return whether nested annotations are meaningful for this annotation.
   * @see Assistant#isAnnotationsSupported(Annotation)
   * @see #validateAnnotations(Annotation, ModelElement, DiagnosticChain, Map)
   */
  protected boolean isAnnotationsSupported(Annotation annotation, ModelElement modelElement)
  {
    return false;
  }

  /**
   * Returns the filtered collection of nested annotations that are valid for this annotation.
   * <p>
   * The annotations argument typically contains the {@link ModelElement#getAnnotations() current nested annotations} of the specified annotation;
   * an implementation may choose to filter from this collection,
   * but it should <b>not</b> remove nested annotations currently contained by the annotation that are valid.
   * This implementation takes into account the fact that annotations may be specifically designed to annotate other annotations,
   * i.e., that the nested annotation source might correspond to a {@link org.eclipse.emf.cdo.etypes.AnnotationValidator.Registry#getAnnotationValidator(String) registered annotation validator}
   * that considers its annotations {@link BasicAnnotationValidator#isValidLocation(Annotation, ModelElement) valid} when contained by the specified annotation.
   * As such, this implementation does not remove nested annotations for which there is a registered validator that considers its annotation valid in the specified annotation.
   * Note that this method is used to {@link #validateAnnotations(Annotation, ModelElement, DiagnosticChain, Map) determine} which nested annotations are valid
   * and that is why it should <b>not</b> remove values from the provided annotations argument if they are valid.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param annotations typically the {@link ModelElement#getAnnotations() current nested annotations} of the annotation.
   * @return the nested annotations that are valid as annotations for this annotation.
   * @see Assistant#getValidAnnotations(Annotation, Collection)
   */
  protected Collection<? extends Annotation> getValidAnnotations(Annotation annotation, ModelElement modelElement, Collection<? extends Annotation> annotations)
  {
    List<Annotation> result = new ArrayList<>(annotations);
    for (Annotation nestedAnnotation : annotations)
    {
      AnnotationValidator annotationValidator = AnnotationValidator.Registry.INSTANCE.getAnnotationValidator(nestedAnnotation.getSource());
      if (!(annotationValidator instanceof BasicAnnotationValidator)
          || !((BasicAnnotationValidator)annotationValidator).isValidLocation(nestedAnnotation, annotation))
      {
        result.remove(nestedAnnotation);
      }
    }
    return result;
  }

  /**
   * Returns the filtered collection of nested annotations that are valid for this annotation.
   * <p>
   * An induced user interface should provide the ability to specify only the nested annotations returned by this method.
   * The annotations argument may contain nothing at all, or the {@link ModelElement#getAnnotations() current nested annotations} of the specified annotation;
   * an implementation may choose to filter from this collection or to provide its own result, including objects not in this collection,
   * but it should <b>not</b> remove nested annotations currently contained by the annotation that are valid.
   * This implementation takes into account the fact that annotations may be specifically designed to annotate other annotations,
   * i.e., that the nested annotation source might correspond to a {@link org.eclipse.emf.cdo.etypes.AnnotationValidator.Registry#getAnnotationValidator(String) registered annotation validator}
   * that considers its annotations {@link BasicAnnotationValidator#isValidLocation(Annotation, ModelElement) valid} when contained by the specified annotation.
   * As such, this implementation does not remove nested annotations for which there is a registered validator that considers its annotation valid in the specified annotation.
   * Also, this implementation's result will include an additional annotation for each registered annotation validator that considers its annotations valid when nested in this annotation.
   * In fact, an override should <b>only</b> add values to those returned by this implementation.
   * An implementation that overrides this method should also override {@link #isAnnotationsSupported(Annotation, ModelElement)}.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param annotations nothing at all, or the {@link ModelElement#getAnnotations() current or potential nested annotations} of the annotation.
   * @return the nested annotations that are valid as annotations for this annotation.
   * @see Assistant#getValidAnnotations(Annotation, Collection)
   */
  protected Collection<? extends Annotation> getAllValidAnnotations(Annotation annotation, ModelElement modelElement,
      Collection<? extends Annotation> annotations)
  {
    List<Annotation> result = new ArrayList<>(getValidAnnotations(annotation, modelElement, annotations));

    for (String annotationSource : AnnotationValidator.Registry.INSTANCE.getAnnotationSources())
    {
      AnnotationValidator annotationValidator;
      try
      {
        annotationValidator = AnnotationValidator.Registry.INSTANCE.getAnnotationValidator(annotationSource);
      }
      catch (RuntimeException exception)
      {
        annotationValidator = null;
        OM.LOG.error(exception);
      }

      if (annotationValidator instanceof BasicAnnotationValidator)
      {
        BasicAnnotationValidator basicAnnotationValidator = (BasicAnnotationValidator)annotationValidator;
        Annotation nestedAnnotation = EtypesFactory.eINSTANCE.createAnnotation();
        nestedAnnotation.setSource(annotationSource);
        Annotation otherAnnotation = annotation.getAnnotation(annotationSource);
        if ((otherAnnotation == null || basicAnnotationValidator.isDuplicateValid(annotation, otherAnnotation, nestedAnnotation))
            && ((BasicAnnotationValidator)annotationValidator).isValidLocation(nestedAnnotation, annotation))
        {
          result.add(nestedAnnotation);
        }
      }
    }
    return result;
  }

  /**
   * Returns whether this annotation's {@link Annotation#getDetails() details} are valid.
   * <p>
   * This implementation uses the {@link #getProperties(ModelElement) properties of the model element}.
   * For each detail, it determines whether there is a corresponding feature in the properties.
   * If not, it validates the detail {@link #validateDetail(Annotation, ModelElement, java.util.Map.Entry, DiagnosticChain, Map) without a property feature}.
   * If so, it validates the detail {@link #validateFeatureDetail(Annotation, ModelElement, java.util.Map.Entry, EStructuralFeature, DiagnosticChain, Map) with the property feature}.
   * If all the details are valid,
   * it will check whether any {@link EStructuralFeature#isRequired() required} property feature is absent from the details
   * and {@link #reportMissingEntry(Annotation, ModelElement, String, EStructuralFeature, DiagnosticChain, Map) reports it missing}.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether this annotation's details are valid.
   */
  protected boolean validateDetails(Annotation annotation, ModelElement modelElement, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    Map<String, EStructuralFeature> properties = new LinkedHashMap<>(getProperties(modelElement));

    for (Map.Entry<String, String> entry : annotation.getDetails())
    {
      String key = entry.getKey();

      EStructuralFeature feature = properties.remove(key);
      if (feature == null)
      {
        result &= validateDetail(annotation, modelElement, entry, diagnostics, context);
      }
      else
      {
        result &= validateFeatureDetail(annotation, modelElement, entry, feature, diagnostics, context);
      }

      if (!result && diagnostics == null)
      {
        return false;
      }
    }

    if (result)
    {
      for (Map.Entry<String, EStructuralFeature> entry : properties.entrySet())
      {
        EStructuralFeature feature = entry.getValue();
        if (feature.isRequired())
        {
          result = false;
          if (diagnostics == null)
          {
            break;
          }

          reportMissingEntry(annotation, modelElement, entry.getKey(), feature, diagnostics, context);
        }
      }
    }

    return result;
  }

  /**
   * Returns a map from key to {@link EStructuralFeature feature}.
   * These represents the keys that are considered valid and can be processed by this annotation validator.
   * <p>
   * This implementation uses {@link #getPropertyClasses(ModelElement)}, iterating over each class and each feature of each class, adding to the map each {@link #isIncludedProperty(ModelElement, EClass, EStructuralFeature) included} feature.
   * If that method returns an empty list, then implementation returns an empty map.
   * In that case, {@link #validateDetails(Annotation, ModelElement, DiagnosticChain, Map) validating the details} of any annotation will report all detail entries as being ignored.
   * An annotation validator implement must override either this method, the <code>getPropertyClasses</code> method or the <code>validateDetails</code> method.
   * </p>
   * @param modelElement the model element that is being annotated.
   * @return a map from key to feature.
   * @see #validateDetails(Annotation, ModelElement, DiagnosticChain, Map)
   * @see #getPropertyClasses(ModelElement)
   */
  protected Map<String, EStructuralFeature> getProperties(ModelElement modelElement)
  {
    Map<String, EStructuralFeature> properties = new LinkedHashMap<>();
    for (EClass eClass : getPropertyClasses(modelElement))
    {
      for (EStructuralFeature eStructuralFeature : eClass.getEAllStructuralFeatures())
      {
        if (isIncludedProperty(modelElement, eClass, eStructuralFeature))
        {
          properties.put(eStructuralFeature.getName(), eStructuralFeature);
        }
      }
    }
    return Collections.unmodifiableMap(properties);
  }

  /**
   * Returns whether the given structural feature of the given class for the given model element is {@link #getProperties(ModelElement) included as a property}.
   * @param modelElement the model element.
   * @param eClass the class used to model the annotation for the model element.
   * @param eStructuralFeature a structural feature of the class.
   * @return whether the given structural feature of the given class for the given model element is included as a property.
   */
  protected boolean isIncludedProperty(ModelElement modelElement, EClass eClass, EStructuralFeature eStructuralFeature)
  {
    return true;
  }

  /**
   * Creates an instance of the modeled representation for the given annotation.
   * <p>
   * This implementation {@link EcoreUtil#create(EClass) creates} an instance and {@link #initialize(EObject, Annotation) initializes} it.
   * </p>
   * @param eClass the class to be instantiated.
   * @param annotation the annotation with the information that needs to be represented.
   * @return creates an instance of the modeled representation for the given annotation.
   */
  protected EObject createInstance(EClass eClass, Annotation annotation)
  {
    ModelElement modelElement = annotation.getModelElement();
    if (!getPropertyClasses(modelElement).contains(eClass))
    {
      throw new IllegalArgumentException("The eClass is not a property class of the model element");
    }

    return initialize(EcoreUtil.create(eClass), annotation);
  }

  /**
   * Returns an initialized instance of the given object for the given annotation.
   * <p>
   * This implementation handles only the case of modeled attributes.
   * For each {@link Annotation#getDetails() detail entry},
   * it looks up the corresponding {@link #getProperties(ModelElement) property} via the key.
   * If it's not an {@link EAttribute} it will throw an {@link UnsupportedOperationException}.
   * If the attribute property is {@link EStructuralFeature#isMany() multi-valued},
   * it {@link #split(Annotation, ModelElement, Map.Entry, String, EStructuralFeature, DiagnosticChain, Map) splits} the detail entry value, and {@link EcoreUtil#convertToString(EDataType, Object) converts} each literal item into a value of the {@link EAttribute#getEAttributeType() attribute's data type}.
   * If it's single-valued, the literal value is directly converted to the attributes data type.
   * The resulting list value or single value is {@link EObject#eSet(EStructuralFeature, Object) reflectively set} into the instance.
   * If the model representation includes references,
   * an annotation validator implementation must specialize this method for the {@link Assistant#createInstance(EClass, Annotation) assistant} to function correctly.
   * </p>
   * @param eObject the object to initialize.
   * @param annotation the annotation used to initialize the object.
   * @return an initialized instance.
   */
  protected EObject initialize(EObject eObject, Annotation annotation)
  {
    ModelElement modelElement = annotation.getModelElement();
    Map<String, EStructuralFeature> properties = getProperties(modelElement);
    for (Map.Entry<String, String> entry : annotation.getDetails())
    {
      String key = entry.getKey();
      EStructuralFeature eStructuralFeature = properties.get(key);
      if (eStructuralFeature instanceof EAttribute)
      {
        EDataType eDataType = (EDataType)eStructuralFeature.getEType();
        String literalValue = entry.getValue();
        if (eStructuralFeature.isMany())
        {
          List<String> literalValues = split(annotation, modelElement, entry, literalValue, eStructuralFeature, null, null);
          List<Object> value = new ArrayList<>();
          if (literalValues != null)
          {
            for (String itemLiteralValue : literalValues)
            {
              value.add(EcoreUtil.createFromString(eDataType, itemLiteralValue));
            }
          }
          eObject.eSet(eStructuralFeature, value);
        }
        else
        {
          eObject.eSet(eStructuralFeature, EcoreUtil.createFromString(eDataType, literalValue));
        }
      }
      else if (eStructuralFeature != null)
      {
        throw new UnsupportedOperationException("Initializing of references is not supported");
      }
    }
    return eObject;
  }

  /**
   * Returns the value of the feature of the modeled object converted to a literal representation as used in {@link Annotation#getDetails() detail entry}.
   * <p>
   * This implementation handles both {@link EAttribute} and {@link EReference}.
   * For a {@link EStructuralFeature#isMany() multi-valued} feature, it {@link #convertPropertyReferenceValueToLiteralItem(EObject, EReference, Object) converts} for each item in the list, and {@link #join(EObject, EStructuralFeature, List) joins} them into a single string.
   * For a single-valued feature, it returns the {@link #convertPropertyReferenceValueToLiteralItem(EObject, EReference, Object) converted} value.
   * This method is not used by the validator but is useful for specializing the {@link Assistant#convertPropertyValueToLiteral(EObject, EStructuralFeature, Object)}.
   * </p>
   * @param eObject the modeled object.
   * @param eStructuralFeature a feature of that object.
   * @param value the value to converted to a literal representation.
   * @return the value of the feature of the modeled object converted to a literal representation.
   * @see #convertPropertyValueToLiteralItem(EObject, EStructuralFeature, Object)
   */
  protected String convertPropertyValueToLiteral(EObject eObject, EStructuralFeature eStructuralFeature, Object value)
  {
    if (eStructuralFeature.isMany())
    {
      List<String> result = new ArrayList<>();

      if (value != null)
      {
        @SuppressWarnings("unchecked")
        List<Object> values = (List<Object>)value;

        for (Object valueItem : values)
        {
          result.add(convertPropertyValueToLiteralItem(eObject, eStructuralFeature, valueItem));
        }
      }

      return join(eObject, eStructuralFeature, result);
    }

    return convertPropertyValueToLiteralItem(eObject, eStructuralFeature, value);
  }

  /**
   * Returns the single value of the feature's {@link ETypedElement#getEType() type} for the modeled object converted to a literal representation as used in {@link Annotation#getDetails() detail entry}.
   * <p>
   * This implementation delegates to {@link #convertPropertyAttributeValueToLiteralItem(EObject, EAttribute, Object)} or {@link #convertPropertyReferenceValueToLiteralItem(EObject, EReference, Object)} as appropriate.
   * </p>
   * @param eObject the modeled object.
   * @param eStructuralFeature a feature of that object.
   * @param value the value of the feature's type to converted to a literal representation.
   * @return a value of the feature's type converted to a literal representation.
   * @see #convertPropertyValueToLiteral(EObject, EStructuralFeature, Object)
   */
  protected String convertPropertyValueToLiteralItem(EObject eObject, EStructuralFeature eStructuralFeature, Object value)
  {
    if (eStructuralFeature instanceof EAttribute)
    {
      return convertPropertyAttributeValueToLiteralItem(eObject, (EAttribute)eStructuralFeature, value);
    }

    return convertPropertyReferenceValueToLiteralItem(eObject, (EReference)eStructuralFeature, value);
  }

  /**
   * Returns the single value of the attribute's {@link ETypedElement#getEType() type} for the modeled object converted to a literal representation as used in {@link Annotation#getDetails() detail entry}.
   * <p>
   * This implementation simple uses {@link EcoreUtil#convertToString(EDataType, Object)}.
   * </p>
   * @param eObject the modeled object.
   * @param eAttribute an attribute feature of that object.
   * @param value the value of the feature's type to converted to a literal representation.
   * @return a value of the feature's type converted to a literal representation.
   * @see #convertPropertyValueToLiteral(EObject, EStructuralFeature, Object)
   */
  protected String convertPropertyAttributeValueToLiteralItem(EObject eObject, EAttribute eAttribute, Object value)
  {
    return EcoreUtil.convertToString(eAttribute.getEAttributeType(), value);
  }

  /**
   * Returns the single value of the references's {@link ETypedElement#getEType() type} for the modeled object converted to a literal representation as used in {@link Annotation#getDetails() detail entry}.
   * <p>
   * This implementation is incomplete.
   * It can't generally be known how to represent a reference to an object.
   * This implementation looks for a feature called "name", {@link EObject#eGet(EStructuralFeature) gets} the value, and if it's a string returns it.
   * Failing that, it throws an {@link UnsupportedOperationException}.
   * </p>
   * @param eObject the modeled object.
   * @param eReference a reference feature of that object.
   * @param value the value of the reference's type to converted to a literal representation.
   * @return a value of the references's type converted to a literal representation.
   * @see #convertPropertyValueToLiteral(EObject, EStructuralFeature, Object)
   */
  protected String convertPropertyReferenceValueToLiteralItem(EObject eObject, EReference eReference, Object value)
  {
    if (value == null)
    {
      return null;
    }
    else if (value instanceof EObject)
    {
      EObject valueEObject = (EObject)value;
      EStructuralFeature eStructuralFeature = valueEObject.eClass().getEStructuralFeature("name");
      if (eStructuralFeature != null)
      {
        Object name = valueEObject.eGet(eStructuralFeature);
        if (name instanceof String)
        {
          return name.toString();
        }
      }
    }

    throw new UnsupportedOperationException("Unable to convert '" + value + "' to a literal value for feature " + eReference + " of " + eObject);
  }

  /**
   * Returns the joined list of values of this modeled object's feature.
   * <p>
   * This implementation simply separates the individual literal value items with a " ".
   * </p>
   * @param eObject the modeled object.
   * @param eStructuralFeature a feature of that object.
   * @param literalValues the literal value to join into a single value.
   */
  protected String join(EObject eObject, EStructuralFeature eStructuralFeature, List<String> literalValues)
  {
    return XMLTypeFactory.eINSTANCE.convertENTITIESBase(literalValues);
  }

  /**
   * Returns whether the given feature of the given modeled representation is meaningful for the current state of the model.
   * <p>
   * This method is used to induce the {@link Assistant#getApplicableProperties(EObject, Annotation) applicable properties} by the assistant.
   * It is not directly used by the annotation validator itself.
   * This implementation always returns <code>true</code>.
   * </p>
   * @param eObject the modeled object in question.
   * @param eStructuralFeature a feature of that object.
   * @return whether the given feature of the given modeled representation is meaningful for the current state of the model.
   */
  protected boolean isApplicable(EObject eObject, EStructuralFeature eStructuralFeature)
  {
    return true;
  }

  /**
   * Returns whether this detail entry is valid.
   * <p>
   * This method is only called when there is no {@link #getProperties(ModelElement) property} associated with this entry's {@link java.util.Map.Entry#getKey() key}.
   * This implementation always {@link #reportIgnoredEntry(Annotation, ModelElement, Map.Entry, DiagnosticChain, Map) reports an ignored entry}.
   * An annotation validator implementation may choose to support validation by specializing this method, rather than {@link #getPropertyClasses(ModelElement) providing a modeled representation},
   * but the {@link Assistant assistant} will not provide any useful support.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether this detail entry is valid.
   */
  protected boolean validateDetail(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    if (diagnostics != null)
    {
      reportIgnoredEntry(annotation, modelElement, entry, diagnostics, context);
    }
    return false;
  }

  /**
   * Returns whether the value of this detail entry for the corresponding feature is valid.
   * <p>
   * This implementation delegates
   * to {@link #validateAttributeDetailLiteralValue(Annotation, ModelElement, java.util.Map.Entry, EAttribute, List, DiagnosticChain, Map)  validateAttributeDetail}
   * or to {@link #validateReferenceDetailLiteralValue(Annotation, ModelElement, Map.Entry, EReference, List, DiagnosticChain, Map)  validateReferenceDetail}
   * depending on whether the feature is an {@link EAttribute} or an {@link EReference}.
   * It {@link #createValueDiagnostic(Annotation, ModelElement, java.util.Map.Entry, EStructuralFeature) creates} a place holder diagnostic that it passed to those methods,
   * so all diagnostics gathered by those methods are grouped as {@link Diagnostic#getChildren() children} of the placeholder.
   * Both those methods build the corresponding modeled values as a side-effect.
   * If the detail entry is otherwise valid,
   * the modeled value {@link #validateFeatureDetailValue(Annotation, ModelElement, java.util.Map.Entry, EStructuralFeature, List, DiagnosticChain, Map) is validated}.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param feature the {@link #getProperties(ModelElement) property} associated with entry.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether the value of this detail entry for the corresponding feature is valid.
   */
  protected boolean validateFeatureDetail(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EStructuralFeature feature,
      DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    BasicDiagnostic badValueDiagnostic = diagnostics == null ? null : createValueDiagnostic(annotation, modelElement, entry, feature);
    List<Object> values = new ArrayList<>();
    boolean result = feature instanceof EAttribute
        ? validateAttributeDetailLiteralValue(annotation, modelElement, entry, (EAttribute)feature, values, badValueDiagnostic, context)
        : validateReferenceDetailLiteralValue(annotation, modelElement, entry, (EReference)feature, values, badValueDiagnostic, context);
    if (result)
    {
      result &= validateFeatureDetailValue(annotation, modelElement, entry, feature, values, badValueDiagnostic, context);
    }
    if (!result && diagnostics != null)
    {
      diagnostics.add(badValueDiagnostic);
    }
    return result;
  }

  /**
   * Returns whether the modeled values for this detail entry's corresponding feature are valid.
   * <p>
   * For a {@link EStructuralFeature#isMany() many-valued} feature, it validates that the {@link EStructuralFeature#getLowerBound() lower} and {@link EStructuralFeature#getUpperBound() upper} bounds are respected.
   * For a singled valued feature that is {@link EStructuralFeature#isRequired() required}, it validates that the value is present;
   * in the single-valued case, the values list should contain a single value.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param feature the {@link #getProperties(ModelElement) property} associated with entry.
   * @param values the list of instance values for this entry.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether the modeled values for this detail entry's corresponding feature are valid.
   */
  protected boolean validateFeatureDetailValue(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EStructuralFeature feature,
      List<Object> values, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    int size = values.size();
    if (feature.isMany())
    {
      int lowerBound = feature.getLowerBound();
      if (lowerBound > 0 && size < lowerBound)
      {
        if (diagnostics != null)
        {
          reportTooFewValues(annotation, modelElement, entry, feature, values, size, lowerBound, diagnostics, context);
        }
        result = false;
      }
      int upperBound = feature.getUpperBound();
      if (upperBound > 0 && size > upperBound)
      {
        if (diagnostics != null)
        {
          reportTooManyValues(annotation, modelElement, entry, feature, values, size, upperBound, diagnostics, context);
        }
        result = false;
      }
    }
    else
    {
      if (feature.isRequired())
      {
        if (size == 0 || values.get(0) == null)
        {
          result = false;
          if (diagnostics != null)
          {
            reportMissingRequiredEntryValue(annotation, modelElement, feature, values, diagnostics, context);
          }
        }
      }
    }
    return result;
  }

  /**
   * Returns whether the literal value of this detail entry for the corresponding attribute is valid.
   * <p>
   * This implementation,
   * for a {@link EStructuralFeature#isMany() many-valued} attribute,
   * {@link #split(Annotation, ModelElement, Map.Entry, String, EStructuralFeature, DiagnosticChain, Map) splits} the detail value, if present, into a list of literal values
   * and {@link #validateAttributeDetailValueLiteral(Annotation, ModelElement, Map.Entry, EAttribute, String, List, DiagnosticChain, Map) validates each literal value}.
   * For a single-valued attribute, it {@link #validateAttributeDetailValueLiteral(Annotation, ModelElement, Map.Entry, EAttribute, String, List, DiagnosticChain, Map) directly validates} the literal value.
   * As a side-effect, each literal value of a many-valued attribute, or the literal value of a single-valued attribute,
   * is converted to an instance of the attribute's {@link EAttribute#getEAttributeType() data type} and is added to the data values list.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param attribute feature the {@link EAttribute attribute} {@link #getProperties(ModelElement) property} associated with entry.
   * @param dataValues the list in which to accumulate valid instance values.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether the literal value of this detail entry for the corresponding attribute is valid.
   *
   * @see #validateAttributeDetailValueLiteral(Annotation, ModelElement, Map.Entry, EAttribute, String, List, DiagnosticChain, Map)
   */
  protected boolean validateAttributeDetailLiteralValue(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EAttribute attribute,
      List<Object> dataValues, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    String literalValue = entry.getValue();
    if (attribute.isMany())
    {
      List<String> literalValues = split(annotation, modelElement, entry, literalValue, attribute, diagnostics, context);
      if (literalValues != null)
      {
        for (String literalValueItem : literalValues)
        {
          result &= validateAttributeDetailValueLiteral(annotation, modelElement, entry, attribute, literalValueItem, dataValues, diagnostics, context);
          if (!result && diagnostics == null)
          {
            break;
          }
        }
      }
    }
    else
    {
      result = validateAttributeDetailValueLiteral(annotation, modelElement, entry, attribute, literalValue, dataValues, diagnostics, context);
    }
    return result;
  }

  /**
   * Returns whether the given literal value is valid with respect to this detail entry's corresponding attribute's {@link EAttribute#getEAttributeType() data type}.
   * As a side-effect, if the literal value can be converted to an instance value, the corresponding instance value is added to the list of data values.
   * <p>
   * This implementation first tries to {@link EcoreUtil#createFromString(EDataType, String) convert} the literal value to an instance value of the data type.
   * If that fails, it creates a diagnostic that includes the exception {@link Exception#getLocalizedMessage() message}.
   * Otherwise, it adds the instance value to the list of values
   * and {@link EValidator#validate(EDataType, Object, DiagnosticChain, Map) validates} the instance value.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param attribute feature the {@link EAttribute attribute} {@link #getProperties(ModelElement) property} associated with entry.
   * @param literalValue the literal value of the data type.
   * @param dataValues the list in which to accumulate a valid instance value.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether the given literal value is valid with respect to this detail entry's corresponding attribute's data type.
   */
  protected boolean validateAttributeDetailValueLiteral(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EAttribute attribute,
      String literalValue, List<Object> dataValues, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    EDataType dataType = attribute.getEAttributeType();
    boolean result;
    try
    {
      Object value = EcoreUtil.createFromString(dataType, literalValue);
      dataValues.add(value);
      EValidator rootEValidator = getRootEValidator(context);
      ValidationContext validationContext = new ValidationContext(annotation, modelElement, entry, attribute);
      context.put(ValidationContext.CONTEXT_KEY, validationContext);
      try
      {
        result = rootEValidator == null || rootEValidator.validate(dataType, value, diagnostics, context);
      }
      finally
      {
        context.remove(ValidationContext.CONTEXT_KEY);
      }
    }
    catch (RuntimeException exception)
    {
      result = false;
      if (diagnostics != null)
      {
        reportInvalidValueLiteral(annotation, modelElement, entry, attribute, literalValue, dataType, diagnostics, exception, context);
      }
    }
    return result;
  }

  /**
   * Returns whether the literal value of this detail entry for the corresponding reference is valid.
   * <p>
   * This implementation,
   * for a {@link EStructuralFeature#isMany() many-valued} reference,
   * {@link #split(Annotation, ModelElement, Map.Entry, String, EStructuralFeature, DiagnosticChain, Map) splits} the detail value, if present, into a list of literal values
   * and {@link #validateReferenceDetailValueLiteral(Annotation, ModelElement, Map.Entry, EReference, String, List, DiagnosticChain, Map) validates each literal value}.
   * For a single-valued attribute, it {@link #validateReferenceDetailValueLiteral(Annotation, ModelElement, Map.Entry, EReference, String, List, DiagnosticChain, Map) directly validates} the literal value.
   * As a side-effect, each literal value of a many-valued reference, or the literal value of a single-valued reference,
   * is converted to an instance of the references's {@link EReference#getEReferenceType() class} and added to the reference values list.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param reference the {@link EReference reference} {@link #getProperties(ModelElement) property} associated with entry.
   * @param referenceValues the list in which to accumulate valid instance values.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether the literal value of this detail entry for the corresponding reference is valid.
   */
  protected boolean validateReferenceDetailLiteralValue(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EReference reference,
      List<Object> referenceValues, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    boolean result = true;
    String literalValue = entry.getValue();
    if (reference.isMany())
    {
      List<String> literalValues = split(annotation, modelElement, entry, literalValue, reference, diagnostics, context);
      if (literalValues != null)
      {
        for (String literalValueItem : literalValues)
        {
          result &= validateReferenceDetailValueLiteral(annotation, modelElement, entry, reference, literalValueItem, referenceValues, diagnostics, context);
          if (!result && diagnostics == null)
          {
            break;
          }
        }
      }
    }
    else
    {
      result = validateReferenceDetailValueLiteral(annotation, modelElement, entry, reference, literalValue, referenceValues, diagnostics, context);
    }
    return result;
  }

  /**
   * Returns whether the given literal value is valid with respect to this detail entry's corresponding reference's {@link EReference#getEReferenceType() class}.
   * As a side-effect, if the literal value can be converted to an instance value, the corresponding instance value is added to the list of reference values.
   * <p>
   * This implementation always returns <code>false</code> and {@link #reportInvalidReferenceLiteral(Annotation, ModelElement, Map.Entry, EReference, String, DiagnosticChain, Map) reports an invalid reference literal}.
   * An annotation validator implementation that supports reference literals must specialize this method.
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param reference the {@link EReference reference} {@link #getProperties(ModelElement) property} associated with entry.
   * @param literalValue the literal value of the class.
   * @param referenceValues the list in which to accumulate valid instance values.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether the given literal value is valid with respect to this detail entry's corresponding references's class.
   */
  protected boolean validateReferenceDetailValueLiteral(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EReference reference,
      String literalValue, List<Object> referenceValues, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (diagnostics != null)
    {
      reportInvalidReferenceLiteral(annotation, modelElement, entry, reference, literalValue, diagnostics, context);
    }
    return false;
  }

  /**
   * Splits the literal value into a list of literal values as appropriate for this feature.
   * <p>
   * This implementation splits the values at whitespace boundaries for all features..
   * </p>
   * @param annotation the annotation in question.
   * @param modelElement the annotation's {@link Annotation#getModelElement() containing} model element.
   * @param entry the annotation {@link Annotation#getDetails() detail} in question.
   * @param literalValue a literal value of this feature's {@link EStructuralFeature#getEType() type}.
   * @param feature the {@link #getProperties(ModelElement) property} associated with entry.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return splits the literal value into a list of literal values as appropriate for this feature.
   *
   * @see #validateAttributeDetailValueLiteral(Annotation, ModelElement, Map.Entry, EAttribute, String, List, DiagnosticChain, Map)
   * @see #validateReferenceDetailValueLiteral(Annotation, ModelElement, Map.Entry, EReference, String, List, DiagnosticChain, Map)
   */
  protected List<String> split(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, String literalValue,
      EStructuralFeature feature, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return XMLTypeFactory.eINSTANCE.createENTITIESBase(literalValue);
  }

  /**
   * @see #INVALID_REFERENCE_LITERAL
   */
  protected void reportInvalidReferenceLiteral(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EReference reference,
      String literalValue, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_REFERENCE_LITERAL,
        getString(getEcoreResourceLocator(), "_UI_InvalidValue_diagnostic", literalValue,
            getString(getEcoreResourceLocator(), "_UI_InvalidReferenceValue_substitution", reference.getEReferenceType().getName())),
        literalValue, reference.getEReferenceType()));
  }

  /**
   * @see #INVALID_VALUE_LITERAL
   */
  protected void reportInvalidValueLiteral(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EAttribute attribute,
      String literalValue, EDataType dataType, DiagnosticChain diagnostics, RuntimeException exception, Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_VALUE_LITERAL,
        getString(getEcoreResourceLocator(), "_UI_InvalidValue_diagnostic", literalValue, exception.getLocalizedMessage()), literalValue, dataType));
  }

  /**
   * @see #MISSING_REQUIRED_ENTRY_VALUE
   */
  protected void reportMissingRequiredEntryValue(Annotation annotation, ModelElement modelElement, EStructuralFeature feature, List<Object> values,
      DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, MISSING_REQUIRED_ENTRY_VALUE,
        getString(getEcoreResourceLocator(), "_UI_InvalidValueRequiredFeatureMustBeSet_diagnostic"), (Object)null));
  }

  /**
   * @see #TOO_FEW_VALUES
   */
  protected void reportTooFewValues(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EStructuralFeature feature,
      List<Object> values, int size, int lowerBound, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, TOO_FEW_VALUES,
        getString(getEcoreResourceLocator(), "_UI_InvalidValueFeatureHasTooFewValues_diagnostic", size, lowerBound), values));
  }

  /**
   * @see #TOO_MANY_VALUES
   */
  protected void reportTooManyValues(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EStructuralFeature feature,
      List<Object> values, int size, int upperBound, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, TOO_MANY_VALUES,
        getString(getEcoreResourceLocator(), "_UI_InvalidValueFeatureHasTooManyValues_diagnostic", size, upperBound), values));
  }

  /**
   * @see #INVALID_LOCATION
   */
  protected void reportInvalidLocation(Annotation annotation, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_LOCATION,
        getString(getEcoreResourceLocator(), "_UI_InvalidAnnotationLocation_diagnostic", annotationName, getValidLocationDescription()), annotation,
        EcorePackage.Literals.EANNOTATION__SOURCE));
  }

  /**
   * @see #INVALID_DUPLICATE
   */
  protected void reportDuplicate(Annotation primaryAnnotation, Annotation secondaryAnnotation, ModelElement modelElement, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_DUPLICATE,
        getString(getEcoreResourceLocator(), "_UI_InvalidDuplicateAnnotation_diagnostic", annotationName, getValidLocationDescription()), secondaryAnnotation,
        EcorePackage.Literals.EANNOTATION__SOURCE, primaryAnnotation));
  }

  /**
   * @see #IGNORED_ENTRY
   */
  protected void reportIgnoredEntry(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, IGNORED_ENTRY,
        getString(getEcoreResourceLocator(), "_UI_InvalidAnnotationEntryKey_diagnostic", annotationName, entry.getKey()), entry,
        EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY__KEY));
  }

  /**
   * @see #MISSING_ENTRY
   */
  protected void reportMissingEntry(Annotation annotation, ModelElement modelElement, String key, EStructuralFeature property, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, MISSING_ENTRY, getString(getEcoreResourceLocator(), "_UI_MissingAnnotationEntryKey_diagnostic", key),
        annotation, EcorePackage.Literals.EANNOTATION__DETAILS));
  }

  /**
   * @see #IGNORED_REFERENCES
   */
  protected void reportIgnoredReferences(Annotation annotation, ModelElement modelElement, Collection<? extends EObject> ignoredReferences,
      DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    List<Object> data = new ArrayList<>();
    data.add(annotation);
    data.add(EcorePackage.Literals.EANNOTATION__REFERENCES);
    data.addAll(ignoredReferences);
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, IGNORED_REFERENCES,
        getString(getEcoreResourceLocator(), "_UI_IgnoredAnnotationReferences_diagnostic", annotationName), data.toArray()));
  }

  /**
   * @see #INVALID_REFERENCE
   */
  protected void reportInvalidReference(Annotation annotation, ModelElement modelElement, EObject reference, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_REFERENCE,
        getString(getEcoreResourceLocator(), "_UI_InvalidAnnotationReference_diagnostic", annotationName, EObjectValidator.getObjectLabel(reference, context)),
        annotation, EcorePackage.Literals.EANNOTATION__REFERENCES, reference));
  }

  /**
   * @see #INVALID_REFERENCE
   */
  protected void reportIgnoredContents(Annotation annotation, ModelElement modelElement, Collection<? extends EObject> ignoredContents,
      DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    List<Object> data = new ArrayList<>();
    data.add(annotation);
    data.add(EcorePackage.Literals.EANNOTATION__CONTENTS);
    data.addAll(ignoredContents);
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_REFERENCE,
        getString(getEcoreResourceLocator(), "_UI_IgnoredAnnotationContents_diagnostic", annotationName), data.toArray()));
  }

  /**
   * @see #INVALID_CONTENT
   */
  protected void reportInvalidContent(Annotation annotation, ModelElement modelElement, EObject content, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_CONTENT,
        getString(getEcoreResourceLocator(), "_UI_InvalidAnnotationContent_diagnostic", annotationName, EObjectValidator.getObjectLabel(content, context)),
        annotation, EcorePackage.Literals.EANNOTATION__CONTENTS, content));
  }

  /**
   * @see #IGNORED_ANNOTATIONS
   */
  protected void reportIgnoredAnnotations(Annotation annotation, ModelElement modelElement, Collection<? extends Annotation> ignoredAnnotations,
      DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    List<Object> data = new ArrayList<>();
    data.add(annotation);
    data.add(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS);
    data.addAll(ignoredAnnotations);
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, IGNORED_ANNOTATIONS,
        getString(getEcoreResourceLocator(), "_UI_IgnoredAnnotationAnnotations_diagnostic", annotationName), data.toArray()));
  }

  /**
   * @see #INVALID_ANNOTATION
   */
  protected void reportInvalidAnnotation(Annotation annotation, ModelElement modelElement, Annotation nestedAnnotation, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    diagnostics.add(createDiagnostic(Diagnostic.WARNING, INVALID_ANNOTATION, getString(getEcoreResourceLocator(), "_UI_InvalidAnnotationAnnotation_diagnostic",
        annotationName, EObjectValidator.getObjectLabel(nestedAnnotation, context)), annotation, EcorePackage.Literals.EANNOTATION__CONTENTS,
        nestedAnnotation));
  }

  /**
   * Returns a description of the valid locations supported for annotations of this annotation validator.
   * <p>
   * A annotation validator implementation must provide the key <code>"_UI_Valid" + {@link #annotationName annotationName} + "AnnotationLocation_substitution"</code> in its {@link #getResourceLocator() resource locator}
   * with a very short description of the valid locations of annotations.
   * </p>
   * @return a description of the valid locations supported for annotations of this annotation validator.
   * @see #reportInvalidLocation(Annotation, DiagnosticChain, Map)
   */
  protected String getValidLocationDescription()
  {
    String description;
    try
    {
      description = getString(getResourceLocator(), "_UI_Valid" + annotationName + "AnnotationLocation_substitution");
    }
    catch (MissingResourceException exception)
    {
      OM.LOG.error(exception);
      description = "unknown; Implementation error for " + getClass().getName() + ":" + exception.getLocalizedMessage();
    }

    return description;
  }

  /**
   * Creates the placeholder diagnostic used by {@link #validateFeatureDetail(Annotation, ModelElement, Map.Entry, EStructuralFeature, DiagnosticChain, Map) validateFeatureDetail}.
   * Diagnostics about problems with the value of a {@link Annotation#getDetails() detail entry} will be nested as {@link Diagnostic#getChildren() children} of this annotation.
   * @param annotation the annotation.
   * @param modelElement the model element of that annotation.
   * @param entry the entry.
   * @param feature the feature.
   * @return Creates a placeholder diagnostic.
   */
  protected BasicDiagnostic createValueDiagnostic(Annotation annotation, ModelElement modelElement, Map.Entry<String, String> entry, EStructuralFeature feature)
  {
    return createDiagnostic(Diagnostic.OK, INVALID_DETAIL_VALUE,
        getString(getEcoreResourceLocator(), "_UI_InvalidAnnotationEntryValue_diagnostic", annotationName, entry.getKey()), entry,
        EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY__VALUE, feature);
  }

  /**
   * Returns the resource locator for fetching messages supported directly by the base implementation.
   * @return the resource locator for fetching messages supported directly by the base implementation.
   */
  protected ResourceLocator getEcoreResourceLocator()
  {
    return EcorePlugin.INSTANCE;
  }

  /**
   * Creates a diagnostic using the given parameters and the {@link #annotationSource}.
   * @param severity
   * @param code
   * @param message
   * @param data
   * @return a diagnostic.
   */
  protected BasicDiagnostic createDiagnostic(int severity, int code, String message, Object... data)
  {
    return new BasicDiagnostic(severity, diagnosticSource, code, message, data);
  }

  /**
   * Fetches a translated string from the resource locator using the message key and the give substitutions, if any.
   * @param resourceLocator
   * @param key
   * @param substitutions
   * @return the translated string for the message key.
   */
  protected String getString(ResourceLocator resourceLocator, String key, Object... substitutions)
  {
    return substitutions == null ? resourceLocator.getString(key) : resourceLocator.getString(key, substitutions);
  }

  /**
   * Returns the root validator of the context.
   * @param context the context.
   * @return the root validator
   */
  protected EValidator getRootEValidator(Map<Object, Object> context)
  {
    if (context != null)
    {
      EValidator result = (EValidator)context.get(EValidator.class);
      if (result != null)
      {
        return result;
      }
    }

    return Diagnostician.INSTANCE;
  }

  /**
   * Returns the package loaded from the location specified by the given URI.
   * If the resource loads successfully---the org.eclipse.ecore.xmi jar must be on the class---and it contains an {@link EPackage},
   * that package is registered in the {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE}.
   *
   * @param uri the location of a resource containing an EPackage.
   * @return the loaded package registered package.
   */
  protected static EPackage loadEPackage(String uri)
  {
    Resource resource = new EPackageImpl()
    {
      @Override
      public Resource createResource(String uri)
      {
        Resource resource = super.createResource(uri);
        resource.getContents().clear();
        resource.unload();
        return resource;
      }
    }.createResource(uri);

    try
    {
      resource.load(null);
      EPackage ePackage = (EPackage)EcoreUtil.getObjectByType(resource.getContents(), EcorePackage.Literals.EPACKAGE);
      if (ePackage != null)
      {
        String nsURI = ePackage.getNsURI();
        EPackage.Registry.INSTANCE.put(nsURI, ePackage);
        resource.setURI(URI.createURI(nsURI));
      }
      return ePackage;
    }
    catch (IOException e)
    {
      return null;
    }
  }

  /**
   * An assistant that is useful for inducing a user interface that represents the annotation information in a more structured way
   * using {@link #getPropertyClasses(ModelElement) modeled objects} that are created by {@link #createInstance(EClass, Annotation)}.
   * This implementation delegates to the {@link BasicAnnotationValidator annotation validator} which in turn provides an {@link BasicAnnotationValidator#getAssistant() accessor} for its to corresponding assistant.
   * This class generally does not need to be specialized nor instantiated because every annotation validator provides an assistant.
   * This class therefore is abstract though none of its methods are abstract.
   */
  public static abstract class Assistant
  {
    /**
     * The annotation validator to which this assistant delegates.
     */
    protected final BasicAnnotationValidator annotationValidator;

    /**
     * Creates an instance that delegates to the give annotation validator.
     */
    public Assistant(BasicAnnotationValidator annotationValidator)
    {
      this.annotationValidator = annotationValidator;
    }

    /**
     * Returns whether this annotation with this annotation validator's {@link Annotation#getSource() annotation source} is valid at its {@link Annotation#getModelElement() current location}.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#isValidLocation(Annotation)}.
     * An induced user interface can use this to determine if it can/should use this assistant's information for representing modeled annotation information.
     * </p>
     * @param annotation the annotation in question.
     * @return whether this annotation with this annotation validator's annotation source is valid at its current location.
     */
    public boolean isValidLocation(Annotation annotation)
    {
      return annotationValidator.isValidLocation(annotation);
    }

    /**
     * Returns a map from key to {@link EStructuralFeature feature}.
     * These represents the keys that are considered valid and can be processed by this annotation validator.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#getProperties(ModelElement)}.
     * An induced user interface can use this method to determine which properties to display.
     * </p>
     * @param modelElement the model element that is being annotated.
     * @return a map from key to feature.
     * @see #getApplicableProperties(EObject, Annotation)
     */
    public Map<String, EStructuralFeature> getProperties(ModelElement modelElement)
    {
      return annotationValidator.getProperties(modelElement);
    }

    /**
     * Returns the model classes used to represent annotations for the given model element.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#getPropertyClasses(ModelElement)}.
     * An induced user interface can use this method to determine which instances to create for display purposes.
     * </p>
     * @param modelElement the model element in question.
     * @return the model classes used to represent annotations for the given model element.
     */
    public List<EClass> getPropertyClasses(ModelElement modelElement)
    {
      return annotationValidator.getPropertyClasses(modelElement);
    }

    /**
     * Creates an initialized instance of the modeled representation for the given annotation.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#createInstance(EClass, Annotation)}.
     * An induced user interface can use this method to create instances for display purposes.
     * </p>
     * @param eClass the class to be instantiated.
     * @param annotation the annotation with the information that needs to be represented.
     * @return creates an initialized instance of the modeled representation for the given annotation.
     */
    public EObject createInstance(EClass eClass, Annotation annotation)
    {
      return annotationValidator.createInstance(eClass, annotation);
    }

    public String convertPropertyValueToLiteral(EObject eObject, EStructuralFeature eStructuralFeature, Object value)
    {
      return annotationValidator.convertPropertyValueToLiteral(eObject, eStructuralFeature, value);
    }

    /**
     * Returns the {@link #getProperties(ModelElement) subset of properties} that are applicable for the current state of the modeled annotation instance.
     * <p>
     * This subset includes only those properties of the annotation's {@link Annotation#getModelElement() containing} model element
     * for which {@link BasicAnnotationValidator#isApplicable(EObject, EStructuralFeature)} returns <code>true</code>.
     * An induced user interface should avoid displaying properties that are not applicable for the current state of the modeled annotation instance.
     * </p>
     * @param eObject the modeled instance in question.
     * @param annotation the corresponding annotation of that modeled instance.
     * @return the subset of properties that are applicable for the current state of the modeled annotation instance.
     */
    public Map<String, EStructuralFeature> getApplicableProperties(EObject eObject, Annotation annotation)
    {
      ModelElement modelElement = annotation.getModelElement();
      Map<String, EStructuralFeature> properties = getProperties(modelElement);
      Map<String, EStructuralFeature> result = new LinkedHashMap<>();
      for (Map.Entry<String, EStructuralFeature> entry : properties.entrySet())
      {
        EStructuralFeature eStructuralFeature = entry.getValue();
        if (annotationValidator.isApplicable(eObject, eStructuralFeature))
        {
          result.put(entry.getKey(), eStructuralFeature);
        }
      }
      return Collections.unmodifiableMap(result);
    }

    /**
     * Returns whether {@link Annotation#getReferences() references} are meaningful for this annotation.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#isReferencesSupported(Annotation, ModelElement)},
     * passing in the {@link Annotation#getModelElement() containing} model element.
     * An induced user interface should avoid providing the ability to specify references when this returns <code>false</code>.
     * </p>
     * @param annotation the annotation in question.
     * @return whether references are meaningful for this annotation.
     */
    public boolean isReferencesSupported(Annotation annotation)
    {
      return annotationValidator.isReferencesSupported(annotation, annotation.getModelElement());
    }

    /**
     * Returns the filtered collection of references that are valid for this annotation.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#getValidReferences(Annotation, ModelElement, Collection)}, passing in the {@link Annotation#getModelElement() containing model element}.
     * An induced user interface should provide the ability to specify only the references returned by this method.
     * The references argument may contain all reachable objects, some subset there of, or none at all;
     * an implementation may choose to filter from this collection or to provide its own result, including objects not in this collection.
     * </p>
     * @param annotation the annotation in question.
     * @param references all reachable objects, some subset there of, or none at all.
     * @return the objects that are valid as references for this annotation.
     */
    public Collection<?> getValidReferences(Annotation annotation, Collection<?> references)
    {
      return annotationValidator.getValidReferences(annotation, annotation.getModelElement(), references);
    }

    /**
     * Returns whether {@link Annotation#getContents() contents} are meaningful for this annotation.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#isContentsSupported(Annotation, ModelElement)},
     * passing in the {@link Annotation#getModelElement() containing} model element and an empty list.
     * An induced user interface should avoid providing the ability to specify contents when this returns <code>false</code>.
     * </p>
     * @param annotation the annotation in question.
     * @return whether contents are meaningful for this annotation.
     */
    public boolean isContentsSupported(Annotation annotation)
    {
      return annotationValidator.isContentsSupported(annotation, annotation.getModelElement());
    }

    /**
     * Returns the filtered collection of contents that are valid for this annotation.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#getValidContents(Annotation, ModelElement, Collection)}
     * passing in the {@link Annotation#getModelElement() containing} model element.
     * An induced user interface should provide the ability to specify only the contents returned by this method.
     * </p>
     * @param annotation the annotation in question.
     * @param contents nothing at all, or the {@link Annotation#getContents() potential contents} of the annotation.
     * @return the objects that are valid as contents for this annotation.
     */
    public Collection<? extends EObject> getValidContents(Annotation annotation, Collection<? extends EObject> contents)
    {
      return annotationValidator.getValidContents(annotation, annotation.getModelElement(), contents);
    }

    /**
     * Returns whether {@link Annotation#getAnnotations() nested annotations} are meaningful for this annotation.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#isAnnotationsSupported(Annotation, ModelElement)}, passing in the {@link Annotation#getModelElement() containing model element}.
     * An induced user interface should avoid providing the ability to specify nested annotations when this returns <code>false</code>.
     * </p>
     * @param annotation the annotation in question.
     * @return whether annotations are meaningful for this annotation.
     */
    public boolean isAnnotationsSupported(Annotation annotation)
    {
      return annotationValidator.isAnnotationsSupported(annotation, annotation.getModelElement());
    }

    /**
     * Returns the filtered collection of nested annotations that are valid for this annotation.
     * <p>
     * The implementation delegates to {@link BasicAnnotationValidator#getAllValidAnnotations(Annotation, ModelElement, Collection)}
     * passing in the {@link Annotation#getModelElement() containing} model element.
     * An induced user interface should provide the ability to specify only the nested annotations returned by this method.
     * </p>
     * @param annotation the annotation in question.
     * @param annotations nothing at all, or the {@link ModelElement#getAnnotations() current or potential nested annotations} of the annotation.
     * @return the annotations that are valid as nested annotations for this annotation.
     */
    public Collection<? extends Annotation> getValidAnnotations(Annotation annotation, Collection<? extends Annotation> annotations)
    {
      return annotationValidator.getAllValidAnnotations(annotation, annotation.getModelElement(), annotations);
    }
  }

  /**
   * Context data used by {@link BasicAnnotationValidator#validateAttributeDetailValueLiteral(Annotation, ModelElement, Entry, EAttribute, String, List, DiagnosticChain, Map) validateAttributeDetailValueLiteral}
   * to pass contextual information that can be used when a data type's value is {@link EValidator#validate(EDataType, Object, DiagnosticChain, Map) validated}.
   */
  public static class ValidationContext
  {
    /**
     * The key used in the context map.
     */
    public static final String CONTEXT_KEY = "EANNOTATION_VALIDATION_CONTEXT";

    /**
     * The annotation being validated.
     */
    private final Annotation annotation;

    /**
     * The model element containing that annotation.
     */
    private final ModelElement modelElement;

    /**
     * The detail entry being validated.
     */
    private final Entry<String, String> entry;

    /**
     * The attribute of the data type.
     */
    private final EAttribute eAttribute;

    /**
     * Creates an instance.
     * @param annotation the annotation being validated.
     * @param modelElement the model element containing that annotation.
     * @param entry the detail entry being validated.
     * @param eAttribute the structural feature of the data type.
     */
    public ValidationContext(Annotation annotation, ModelElement modelElement, Entry<String, String> entry, EAttribute eAttribute)
    {
      this.annotation = annotation;
      this.modelElement = modelElement;
      this.entry = entry;
      this.eAttribute = eAttribute;
    }

    /**
     * Returns the annotation being validated.
     * @return the annotation being validated.
     */
    public Annotation getAnnotation()
    {
      return annotation;
    }

    /**
     * The containing model elements of the annotation being validated.
     * @return the containing model elements of the annotation being validated.
     */
    public ModelElement getModelElement()
    {
      return modelElement;
    }

    /**
     * The {@link Annotation#getDetails() detail entry} being validated.
     * @return the detail entry being validated.
     */
    public Entry<String, String> getEntry()
    {
      return entry;
    }

    /**
     * The attribute of the data type being validated.
     * @return the attribute of the data type being validated.
     */
    public EAttribute getEAttribute()
    {
      return eAttribute;
    }
  }
}
