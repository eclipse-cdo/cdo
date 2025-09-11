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
package org.eclipse.emf.cdo.tests.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public class H2Playground
{
  public static void main(String[] args) throws Exception
  {
    String url = "jdbc:h2:./state/H2Playground";

    try (Connection conn = DriverManager.getConnection(url, "sa", ""))
    {
      Statement stmt = conn.createStatement();

      // Insert example teams
      stmt.execute("DROP TABLE IF EXISTS teams");
      stmt.execute("CREATE TABLE teams (id VARCHAR(100) PRIMARY KEY, name VARCHAR(100), num_persons INT DEFAULT 0)");
      stmt.execute("INSERT INTO teams (id, name, num_persons) VALUES ('Alpha', 'Alpha', 0)");
      stmt.execute("INSERT INTO teams (id, name, num_persons) VALUES ('Beta', 'Beta', 0)");
      stmt.execute("INSERT INTO teams (id, name, num_persons) VALUES ('Gamma', 'Gamma', 0)");

      // Insert example persons
      stmt.execute("DROP TABLE IF EXISTS persons");
      stmt.execute("CREATE TABLE persons (id INT PRIMARY KEY, name VARCHAR(100), team_id VARCHAR(100))");
      stmt.execute("INSERT INTO persons (id, name, team_id) VALUES (1, 'Alice-1', 'Alpha-1')");
      stmt.execute("INSERT INTO persons (id, name, team_id) VALUES (2, 'Bob-1', 'Alpha-1')");
      stmt.execute("INSERT INTO persons (id, name, team_id) VALUES (3, 'Charlie-1', 'Alpha-1')");
      stmt.execute("INSERT INTO persons (id, name, team_id) VALUES (4, 'Diana-0', NULL)");
      stmt.execute("INSERT INTO persons (id, name, team_id) VALUES (5, 'Eve-3', 'Gamma-3')");
      stmt.execute("INSERT INTO persons (id, name, team_id) VALUES (6, 'Frank-3', 'Gamma-3')");
      stmt.execute("INSERT INTO persons (id, name, team_id) VALUES (7, 'Grace-3', 'Gamma-3')");

      System.out.println("LOCATE:");
      print(stmt, "SELECT name, LOCATE('-', name) pos FROM persons");

      // Update num_persons in teams table
      // SUBSTRING(t_p.team_id, 1, LOCATE(t_p.team_id, '-')-1)
      stmt.execute("UPDATE teams SET num_persons = (SELECT COUNT(*) FROM persons WHERE persons.team_id LIKE teams.id || '-%')");
      // stmt.execute("UPDATE teams SET num_persons = num_persons + (SELECT COUNT(*) FROM persons WHERE team_id =
      // teams.id)");

      System.out.println("Teams:");
      print(stmt, "SELECT * FROM teams");

      System.out.println("Persons:");
      print(stmt, "SELECT * FROM persons");
    }
  }

  private static void print(Statement stmt, String sql) throws Exception
  {
    try (ResultSet rs = stmt.executeQuery(sql))
    {
      print(rs);
    }
  }

  private static void print(ResultSet rs) throws Exception
  {
    int columnCount = rs.getMetaData().getColumnCount();

    while (rs.next())
    {
      for (int i = 1; i <= columnCount; i++)
      {
        if (i > 1)
        {
          System.out.print(", ");
        }

        String columnName = rs.getMetaData().getColumnName(i).toLowerCase();
        Object value = rs.getObject(i);
        System.out.print(columnName + "=" + value);
      }

      System.out.println();
    }

    System.out.println();
  }
}
