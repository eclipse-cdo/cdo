package org.eclipse.emf.cdo.server.internal.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Ref extends BasicDBObject
{
  private static final long serialVersionUID = 1L;

  public Ref()
  {
  }

  public Ref(int size)
  {
    super(size);
  }

  public Ref(@SuppressWarnings("rawtypes") Map m)
  {
    super(m);
  }

  public Ref(String key, Object value)
  {
    super(key, value);
  }

  public Ref or(DBObject... expressions)
  {
    put("$or", expressions);
    return this;
  }

  public Ref eq(String field, Object value)
  {
    put(field, value);
    return this;
  }

  public Ref ne(String field, Object value)
  {
    return op(field, value, "$ne");
  }

  public Ref gt(String field, Object value)
  {
    return op(field, value, "$gt");
  }

  public Ref gte(String field, Object value)
  {
    return op(field, value, "$gte");
  }

  public Ref lt(String field, Object value)
  {
    return op(field, value, "$lt");
  }

  public Ref lte(String field, Object value)
  {
    return op(field, value, "$lte");
  }

  public Ref exists(String field, boolean yes)
  {
    return op(field, yes, "$exists");
  }

  private Ref op(String field, Object value, String op)
  {
    put(field, new BasicDBObject(op, value));
    return this;
  }
}