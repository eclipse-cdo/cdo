/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - added hibernate specifics
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.internal.protocol.id.AbstractCDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDObject;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.hibernate.CDOIDHibernate;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class CDOIDHibernateImpl extends AbstractCDOID implements CDOIDHibernate
{
  private static final long serialVersionUID = 1L;

  private Serializable id;

  private String entityName;

  public CDOIDHibernateImpl()
  {
  }

  public Type getType()
  {
    return Type.OBJECT;
  }

  public CDOClassRef getClassRef()
  {
    return null;
  }

  public CDOIDObject asLegacy(CDOClassRef classRef)
  {
    return new Legacy(classRef);
  }

  public void read(ExtendedDataInput in) throws IOException
  {
    final byte[] content = in.readByteArray();
    setContent(content);
  }

  public void setContent(byte[] content)
  {
    try
    {
      final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(content));
      final SerializableContent contentObject = (SerializableContent)ois.readObject();
      setId(contentObject.getId());
      setEntityName(contentObject.getEntityName());
      ois.close();
    }
    catch (ClassNotFoundException e)
    {
      throw new IllegalArgumentException(e);
    }
    catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public byte[] getContent()
  {
    try
    {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final ObjectOutputStream oos = new ObjectOutputStream(bos);
      final SerializableContent content = new SerializableContent();
      content.setId(getId());
      content.setEntityName(getEntityName());
      oos.writeObject(content);
      return bos.toByteArray();
    }
    catch (IOException e)
    {
      throw new IllegalStateException(e);
    }

  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeByteArray(getContent());
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof CDOIDHibernate))
    {
      return false;
    }

    return id.equals(((CDOIDHibernate)obj).getId()) && entityName.equals(((CDOIDHibernate)obj).getEntityName());
  }

  @Override
  public int hashCode()
  {
    return id.hashCode();
  }

  @Override
  public String toString()
  {
    return getClass().getName() + ": " + entityName + " (id:" + id.toString() + ")";
  }

  // used for serialization
  private static class SerializableContent implements Serializable
  {
    private static final long serialVersionUID = 1L;

    private Serializable id;

    private String entityName;

    /**
     * @return the id
     */
    public Serializable getId()
    {
      return id;
    }

    /**
     * @param id
     *          the id to set
     */
    public void setId(Serializable id)
    {
      this.id = id;
    }

    /**
     * @return the entityName
     */
    public String getEntityName()
    {
      return entityName;
    }

    /**
     * @param entityName
     *          the entityName to set
     */
    public void setEntityName(String entityName)
    {
      this.entityName = entityName;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Legacy extends CDOIDHibernateImpl
  {
    private static final long serialVersionUID = 1L;

    private CDOClassRef classRef;

    public Legacy()
    {
    }

    public Legacy(CDOClassRef classRef)
    {
      if (classRef == null)
      {
        throw new IllegalArgumentException("classRef == null");
      }

      this.classRef = classRef;
    }

    @Override
    public Type getType()
    {
      return Type.LEGACY_OBJECT;
    }

    @Override
    public CDOClassRef getClassRef()
    {
      return classRef;
    }

    public void setClassRef(CDOClassRef classRef)
    {
      this.classRef = classRef;
    }

    @Override
    public Legacy asLegacy(CDOClassRef classRef)
    {
      return this;
    }

    @Override
    public String toString()
    {
      return super.toString() + "(" + classRef + ")";
    }
  }

  /**
   * @return the id
   */
  public Serializable getId()
  {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(Serializable id)
  {
    this.id = id;
  }

  /**
   * @return the entityName
   */
  public String getEntityName()
  {
    return entityName;
  }

  /**
   * @param entityName
   *          the entityName to set
   */
  public void setEntityName(String entityName)
  {
    this.entityName = entityName;
  }
}
