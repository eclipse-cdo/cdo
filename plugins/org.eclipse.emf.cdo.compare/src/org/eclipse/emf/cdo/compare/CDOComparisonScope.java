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
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.scope.AbstractComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.ProperContentIterator;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSession.MergeData;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A CDO-specific base implementation of a {@link IComparisonScope comparison scope}.
 *
 * @author Eike Stepper
 */
public abstract class CDOComparisonScope extends AbstractComparisonScope
{
  private boolean resolveProxies = true;

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

  public final boolean isResolveProxies()
  {
    return resolveProxies;
  }

  public final void setResolveProxies(boolean resolveProxies)
  {
    this.resolveProxies = resolveProxies;
  }

  private static CDOView openOriginView(CDOView leftView, CDOView rightView, CDOView[] originView)
  {
    if (leftView.getSession() != rightView.getSession())
    {
      throw new IllegalArgumentException("Sessions are different");
    }

    if (originView != null)
    {
      if (originView.length != 1)
      {
        throw new IllegalArgumentException("originView.length != 1");
      }

      if (originView[0] != null)
      {
        throw new IllegalArgumentException("originView[0] != null");
      }

      CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(leftView, rightView);
      if (!ancestor.equals(leftView) && !ancestor.equals(rightView))
      {
        originView[0] = leftView.getSession().openView(ancestor);
        return originView[0];
      }
    }

    return null;
  }

