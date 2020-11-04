/*
 * Copyright (c) 2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.compare;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ReflectUtil;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.conflict.IConflictDetector;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.equi.IEquiEngine;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IEqualityHelperFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.req.IReqEngine;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class with many overridable factory methods that help to create EMF {@link Comparison comparisons}.
 *
 * @author Eike Stepper
 */
public class CDOCompare
{
  public Comparison compare(IComparisonScope scope)
  {
    Function<EObject, String> idFunction = createIDFunction();
    IEObjectMatcher matcher = createMatcher(idFunction);
    IEqualityHelperFactory equalityHelperFactory = createEqualityHelperFactory();
    IComparisonFactory comparisonFactory = createComparisonFactory(equalityHelperFactory);
    EMFCompare comparator = createComparator(matcher, comparisonFactory);

    Comparison comparison = comparator.compare(scope);

    // TODO See EMFCompare.java, line 316
    comparison.eAdapters().add(new ComparisonScopeAdapter(scope));

    return comparison;
  }

  protected CDOIDFunction createIDFunction()
  {
    return new CDOIDFunction();
  }

  protected IdentifierEObjectMatcher createMatcher(Function<EObject, String> idFunction)
  {
    return new CDOMatcher(idFunction);
  }

  protected IEqualityHelperFactory createEqualityHelperFactory()
  {
    return new DefaultEqualityHelperFactory();
  }

  protected IComparisonFactory createComparisonFactory(IEqualityHelperFactory equalityHelperFactory)
  {
    return new DefaultComparisonFactory(equalityHelperFactory);
  }

