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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
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
   * Takes an arbitrary {@link CDOObject object} (including {@link CDOResourceNode resource nodes})
   * and returns {@link Match matches} for <b>all</b> elements of its {@link EObject#eAllContents() content tree}. This scope has the advantage that the comparison can
   * be rooted at specific objects that are different from (below of) the root resource. The disadvantage is that all the transitive children of this specific object are
   * matched, whether they differ or not. Major parts of huge repositories can be loaded to the client side easily, if no attention is paid.
   * The following methods return comparisons that are based on this scope algorithm:
   * <ul>
   * <li>{@link CDOCompareUtil#compare(EObject, CDOBranchPoint)}
   * <li>{@link CDOCompareUtil#compare(EObject, CDOBranchPoint, boolean)}
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
      return eObject.eAllContents();
    }
  }

  /**
   * Takes a {@link CDOView view}/{@link CDOTransaction transaction}
   * and returns {@link Match matches} only for the <b>changed</b> elements of the entire content tree of its {@link CDOView#getRootResource() root resource}.
   * The advantage of this scope is that CDO-specific mechanisms are used to efficiently (remotely) determine the set of changed objects. Only those and their container
   * objects are considered as matches, making this scope scale seamlessly with the overall size of a repository.
   * The following methods return comparisons that are based on this scope algorithm:
   * <ul>
   * <li>{@link CDOCompareUtil#compare(CDOView, CDOBranchPoint)}
   * <li>{@link CDOCompareUtil#compare(CDOView, CDOBranchPoint, boolean)}
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
          return Iterators.filter(((EObject)object).eContents().iterator(), Minimal.this);
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
