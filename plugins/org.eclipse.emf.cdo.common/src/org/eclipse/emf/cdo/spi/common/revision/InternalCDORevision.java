/*
 * Copyright (c) 2009-2014, 2018, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchAdjustable;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;

import java.io.IOException;
import java.util.List;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDORevision extends CDORevision, CDORevisionData, CDOReferenceAdjustable, CDOBranchAdjustable
{
  /**
   * @since 4.7
   */
  public static final int DO_NOT_CREATE_LIST = -1;

  /**
   * @since 4.2
   */
  @Override
  public InternalCDOClassInfo getClassInfo();

  /**
   * @since 4.2
   */
  @Override
  public InternalCDOBranch getBranch();

  /**
   * @since 4.2
   */
  public InternalCDORevision getRevisionForID(CDOID id);

  /**
   * @since 4.2
   */
  public InternalCDORevision getProperRevision();

  public void setID(CDOID id);

  public void setVersion(int version);

  /**
   * @since 3.0
   */
  public void setBranchPoint(CDOBranchPoint branchPoint);

  public void setRevised(long revised);

  public void setResourceID(CDOID resourceID);

  public void setContainerID(Object containerID);

  /**
   * @since 4.27
   */
  public void setContainerFeatureID(int containerFeatureID);

  /**
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.26
   */
  public default void setContainingReference(EReference containingReference)
  {
    int containerReferenceID = calculateContainerReferenceID(containingReference);
    setContainerFeatureID(containerReferenceID);
  }

  /**
   * @since 3.0
   */
  public void adjustForCommit(CDOBranch branch, long timeStamp);

  public void add(EStructuralFeature feature, int index, Object value);

  public void clear(EStructuralFeature feature);

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex);

  public Object remove(EStructuralFeature feature, int index);

  public Object set(EStructuralFeature feature, int index, Object value);

  public void unset(EStructuralFeature feature);

  public Object setValue(EStructuralFeature feature, Object value);

  public void setList(EStructuralFeature feature, InternalCDOList list);

  /**
   * @deprecated As of 4.7 use either {@link #getListOrNull(EStructuralFeature)} or {@link #getOrCreateList(EStructuralFeature)}.
   */
  @Deprecated
  public CDOList getList(EStructuralFeature feature);

  /**
   * @deprecated As of 4.7 use either {@link #getListOrNull(EStructuralFeature)} or {@link #getOrCreateList(EStructuralFeature, int)}.
   */
  @Deprecated
  public CDOList getList(EStructuralFeature feature, int initialCapacity);

  /**
   * Same as {@link #getOrCreateList(EStructuralFeature, int) getOrCreateList(feature, 0)}.
   * <p>
   * <b>Warning</b>: Must be used with caution because list creation for an {@link EStructuralFeature#isUnsettable() unsettable}
   * feature implies a transition from UNSET to SET!
   *
   * @since 4.7
   */
  public CDOList getOrCreateList(EStructuralFeature feature);

  /**
   * Returns the list that represents the passed feature, possibly creates it if needed.
   * <p>
   * <b>Warning</b>: Must be used with caution because list creation for an {@link EStructuralFeature#isUnsettable() unsettable}
   * feature implies a transition from UNSET to SET!
   *
   * @param initialCapacity
   *          the initialCapacity of a new list to be created if this revision has no list so far (its size will always
   *          be 0), or -1 to skip list creation and return <code>null</code> in this case.
   *
   * @see #DO_NOT_CREATE_LIST
   * @since 4.7
   */
  public CDOList getOrCreateList(EStructuralFeature feature, int initialCapacity);

  /**
   * @since 3.0
   */
  public void read(CDODataInput in) throws IOException;

  /**
   * @since 4.3
   */
  public boolean readValues(CDODataInput in) throws IOException;

  /**
   * @since 4.26
   */
  public boolean readValue(CDODataInput in, EClass owner, EStructuralFeature feature, int i, boolean unchunked) throws IOException;

  /**
   * @since 3.0
   */
  public void write(CDODataOutput out, int referenceChunk) throws IOException;

  /**
   * @since 4.1
   */
  public void write(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext) throws IOException;

  /**
   * @since 4.3
   */
  public void writeValues(CDODataOutput out, int referenceChunk) throws IOException;

  /**
   * @since 4.26
   */
  public void writeValue(CDODataOutput out, EClass owner, EStructuralFeature feature, int i, int referenceChunk) throws IOException;

  /**
   * @since 3.0
   */
  public void convertEObjects(CDOIDProvider oidProvider);

  /**
   * @since 3.0
   */
  @Override
  public InternalCDORevisionDelta compare(CDORevision origin);

  /**
   * @since 3.0
   */
  @Override
  public InternalCDORevision copy();

  /**
   * @since 4.2
   */
  public EStructuralFeature[] clearValues();

  /**
   * @since 4.3
   */
  public String getResourceNodeName();

  /**
   * @since 4.1
   */
  public void setPermission(CDOPermission permission);

  /**
   * Enables or disables permission checking for this revision.
   * <p>
   * This method is used on the client side if the framework changes the revision on another
   * user's behalf, e.g., during invalidation or general revision copying. It's safe to offer this method
   * on the client side because the server is always the permission checking authority (during load or commit).
   *
   * @since 4.3
   */
  public boolean bypassPermissionChecks(boolean on);

  /**
   * @since 4.3
   */
  public boolean isListPreserving();

  /**
   * The default behavior of a revision for calls to the {@link #clear(EStructuralFeature)} and
   * {@link #unset(EStructuralFeature)} methods is to set the feature's value to null (discarding the
   * value itself, a List). By calling this {@link #setListPreserving()} method the default behavior
   * is changed; instead of setting the feature's value to null, the {@link List#clear()} method is
   * called on the feature's list instance.
   *
   * @since 4.3
   */
  public void setListPreserving();

  /**
   * @since 4.0
   */
  public void freeze();

  /**
   * @since 4.2
   */
  public boolean isFrozen();

  /**
   * @since 4.1
   */
  public boolean isUnchunked();

  /**
   * @since 4.1
   */
  public void setUnchunked();

  /**
   * @deprecated As of 4.27 use {@link #setContainerFeatureID(int)}.
   */
  @Deprecated
  public void setContainingFeatureID(int containerFeatureID);
}