  protected EMFCompare createComparator(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
  {
    Builder builder = EMFCompare.builder();

    IMatchEngine.Factory.Registry matchEngineFactoryRegistry = createMatchEngineFactoryRegistry(matcher, comparisonFactory);
    if (matchEngineFactoryRegistry != null)
    {
      builder.setMatchEngineFactoryRegistry(matchEngineFactoryRegistry);
    }

    IDiffEngine diffEngine = createDiffEngine();
    if (diffEngine != null)
    {
      builder.setDiffEngine(diffEngine);
    }

    IReqEngine reqEngine = createRequirementEngine();
    if (reqEngine != null)
    {
      builder.setRequirementEngine(reqEngine);
    }

    IEquiEngine equiEngine = createEquivalenceEngine();
    if (equiEngine != null)
    {
      builder.setEquivalenceEngine(equiEngine);
    }

    IPostProcessor.Descriptor.Registry<?> registry = createPostProcessorRegistry();
    if (registry != null)
    {
      builder.setPostProcessorRegistry(registry);
    }

    IConflictDetector conflictDetector = createConflictDetector();
    if (conflictDetector != null)
    {
      builder.setConflictDetector(conflictDetector);
    }

    return builder.build();
  }

  protected IMatchEngine.Factory.Registry createMatchEngineFactoryRegistry(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
  {
    IMatchEngine.Factory.Registry registry = new MatchEngineFactoryRegistryImpl();
    registry.add(new CDOMatchEngine.Factory(matcher, comparisonFactory));
    return registry;
  }

  protected IDiffEngine createDiffEngine()
  {
    return null;
  }

  protected IReqEngine createRequirementEngine()
  {
    return null;
  }

  protected IEquiEngine createEquivalenceEngine()
  {
    return null;
  }

  protected IPostProcessor.Descriptor.Registry<?> createPostProcessorRegistry()
  {
    return null;
  }

  protected IConflictDetector createConflictDetector()
  {
    return null;
  }

  public static IComparisonScope getScope(Comparison comparison)
  {
    ComparisonScopeAdapter adapter = EMFUtil.getAdapter(comparison, ComparisonScopeAdapter.class);
    if (adapter == null)
    {
      return null;
    }

    return adapter.getScope();
  }

  /**
   * An {@link CDOIDFunction ID function} that considers the {@link CDOID}s of {@link CDOObject objects}.
   *
   * @author Eike Stepper
   */
  public static class CDOIDFunction implements Function<EObject, String>
  {
    @Override
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
   * A {@link IEObjectMatcher matcher} that treats {@link Resource resources} as {@link EObject EObjects}.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public static class CDOMatcher extends IdentifierEObjectMatcher
  {
    private static final Method GETPARENTEOBJECT_METHOD;

    private final Function<EObject, String> idComputation;

    public CDOMatcher(Function<EObject, String> idComputation)
    {
      super(idComputation);
      this.idComputation = idComputation;
    }

    @Override
    protected EObject getParentEObject(EObject eObject)
    {
      return CDOElement.getParentOf(eObject);
    }

    @Override
    protected Set<Match> matchPerId(Iterator<? extends EObject> leftEObjects, Iterator<? extends EObject> rightEObjects,
        Iterator<? extends EObject> originEObjects, List<EObject> leftEObjectsNoID, List<EObject> rightEObjectsNoID, List<EObject> originEObjectsNoID)
    {
      if (GETPARENTEOBJECT_METHOD == null)
      {
        return matchPerIdCompatibility(leftEObjects, rightEObjects, originEObjects, leftEObjectsNoID, rightEObjectsNoID, originEObjectsNoID);
      }

      return super.matchPerId(leftEObjects, rightEObjects, originEObjects, leftEObjectsNoID, rightEObjectsNoID, originEObjectsNoID);
    }

    private Set<Match> matchPerIdCompatibility(Iterator<? extends EObject> leftEObjects, Iterator<? extends EObject> rightEObjects,
        Iterator<? extends EObject> originEObjects, List<EObject> leftEObjectsNoID, List<EObject> rightEObjectsNoID, List<EObject> originEObjectsNoID)
    {
      final List<EObject> leftEObjectsNoID1 = leftEObjectsNoID;
      final List<EObject> rightEObjectsNoID1 = rightEObjectsNoID;
      final List<EObject> originEObjectsNoID1 = originEObjectsNoID;
      final Set<Match> matches = Sets.newLinkedHashSet();

      // This lookup map will be used by iterations on right and origin to find the match in which they
      // should add themselves
      final Map<String, Match> idToMatch = Maps.newHashMap();

      // We will try and mimic the structure of the input model.
      // These map do not need to be ordered, we only need fast lookup.
      final Map<EObject, Match> leftEObjectsToMatch = Maps.newHashMap();
      final Map<EObject, Match> rightEObjectsToMatch = Maps.newHashMap();
      final Map<EObject, Match> originEObjectsToMatch = Maps.newHashMap();

      // We'll only iterate once on each of the three sides, building the matches as we go
      while (leftEObjects.hasNext())
      {
        final EObject left = leftEObjects.next();

        final String identifier = idComputation.apply(left);
        if (identifier != null)
        {
          final Match match = CompareFactory.eINSTANCE.createMatch();
          match.setLeft(left);

          // Can we find a parent? Assume we're iterating in containment order
          final EObject parentEObject = getParentEObject(left);
          final Match parent = leftEObjectsToMatch.get(parentEObject);
          if (parent != null)
          {
            ((InternalEList<Match>)parent.getSubmatches()).addUnique(match);
          }
          else
          {
            matches.add(match);
          }
          idToMatch.put(identifier, match);
          leftEObjectsToMatch.put(left, match);
        }
        else
        {
          leftEObjectsNoID1.add(left);
        }
      }

      while (rightEObjects.hasNext())
      {
        final EObject right = rightEObjects.next();

        // Do we have an existing match?
        final String identifier = idComputation.apply(right);
        if (identifier != null)
        {
          Match match = idToMatch.get(identifier);
          if (match != null)
          {
            match.setRight(right);

            rightEObjectsToMatch.put(right, match);
          }
          else
          {
            // Otherwise, create and place it.
            match = CompareFactory.eINSTANCE.createMatch();
            match.setRight(right);

            // Can we find a parent?
            final EObject parentEObject = getParentEObject(right);
            final Match parent = rightEObjectsToMatch.get(parentEObject);
            if (parent != null)
            {
              ((InternalEList<Match>)parent.getSubmatches()).addUnique(match);
            }
            else
            {
              matches.add(match);
            }

            rightEObjectsToMatch.put(right, match);
            idToMatch.put(identifier, match);
          }
        }
        else
        {
          rightEObjectsNoID1.add(right);
        }
      }

      while (originEObjects.hasNext())
      {
        final EObject origin = originEObjects.next();

        // Do we have an existing match?
        final String identifier = idComputation.apply(origin);
        if (identifier != null)
        {
          Match match = idToMatch.get(identifier);
          if (match != null)
          {
            match.setOrigin(origin);

            originEObjectsToMatch.put(origin, match);
          }
          else
          {
            // Otherwise, create and place it.
            match = CompareFactory.eINSTANCE.createMatch();
            match.setOrigin(origin);

            // Can we find a parent?
            final EObject parentEObject = getParentEObject(origin);
            final Match parent = originEObjectsToMatch.get(parentEObject);
            if (parent != null)
            {
              ((InternalEList<Match>)parent.getSubmatches()).addUnique(match);
            }
            else
            {
              matches.add(match);
            }

            idToMatch.put(identifier, match);
            originEObjectsToMatch.put(origin, match);
          }
        }
        else
        {
          originEObjectsNoID1.add(origin);
        }
      }
      return matches;
    }

    static
    {
      Method method = null;

      try
      {
        method = ReflectUtil.getMethod(IdentifierEObjectMatcher.class, "getParentEObject", EObject.class);
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }

      GETPARENTEOBJECT_METHOD = method;
    }
  }

  /**
   * A {@link DefaultMatchEngine match engine} that treats {@link Resource resources} as {@link EObject EObjects}.
   *
   * @author Eike Stepper
   */
  public static class CDOMatchEngine extends DefaultMatchEngine
  {
    CDOMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
    {
      super(matcher, comparisonFactory);
    }

    @Override
    protected void match(Comparison comparison, IComparisonScope scope, final Notifier left, final Notifier right, final Notifier origin, Monitor monitor)
    {
      // Omit special treatment of Resources (and ResourceSets). Just match EObjects.
      match(comparison, scope, (EObject)left, (EObject)right, (EObject)origin, monitor);
    }

    /**
     * Creates {@link CDOMatchEngine match engine} instances.
     *
     * @author Eike Stepper
     */
    public static class Factory implements IMatchEngine.Factory
    {
      private final IMatchEngine matchEngine;

      private int ranking;

      public Factory(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
      {
        matchEngine = createMatchEngine(matcher, comparisonFactory);
      }

      protected Factory(IMatchEngine matchEngine)
      {
        this.matchEngine = matchEngine;
      }

      protected CDOMatchEngine createMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
      {
        return new CDOMatchEngine(matcher, comparisonFactory);
      }

      @Override
      public IMatchEngine getMatchEngine()
      {
        return matchEngine;
      }

      @Override
      public int getRanking()
      {
        return ranking;
      }

      @Override
      public void setRanking(int ranking)
      {
        this.ranking = ranking;
      }

      @Override
      public boolean isMatchEngineFactoryFor(IComparisonScope scope)
      {
        return scope instanceof CDOComparisonScope;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ComparisonScopeAdapter extends AdapterImpl
  {
    private IComparisonScope scope;

    public ComparisonScopeAdapter(IComparisonScope scope)
    {
      this.scope = scope;
    }

    public final IComparisonScope getScope()
    {
      return scope;
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type == ComparisonScopeAdapter.class;
    }
  }
}
