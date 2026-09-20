/*
 * Copyright (c) 2025-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Large Objects
 * <p>
 * CDO large objects are immutable, identifiable values for content that should not be serialized as an ordinary
 * in-line model attribute. A {@link CDOBlob} represents binary content and a {@link CDOClob} represents character
 * content. A model revision stores the large object's identity and size; the content is handled by the CDO large-object
 * store and is read through a stream or reader when needed.
 * <p>
 * This chapter explains the client-facing model, creation and consumption APIs, transaction behavior, cache-backed
 * loading, model representation, and resource ownership. It is about CDO large objects rather than JDBC BLOB/CLOB
 * objects. General session, view, and transaction lifecycle remains in {@link Doc03_WorkingWithSessions},
 * {@link Doc04_WorkingWithViews}, and {@link Doc05_WorkingWithTransactions}.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc07_LargeObjects
{
  /**
   * The Large-Object Model
   * <p>
   * CDO's public large-object types are {@link CDOBlob} and {@link CDOClob}. They are the Java values for the
   * {@link EtypesPackage#BLOB Blob} and {@link EtypesPackage#CLOB Clob} EDataTypes in the CDO types package. A
   * generated model feature using one of those data types has a Java type of {@code CDOBlob} or {@code CDOClob}; the
   * value is not a {@code byte[]}, a {@code String}, or a mutable file handle.
   * <p>
   * A byte array or string attribute is part of the ordinary revision value and is normally materialized with that
   * revision. A large-object attribute is represented by a content identifier and size, while the content is stored and
   * transferred separately. This separation avoids putting the complete payload into every revision serialization.
   * <p>
   * Both large-object classes are immutable. {@link CDOLobInfo#getID()} identifies the content with a digest and
   * {@link CDOLobInfo#getSize()} reports its size. The size is bytes for a blob and characters for a clob. Treat the
   * returned ID array as read-only. The public equality and hash-code contract is inherited from {@link CDOLobInfo}:
   * it compares the ID bytes and size, rather than reading and comparing the content.
   */
  public class LargeObjectModel
  {
  }

  /**
   * Creating Binary Large Objects
   * <p>
   * Create a blob from an {@link InputStream} when the source is naturally streamed, or from a {@code byte[]} when the
   * content is already in memory. A hexadecimal string is also accepted by the low-level {@link CDOBlob} constructor,
   * but it represents encoded binary data, not ordinary text. Prefer the session factories
   * {@link CDOSession#newBlob(InputStream)} and {@link CDOSession#newBlob(byte[])} because they use the session's
   * configured large-object cache.
   * <p>
   * Construction consumes the supplied stream immediately to compute the identity and populate the client
   * large-object store. The constructor does not retain the stream for a later commit. The caller owns the supplied
   * stream and should close it, normally with try-with-resources.
   *
   * {@link #storeBinaryResource(CDOTransaction, String, File) StoreBinaryResource.java}
   */
  public class BinaryLargeObjects
  {
    /**
     * Creates a blob from a file, assigns it to a persistent binary resource, and commits the resource.
     *
     * @param transaction the transaction that owns the new resource
     * @param resourcePath the repository path of the binary resource
     * @param file the file whose bytes are copied into the blob
     * @throws IOException if the file cannot be read or the local large-object store cannot write the content
     * @throws Exception if the transaction commit fails
     * @snip
     */
    public void storeBinaryResource(CDOTransaction transaction, String resourcePath, File file) throws Exception
    {
      CDOBinaryResource resource = transaction.createBinaryResource(resourcePath);

      try (InputStream input = new FileInputStream(file))
      {
        CDOBlob blob = transaction.getSession().newBlob(input);
        resource.setContents(blob);
        transaction.commit();
      }
    }
  }

  /**
   * Creating Character Large Objects
   * <p>
   * Create a clob from a {@link Reader} for streamed character input or from a {@link String} for already-materialized
   * text. Use {@link CDOSession#newClob(Reader)} and {@link CDOSession#newClob(String)} so that the session's configured
   * large-object store is used. Character decoding belongs at the boundary: if the source is bytes, choose its charset
   * when creating the reader. A clob's size is counted in characters, not source bytes.
   * <p>
   * As with blobs, construction consumes the supplied reader immediately and does not retain it. The caller owns and
   * closes the supplied reader.
   *
   * {@link #storeTextResource(CDOTransaction, String, File) StoreTextResource.java}
   */
  public class CharacterLargeObjects
  {
    /**
     * Creates a UTF-8 clob from a file, assigns it to a persistent text resource, and commits the resource.
     *
     * @param transaction the transaction that owns the new resource
     * @param resourcePath the repository path of the text resource
     * @param file the UTF-8 text file whose characters are copied into the clob
     * @throws IOException if the file cannot be read or the local large-object store cannot write the content
     * @throws Exception if the transaction commit fails
     * @snip
     */
    public void storeTextResource(CDOTransaction transaction, String resourcePath, File file) throws Exception
    {
      CDOTextResource resource = transaction.createTextResource(resourcePath);

      try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))
      {
        CDOClob clob = transaction.getSession().newClob(reader);
        resource.setContents(clob);
        transaction.commit();
      }
    }
  }

  /**
   * Assigning and Persisting Large Objects
   * <p>
   * A large object is assigned to a model feature in the same way as any other EDataType value. The assignment changes
   * the transaction's local model and makes the transaction dirty; the value becomes visible to other views only after
   * a successful {@link CDOTransaction#commit() commit}. Replacing a blob or clob is an ordinary feature replacement
   * from the transaction's perspective.
   * <p>
   * The payload has already been consumed into the client large-object store when the value is constructed. During
   * commit, CDO sends the content needed by the repository for large-object identities that are not already known
   * there, together with the revision change. Consequently, an I/O failure can occur during creation as well as during
   * commit. Rolling back removes the feature change from the transaction; it does not imply that every temporary local
   * cache file is immediately deleted.
   * <p>
   * Content identity allows a repository to recognize an already-known large object and avoid storing or transferring a
   * duplicate payload. This is an identity optimization, not a reason to mutate a large object: large-object values are
   * immutable, so replacing content means assigning a new value.
   * <p>
   * See {@link Doc05_WorkingWithTransactions} for dirty state, commit, rollback, and conflict handling.
   */
  public class PersistingLargeObjects
  {
  }

  /**
   * Reading and Streaming Contents
   * <p>
   * {@link CDOBlob#getContents()} returns an {@link InputStream}; {@link CDOClob#getContents()} returns a
   * {@link Reader}. Both can be requested repeatedly. The returned resource belongs to the caller and must be closed.
   * The convenience methods {@link CDOBlob#copyTo(OutputStream)}, {@link CDOClob#copyTo(Writer)},
   * {@link CDOBlob#getBytes()}, and {@link CDOClob#getString()} are useful when their materialization cost is
   * acceptable. For large payloads, prefer copying from the stream or reader to the final destination.
   * <p>
   * A model object or revision can be loaded without reading the full large-object content. On the first content access,
   * a session-backed store can load missing content from the repository into its configured cache and then return a
   * stream or reader. Subsequent reads can use that cache. The exact cache behavior is determined by the configured
   * {@link CDOSession.Options#getLobCache() session LOB cache}; do not assume that a custom store is disk-backed or
   * reusable after its owner has been closed. Keep the session available while a missing remote payload is being read.
   *
   * {@link #copyBinaryResource(CDOView, String, File) CopyBinaryResource.java}
   */
  public class ReadingLargeObjects
  {
    /**
     * Streams a binary resource to a file and closes the CDO-provided stream.
     *
     * @param view the view used to load the resource
     * @param resourcePath the repository path of the binary resource
     * @param file the local destination file
     * @throws IOException if the resource cannot be loaded or either stream cannot be read or written
     * @snip
     */
    public void copyBinaryResource(CDOView view, String resourcePath, File file) throws IOException
    {
      CDOBinaryResource resource = view.getBinaryResource(resourcePath);
      CDOBlob blob = resource.getContents();

      try (InputStream input = blob.getContents(); OutputStream output = new FileOutputStream(file))
      {
        IOUtil.copyBinary(input, output);
      }
    }
  }

  /**
   * Loading and Caching
   * <p>
   * The revision value contains large-object identity and size, while the bytes or characters are obtained through the
   * large-object store. The default client store is file-backed, and a session exposes its configurable
   * {@link CDOSession.Options#getLobCache() LOB cache} through session options. If a requested value is absent from the
   * cache, the session's store can fetch it from the repository before returning the content stream or reader.
   * <p>
   * The cache is keyed by the large-object identity and is separate from the ordinary object/revision cache. It can
   * therefore reduce repeated network transfers without making a blob or clob mutable. Cache location, retention, and
   * eviction are properties of the selected {@link CDOLobStore}; applications should
   * choose a suitable store when local disk or memory usage matters.
   * <p>
   * Loading a revision is not the same as loading every large-object payload referenced by that revision. Avoid calling
   * {@link CDOBlob#getBytes()} or {@link CDOClob#getString()} merely to inspect metadata; use {@link CDOLobInfo#getID()}
   * and {@link CDOLobInfo#getSize()} instead.
   */
  public class LoadingAndCaching
  {
  }

  /**
   * Repository and Store Support
   * <p>
   * Large-object support is a store capability. CDO's public client API does not expose a universal boolean capability
   * query on {@link CDOCommonRepository}; applications should therefore treat large-object
   * support as a repository/store deployment requirement and verify it for the repositories they target. A store that
   * does not support large objects cannot provide the normal commit and load behavior.
   * <p>
   * The repository also exposes its configured {@link CDOCommonRepository#getLobDigestAlgorithm()
   * LOB digest algorithm}. Applications normally do not need to depend on that value, but it explains why the ID is a
   * content-derived identity rather than an arbitrary object ID. Server/store cleanup of unreferenced large objects is a
   * store concern and is not part of ordinary client transaction lifecycle.
   */
  public class RepositoryAndStoreSupport
  {
  }

  /**
   * Large Objects in Model Features
   * <p>
   * Declare a persistent EAttribute with the CDO {@link EtypesPackage#BLOB Blob} or
   * {@link EtypesPackage#CLOB Clob} EDataType when a generated model feature should hold a CDO large object. The
   * generated accessor then uses {@link CDOBlob} or {@link CDOClob}. The built-in {@link CDOBinaryResource} and
   * {@link CDOTextResource} are examples of model objects whose persistent contents feature has exactly this shape.
   * <p>
   * These values are attributes, not EObjects: do not put them in containment, and do not model their payload as an
   * ordinary byte-array or string feature if the application needs CDO large-object storage and streaming. Lists of
   * blob or clob values are supported by the same value semantics; each entry is still immutable and identified by its
   * own content ID. General model preparation is covered by the Preparing Models material rather than repeated here.
   */
  public class ModelFeatures
  {
  }

  /**
   * Identity, Equality, and Change Detection
   * <p>
   * A large object's {@link CDOLobInfo#getID() ID} is a digest of its content, with the repository's configured digest
   * algorithm used consistently by the client and store. The ID also distinguishes binary and character content in the
   * standard store. {@link CDOLobInfo#equals(Object)} compares the ID bytes and size; it does not stream the payload.
   * This makes equality inexpensive and lets CDO recognize an already-known content value during commit.
   * <p>
   * Equality is not Java object identity: two separately constructed values with the same content can compare equal.
   * Conversely, do not compare a blob and clob merely because their visible text happens to match; they are different
   * large-object kinds and have different content semantics. Treat the ID array returned by the API as immutable even
   * though the array itself is exposed.
   */
  public class IdentityAndEquality
  {
  }

  /**
   * Resource Ownership and Failure Behavior
   * <p>
   * Close input streams and readers supplied to blob/clob constructors or session factories after construction has
   * completed. Those factories consume the source synchronously but do not make the source application's lifecycle their
   * responsibility. Always close streams and readers returned by {@link CDOBlob#getContents()} and
   * {@link CDOClob#getContents()}, even when copying fails.
   * <p>
   * Creation can fail with {@link IOException} while reading the source or writing the client store. Reading can fail
   * with {@link IOException} because cached content is missing, a repository transfer fails, or the returned stream or
   * reader cannot be consumed. A closed or disconnected session is especially relevant when the needed content is not
   * already in the local cache. Handle these failures as I/O and repository failures; do not assume that model-object
   * loading has already validated the payload.
   * <p>
   * Keep only metadata when possible. Holding a {@code byte[]} or {@code String} obtained from {@link CDOBlob#getBytes()}
   * or {@link CDOClob#getString()} defeats the memory benefit of the streaming API.
   */
  public class ResourceOwnership
  {
  }

  /**
   * Performance and Usage Guidance
   * <p>
   * Use a CDO large-object feature when content is large enough that embedding it in ordinary revision values would
   * create undesirable materialization, serialization, or memory costs. Construct from streams/readers for file or
   * network sources, and consume through streams/readers for file or network destinations. Use byte arrays, strings,
   * {@link CDOBlob#getBytes()}, or {@link CDOClob#getString()} only when the complete content is intentionally needed.
   * <p>
   * Remember that construction performs a complete local read to calculate identity and cache the content; streaming
   * creation avoids an extra application buffer but does not make digesting free. Commit may transfer content that the
   * repository does not already know, and the first read of a missing value may transfer it back. Reuse the immutable
   * large-object value or its cache when appropriate, close resources promptly, and configure a suitable session LOB
   * cache for the application's disk and memory constraints.
   * <p>
   * Choose a blob for binary data and a clob for character data. Decode bytes with an explicit charset before creating a
   * clob, and do not use a hexadecimal blob constructor as a text encoding shortcut.
   */
  public class UsageGuidance
  {
  }
}