  /**
   * Takes an arbitrary {@link CDOObject object} (including {@link CDOResourceNode resource nodes})
   * and returns {@link Match matches} for <b>all</b> elements of its {@link EObject#eAllContents() content tree}. This scope has the advantage that the comparison can
   * be rooted at specific objects that are different from (below of) the root resource. The disadvantage is that all the transitive children of this specific object are
   * matched, whether they differ or not. Major parts of huge repositories can be loaded to the client side easily, if no attention is paid.
   * The following method returns comparisons that are based on this scope algorithm:
   * <ul>
   * <li>{@link CDOCompareUtil#compare(CDOObject, CDOView, CDOView[])}
   * </ul>
   *
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
      return EcoreUtil.getAllProperContents(eObject, isResolveProxies());
    }

    /**
     * Takes an arbitrary {@link CDOObject object} (including {@link CDOResourceNode resource nodes}) and returns {@link Match matches} for <b>all</b> elements of its {@link EObject#eAllContents() content tree}. This scope has the advantage that the comparison can
     * be rooted at specific objects that are different from (below of) the root resource. The disadvantage is that all the transitive children of this specific object are
     * matched, whether they differ or not. Major parts of huge repositories can be loaded to the client side easily, if no attention is paid.
     */
    public static AllContents create(CDOObject left, CDOView rightView, CDOView[] originView)
    {
      CDOView leftView = left.cdoView();
      CDOView view = openOriginView(leftView, rightView, originView);

      CDOObject right = CDOUtil.getCDOObject(rightView.getObject(left));
      CDOObject origin = view == null ? null : CDOUtil.getCDOObject(view.getObject(left));

      return new CDOComparisonScope.AllContents(left, right, origin);
    }
  }

  /**
   * Takes a {@link CDOView view}/{@link CDOTransaction transaction}
   * and returns {@link Match matches} only for the <b>changed</b> elements of the entire content tree of its {@link CDOView#getRootResource() root resource}.
   * The advantage of this scope is that CDO-specific mechanisms are used to efficiently (remotely) determine the set of changed objects. Only those and their container
   * objects are considered as matches, making this scope scale seamlessly with the overall size of a repository.
   * The following method returns comparisons that are based on this scope algorithm:
   * <ul>
   * <li>{@link CDOCompareUtil#compare(CDOView, CDOView, CDOView[])}
   * </ul>
   *
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
        collectRequiredParentID(leftView, id, requiredParentIDs);
        collectRequiredParentID(rightView, id, requiredParentIDs);
        if (originView != null)
        {
          collectRequiredParentID(originView, id, requiredParentIDs);
        }
      }

      ids.addAll(requiredParentIDs);
    }

    public Iterator<? extends EObject> getChildren(EObject eObject)
    {
      return new AbstractTreeIterator<EObject>(eObject, false)
      {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<EObject> getChildren(Object object)
        {
          if (object instanceof Resource)
          {
            Iterator<EObject> iterator = ((Resource)object).getContents().iterator();
            return Iterators.filter(iterator, Minimal.this);
          }

          Iterator<EObject> iterator = new ProperContentIterator<EObject>((EObject)object, isResolveProxies());
          return Iterators.filter(iterator, Minimal.this);
        }
      };
    }

    public boolean apply(EObject input)
    {
      CDOObject object = CDOUtil.getCDOObject(input);
      CDOID id = object.cdoID();
      return ids.contains(id);
    }

    private void collectRequiredParentIDs(CDOObject object, Set<CDOID> requiredParentIDs)
    {
      CDOState state = object.cdoState();
      if (state == CDOState.TRANSIENT)
      {
        return;
      }

      CDOView view = object.cdoView();
      if (state == CDOState.PROXY)
      {
        CDOUtil.load(object, view);
      }

      CDORevisionData revisionData = object.cdoRevision().data();

      CDOID resourceID = revisionData.getResourceID();
      if (!CDOIDUtil.isNull(resourceID))
      {
        collectRequiredParentIDs(view, resourceID, requiredParentIDs);
      }
      else
      {
        CDOID containerID;

        Object containerOrID = revisionData.getContainerID();
        if (containerOrID instanceof EObject)
        {
          containerID = (CDOID)((InternalCDOView)object.cdoView()).convertObjectToID(containerOrID);
        }
        else
        {
          containerID = (CDOID)containerOrID;
        }

        collectRequiredParentIDs(view, containerID, requiredParentIDs);
      }
    }

    private void collectRequiredParentIDs(CDOView view, CDOID id, Set<CDOID> requiredParentIDs)
    {
      if (!CDOIDUtil.isNull(id))
      {
        if (!ids.contains(id) && requiredParentIDs.add(id))
        {
          collectRequiredParentID(view, id, requiredParentIDs);
        }
      }
    }

    protected void collectRequiredParentID(CDOView view, CDOID id, Set<CDOID> requiredParentIDs)
    {
      try
      {
        CDOObject object = view.getObject(id);
        if (object != null)
        {
          collectRequiredParentIDs(object, requiredParentIDs);
        }
      }
      catch (ObjectNotFoundException ex)
      {
        //$FALL-THROUGH$
      }
    }

    public static IComparisonScope create(CDOView leftView, CDOView rightView, CDOView[] originView)
    {
      CDOView view = openOriginView(leftView, rightView, originView);
      Set<CDOID> ids = getAffectedIDs(leftView, rightView, view);
      return new CDOComparisonScope.Minimal(leftView, rightView, view, ids);
    }

    public static IComparisonScope create(CDOView leftView, CDOView rightView, CDOView[] originView, Set<CDOID> ids)
    {
      CDOView view = openOriginView(leftView, rightView, originView);
      return new CDOComparisonScope.Minimal(leftView, rightView, view, ids);
    }

    public static IComparisonScope create(CDOTransaction transaction)
    {
      CDOSession session = transaction.getSession();
      CDOView lastView = session.openView(transaction.getLastUpdateTime());

      Set<CDOID> ids = new HashSet<CDOID>();
      ids.addAll(transaction.getNewObjects().keySet());
      ids.addAll(transaction.getDirtyObjects().keySet());
      ids.addAll(transaction.getDetachedObjects().keySet());

      return new CDOComparisonScope.Minimal(transaction, lastView, null, ids);
    }

    private static Set<CDOID> getAffectedIDs(CDOView leftView, CDOView rightView, CDOView originView)
    {
      if (originView != null)
      {
        InternalCDOSession session = (InternalCDOSession)leftView.getSession();
        MergeData mergeData = session.getMergeData(leftView, rightView, originView, false);
        return mergeData.getIDs();
      }

      CDOChangeSetData changeSetData = leftView.compareRevisions(rightView);
      return new HashSet<CDOID>(changeSetData.getChangeKinds().keySet());
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
