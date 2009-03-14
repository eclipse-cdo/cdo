/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface InternalCDOList extends CDOList
{
  /**
   * Adjusts references according to the passed adjuster and resynchronizes indexes.
   */
  public void adjustReferences(CDOReferenceAdjuster adjuster, EClass classifier);

  /**
   * Clones the list.
   */
  public InternalCDOList clone(EClassifier classifier);
}
