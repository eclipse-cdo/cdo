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
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobLoader;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.FeatureStrategy.Decision;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

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
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * TODO unordered lists
 *
 * @author Eike Stepper
 * @since 4.26
 */
public final class CDORevisionCrawler extends Lifecycle
{
  private Handler handler;

  private ContainmentProxyStrategy containmentProxyStrategy = ContainmentProxyStrategy.Physical;

  private FeatureStrategy featureStrategy = FeatureStrategy.TREE;

  private CDORevisionProvider revisionProvider;

  private long revisionCount;

  public CDORevisionCrawler()
  {
  }

  public Handler handler()
  {
    return handler;
  }

  public CDORevisionCrawler handler(Handler handler)
  {
    checkInactive();
    this.handler = handler;
    return this;
  }

  public ContainmentProxyStrategy containmentProxyStrategy()
  {
    return containmentProxyStrategy;
  }

  public CDORevisionCrawler containmentProxyStrategy(ContainmentProxyStrategy containmentProxyStrategy)
  {
    checkInactive();
    this.containmentProxyStrategy = containmentProxyStrategy;
    return this;
  }

  public FeatureStrategy featureStrategy()
  {
    return featureStrategy;
  }

  public CDORevisionCrawler featureStrategy(FeatureStrategy featureStrategy)
  {
    checkInactive();
    this.featureStrategy = featureStrategy;
    return this;
  }

  public CDORevisionProvider revisionProvider()
  {
    return revisionProvider;
  }

  public CDORevisionCrawler revisionProvider(CDORevisionProvider revisionProvider)
  {
    checkInactive();
    this.revisionProvider = revisionProvider;
    return this;
  }

  public long revisionCount()
  {
    return revisionCount;
  }

  public CDORevisionCrawler addRevision(CDORevision revision)
  {
    checkActive();

    Queue<InternalCDORevision> queue = new LinkedList<>();
    queue.offer((InternalCDORevision)revision);

    while (!queue.isEmpty())
    {
      InternalCDORevision rev = queue.remove();
      ++revisionCount;

      if (handler.beginRevision(rev))
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

        handler.endRevision(rev);
      }
    }

