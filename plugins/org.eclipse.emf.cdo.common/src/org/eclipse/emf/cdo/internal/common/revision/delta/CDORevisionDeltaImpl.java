/*
 * Copyright (c) 2008-2014, 2016-2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 204890
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.id.CDOWithID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOOriginSizeProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.common.util.PartialCollectionLoadingNotSupportedException;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchAdjustable;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.Predicate;
import org.eclipse.net4j.util.Predicates;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.emf.ecore.change.util.ListDifferenceAnalyzer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDORevisionDeltaImpl implements InternalCDORevisionDelta, ListComparisonHandler
{
  private static final boolean WORK_AROUND_BUG_308618 = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.common.revision.delta.WORK_AROUND_BUG_308618");

  private EClass eClass;

  private CDOID id;

  private CDOBranch branch;

  private int version;

  private CDORevisable target;

  private Map<EStructuralFeature, CDOFeatureDelta> featureDeltas = new HashMap<EStructuralFeature, CDOFeatureDelta>();

  public CDORevisionDeltaImpl(CDORevision revision)
  {
    eClass = revision.getEClass();
    id = revision.getID();
    branch = revision.getBranch();
    version = revision.getVersion();
  }

  public CDORevisionDeltaImpl(CDORevisionDelta revisionDelta, boolean copyFeatureDeltas)
  {
    eClass = revisionDelta.getEClass();
    id = revisionDelta.getID();
    branch = revisionDelta.getBranch();
    version = revisionDelta.getVersion();

    if (copyFeatureDeltas)
    {
      for (CDOFeatureDelta delta : revisionDelta.getFeatureDeltas())
      {
        CDOFeatureDelta copy = ((InternalCDOFeatureDelta)delta).copy();
        addFeatureDelta(copy, null);
      }
    }
  }

  public CDORevisionDeltaImpl(CDORevision sourceRevision, CDORevision targetRevision)
  {
    if (sourceRevision.getEClass() != targetRevision.getEClass())
    {
      throw new IllegalArgumentException();
    }

    eClass = sourceRevision.getEClass();
    id = sourceRevision.getID();
    branch = sourceRevision.getBranch();
    version = sourceRevision.getVersion();
    target = CDORevisionUtil.copyRevisable(targetRevision);

    InternalCDORevision internalSourceRevision = (InternalCDORevision)sourceRevision;
    InternalCDORevision internalTargetRevision = (InternalCDORevision)targetRevision;
    compare(internalSourceRevision, internalTargetRevision);

    Object dirtyContainerID = internalTargetRevision.getContainerID();
    if (dirtyContainerID instanceof CDOWithID)
    {
      dirtyContainerID = ((CDOWithID)dirtyContainerID).cdoID();
    }

    CDOID dirtyResourceID = internalTargetRevision.getResourceID();
    int dirtyContainingFeatureID = internalTargetRevision.getContainingFeatureID();
    if (!compareValue(CDOContainerFeatureDelta.CONTAINER_FEATURE, internalSourceRevision.getContainerID(), dirtyContainerID)
        || !compareValue(null, internalSourceRevision.getContainingFeatureID(), dirtyContainingFeatureID)
        || !compareValue(CDOContainerFeatureDelta.CONTAINER_FEATURE, internalSourceRevision.getResourceID(), dirtyResourceID))
    {
      CDOFeatureDelta delta = new CDOContainerFeatureDeltaImpl(dirtyResourceID, dirtyContainerID, dirtyContainingFeatureID);
      addFeatureDelta(delta, null);
    }
  }

  public CDORevisionDeltaImpl(CDODataInput in) throws IOException
  {
    eClass = (EClass)in.readCDOClassifierRefAndResolve();
    id = in.readCDOID();
    branch = in.readCDOBranch();
    version = in.readXInt();
    if (version < 0)
    {
      version = -version;
      target = in.readCDORevisable();
    }

    int size = in.readXInt();
    for (int i = 0; i < size; i++)
    {
      CDOFeatureDelta featureDelta = in.readCDOFeatureDelta(eClass);
      featureDeltas.put(featureDelta.getFeature(), featureDelta);
    }
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOClassifierRef(eClass);
    out.writeCDOID(id);
    out.writeCDOBranch(branch);
    if (target == null)
    {
      out.writeXInt(version);
    }
    else
    {
      out.writeXInt(-version);
      out.writeCDORevisable(target);
    }

    out.writeXInt(featureDeltas.size());
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      out.writeCDOFeatureDelta(eClass, featureDelta);
    }
  }

  @Override
  public EClass getEClass()
  {
    return eClass;
  }

  @Override
  public CDOID getID()
  {
    return id;
  }

  @Override
  public CDOBranch getBranch()
  {
    return branch;
  }

  @Override
  public void setBranch(CDOBranch branch)
  {
    this.branch = branch;
  }

  @Override
  public int getVersion()
  {
    return version;
  }

  @Override
  public void setVersion(int version)
  {
    this.version = version;
  }

  @Override
  public CDORevisable getTarget()
  {
    return target;
  }

  @Override
  public void setTarget(CDORevisable target)
  {
    this.target = target;
  }

  @Override
  public int size()
  {
    return featureDeltas.size();
  }

  @Override
  public boolean isEmpty()
  {
    return featureDeltas.isEmpty();
  }

  @Override
  public CDORevisionDelta copy()
  {
    return new CDORevisionDeltaImpl(this, true);
  }

  @Override
  public Map<EStructuralFeature, CDOFeatureDelta> getFeatureDeltaMap()
  {
    return featureDeltas;
  }

  @Override
  public CDOFeatureDelta getFeatureDelta(EStructuralFeature feature)
  {
    return featureDeltas.get(feature);
  }

  @Override
  public List<CDOFeatureDelta> getFeatureDeltas()
  {
    return new ArrayList<CDOFeatureDelta>(featureDeltas.values());
  }

  @Override
  @Deprecated
  public void apply(CDORevision revision)
  {
    applyTo(revision);
  }

  @Override
  public void applyTo(CDORevision revision)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      ((CDOFeatureDeltaImpl)featureDelta).applyTo(revision);
    }
  }

  @Override
  @Deprecated
  public void addFeatureDelta(CDOFeatureDelta delta)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addFeatureDelta(CDOFeatureDelta delta, CDOOriginSizeProvider originSizeProvider)
  {
    if (delta instanceof CDOListFeatureDelta)
    {
      CDOListFeatureDelta listDelta = (CDOListFeatureDelta)delta;
      for (CDOFeatureDelta listChange : listDelta.getListChanges())
      {
        addSingleFeatureDelta(listChange, listDelta);
      }
    }
    else
    {
      addSingleFeatureDelta(delta, originSizeProvider);
    }
  }

  private void addSingleFeatureDelta(CDOFeatureDelta delta, CDOOriginSizeProvider originSizeProvider)
  {
    EStructuralFeature feature = delta.getFeature();
    if (feature.isMany())
    {
      CDOListFeatureDeltaImpl listDelta = (CDOListFeatureDeltaImpl)featureDeltas.get(feature);
      if (listDelta == null)
      {
        int originSize = originSizeProvider.getOriginSize();
        listDelta = new CDOListFeatureDeltaImpl(feature, originSize);
        featureDeltas.put(feature, listDelta);
      }

      // Remove all previous changes
      if (delta instanceof CDOClearFeatureDelta || delta instanceof CDOUnsetFeatureDelta)
      {
        listDelta.getListChanges().clear();
      }

      listDelta.add(delta);
    }
    else
    {
      CDOFeatureDelta oldDelta = featureDeltas.put(feature, delta);
      if (oldDelta instanceof CDOSetFeatureDelta && delta instanceof CDOSetFeatureDelta)
      {
        Object oldValue = ((CDOSetFeatureDelta)oldDelta).getOldValue();
        CDOSetFeatureDeltaImpl newDelta = (CDOSetFeatureDeltaImpl)delta;
        newDelta.setOldValue(oldValue);
      }
    }
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    boolean changed = false;
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      changed |= ((CDOFeatureDeltaImpl)featureDelta).adjustReferences(referenceAdjuster);
    }

    return changed;
  }

  @Override
  public void adjustBranches(CDOBranchManager newBranchManager)
  {
    if (branch != null)
    {
      branch = newBranchManager.getBranch(branch.getID());
    }

    if (target instanceof CDOBranchAdjustable)
    {
      ((CDOBranchAdjustable)target).adjustBranches(newBranchManager);
    }
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    accept(visitor, Predicates.<EStructuralFeature> alwaysTrue());
  }

  @Override
  public void accept(CDOFeatureDeltaVisitor visitor, Predicate<EStructuralFeature> filter)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      EStructuralFeature feature = featureDelta.getFeature();
      if (filter.apply(feature))
      {
        ((CDOFeatureDeltaImpl)featureDelta).accept(visitor);
      }
    }
  }

  @Override
  public void addDelta(CDOListFeatureDelta listFeatureDelta)
  {
    featureDeltas.put(listFeatureDelta.getFeature(), listFeatureDelta);
  }

  @Override
  public void addClearDelta(CDOClearFeatureDelta clearFeatureDelta, CDOOriginSizeProvider originSizeProvider)
  {
    addFeatureDelta(clearFeatureDelta, originSizeProvider);
  }

  private void compare(InternalCDORevision originRevision, InternalCDORevision dirtyRevision)
  {
    for (EStructuralFeature feature : originRevision.getClassInfo().getAllPersistentFeatures())
    {
      if (feature.isMany())
      {
        compareLists(originRevision, dirtyRevision, feature, this);
      }
      else
      {
        Object originValue = originRevision.get(feature, 0);
        Object dirtyValue = dirtyRevision.get(feature, 0);

        if (!compareValue(feature, originValue, dirtyValue))
        {
          if (dirtyValue == null)
          {
            CDOFeatureDelta delta = new CDOUnsetFeatureDeltaImpl(feature);
            addFeatureDelta(delta, null);
          }
          else
          {
            CDOFeatureDelta delta = new CDOSetFeatureDeltaImpl(feature, 0, dirtyValue, originValue);
            addFeatureDelta(delta, null);
          }
        }
      }
    }
  }

  private static boolean compareValue(EStructuralFeature feature, Object originValue, Object dirtyValue)
  {
    if (feature != null)
    {
      if (feature instanceof EReference)
      {
        originValue = convertEObject(originValue);
        dirtyValue = convertEObject(dirtyValue);
      }
      else
      {
        Object defaultValue = ((EAttribute)feature).getDefaultValue();
        if (defaultValue != null)
        {
          originValue = convertDefaultValue(originValue, defaultValue);
          dirtyValue = convertDefaultValue(dirtyValue, defaultValue);
        }
      }
    }

    if (originValue == null)
    {
      return dirtyValue == null;
    }

    if (dirtyValue == null)
    {
      return false;
    }

    if (originValue == dirtyValue)
    {
      return true;
    }

    if (originValue instanceof CDOID)
    {
      return false;
    }

    return originValue.equals(dirtyValue);
  }

  private static Object convertEObject(Object value)
  {
    CDOID id = CDOIDUtil.getCDOID(value);
    if (id != null)
    {
      return id;
    }

    return value;
  }

  private static Object convertDefaultValue(Object value, Object defaultValue)
  {
    // if (value == null)
    // {
    // return defaultValue;
    // }

    return value;
  }

  private static void compareLists(final InternalCDORevision originRevision, final InternalCDORevision dirtyRevision, final EStructuralFeature feature,
      ListComparisonHandler handler)
  {
    final int originSize = originRevision.size(feature);
    if (originSize > 0 && dirtyRevision.size(feature) == 0)
    {
      handler.addClearDelta(new CDOClearFeatureDeltaImpl(feature), new CDOOriginSizeProvider()
      {
        @Override
        public int getOriginSize()
        {
          return originSize;
        }
      });
    }
    else
    {
      CDOListFeatureDelta listFeatureDelta = new CDOListFeatureDeltaImpl(feature, originSize);
      final List<CDOFeatureDelta> changes = listFeatureDelta.getListChanges();

      ListDifferenceAnalyzer analyzer = new ListDifferenceAnalyzer()
      {
        @Override
        public void analyzeLists(EList<Object> oldList, EList<?> newList, EList<ListChange> listChanges)
        {
          checkNoProxies(oldList, originRevision);
          checkNoProxies(newList, dirtyRevision);
          super.analyzeLists(oldList, newList, listChanges);
        }

        @Override
        protected void createAddListChange(EList<Object> oldList, EList<ListChange> listChanges, Object value, int index)
        {
          CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(feature, index, value);
          changes.add(delta);
          oldList.add(index, value);
        }

        @Override
        protected void createRemoveListChange(EList<?> oldList, EList<ListChange> listChanges, Object value, int index)
        {
          CDORemoveFeatureDeltaImpl delta = new CDORemoveFeatureDeltaImpl(feature, index);

          if (WORK_AROUND_BUG_308618)
          {
            // Fix until ListDifferenceAnalyzer delivers the correct value (bug 308618).
            delta.setValue(oldList.get(index));
          }
          else
          {
            delta.setValue(value);
          }

          changes.add(delta);
          oldList.remove(index);
        }

        @Override
        protected void createMoveListChange(EList<?> oldList, EList<ListChange> listChanges, Object value, int index, int toIndex)
        {
          CDOMoveFeatureDeltaImpl delta = new CDOMoveFeatureDeltaImpl(feature, toIndex, index);

          if (WORK_AROUND_BUG_308618)
          {
            // Fix until ListDifferenceAnalyzer delivers the correct value (bug 308618).
            delta.setValue(oldList.get(index));
          }
          else
          {
            delta.setValue(value);
          }

          changes.add(delta);
          oldList.move(toIndex, index);
        }

        @Override
        protected boolean equal(Object originValue, Object dirtyValue)
        {
          return compareValue(feature, originValue, dirtyValue);
        }

        private void checkNoProxies(EList<?> list, CDORevision revision)
        {
          if (list != null && !((InternalCDORevision)revision).isUnchunked())
          {
            for (Object element : list)
            {
              if (element instanceof CDOElementProxy || element == CDOListImpl.UNINITIALIZED)
              {
                throw new PartialCollectionLoadingNotSupportedException("List contains proxy elements");
              }
            }
          }
        }
      };

      CDOList originList = originRevision.getListOrNull(feature);
      if (originList == null)
      {
        originList = new CDOListImpl(0, 0);
      }

      CDOList dirtyList = dirtyRevision.getListOrNull(feature);
      if (dirtyList == null)
      {
        dirtyList = new CDOListImpl(0, 0);
      }

      analyzer.analyzeLists(originList, dirtyList, new NOOPList());
      if (!changes.isEmpty())
      {
        handler.addDelta(listFeatureDelta);
      }
    }
  }

  public static CDOListFeatureDelta compareLists(final InternalCDORevision originRevision, InternalCDORevision dirtyRevision, final EStructuralFeature feature)
  {
    final CDOListFeatureDelta[] result = { null };
    compareLists(originRevision, dirtyRevision, feature, new ListComparisonHandler()
    {
      @Override
      public void addDelta(CDOListFeatureDelta listFeatureDelta)
      {
        result[0] = listFeatureDelta;
      }

      @Override
      public void addClearDelta(CDOClearFeatureDelta clearFeatureDelta, CDOOriginSizeProvider originSizeProvider)
      {
        CDOListFeatureDeltaImpl listFeatureDelta = new CDOListFeatureDeltaImpl(feature, originRevision.size(feature));
        listFeatureDelta.add(clearFeatureDelta);
        result[0] = listFeatureDelta;
      }
    });

    return result[0];
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDORevisionDelta[{0}@{1}:{2}v{3} --> {4}]", eClass.getName(), id, branch.getID(), version, featureDeltas.values());
  }

  /**
   * @author Eike Stepper
   */
  public static class NOOPList implements EList<ListChange>
  {
    private static final EList<ListChange> LIST = ECollections.emptyEList();

    public NOOPList()
    {
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public boolean isEmpty()
    {
      return true;
    }

    @Override
    public boolean contains(Object o)
    {
      return false;
    }

    @Override
    public Iterator<ListChange> iterator()
    {
      return LIST.iterator();
    }

    @Override
    public Object[] toArray()
    {
      return LIST.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
      return LIST.toArray(a);
    }

    @Override
    public boolean add(ListChange o)
    {
      return false;
    }

    @Override
    public boolean remove(Object o)
    {
      return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
      return false;
    }

    @Override
    public boolean addAll(Collection<? extends ListChange> c)
    {
      return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends ListChange> c)
    {
      return false;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
      return false;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
      return false;
    }

    @Override
    public void clear()
    {
    }

    @Override
    public ListChange get(int index)
    {
      return LIST.get(index);
    }

    @Override
    public ListChange set(int index, ListChange element)
    {
      return null;
    }

    @Override
    public void add(int index, ListChange element)
    {
    }

    @Override
    public ListChange remove(int index)
    {
      return null;
    }

    @Override
    public int indexOf(Object o)
    {
      return LIST.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
      return LIST.lastIndexOf(o);
    }

    @Override
    public ListIterator<ListChange> listIterator()
    {
      return LIST.listIterator();
    }

    @Override
    public ListIterator<ListChange> listIterator(int index)
    {
      return LIST.listIterator(index);
    }

    @Override
    public List<ListChange> subList(int fromIndex, int toIndex)
    {
      return LIST.subList(fromIndex, toIndex);
    }

    @Override
    public void move(int newPosition, ListChange object)
    {
    }

    @Override
    public ListChange move(int newPosition, int oldPosition)
    {
      return null;
    }
  }
}

/**
 * @author Eike Stepper
 * @since 4.8
 */
interface ListComparisonHandler
{
  public void addDelta(CDOListFeatureDelta listFeatureDelta);

  public void addClearDelta(CDOClearFeatureDelta clearFeatureDelta, CDOOriginSizeProvider originSizeProvider);
}
