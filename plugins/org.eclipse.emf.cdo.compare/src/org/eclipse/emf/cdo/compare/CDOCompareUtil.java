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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.compare.CDOCompareUtil.CDOComparisonScope.AllContents;
import org.eclipse.emf.cdo.compare.CDOCompareUtil.CDOComparisonScope.Minimal;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompareConfiguration;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.MatchResource;
import org.eclipse.emf.compare.conflict.DefaultConflictDetector;
import org.eclipse.emf.compare.conflict.IConflictDetector;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.compare.equi.DefaultEquiEngine;
import org.eclipse.emf.compare.equi.IEquiEngine;
import org.eclipse.emf.compare.extension.EMFCompareExtensionRegistry;
import org.eclipse.emf.compare.extension.IPostProcessor;
import org.eclipse.emf.compare.extension.PostProcessorDescriptor;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.match.resource.IResourceMatcher;
import org.eclipse.emf.compare.match.resource.StrategyResourceMatcher;
import org.eclipse.emf.compare.req.DefaultReqEngine;
import org.eclipse.emf.compare.req.IReqEngine;
import org.eclipse.emf.compare.scope.AbstractComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSession.MergeData;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Provides static factory methods that return CDO-scoped {@link Comparison comparisons}.
 * <p>
 * Two different {@link IComparisonScope scopes} are supported:
 * <ul>
 * <li>{@link AllContents CDOComparisonScope.AllContents} takes an arbitrary {@link CDOObject object} (including {@link CDOResourceNode resource nodes})
 * and returns {@link Match matches} for <b>all</b> elements of its {@link EObject#eAllContents() content tree}. This scope has the advantage that the comparison can
 * be rooted at specific objects that are different from (below of) the root resource. The disadvantage is that all the transitive children of this specific object are
 * matched, whether they differ or not. Major parts of huge repositories can be loaded to the client side easily, if no attention is paid.
 * The following methods return comparisons that are based on this scope algorithm:
 * <ul>
 * <li>{@link #compare(EObject, CDOBranchPoint)}
 * <li>{@link #compare(EObject, CDOBranchPoint, boolean)}
 * </ul>
 * <li>{@link Minimal CDOComparisonScope.Minimal} takes a {@link CDOView view}/{@link CDOTransaction transaction}
 * and returns {@link Match matches} only for the <b>changed</b> elements of the entire content tree of its {@link CDOView#getRootResource() root resource}.
 * The advantage of this scope is that CDO-specific mechanisms are used to efficiently (remotely) determine the set of changed objects. Only those and their container
 * objects are considered as matches, making this scope scale seamlessly with the overall size of a repository.
 * The following methods return comparisons that are based on this scope algorithm:
 * <ul>
 * <li>{@link #compare(CDOView, CDOBranchPoint)}
 * <li>{@link #compare(CDOView, CDOBranchPoint, boolean)}
 * </ul>
 * </ul>
 * The {@link IComparisonScope#getRight() right side} of a comparison is specified as a {@link CDOBranchPoint} which, among others, can be another {@link CDOView}
 * (which then is not closed when the comparison is closed) or a {@link CDOCommitInfo}. The {@link IComparisonScope#getOrigin() origin side} of a comparison is
 * automatically {@link CDOBranchUtil#getAncestor(CDOBranchPoint, CDOBranchPoint) determined} by inspecting the {@link CDOBranch branch tree} and used if its different from the left or right side.
 * <p>
 * The comparions returned from these factory methods are all of the type {@link CloseableComparison} and the caller is responsible to call {@link CloseableComparison#close()}
 * on them when they're not needed anymore. The reason is that the scopes may or may not open a number of addional {@link CDOView views} on the local {@link CDOSession session}
 * that need to be closed at some point.
 * <p>
 * The {@link IEObjectMatcher matcher} used by the comparisons is based on an {@link CDOIDFunction ID function} that considers the {@link CDOID}s of the {@link CDOObject objects}.
 * {@link CDOResource Resources} and {@link CDOResourceFolder folders} are treated as normal {@link EObject}s.
 *
 * @author Eike Stepper
 */
public final class CDOCompareUtil
{
  private CDOCompareUtil()
  {
  }

  /**
   * Same as {@link #compare(CDOView, CDOBranchPoint, boolean) compare(leftRoot, right, true)}.
   */
  public static CloseableComparison compare(EObject leftRoot, CDOBranchPoint right)
  {
    return compare(leftRoot, right, true);
  }

  /**
   * Takes an arbitrary {@link CDOObject object} (including {@link CDOResourceNode resource nodes}) and returns {@link Match matches} for <b>all</b> elements of its {@link EObject#eAllContents() content tree}. This scope has the advantage that the comparison can
   * be rooted at specific objects that are different from (below of) the root resource. The disadvantage is that all the transitive children of this specific object are
   * matched, whether they differ or not. Major parts of huge repositories can be loaded to the client side easily, if no attention is paid.
   */
  public static CloseableComparison compare(EObject leftRoot, CDOBranchPoint right, boolean tryThreeWay)
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
    if (tryThreeWay)
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

  /**
   * Same as {@link #compare(EObject, CDOBranchPoint, boolean) compare(leftView, right, true)}.
   */
  public static CloseableComparison compare(CDOView leftView, CDOBranchPoint right)
  {
    return compare(leftView, right, true);
  }

  /**
   * Takes a {@link CDOView view}/{@link CDOTransaction transaction}
   * and returns {@link Match matches} only for the <b>changed</b> elements of the entire content tree of its {@link CDOView#getRootResource() root resource}.
   * The advantage of this scope is that CDO-specific mechanisms are used to efficiently (remotely) determine the set of changed objects. Only those and their container
   * objects are considered as matches, making this scope scale seamlessly with the overall size of a repository.
   */
  public static CloseableComparison compare(CDOView leftView, CDOBranchPoint right, boolean tryThreeWay)
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
    if (tryThreeWay)
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

  private static EMFCompare createComparator(IComparisonScope scope)
  {
    Function<EObject, String> idFunction = new CDOIDFunction();
    IEObjectMatcher matcher = new IdentifierEObjectMatcher.Builder().idFunction(idFunction).build();

    EMFCompare comparator = EMFCompare.newComparator(scope);
    comparator.setEObjectMatcher(matcher);
    return comparator;
  }

  private static CloseableComparison createComparison(IComparisonScope scope, Set<Object> objectsToDeactivateOnClose)
  {
    EMFCompare comparator = createComparator(scope);
    Comparison comparison = comparator.compare();
    return new CDOComparison(comparison, objectsToDeactivateOnClose);
  }

  /**
   * A {@link Comparison comparison} that can be closed to dispose of used resources.
   *
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
   * A CDO-specific base implementation of a {@link IComparisonScope comparison scope}.
   *
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
   * An {@link CDOIDFunction ID function} that considers the {@link CDOID}s of {@link CDOObject objects}.
   *
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

  /**
   * FIXME: To be removed when bug 390849 is resolved.
   *
   * @author Eike Stepper
   */
  private static final class EMFCompare
  {
    private IEObjectMatcher eObjectMatcher;

    private Monitor progressMonitor;

    private final IComparisonScope scope;

    private EMFCompare(IComparisonScope scope)
    {
      checkNotNull(scope);
      this.scope = scope;
    }

    public static EMFCompare newComparator(IComparisonScope scope)
    {
      return new EMFCompare(scope);
    }

    private static Comparison compare(IComparisonScope scope, EMFCompareConfiguration configuration,
        IEObjectMatcher matcher)
    {
      final IMatchEngine matchEngine = new DefaultMatchEngine(matcher)
      {
        /**
         * FIXME: CDO-specific.
         */
        @Override
        protected void match(Notifier left, Notifier right, Notifier origin)
        {
          match((EObject)left, (EObject)right, (EObject)origin);
        }
      };

      Comparison comparison = matchEngine.match(scope, configuration);

      IPostProcessor postProcessor = getPostProcessor(scope);
      if (postProcessor != null)
      {
        postProcessor.postMatch(comparison);
      }

      final IDiffProcessor diffBuilder = new DiffBuilder();

      final IDiffEngine diffEngine = new DefaultDiffEngine(diffBuilder);
      diffEngine.diff(comparison);

      if (postProcessor != null)
      {
        postProcessor.postDiff(comparison);
      }

      final IReqEngine reqEngine = new DefaultReqEngine();
      reqEngine.computeRequirements(comparison);

      if (postProcessor != null)
      {
        postProcessor.postRequirements(comparison);
      }

      final IEquiEngine equiEngine = new DefaultEquiEngine();
      equiEngine.computeEquivalences(comparison);

      if (postProcessor != null)
      {
        postProcessor.postEquivalences(comparison);
      }

      if (comparison.isThreeWay())
      {
        final IConflictDetector conflictDetector = new DefaultConflictDetector();
        conflictDetector.detect(comparison);

        if (postProcessor != null)
        {
          postProcessor.postConflicts(comparison);
        }
      }

      return comparison;
    }

    private static IPostProcessor getPostProcessor(IComparisonScope scope)
    {
      IPostProcessor postProcessor = null;
      final Iterator<PostProcessorDescriptor> postProcessorIterator = EMFCompareExtensionRegistry
          .getRegisteredPostProcessors().iterator();
      while (postProcessorIterator.hasNext() && postProcessor == null)
      {
        final PostProcessorDescriptor descriptor = postProcessorIterator.next();
        if (descriptor.getNsURI() != null && descriptor.getNsURI().trim().length() != 0)
        {
          final Iterator<String> nsUris = scope.getNsURIs().iterator();
          while (nsUris.hasNext() && postProcessor == null)
          {
            if (nsUris.next().matches(descriptor.getNsURI()))
            {
              postProcessor = descriptor.getPostProcessor();
            }
          }
        }

        if (descriptor.getResourceURI() != null && descriptor.getResourceURI().trim().length() != 0)
        {
          final Iterator<String> resourceUris = scope.getResourceURIs().iterator();
          while (resourceUris.hasNext() && postProcessor == null)
          {
            if (resourceUris.next().matches(descriptor.getResourceURI()))
            {
              postProcessor = descriptor.getPostProcessor();
            }
          }
        }
      }
      return postProcessor;
    }

    public Comparison compare()
    {
      final Monitor monitor;
      if (progressMonitor != null)
      {
        monitor = progressMonitor;
      }
      else
      {
        monitor = new BasicMonitor();
      }

      EqualityHelper helper = new EqualityHelper();
      EMFCompareConfiguration configuration = new EMFCompareConfiguration(monitor, helper);
      IEObjectMatcher matcher = createMatcher(helper);

      return compare(scope, configuration, matcher);
    }

    public EMFCompare setEObjectMatcher(IEObjectMatcher matcher)
    {
      if (matcher != null)
      {
        eObjectMatcher = matcher;
      }
      return this;
    }

    private IEObjectMatcher createMatcher(EqualityHelper helper)
    {
      return eObjectMatcher;
    }

    /**
     * FIXME: Remove this when bug 390846 has been resolved.
     *
     * @author Eike Stepper
     */
    private static class DefaultMatchEngine implements IMatchEngine
    {
      private Comparison comparison;

      private IComparisonScope comparisonScope;

      private IEObjectMatcher eObjectMatcher;

      public DefaultMatchEngine(IEObjectMatcher matcher)
      {
        checkNotNull(matcher);
        eObjectMatcher = matcher;
      }

      public Comparison match(IComparisonScope scope, EMFCompareConfiguration configuration)
      {
        comparisonScope = scope;
        associate(getComparison(), configuration);

        final Notifier left = getScope().getLeft();
        final Notifier right = getScope().getRight();
        final Notifier origin = getScope().getOrigin();

        getComparison().setThreeWay(origin != null);

        match(left, right, origin);

        return getComparison();
      }

      protected void match(final Notifier left, final Notifier right, final Notifier origin)
      {
        if (left instanceof ResourceSet || right instanceof ResourceSet)
        {
          match((ResourceSet)left, (ResourceSet)right, (ResourceSet)origin);
        }
        else if (left instanceof Resource || right instanceof Resource)
        {
          match((Resource)left, (Resource)right, (Resource)origin);
        }
        else if (left instanceof EObject || right instanceof EObject)
        {
          match((EObject)left, (EObject)right, (EObject)origin);
        }
      }

      protected void match(ResourceSet left, ResourceSet right, ResourceSet origin)
      {
        final Iterator<? extends Resource> leftChildren = getScope().getCoveredResources(left);
        final Iterator<? extends Resource> rightChildren = getScope().getCoveredResources(right);
        final Iterator<? extends Resource> originChildren;
        if (origin != null)
        {
          originChildren = getScope().getCoveredResources(origin);
        }
        else
        {
          originChildren = Iterators.emptyIterator();
        }

        final IResourceMatcher resourceMatcher = getResourceMatcher();
        final Iterable<MatchResource> mappings = resourceMatcher.createMappings(leftChildren, rightChildren,
            originChildren);

        Iterator<? extends EObject> leftEObjects = Iterators.emptyIterator();
        Iterator<? extends EObject> rightEObjects = Iterators.emptyIterator();
        Iterator<? extends EObject> originEObjects = Iterators.emptyIterator();

        for (MatchResource mapping : mappings)
        {
          getComparison().getMatchedResources().add(mapping);

          final Resource leftRes = mapping.getLeft();
          final Resource rightRes = mapping.getRight();
          final Resource originRes = mapping.getOrigin();

          if (leftRes != null)
          {
            leftEObjects = Iterators.concat(leftEObjects, getScope().getCoveredEObjects(leftRes));
          }

          if (rightRes != null)
          {
            rightEObjects = Iterators.concat(rightEObjects, getScope().getCoveredEObjects(rightRes));
          }

          if (originRes != null)
          {
            originEObjects = Iterators.concat(originEObjects, getScope().getCoveredEObjects(originRes));
          }
        }

        final Iterable<Match> matches = getEObjectMatcher().createMatches(leftEObjects, rightEObjects, originEObjects);
        Iterables.addAll(getComparison().getMatches(), matches);
      }

      protected void match(Resource left, Resource right, Resource origin)
      {
        // Our "roots" are Resources. Consider them matched
        final MatchResource match = CompareFactory.eINSTANCE.createMatchResource();

        match.setLeft(left);
        match.setRight(right);
        match.setOrigin(origin);

        if (left != null)
        {
          URI uri = left.getURI();
          if (uri != null)
          {
            match.setLeftURI(uri.toString());
          }
        }

        if (right != null)
        {
          URI uri = right.getURI();
          if (uri != null)
          {
            match.setRightURI(uri.toString());
          }
        }

        if (origin != null)
        {
          URI uri = origin.getURI();
          if (uri != null)
          {
            match.setOriginURI(uri.toString());
          }
        }

        getComparison().getMatchedResources().add(match);

        // We need at least two resources to match them
        if (atLeastTwo(left == null, right == null, origin == null))
        {
          return;
        }

        final Iterator<? extends EObject> leftEObjects;
        if (left != null)
        {
          leftEObjects = getScope().getCoveredEObjects(left);
        }
        else
        {
          leftEObjects = Iterators.emptyIterator();
        }
        final Iterator<? extends EObject> rightEObjects;
        if (right != null)
        {
          rightEObjects = getScope().getCoveredEObjects(right);
        }
        else
        {
          rightEObjects = Iterators.emptyIterator();
        }
        final Iterator<? extends EObject> originEObjects;
        if (origin != null)
        {
          originEObjects = getScope().getCoveredEObjects(origin);
        }
        else
        {
          originEObjects = Iterators.emptyIterator();
        }

        final Iterable<Match> matches = getEObjectMatcher().createMatches(leftEObjects, rightEObjects, originEObjects);

        Iterables.addAll(getComparison().getMatches(), matches);
      }

      protected void match(EObject left, EObject right, EObject origin)
      {
        if (left == null || right == null)
        {
          throw new IllegalArgumentException();
        }

        final Iterator<? extends EObject> leftEObjects = Iterators.concat(Iterators.singletonIterator(left), getScope()
            .getChildren(left));
        final Iterator<? extends EObject> rightEObjects = Iterators.concat(Iterators.singletonIterator(right),
            getScope().getChildren(right));
        final Iterator<? extends EObject> originEObjects;
        if (origin != null)
        {
          originEObjects = Iterators.concat(Iterators.singletonIterator(origin), getScope().getChildren(origin));
        }
        else
        {
          originEObjects = Iterators.emptyIterator();
        }

        final Iterable<Match> matches = getEObjectMatcher().createMatches(leftEObjects, rightEObjects, originEObjects);

        Iterables.addAll(getComparison().getMatches(), matches);
      }

      protected IResourceMatcher getResourceMatcher()
      {
        return new StrategyResourceMatcher();
      }

      protected IEObjectMatcher getEObjectMatcher()
      {
        return eObjectMatcher;
      }

      protected Comparison getComparison()
      {
        if (comparison == null)
        {
          comparison = CompareFactory.eINSTANCE.createComparison();
        }
        return comparison;
      }

      protected IComparisonScope getScope()
      {
        return comparisonScope;
      }

      protected static boolean atLeastTwo(boolean condition1, boolean condition2, boolean condition3)
      {
        // CHECKSTYLE:OFF This expression is alone in its method, and documented.
        return condition1 && (condition2 || condition3) || condition2 && condition3;
        // CHECKSTYLE:ON
      }

      private static void associate(Comparison comparison, EMFCompareConfiguration configuration)
      {
        Iterator<Adapter> eAdapters = comparison.eAdapters().iterator();
        while (eAdapters.hasNext())
        {
          Adapter eAdapter = eAdapters.next();
          if (eAdapter.isAdapterForType(EMFCompareConfiguration.class))
          {
            eAdapters.remove();
            if (eAdapter instanceof Adapter.Internal)
            {
              ((Adapter.Internal)eAdapter).unsetTarget(comparison);
            }
          }
        }

        comparison.eAdapters().add(configuration);
        configuration.setTarget(comparison);
      }
    }
  }
}
