/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.compare;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.scope.AbstractComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSession.MergeData;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Various static methods that may help EMF Compare in a CDO scope.
 *
 * @author Eike Stepper
 */
public final class CDOCompareUtil
{
  private CDOCompareUtil()
  {
  }

  public static CDOComparison compare(CDOView leftView, CDOBranchPoint right)
  {
    return compare(leftView, right, true);
  }

  public static CDOComparison compare(CDOView leftView, CDOBranchPoint right, boolean threeWay)
  {
    Set<Object> objectsToDeactivateOnClose = new HashSet<Object>();
    CDOSession session = leftView.getSession();

    CDOView rightView;
    if (right instanceof CDOView)
    {
      rightView = (CDOView)right;
      if (rightView.getSession() != session)
      {
        throw new IllegalArgumentException("Sessions are different");
      }
    }
    else
    {
      rightView = session.openView(right);
      objectsToDeactivateOnClose.add(rightView);
    }

    CDOView originView = null;
    if (threeWay)
    {
      CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(leftView, rightView);
      if (!ancestor.equals(leftView) && !ancestor.equals(rightView))
      {
        originView = session.openView(ancestor);
        objectsToDeactivateOnClose.add(originView);
      }
    }

    Set<CDOID> ids;
    if (originView != null)
    {
      MergeData mergeData = ((InternalCDOSession)session).getMergeData(leftView, rightView, null);
      ids = mergeData.getIDs();
    }
    else
    {
      CDOChangeSetData changeSetData = leftView.compareRevisions(right);
      ids = new HashSet<CDOID>(changeSetData.getChangeKinds().keySet());
    }

    IComparisonScope scope = new CDOComparisonScope.Minimal(leftView, rightView, originView, ids);
    return createComparison(scope, objectsToDeactivateOnClose);
  }

  public static CDOComparison compare(EObject leftRoot, CDOBranchPoint right)
  {
    return compare(leftRoot, right, true);
  }

  public static CDOComparison compare(EObject leftRoot, CDOBranchPoint right, boolean threeWay)
  {
    Set<Object> objectsToDeactivateOnClose = new HashSet<Object>();

    CDOObject leftObject = CDOUtil.getCDOObject(leftRoot);
    CDOView leftView = leftObject.cdoView();
    CDOBranchPoint left = CDOBranchUtil.copyBranchPoint(leftView);
    CDOSession session = leftView.getSession();

    CDOView rightView;
    if (right instanceof CDOView)
    {
      rightView = (CDOView)right;
      if (rightView.getSession() != session)
      {
        throw new IllegalArgumentException("Sessions are different");
      }
    }
    else
    {
      rightView = session.openView(right);
      objectsToDeactivateOnClose.add(rightView);
    }

    Notifier rightObject = rightView.getObject(leftObject);

    CDOView originView = null;
    Notifier originObject = null;
    if (threeWay)
    {
      CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(left, right);
      if (!ancestor.equals(left) && !ancestor.equals(right))
      {
        originView = session.openView(ancestor);
        originObject = originView.getObject(leftObject);
        objectsToDeactivateOnClose.add(originView);
      }
    }

    IComparisonScope scope = new CDOComparisonScope.AllContents(leftObject, rightObject, originObject);
    return createComparison(scope, objectsToDeactivateOnClose);
  }

  private static EMFCompare createComparator(IComparisonScope scope)
  {
    Function<EObject, String> idFunction = new CDOIDFunction();
    IEObjectMatcher matcher = new IdentifierEObjectMatcher.Builder().idFunction(idFunction).build();

    EMFCompare comparator = EMFCompare.newComparator(scope);
    comparator.setEObjectMatcher(matcher);
    return comparator;
  }

  private static CDOComparison createComparison(IComparisonScope scope, Set<Object> objectsToDeactivateOnClose)
  {
    EMFCompare comparator = createComparator(scope);
    Comparison comparison = comparator.compare();
    return new CDOComparison(comparison, objectsToDeactivateOnClose);
  }

