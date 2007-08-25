/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.ITypeManager;

import org.eclipse.net4j.internal.util.lifecycle.QueueWorker;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.io.CachedFileMap;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.SortedFileMap;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class TypeManager extends QueueWorker<ObjectEntry> implements ITypeManager
{
  private Repository repository;

  private boolean persistent;

  private ConcurrentMap<CDOID, CDOClassRef> objectTypes = new ConcurrentHashMap();

  private PackageURIMap packageURIMap;

  private PackageIDMap packageIDMap;

  private ObjectTypeMap objectTypeMap;

  private ObjectTypeMap metaObjectTypeMap;

  private int nextPackageID;

  public TypeManager(Repository repository)
  {
    this.repository = repository;
  }

  public Repository getRepository()
  {
    return repository;
  }

  public boolean isPersistent()
  {
    return persistent;
  }

  public void setPersistent(boolean persistent)
  {
    this.persistent = persistent;
  }

  public CDOClassRef getObjectType(IStoreReader storeReader, CDOID id)
  {
    CDOClassRef type = objectTypes.get(id);
    if (type == null && persistent)
    {
      type = persistentLoadType(id);
    }

    if (type == null && storeReader != null)
    {
      type = storeReader.readObjectType(id);
    }

    if (type == null)
    {
      throw new ImplementationError("Type not found for id " + id);
    }

    objectTypes.put(id, type);
    return type;
  }

  public void registerObjectType(CDOID id, CDOClassRef type)
  {
    objectTypes.putIfAbsent(id, type);
    if (persistent)
    {
      addWork(new ObjectEntry(id, type));
    }
  }

  protected CDOClassRef persistentLoadType(CDOID id)
  {
    TypeEntry entry = null;
    if (id.isMeta())
    {
      if (objectTypeMap != null)
      {
        entry = objectTypeMap.get(id);
      }
    }
    else
    {
      if (metaObjectTypeMap != null)
      {
        entry = metaObjectTypeMap.get(id);
      }
    }

    if (entry == null)
    {
      return null;
    }

    String packageURI = packageURIMap.get(entry.getPackageID());
    return new CDOClassRefImpl(packageURI, entry.getClassifierID());
  }

  @Override
  protected void work(WorkContext context, ObjectEntry element)
  {
    CDOID id = element.getID();
    CDOClassRef type = element.getType();
    String packageURI = type.getPackageURI();
    int classifierID = type.getClassifierID();

    Integer packageID = packageIDMap.get(packageURI);
    if (packageID == null)
    {
      packageID = nextPackageID++;
      packageIDMap.put(packageURI, packageID);
      packageURIMap.put(packageID, packageURI);
    }

    TypeEntry entry = new TypeEntry(classifierID, packageID);
    objectTypeMap.put(id, entry);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (persistent)
    {
      File stateFolder = new File(OM.BUNDLE.getStateLocation());
      File repositoryFolder = new File(stateFolder, repository.getUUID());
      IOUtil.mkdirs(repositoryFolder);

      packageURIMap = new PackageURIMap(new File(repositoryFolder, "package.uris"));
      packageIDMap = new PackageIDMap(new File(repositoryFolder, "package.ids"));
      objectTypeMap = new ObjectTypeMap(new File(repositoryFolder, "object.types"));
      metaObjectTypeMap = new ObjectTypeMap(new File(repositoryFolder, "metaobject.types"));

      Integer max = packageURIMap.getMaxKey();
      nextPackageID = max == null ? 1 : max + 1;
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    IOUtil.close(metaObjectTypeMap);
    IOUtil.close(objectTypeMap);
    IOUtil.close(packageIDMap);
    IOUtil.close(packageURIMap);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static final class TypeEntry
  {
    public static final int SIZE = 8;

    private int classifierID;

    private int packageID;

    public TypeEntry(int classifierID, int packageID)
    {
      this.classifierID = classifierID;
      this.packageID = packageID;
    }

    public TypeEntry(ExtendedDataInput in) throws IOException
    {
      classifierID = in.readInt();
      packageID = in.readInt();
    }

    public void write(ExtendedDataOutput out) throws IOException
    {
      out.writeInt(classifierID);
      out.writeInt(packageID);
    }

    public int getClassifierID()
    {
      return classifierID;
    }

    public int getPackageID()
    {
      return packageID;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class PackageURIMap extends CachedFileMap<Integer, String>
  {
    public PackageURIMap(File file)
    {
      super(file, "rw");
    }

    @Override
    public int getKeySize()
    {
      return 4;
    }

    @Override
    protected Integer readKey(ExtendedDataInput in) throws IOException
    {
      return in.readInt();
    }

    @Override
    protected void writeKey(ExtendedDataOutput out, Integer key) throws IOException
    {
      out.writeInt(key);
    }

    @Override
    public int getValueSize()
    {
      return 260;
    }

    @Override
    protected String readValue(ExtendedDataInput in) throws IOException
    {
      return in.readString();
    }

    @Override
    protected void writeValue(ExtendedDataOutput out, String value) throws IOException
    {
      byte[] bytes = value.getBytes();
      if (bytes.length + 4 > getValueSize())
      {
        throw new IllegalArgumentException("Value size of " + getValueSize() + " exceeded: " + value);
      }

      ExtendedIOUtil.writeByteArray(out, bytes);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class PackageIDMap extends CachedFileMap<String, Integer>
  {
    public PackageIDMap(File file)
    {
      super(file, "rw");
    }

    @Override
    public int getKeySize()
    {
      return 260;
    }

    @Override
    protected String readKey(ExtendedDataInput in) throws IOException
    {
      return in.readString();
    }

    @Override
    protected void writeKey(ExtendedDataOutput out, String key) throws IOException
    {
      byte[] bytes = key.getBytes();
      if (bytes.length + 4 > getKeySize())
      {
        throw new IllegalArgumentException("Key size of " + getKeySize() + " exceeded: " + key);
      }

      ExtendedIOUtil.writeByteArray(out, bytes);
    }

    @Override
    public int getValueSize()
    {
      return 4;
    }

    @Override
    protected Integer readValue(ExtendedDataInput in) throws IOException
    {
      return in.readInt();
    }

    @Override
    protected void writeValue(ExtendedDataOutput out, Integer value) throws IOException
    {
      out.writeInt(value);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ObjectTypeMap extends SortedFileMap<CDOID, TypeEntry>
  {
    public ObjectTypeMap(File file)
    {
      super(file, "rw");
    }

    @Override
    public int getKeySize()
    {
      return 8;
    }

    @Override
    protected CDOID readKey(ExtendedDataInput in) throws IOException
    {
      return CDOIDImpl.read(in);
    }

    @Override
    protected void writeKey(ExtendedDataOutput out, CDOID key) throws IOException
    {
      CDOIDImpl.write(out, key);
    }

    @Override
    public int getValueSize()
    {
      return TypeEntry.SIZE;
    }

    @Override
    protected TypeEntry readValue(ExtendedDataInput in) throws IOException
    {
      return new TypeEntry(in);
    }

    @Override
    protected void writeValue(ExtendedDataOutput out, TypeEntry value) throws IOException
    {
      value.write(out);
    }
  }
}

/**
 * @author Eike Stepper
 */
final class ObjectEntry
{
  private CDOID id;

  private CDOClassRef type;

  public ObjectEntry(CDOID id, CDOClassRef type)
  {
    this.id = id;
    this.type = type;
  }

  public CDOID getID()
  {
    return id;
  }

  public CDOClassRef getType()
  {
    return type;
  }
}
