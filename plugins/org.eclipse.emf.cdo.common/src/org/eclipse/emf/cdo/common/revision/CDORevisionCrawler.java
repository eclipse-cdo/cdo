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
import org.eclipse.emf.cdo.common.util.EObjectCrawler;
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
 * A crawler that visits CDO revisions and their features according to a {@link FeatureStrategy feature strategy}
 * and passes them to a {@link Handler handler}.
 * <p>
 * Example usage:
 * <pre>
 * MessageDigest digest = MessageDigest.getInstance("SHA-256");
 * new CDORevisionCrawler()
 *    .handler(new CDORevisionCrawler.MessageDigestHandler(digest))
 *    .revisionProvider(revisionProvider)
 *    .featureStrategy(CDORevisionCrawler.FeatureStrategy.TREE)
 *    .containmentProxyStrategy(CDORevisionCrawler.ContainmentProxyStrategy.Physical)
 *    .begin()
 *    .addRevision(rootRevision)
 *    .finish();
 * byte[] hash = digest.digest();
 * </pre>
 *
 * @see EObjectCrawler
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

  /**
   * Creates a new CDO revision crawler.
   */
  public CDORevisionCrawler()
  {
  }

  /**
   * Returns the current handler used by this crawler.
   */
  public Handler handler()
  {
    return handler;
  }

  /**
   * Sets the handler to be used by this crawler.
   *
   * @param handler the handler implementation
   * @return this crawler instance for chaining
   */
  public CDORevisionCrawler handler(Handler handler)
  {
    checkInactive();
    this.handler = handler;
    return this;
  }

  /**
   * Returns the current containment proxy strategy.
   */
  public ContainmentProxyStrategy containmentProxyStrategy()
  {
    return containmentProxyStrategy;
  }

  /**
   * Sets the containment proxy strategy.
   *
   * @param containmentProxyStrategy the strategy to use
   * @return this crawler instance for chaining
   */
  public CDORevisionCrawler containmentProxyStrategy(ContainmentProxyStrategy containmentProxyStrategy)
  {
    checkInactive();
    this.containmentProxyStrategy = containmentProxyStrategy;
    return this;
  }

  /**
   * Returns the current feature strategy.
   */
  public FeatureStrategy featureStrategy()
  {
    return featureStrategy;
  }

  /**
   * Sets the feature strategy.
   *
   * @param featureStrategy the strategy to use
   * @return this crawler instance for chaining
   */
  public CDORevisionCrawler featureStrategy(FeatureStrategy featureStrategy)
  {
    checkInactive();
    this.featureStrategy = featureStrategy;
    return this;
  }

  /**
   * Returns the current revision provider.
   */
  public CDORevisionProvider revisionProvider()
  {
    return revisionProvider;
  }

  /**
   * Sets the revision provider.
   *
   * @param revisionProvider the provider to use
   * @return this crawler instance for chaining
   */
  public CDORevisionCrawler revisionProvider(CDORevisionProvider revisionProvider)
  {
    checkInactive();
    this.revisionProvider = revisionProvider;
    return this;
  }

  /**
   * Returns the number of revisions crawled so far.
   */
  public long revisionCount()
  {
    return revisionCount;
  }

  /**
   * Adds a revision to the crawl queue and processes reachable revisions.
   *
   * @param revision the root revision to start crawling from
   * @return this crawler instance for chaining
   */
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

  /**
   * Activates the crawler and prepares it for crawling.
   *
   * @return this crawler instance for chaining
   */
  public CDORevisionCrawler begin()
  {
    activate();
    return this;
  }

  /**
   * Deactivates the crawler and finalizes crawling.
   *
   * @return this crawler instance for chaining
   */
  public CDORevisionCrawler finish()
  {
    deactivate();
    return this;
  }

  /**
   * Checks the state of required fields before activation.
   */
  @Override
  protected void doBeforeActivate() throws Exception
  {
    CheckUtil.checkState(handler, "handler");
    CheckUtil.checkState(containmentProxyStrategy, "containmentProxyStrategy");
    CheckUtil.checkState(featureStrategy, "featureStrategy");
  }

  /**
   * Performs activation logic.
   */
  @Override
  protected void doActivate() throws Exception
  {
    handler.begin(this);
  }

  /**
   * Performs deactivation logic.
   */
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
   * Strategy for handling containment proxies during crawling.
   *
   * @author Eike Stepper
   */
  public enum ContainmentProxyStrategy
  {
    /**
     * Follows only physical containment proxies (parent is resource).
     */
    Physical
    {
      @Override
      protected boolean follow(boolean parentIsResource)
      {
        return parentIsResource;
      }
    },

    /**
     * Follows only logical containment proxies (parent is not resource).
     */
    Logical
    {
      @Override
      protected boolean follow(boolean parentIsResource)
      {
        return !parentIsResource;
      }
    },

    /**
     * Follows all containment proxies.
     */
    Any
    {
      @Override
      protected boolean follow(boolean parentIsResource)
      {
        return true;
      }
    };

    /**
     * Determines whether to follow a containment proxy based on parent resource status.
     *
     * @param parentIsResource true if parent is a resource
     * @return true to follow, false otherwise
     */
    protected abstract boolean follow(boolean parentIsResource);
  }

  /**
   * Strategy for deciding how to handle features during crawling.
   *
   * @author Eike Stepper
   */
  public interface FeatureStrategy
  {
    /**
     * Handles only the current feature, does not follow references.
     */
    public static final FeatureStrategy SINGLE = (revision, feature) -> Decision.Handle;

    /**
     * Handles the current feature and follows containment references.
     */
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

    /**
     * Decides how to handle a feature for a given revision.
     *
     * @param revision the revision
     * @param feature the feature
     * @return the decision
     */
    public Decision decide(CDORevision revision, EStructuralFeature feature);

    /**
     * Decision for feature handling: skip, handle, follow, or both.
     *
     * @author Eike Stepper
     */
    public enum Decision
    {
      /**
       * Skip the feature.
       */
      Skip(false, false),

      /**
       * Handle the feature only.
       */
      Handle(true, false),

      /**
       * Follow the feature only.
       */
      Follow(false, true),

      /**
       * Handle and follow the feature.
       */
      HandleAndFollow(true, true);

      private final boolean handle;

      private final boolean follow;

      private Decision(boolean handle, boolean follow)
      {
        this.handle = handle;
        this.follow = follow;
      }

      /**
       * Returns true if the feature should be handled.
       */
      public boolean isHandle()
      {
        return handle;
      }

      /**
       * Returns true if the feature should be followed.
       */
      public boolean isFollow()
      {
        return follow;
      }
    }
  }

  /**
   * Handler interface for receiving crawl events.
   *
   * @author Eike Stepper
   */
  public interface Handler
  {
    /**
     * Called when crawling begins.
     *
     * @param crawler the crawler instance
     * @return true to continue crawling
     */
    public default boolean begin(CDORevisionCrawler crawler)
    {
      // Continue crawling.
      return true;
    }

    /**
     * Called when a revision is about to be processed.
     *
     * @param revision the revision
     * @return true to continue crawling
     */
    public default boolean beginRevision(CDORevision revision)
    {
      // Continue crawling.
      return true;
    }

    /**
     * Called for each feature to be handled.
     *
     * @param revision the revision
     * @param feature the feature
     */
    public default void handleFeature(CDORevision revision, EStructuralFeature feature)
    {
      // Do nothing.
    }

    /**
     * Called when a revision has been processed.
     *
     * @param revision the revision
     */
    public default void endRevision(CDORevision revision)
    {
      // Do nothing.
    }

    /**
     * Called when crawling is finished.
     */
    public default void finish()
    {
      // Do nothing.
    }
  }

  /**
   * Handler implementation that writes crawl data to an output stream.
   *
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

    /**
     * Constructs an OutputStreamHandler with the given output stream.
     *
     * @param stream the output stream
     */
    public OutputStreamHandler(OutputStream stream)
    {
      this(stream, false, null);
    }

    /**
     * Constructs an OutputStreamHandler with local ID mapping and optional LOB loader.
     *
     * @param stream the output stream
     * @param localIDs true to use local IDs
     * @param lobLoader the LOB loader, may be null
     */
    public OutputStreamHandler(OutputStream stream, boolean localIDs, CDOLobLoader lobLoader)
    {
      this(stream, localIDs ? new IDMapper.InMemory() : null, lobLoader);
    }

    /**
     * Constructs an OutputStreamHandler with custom ID mapper and optional LOB loader.
     *
     * @param stream the output stream
     * @param idMapper the ID mapper
     * @param lobLoader the LOB loader, may be null
     */
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

    /**
     * Constructs an OutputStreamHandler with the given data output and ID mapper.
     */
    protected OutputStreamHandler(CDODataOutput out, IDMapper idMapper)
    {
      this.out = out;
      this.idMapper = idMapper;
    }

    /**
     * Returns the local ID mapper used by this handler, if any.
     */
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

    /**
     * Performs the second phase for local ID mapping, writing features for all mapped revisions.
     */
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

    /**
     * Performs finishing logic for the given revision.
     */
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

    /**
     * Performs feature handling logic for the given revision and feature.
     */
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

    /**
     * Performs ending logic for the given revision.
     */
    protected void doEndRevision(CDORevision revision)
    {
      // Do nothing.
    }

    /**
     * Abstract ID mapper for mapping real IDs to local IDs.
     *
     * @author Eike Stepper
     */
    public static abstract class IDMapper
    {
      private long nextMappedID;

      /**
       * Constructs an IDMapper.
       */
      public IDMapper()
      {
      }

      /**
       * Maps a real ID to a new local ID.
       *
       * @param realID the real ID
       * @return the mapped local ID
       */
      public CDOID map(CDOID realID)
      {
        CDOID mappedID = getNextMappedID();
        register(realID, mappedID);
        return mappedID;
      }

      /**
       * Looks up the mapped local ID for a real ID.
       *
       * @param realID the real ID
       * @return the mapped local ID
       */
      public abstract CDOID lookup(CDOID realID);

      /**
       * Iterates over all real IDs that have been mapped.
       *
       * @param realIDConsumer the consumer for real IDs
       */
      public abstract void forEach(Consumer<? super CDOID> realIDConsumer);

      /**
       * Registers a mapping from real ID to local ID.
       *
       * @param realID the real ID
       * @param localID the local ID
       */
      protected abstract void register(CDOID realID, CDOID localID);

      /**
       * Returns the next available local ID.
       *
       * @return the next local ID
       */
      protected CDOID getNextMappedID()
      {
        return CDOIDUtil.createLong(++nextMappedID);
      }

      /**
       * In-memory implementation of IDMapper.
       * <p>
       * Stores all mappings in memory using a LinkedHashMap to preserve insertion order.
       *
       * @author Eike Stepper
       */
      public static final class InMemory extends IDMapper
      {
        private final Map<CDOID, CDOID> mappedIDs = new LinkedHashMap<>();

        /**
         * Constructs an in-memory IDMapper.
         */
        public InMemory()
        {
        }

        /**
         * Looks up the mapped local ID for a real ID.
         */
        @Override
        public CDOID lookup(CDOID realID)
        {
          return mappedIDs.get(realID);
        }

        /**
         * Iterates over all real IDs that have been mapped.
         */
        @Override
        public void forEach(Consumer<? super CDOID> realIDConsumer)
        {
          // The encounter order of LinkedHashMap.iterator() is preserved by keySet().iterator().
          mappedIDs.keySet().forEach(realIDConsumer);
        }

        /**
         * Registers a mapping from real ID to local ID.
         */
        @Override
        protected void register(CDOID realID, CDOID mappedID)
        {
          mappedIDs.put(realID, mappedID);
        }
      }
    }
  }

  /**
   * Handler implementation that calculates a message digest over crawled data.
   *
   * @author Eike Stepper
   */
  public static class MessageDigestHandler extends OutputStreamHandler
  {
    /**
     * Constructs a MessageDigestHandler with the given digest.
     *
     * @param digest the message digest
     */
    public MessageDigestHandler(MessageDigest digest)
    {
      super(newOutputStream(digest));
    }

    /**
     * Constructs a MessageDigestHandler with digest, local IDs, and optional LOB loader.
     *
     * @param digest the message digest
     * @param localIDs true to use local IDs
     * @param lobLoader the LOB loader, may be null
     */
    public MessageDigestHandler(MessageDigest digest, boolean localIDs, CDOLobLoader lobLoader)
    {
      super(newOutputStream(digest), localIDs, lobLoader);
    }

    /**
     * Constructs a MessageDigestHandler with digest, custom ID mapper, and optional LOB loader.
     *
     * @param digest the message digest
     * @param idMapper the ID mapper
     * @param lobLoader the LOB loader, may be null
     */
    public MessageDigestHandler(MessageDigest digest, IDMapper idMapper, CDOLobLoader lobLoader)
    {
      super(newOutputStream(digest), idMapper, lobLoader);
    }

    /**
     * Constructs a MessageDigestHandler with the given data output and ID mapper.
     */
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
