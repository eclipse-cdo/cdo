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
package org.eclipse.emf.cdo.etypes;

import org.eclipse.emf.cdo.etypes.util.BasicAnnotationValidator;

import org.eclipse.emf.internal.cdo.util.AnnotationValidatorRegistryImpl;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EObjectValidator;

import java.util.Map;
import java.util.Set;

/**
 * An annotation validity checker for a specific {@link Annotation#getSource() annotation source}.
 * <p>
 * Implementations of AnnotationValidator should extend {@link BasicAnnotationValidator BasicAnnotationValidator}
 * or one of its derived classes because methods may be added to this API.
 * </p>
 *
 * @see BasicAnnotationValidator
 *
 * @noimplement Do not implement this interface directly; instead extend {@link BasicAnnotationValidator} or one of its subclasses.
 * @since 4.22
 */
public interface AnnotationValidator
{
  /**
   * Returns the {@link Annotation#getSource() annotation source} of the annotations validated by this annotation validator.
   * @return the annotation source.
   */
  public String getAnnotationSource();

  /**
   * Returns whether this annotation with this annotation validator's {@link Annotation#getSource() annotation source} is valid at its {@link Annotation#getModelElement() current location}.
   * @param annotation the annotation in question.
   * @return whether this annotation with this annotation validator's annotation source is valid at its current location.
   */
  public boolean isValidLocation(Annotation annotation);

  /**
   * Returns whether this annotation is valid.
   * @param annotation the annotation in question.
   * @param diagnostics a place to accumulate diagnostics; if it's <code>null</code>, no diagnostics should be produced.
   * @param context a place to cache information, if it's <code>null</code>, no cache is supported.
   * @return whether this annotation is valid.
   *
   * @see EObjectValidator#validate(EObject, DiagnosticChain, Map)
   */
  public boolean validate(Annotation annotation, DiagnosticChain diagnostics, Map<Object, Object> context);

  /**
   * A registry from {@link Annotation#getSource() annotation source} to {@link AnnotationValidator}.
   */
  public interface Registry
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.annotationValidators"; //$NON-NLS-1$

    /**
     * The global instance of an annotation validator registry.
     */
    public static final Registry INSTANCE = new AnnotationValidatorRegistryImpl();

    public Set<String> getAnnotationSources();

    /**
     * Looks up the annotation source in the registry.
     */
    public AnnotationValidator getAnnotationValidator(String annotationSource);
  }
}
