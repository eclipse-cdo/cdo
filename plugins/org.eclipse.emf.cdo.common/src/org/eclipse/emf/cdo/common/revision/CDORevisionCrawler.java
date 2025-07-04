/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.FeatureStrategy.Decision;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * TODO unordered lists
 *
 * @author Eike Stepper
 * @since 4.26
 */
public final class CDORevisionCrawler implements CDORevisionCacheAdder
{
  private final Handler handler;

  private ContainmentProxyStrategy containmentProxyStrategy = ContainmentProxyStrategy.Physical;

  private FeatureStrategy featureStrategy = FeatureStrategy.TREE;

  private CDORevisionProvider revisionProvider;

  private long revisionCount;

  public CDORevisionCrawler(Handler handler)
  {
    this.handler = Objects.requireNonNull(handler);
  }

  public Handler getHandler()
  {
    return handler;
  }

  public ContainmentProxyStrategy getContainmentProxyStrategy()
  {
    return containmentProxyStrategy;
  }

  public void setContainmentProxyStrategy(ContainmentProxyStrategy containmentProxyStrategy)
  {
    this.containmentProxyStrategy = ObjectUtil.requireNonNullElse(containmentProxyStrategy, ContainmentProxyStrategy.Physical);
  }

  public FeatureStrategy getFeatureStrategy()
  {
    return featureStrategy;
  }

  public void setFeatureStrategy(FeatureStrategy featureStrategy)
  {
    this.featureStrategy = ObjectUtil.requireNonNullElse(featureStrategy, FeatureStrategy.TREE);
  }

  public CDORevisionProvider getRevisionProvider()
  {
    return revisionProvider;
  }

  public void setRevisionProvider(CDORevisionProvider revisionProvider)
  {
    this.revisionProvider = revisionProvider;
  }

  public long getRevisionCount()
  {
    return revisionCount;
  }

  @Override
  public void addRevision(CDORevision revision)
  {
    Queue<InternalCDORevision> queue = new LinkedList<>();
    queue.offer((InternalCDORevision)revision);

    while (!queue.isEmpty())
    {
      InternalCDORevision rev = queue.remove();
      ++revisionCount;

      if (handler.beginRevision(this, rev))
      {
        for (EStructuralFeature feature : rev.getClassInfo().getAllPersistentFeatures())
        {
          Decision decision = featureStrategy.decide(rev, feature);

          if (decision.isHandle())
          {
            handleFeature(rev, feature);
          }

          if (decision.isFollow() && revisionProvider != null)
          {
            followReference(rev, feature, queue);
          }
        }

        handler.endRevision(this, rev);
      }
    }
  }

  private void handleFeature(InternalCDORevision rev, EStructuralFeature feature)
  {
    handler.handleFeature(this, rev, feature);
  }

  private void followReference(InternalCDORevision revision, EStructuralFeature feature, Queue<InternalCDORevision> queue)
  {
    if (feature instanceof EReference)
    {
      EReference reference = (EReference)feature;
      boolean containment = reference.isContainment();
      boolean parentIsResource = revision.isResource();

      if (reference.isMany())
      {
        CDOList list = revision.getListOrNull(reference);
        if (list != null)
        {
          for (Object value : list)
          {
            enqueueTarget(containment, parentIsResource, (CDOID)value, queue);
          }
        }
      }
      else
      {
        Object value = revision.getValue(reference);
        enqueueTarget(containment, parentIsResource, (CDOID)value, queue);
      }
    }
  }

  private void enqueueTarget(boolean containment, boolean parentIsResource, CDOID targetID, Queue<InternalCDORevision> queue)
  {
    if (!CDOIDUtil.isNull(targetID))
    {
      InternalCDORevision target = (InternalCDORevision)revisionProvider.getRevision(targetID);
      if (containment)
      {
        if (!CDOIDUtil.isNull((CDOID)target.getContainerID()) && !CDOIDUtil.isNull(target.getResourceID()))
        {
          if (!containmentProxyStrategy.follow(parentIsResource))
          {
            return;
          }
        }
      }

      queue.offer(target);
    }
  }

  /**
   * @author Eike Stepper
   */
  public enum ContainmentProxyStrategy
  {
    Physical
    {
      @Override
      protected boolean follow(boolean parentIsResource)
      {
        return parentIsResource;
      }
    },