  /**
   * @author Eike Stepper
   */
  public static class CDOComparison extends DelegatingComparison implements CloseableComparison
  {
    private Set<Object> objectsToDeactivateOnClose;

    public CDOComparison(Comparison delegate, Set<Object> objectsToDeactivateOnClose)
    {
      super(delegate);
      this.objectsToDeactivateOnClose = objectsToDeactivateOnClose;
    }

    public boolean isClosed()
    {
      return delegate == null;
    }

    public void close()
    {
      if (delegate != null)
      {
        delegate = null;
        if (objectsToDeactivateOnClose != null)
        {
          for (Object object : objectsToDeactivateOnClose)
          {
            LifecycleUtil.deactivate(object);
          }

          objectsToDeactivateOnClose = null;
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class CDOComparisonScope extends AbstractComparisonScope
  {
    public CDOComparisonScope(Notifier left, Notifier right, Notifier origin)
    {
      super(left, right, origin);
    }

    public Iterator<? extends Resource> getCoveredResources(ResourceSet resourceSet)
    {
      return Iterators.emptyIterator();
    }

    public Iterator<? extends EObject> getCoveredEObjects(Resource resource)
    {
      return Iterators.emptyIterator();
    }

    /**
     * @author Eike Stepper
     */
    public static class AllContents extends CDOComparisonScope
    {
      public AllContents(Notifier left, Notifier right, Notifier origin)
      {
        super(left, right, origin);
      }

      public Iterator<? extends EObject> getChildren(EObject eObject)
      {
        return eObject.eAllContents();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Minimal extends CDOComparisonScope implements Predicate<EObject>
    {
      private Set<CDOID> ids;

      public Minimal(CDOView leftView, CDOView rightView, CDOView originView, Set<CDOID> ids)
      {
        super(getRoot(leftView), getRoot(rightView), getRoot(originView));
        this.ids = ids;

        Set<CDOID> requiredParentIDs = new HashSet<CDOID>();
        for (CDOID id : ids)
        {
          CDOObject leftObject = leftView.getObject(id);
          collectRequiredParentIDs(leftObject, requiredParentIDs);

          CDOObject rightObject = rightView.getObject(id);
          collectRequiredParentIDs(rightObject, requiredParentIDs);

          if (originView != null)
          {
            CDOObject originObject = originView.getObject(id);
            collectRequiredParentIDs(originObject, requiredParentIDs);
          }
        }

        ids.addAll(requiredParentIDs);
      }

      public Iterator<? extends EObject> getChildren(EObject eObject)
      {
        return Iterators.filter(eObject.eAllContents(), this);
      }

      public boolean apply(EObject input)
      {
        CDOObject object = CDOUtil.getCDOObject(input);
        CDOID id = object.cdoID();
        return ids.contains(id);
      }

      private void collectRequiredParentIDs(CDOObject object, Set<CDOID> requiredParentIDs)
      {
        CDOView view = object.cdoView();
        CDORevision revision = object.cdoRevision();

        CDOID containerID = (CDOID)revision.data().getContainerID();
        collectRequiredParentIDs(view, containerID, requiredParentIDs);

        CDOID resourceID = revision.data().getResourceID();
        collectRequiredParentIDs(view, resourceID, requiredParentIDs);
      }

      private void collectRequiredParentIDs(CDOView view, CDOID id, Set<CDOID> requiredParentIDs)
      {
        if (!CDOIDUtil.isNull(id))
        {
          if (!ids.contains(id) && !requiredParentIDs.contains(id))
          {
            requiredParentIDs.add(id);

            CDOObject object = view.getObject(id);
            collectRequiredParentIDs(object, requiredParentIDs);
          }
        }
      }

      private static CDOResource getRoot(CDOView view)
      {
        if (view == null)
        {
          return null;
        }

        return view.getRootResource();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class CDOIDFunction implements Function<EObject, String>
  {
    public String apply(EObject o)
    {
      CDOObject object = CDOUtil.getCDOObject(o);
      CDOID id = object.cdoID();

      StringBuilder builder = new StringBuilder();
      CDOIDUtil.write(builder, id);
      return builder.toString();
    }
  }
}
