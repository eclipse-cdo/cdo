/*
 * Copyright (c) 2014, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import org.eclipse.emf.internal.cdo.transaction.CDOUndoDetectorImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * A strategy used to detect whether the feature of an {@link EObject object} has the original (clean) value after a number of modifications.
 *
 * @see CDOTransaction.Options#setUndoDetector(CDOUndoDetector)
 * @author Eike Stepper
 * @since 4.3
 */
public interface CDOUndoDetector
{
  public static final CDOUndoDetector NO_FEATURES = new CDOUndoDetectorImpl.NoFeatures();

  public static final CDOUndoDetector SINGLE_VALUED_FEATURES = new CDOUndoDetectorImpl.SingleValuedFeatures();

  public static final CDOUndoDetector ALL_FEATURES = new CDOUndoDetectorImpl();

  public boolean detectUndo(CDOTransaction transaction, CDORevision cleanRevision, CDORevision currentRevision, CDOFeatureDelta featureDelta);
}