    Logical
    {
      @Override
      protected boolean follow(boolean parentIsResource)
      {
        return !parentIsResource;
      }
    },

    Any
    {
      @Override
      protected boolean follow(boolean parentIsResource)
      {
        return true;
      }
    };

    protected abstract boolean follow(boolean parentIsResource);
  }

  /**
   * @author Eike Stepper
   */
  public interface FeatureStrategy
  {
    public static final FeatureStrategy SINGLE = (revision, feature) -> Decision.Handle;

    public static final FeatureStrategy TREE = (revision, feature) -> {
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          return Decision.Follow;
        }
      }

      return Decision.Handle;
    };

    public Decision decide(CDORevision revision, EStructuralFeature feature);

    /**
     * @author Eike Stepper
     */
    public enum Decision
    {
      Skip(false, false), //
      Handle(true, false), //
      Follow(false, true), //
      HandleAndFollow(true, true);

      private final boolean handle;

      private final boolean follow;

      private Decision(boolean handle, boolean follow)
      {
        this.handle = handle;
        this.follow = follow;
      }

      public boolean isHandle()
      {
        return handle;
      }

      public boolean isFollow()
      {
        return follow;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Handler
  {
    public default boolean beginRevision(CDORevisionCrawler crawler, CDORevision revision)
    {
      // Continue crawling.
      return true;
    }

    public default void handleFeature(CDORevisionCrawler crawler, CDORevision revision, EStructuralFeature feature)
    {
      // Do nothing.
    }

    public default void endRevision(CDORevisionCrawler crawler, CDORevision revision)
    {
      // Do nothing.
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class OutputStreamHandler implements Handler
  {
    private static final byte OPCODE_UNSET = 0;

    private static final byte OPCODE_SET_NULL = 1;

    private static final byte OPCODE_SET_NOT_NULL = 2;

    private final CDODataOutput out;

    private final LocalIDMapper localIDMapper;

    private CDORevisionCrawler crawler;

    public OutputStreamHandler(OutputStream stream)
    {
      this(stream, false);
    }

    public OutputStreamHandler(OutputStream stream, boolean localIDs)
    {
      this(stream, localIDs ? new LocalIDMapper.InMemory() : null);
    }

    public OutputStreamHandler(OutputStream stream, LocalIDMapper localIDMapper)
    {
      this(new CDODataOutputImpl(new ExtendedDataOutputStream(stream))
      {
        private final CDOIDProvider idProvider = localIDMapper == null ? //
            CDOIDProvider.NOOP : //
            realID -> localIDMapper.lookup((CDOID)realID);

        @Override
        public CDOIDProvider getIDProvider()
        {
          return idProvider;
        }
      }, localIDMapper);
    }

    protected OutputStreamHandler(CDODataOutput out, LocalIDMapper localIDMapper)
    {
      this.out = out;
      this.localIDMapper = localIDMapper;
    }

    public LocalIDMapper getLocalIDMapper()
    {
      return localIDMapper;
    }

    @Override
    public final boolean beginRevision(CDORevisionCrawler crawler, CDORevision revision)
    {
      setCrawler(crawler);

      if (localIDMapper != null)
      {
        // Just map a new local ID...
        CDOID realID = revision.getID();
        localIDMapper.map(realID);

        // ... and continue crawling.
        return true;
      }

      return doBeginRevision(crawler, revision);
    }

    @Override
    public final void handleFeature(CDORevisionCrawler crawler, CDORevision revision, EStructuralFeature feature)
    {
      if (localIDMapper != null)
      {
        // For local IDs this is the first (id mapping) phase.
        // The real feature handling happens in finishLocalIDs(CDORevisionProvider).
        return;
      }

      doHandleFeature(crawler, revision, feature);
    }

    @Override
    public final void endRevision(CDORevisionCrawler crawler, CDORevision revision)
    {
      if (localIDMapper != null)
      {
        // For local IDs this is the first (id mapping) phase.
        // The real feature handling happens in finishLocalIDs(CDORevisionProvider).
        return;
      }

      doEndRevision(crawler, revision);
    }

    public final void finishLocalIDs()
    {
      if (localIDMapper == null || crawler == null)
      {
        return;
      }

      CDORevisionProvider revisionProvider = crawler.getRevisionProvider();
      if (revisionProvider == null)
      {
        return;
      }

      FeatureStrategy featureStrategy = crawler.getFeatureStrategy();

      localIDMapper.forEach(realID -> {
        CDORevision rev = revisionProvider.getRevision(realID);

        for (EStructuralFeature feature : rev.getClassInfo().getAllPersistentFeatures())
        {
          Decision decision = featureStrategy.decide(rev, feature);
          if (decision.isHandle())
          {
            doHandleFeature(crawler, rev, feature);
          }
        }

        doEndRevision(crawler, rev);
      });
    }

    protected boolean doBeginRevision(CDORevisionCrawler crawler, CDORevision revision)
    {
      CDOID id = revision.getID();

      if (localIDMapper != null)
      {
        id = localIDMapper.lookup(id);
      }

      try
      {
        out.writeCDOID(id);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }

      return true;
    }

    protected void doHandleFeature(CDORevisionCrawler crawler, CDORevision revision, EStructuralFeature feature)
    {
      try
      {
        Object value = ((InternalCDORevision)revision).getValue(feature);
        if (value == null)
        {
          // Feature IS NOT SET
          out.writeByte(OPCODE_UNSET);
          return;
        }

        // Feature IS SET
        if (value == CDORevisionData.NIL)
        {
          // Feature IS NULL
          out.writeByte(OPCODE_SET_NULL);
          return;
        }

        // Feature IS NOT NULL
        out.writeByte(OPCODE_SET_NOT_NULL);

        if (feature.isMany())
        {
          CDOList list = (CDOList)value;
          EClass eClass = revision.getEClass();
          UnaryOperator<CDOID> idConverter = localIDMapper == null ? null : localIDMapper::lookup;
          CDODataOutputImpl.writeCDOList(out, eClass, feature, list, CDORevision.UNCHUNKED, idConverter);
        }
        else
        {
          if (localIDMapper != null && feature instanceof EReference)
          {
            value = localIDMapper.lookup((CDOID)value);
          }

          out.writeCDOFeatureValue(feature, value);
        }
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    protected void doEndRevision(CDORevisionCrawler crawler, CDORevision revision)
    {
      // Do nothing.
    }

    private void setCrawler(CDORevisionCrawler crawler)
    {
      if (this.crawler == null)
      {
        this.crawler = crawler;
      }
      else if (crawler != this.crawler)
      {
        throw new IllegalArgumentException("Different crawler: " + crawler);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static abstract class LocalIDMapper
    {
      private long nextLocalID;

      public LocalIDMapper()
      {
      }

      public CDOID map(CDOID realID)
      {
        CDOID localID = getNextLocalID();
        register(realID, localID);
        return localID;
      }

      public abstract CDOID lookup(CDOID realID);

      public abstract void forEach(Consumer<? super CDOID> realIDConsumer);

      protected abstract void register(CDOID realID, CDOID localID);

      protected CDOID getNextLocalID()
      {
        return CDOIDUtil.createLong(++nextLocalID);
      }

      /**
       * @author Eike Stepper
       */
      public static final class InMemory extends LocalIDMapper
      {
        private final Map<CDOID, CDOID> localIDs = new LinkedHashMap<>();

        public InMemory()
        {
        }

        @Override
        public CDOID lookup(CDOID realID)
        {
          return localIDs.get(realID);
        }

        @Override
        public void forEach(Consumer<? super CDOID> realIDConsumer)
        {
          // The encounter order of LinkedHashMap.iterator() is preserved by keySet().iterator().
          localIDs.keySet().forEach(realIDConsumer);
        }

        @Override
        protected void register(CDOID realID, CDOID localID)
        {
          localIDs.put(realID, localID);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MessageDigestHandler extends OutputStreamHandler
  {
    public MessageDigestHandler(MessageDigest digest)
    {
      super(newOutputStream(digest));
    }

    public MessageDigestHandler(MessageDigest digest, boolean localIDs)
    {
      super(newOutputStream(digest), localIDs);
    }

    public MessageDigestHandler(MessageDigest digest, LocalIDMapper localIDMapper)
    {
      super(newOutputStream(digest), localIDMapper);
    }

    private static DigestOutputStream newOutputStream(MessageDigest digest)
    {
      return new DigestOutputStream(OutputStream.nullOutputStream(), digest);
    }
  }
}
