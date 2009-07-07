/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.internal.db;

import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Andre Dietisheim
 */
public class DBRevisionCacheUtil
{
  /**
   * Gets the name of a revision of a CDOResourceNode.
   * 
   * @param revision
   *          the revision
   * @return the resource node name
   * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=279817
   */
  // TODO: this should be refactored and put in a place, that's more generic
  // than this class. The same snippet's used in LRURevisionCache and
  // MemRevisionCache
  public static String getResourceNodeName(CDORevision revision)
  {
    CheckUtil.checkArg(revision.isResourceNode(), "The revision is not a resource node!");
    EStructuralFeature feature = revision.getEClass().getEStructuralFeature(
        CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
    return (String)((InternalCDORevision)revision).getValue(feature);
  }
}
