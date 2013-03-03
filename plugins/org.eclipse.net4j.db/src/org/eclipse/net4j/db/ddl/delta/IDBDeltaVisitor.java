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
package org.eclipse.net4j.db.ddl.delta;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBDelta.ChangeKind;

/**
 * @since 4.2
 * @author Eike Stepper
 */
public interface IDBDeltaVisitor
{
  public void visit(IDBSchemaDelta schemaDelta);

  public void visit(IDBTableDelta tableDelta);

  public void visit(IDBFieldDelta fieldDelta);

  public void visit(IDBIndexDelta indexDelta);

  public void visit(IDBIndexFieldDelta indexFieldDelta);

  /**
   * @author Eike Stepper
   */
  public static class Default implements IDBDeltaVisitor
  {
    protected void visitDefault(IDBDelta delta)
    {
    }

    public void visit(IDBSchemaDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBSchemaDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBSchemaDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBSchemaDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBTableDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBTableDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBTableDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBTableDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBFieldDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBFieldDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBIndexDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBIndexDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBIndexDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBIndexDelta delta)
    {
      visitDefault(delta);
    }

    public void visit(IDBIndexFieldDelta delta)
    {
      ChangeKind changeKind = delta.getChangeKind();
      switch (changeKind)
      {
      case ADDED:
        added(delta);
        break;

      case REMOVED:
        removed(delta);
        break;

      case CHANGED:
        changed(delta);
        break;

      default:
        throw new IllegalStateException("Illegal change kind: " + changeKind);
      }
    }

    protected void added(IDBIndexFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void removed(IDBIndexFieldDelta delta)
    {
      visitDefault(delta);
    }

    protected void changed(IDBIndexFieldDelta delta)
    {
      visitDefault(delta);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Applier extends IDBDeltaVisitor.Default
  {
    private final IDBSchema schema;

    public Applier(IDBSchema schema)
    {
      this.schema = schema;
    }

    public final IDBSchema getSchema()
    {
      return schema;
    }

    @Override
    protected void added(IDBTableDelta delta)
    {
      String name = delta.getName();
      schema.addTable(name);
    }

    @Override
    protected void removed(IDBTableDelta delta)
    {
      IDBTable table = delta.getElement(schema);
      table.remove();
    }

    @Override
    protected void changed(IDBTableDelta delta)
    {
    }

    @Override
    protected void added(IDBFieldDelta delta)
    {
      String name = delta.getName();
      DBType type = delta.getPropertyValue(IDBFieldDelta.TYPE_PROPERTY);
      int precision = delta.getPropertyValue(IDBFieldDelta.PRECISION_PROPERTY);
      int scale = delta.getPropertyValue(IDBFieldDelta.SCALE_PROPERTY);
      boolean notNull = delta.getPropertyValue(IDBFieldDelta.NOT_NULL_PROPERTY);

      IDBTable table = delta.getParent().getElement(schema);
      table.addField(name, type, precision, scale, notNull);
    }

    @Override
    protected void removed(IDBFieldDelta delta)
    {
      IDBField field = delta.getElement(schema);
      field.remove();
    }

    @Override
    protected void changed(IDBFieldDelta delta)
    {
    }

    @Override
    protected void added(IDBIndexDelta delta)
    {
      String name = delta.getName();
      IDBIndex.Type type = delta.getPropertyValue(IDBIndexDelta.TYPE_PROPERTY);

      IDBTable table = delta.getParent().getElement(schema);
      table.addIndex(name, type);
    }

    @Override
    protected void removed(IDBIndexDelta delta)
    {
      IDBIndex index = delta.getElement(schema);
      index.remove();
    }

    @Override
    protected void changed(IDBIndexDelta delta)
    {
    }

    @Override
    protected void added(IDBIndexFieldDelta delta)
    {
      IDBIndexDelta parent = delta.getParent();
      IDBTable table = parent.getParent().getElement(schema);

      String name = delta.getName();
      IDBField field = table.getField(name);

      IDBIndex index = parent.getElement(schema);
      index.addIndexField(field);
    }

    @Override
    protected void removed(IDBIndexFieldDelta delta)
    {
      IDBIndexField indexField = delta.getElement(schema);
      indexField.remove();
    }

    @Override
    protected void changed(IDBIndexFieldDelta delta)
    {
    }
  }
}