    return this;
  }

  public CDORevisionCrawler begin()
  {
    activate();
    return this;
  }

  public CDORevisionCrawler finish()
  {
    deactivate();
    return this;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    CheckUtil.checkState(handler, "handler");
    CheckUtil.checkState(containmentProxyStrategy, "containmentProxyStrategy");
    CheckUtil.checkState(featureStrategy, "featureStrategy");
  }

  @Override
  protected void doActivate() throws Exception
  {
    handler.begin(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    handler.finish();
  }

  private void handleFeature(InternalCDORevision rev, EStructuralFeature feature)
  {
    handler.handleFeature(rev, feature);
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
    public default boolean begin(CDORevisionCrawler crawler)
    {
      // Continue crawling.
      return true;
    }

    public default boolean beginRevision(CDORevision revision)
    {
      // Continue crawling.
      return true;
    }

    public default void handleFeature(CDORevision revision, EStructuralFeature feature)
    {
      // Do nothing.
    }

    public default void endRevision(CDORevision revision)
    {
      // Do nothing.
    }

    public default void finish()
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

    private final IDMapper idMapper;

    private CDORevisionCrawler crawler;

    public OutputStreamHandler(OutputStream stream)
    {
      this(stream, false, null);
    }

    public OutputStreamHandler(OutputStream stream, boolean localIDs, CDOLobLoader lobLoader)
    {
      this(stream, localIDs ? new IDMapper.InMemory() : null, lobLoader);
    }

    public OutputStreamHandler(OutputStream stream, IDMapper idMapper, CDOLobLoader lobLoader)
    {
      this(new CDODataOutputImpl(new ExtendedDataOutputStream(stream))
      {
        private final CDOIDProvider idProvider = idMapper == null ? //
            CDOIDProvider.NOOP : //
            realID -> idMapper.lookup((CDOID)realID);

        @Override
        public CDOIDProvider getIDProvider()
        {
          return idProvider;
        }

        @Override
        public void writeCDOFeatureValue(EStructuralFeature feature, Object value) throws IOException
        {
          if (lobLoader != null)
          {
            CDOType type = CDOModelUtil.getType(feature);
            if (type == CDOType.BLOB || type == CDOType.CLOB)
            {
              CDOLob<?> lob = (CDOLob<?>)value;
              lobLoader.loadLob(lob, getDelegate());
              return;
            }

            type.writeValue(this, value);
            return;
          }

          super.writeCDOFeatureValue(feature, value);
        }
      }, idMapper);
    }

    protected OutputStreamHandler(CDODataOutput out, IDMapper idMapper)
    {
      this.out = out;
      this.idMapper = idMapper;
    }

    public IDMapper getLocalIDMapper()
    {
      return idMapper;
    }

    @Override
    public boolean begin(CDORevisionCrawler crawler)
    {
      this.crawler = crawler;
      return true;
    }

    @Override
    public final boolean beginRevision(CDORevision revision)
    {
      if (idMapper != null)
      {
        // Just map a new local ID...
        CDOID realID = revision.getID();
        idMapper.map(realID);

        // ... and continue crawling.
        return true;
      }

      return doBeginRevision(revision);
    }

    @Override
    public final void handleFeature(CDORevision revision, EStructuralFeature feature)
    {
      if (idMapper != null)
      {
        // For local IDs this is the first (id mapping) phase.
        // The real feature handling happens in finishLocalIDs(CDORevisionProvider).
        return;
      }

      doHandleFeature(revision, feature);
    }

    @Override
    public final void endRevision(CDORevision revision)
    {
      if (idMapper != null)
      {
        // For local IDs this is the first (id mapping) phase.
        // The real feature handling happens in finishLocalIDs(CDORevisionProvider).
        return;
      }

      doEndRevision(revision);
    }

    public final void finishLocalIDs()
    {
      if (idMapper == null || crawler == null)
      {
        return;
      }

      CDORevisionProvider revisionProvider = crawler.revisionProvider();
      if (revisionProvider == null)
      {
        return;
      }

      FeatureStrategy featureStrategy = crawler.featureStrategy();

      idMapper.forEach(realID -> {
        CDORevision rev = revisionProvider.getRevision(realID);

        for (EStructuralFeature feature : rev.getClassInfo().getAllPersistentFeatures())
        {
          Decision decision = featureStrategy.decide(rev, feature);
          if (decision.isHandle())
          {
            doHandleFeature(rev, feature);
          }
        }

        doEndRevision(rev);
      });
    }

    protected boolean doBeginRevision(CDORevision revision)
    {
      CDOID id = revision.getID();

      if (idMapper != null)
      {
        id = idMapper.lookup(id);
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

    protected void doHandleFeature(CDORevision revision, EStructuralFeature feature)
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
          UnaryOperator<CDOID> idConverter = idMapper == null ? null : idMapper::lookup;
          CDODataOutputImpl.writeCDOList(out, eClass, feature, list, CDORevision.UNCHUNKED, idConverter);
        }
        else
        {
          if (idMapper != null && feature instanceof EReference)
          {
            value = idMapper.lookup((CDOID)value);
          }

          out.writeCDOFeatureValue(feature, value);
        }
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    protected void doEndRevision(CDORevision revision)
    {
      // Do nothing.
    }

    /**
     * @author Eike Stepper
     */
    public static abstract class IDMapper
    {
      private long nextMappedID;

      public IDMapper()
      {
      }

      public CDOID map(CDOID realID)
      {
        CDOID mappedID = getNextMappedID();
        register(realID, mappedID);
        return mappedID;
      }

      public abstract CDOID lookup(CDOID realID);

      public abstract void forEach(Consumer<? super CDOID> realIDConsumer);

      protected abstract void register(CDOID realID, CDOID localID);

      protected CDOID getNextMappedID()
      {
        return CDOIDUtil.createLong(++nextMappedID);
      }

      /**
       * @author Eike Stepper
       */
      public static final class InMemory extends IDMapper
      {
        private final Map<CDOID, CDOID> mappedIDs = new LinkedHashMap<>();

        public InMemory()
        {
        }

        @Override
        public CDOID lookup(CDOID realID)
        {
          return mappedIDs.get(realID);
        }

        @Override
        public void forEach(Consumer<? super CDOID> realIDConsumer)
        {
          // The encounter order of LinkedHashMap.iterator() is preserved by keySet().iterator().
          mappedIDs.keySet().forEach(realIDConsumer);
        }

        @Override
        protected void register(CDOID realID, CDOID mappedID)
        {
          mappedIDs.put(realID, mappedID);
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

    public MessageDigestHandler(MessageDigest digest, boolean localIDs, CDOLobLoader lobLoader)
    {
      super(newOutputStream(digest), localIDs, lobLoader);
    }

    public MessageDigestHandler(MessageDigest digest, IDMapper idMapper, CDOLobLoader lobLoader)
    {
      super(newOutputStream(digest), idMapper, lobLoader);
    }

    protected MessageDigestHandler(CDODataOutput out, IDMapper idMapper)
    {
      super(out, idMapper);
    }

    private static DigestOutputStream newOutputStream(MessageDigest digest)
    {
      return new DigestOutputStream(IOUtil.nullOutputStream(), digest);
    }
  }
}
