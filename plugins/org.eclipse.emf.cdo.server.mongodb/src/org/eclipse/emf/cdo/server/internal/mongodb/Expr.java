/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Eike Stepper
 */
public abstract class Expr
{
  public void evaluate(DBCollection collection, ResultHandler handler)
  {
    DBObject query = buildQuery(null);
    DBCursor cursor = findDocuments(collection, query);

    try
    {
      while (cursor.hasNext())
      {
        DBObject doc = cursor.next();
        handleDocument(doc, handler);
      }
    }
    finally
    {
      cursor.close();
    }
  }

  protected DBCursor findDocuments(DBCollection collection, DBObject query)
  {
    return collection.find(query);
  }

  protected void handleDocument(DBObject document, ResultHandler handler)
  {
    handleObject(document, handler);
  }

  protected abstract DBObject buildQuery(String context);

  protected abstract void handleObject(DBObject object, ResultHandler handler);

  /**
   * @author Eike Stepper
   */
  public static interface ResultHandler
  {
    public void handleResult(DBObject object);
  }

  /**
   * @author Eike Stepper
   */
  public static class Embed extends Expr
  {
    private String field;

    private Expr expr;

    public Embed(String field, Expr expr)
    {
      this.field = field;
      this.expr = expr;
    }

    public String getField()
    {
      return field;
    }

    public Expr getExpr()
    {
      return expr;
    }

    @Override
    protected DBObject buildQuery(String context)
    {
      String newContext = context == null ? field : context + "." + field;
      return expr.buildQuery(newContext);
    }

    @Override
    protected void handleObject(DBObject object, ResultHandler handler)
    {
      DBObject embedded = (DBObject)object.get(field);
      expr.handleObject(embedded, handler);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Or extends Expr
  {
    private Expr[] operands;

    public Or(Expr... operands)
    {
      this.operands = operands;
    }

    public Expr[] getOperands()
    {
      return operands;
    }

    @Override
    protected DBObject buildQuery(String context)
    {
      BasicDBList list = new BasicDBList();
      for (Expr expr : operands)
      {
        list.add(expr.buildQuery(context));
      }

      return new BasicDBObject("$or", list);
    }

    @Override
    protected void handleObject(DBObject object, ResultHandler handler)
    {
      for (Expr expr : operands)
      {
        expr.handleObject(object, handler);
      }
    }
  }
}
