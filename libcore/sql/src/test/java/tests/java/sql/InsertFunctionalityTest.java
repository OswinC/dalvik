/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.java.sql;

import dalvik.annotation.KnownFailure;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import tests.support.Support_SQL;
import tests.support.DatabaseCreator;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@TestTargetClass(Statement.class)
public class InsertFunctionalityTest extends TestCase {

    private static Connection conn = null;

    private static Statement statement = null;

    protected void setUp() throws Exception {
        super.setUp();
        Support_SQL.loadDriver();
        conn = Support_SQL.getConnection();

    }

    protected void tearDown() throws Exception {
        statement.execute("DELETE FROM " + DatabaseCreator.SIMPLE_TABLE2);
        statement.execute("DELETE FROM " + DatabaseCreator.SIMPLE_TABLE1);
        statement.execute("DELETE FROM " + DatabaseCreator.FKSTRICT_TABLE);
        statement.execute("DELETE FROM " + DatabaseCreator.FKCASCADE_TABLE);
        statement.execute("DELETE FROM " + DatabaseCreator.PARENT_TABLE);
        statement.execute("DELETE FROM " + DatabaseCreator.TEST_TABLE5);
        super.tearDown();
    }

    public static Test suite() {
        TestSetup setup = new TestSetup(new TestSuite(
                InsertFunctionalityTest.class)) {
            protected void setUp() throws Exception {
                Support_SQL.loadDriver();
                try {
                    conn = Support_SQL.getConnection();
                    statement = conn.createStatement();
                    createTestTables();
                } catch (SQLException e) {
                    fail("Unexpected SQLException " + e.toString());
                }
            }

            protected void tearDown() throws Exception {
                deleteTestTables();
                statement.close();
                conn.close();
            }

            private void createTestTables() {
                try {
                    DatabaseMetaData meta = conn.getMetaData();
                    ResultSet userTab = meta.getTables(null, null, null, null);

                    while (userTab.next()) {
                        String tableName = userTab.getString("TABLE_NAME");
                        if (tableName.equals(DatabaseCreator.PARENT_TABLE)) {
                            statement
                                    .execute(DatabaseCreator.DROP_TABLE_PARENT);
                        } else if (tableName
                                .equals(DatabaseCreator.FKCASCADE_TABLE)) {
                            statement
                                    .execute(DatabaseCreator.DROP_TABLE_FKCASCADE);
                        } else if (tableName
                                .equals(DatabaseCreator.FKSTRICT_TABLE)) {
                            statement
                                    .execute(DatabaseCreator.DROP_TABLE_FKSTRICT);
                        } else if (tableName
                                .equals(DatabaseCreator.SIMPLE_TABLE1)) {
                            statement
                                    .execute(DatabaseCreator.DROP_TABLE_SIMPLE1);
                        } else if (tableName
                                .equals(DatabaseCreator.SIMPLE_TABLE2)) {
                            statement
                                    .execute(DatabaseCreator.DROP_TABLE_SIMPLE2);
                        } else if (tableName
                                .equals(DatabaseCreator.TEST_TABLE5)) {
                            statement.execute(DatabaseCreator.DROP_TABLE5);
                        }
                    }
                    userTab.close();
                    statement.execute(DatabaseCreator.CREATE_TABLE_PARENT);
                    statement.execute(DatabaseCreator.CREATE_TABLE_FKSTRICT);
                    statement.execute(DatabaseCreator.CREATE_TABLE_FKCASCADE);
                    statement.execute(DatabaseCreator.CREATE_TABLE_SIMPLE2);
                    statement.execute(DatabaseCreator.CREATE_TABLE_SIMPLE1);
                    statement.execute(DatabaseCreator.CREATE_TABLE5);
                } catch (SQLException e) {
                    fail("Unexpected SQLException " + e.toString());
                }
            }

            private void deleteTestTables() {
                try {
                    statement.execute(DatabaseCreator.DROP_TABLE_FKCASCADE);
                    statement.execute(DatabaseCreator.DROP_TABLE_FKSTRICT);
                    statement.execute(DatabaseCreator.DROP_TABLE_PARENT);
                    statement.execute(DatabaseCreator.DROP_TABLE_SIMPLE2);
                    statement.execute(DatabaseCreator.DROP_TABLE_SIMPLE1);
                    statement.execute(DatabaseCreator.DROP_TABLE5);
                } catch (SQLException e) {
                    fail("Unexpected SQLException " + e.toString());
                }
            }
        };
        return setup;
    }

    /**
     * @tests InsertFunctionalityTest#testInsert1(). Attempts to insert row into
     *        table with integrity checking
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Functionality test: Attempts to insert row into table with integrity checking",
        method = "execute",
        args = {java.lang.String.class}
    )
    @KnownFailure(" SQLite.Exception: error in prepare/compile")
    public void testInsert1() throws SQLException {
        DatabaseCreator.fillParentTable(conn);
        DatabaseCreator.fillFKStrictTable(conn);
        DatabaseCreator.fillFKCascadeTable(conn);
        statement.execute("INSERT INTO " + DatabaseCreator.FKSTRICT_TABLE
                + " VALUES(4, 1, 'testInsert')");
        statement.execute("INSERT INTO " + DatabaseCreator.FKCASCADE_TABLE
                + " VALUES(4, 1, 'testInsert')");
    }

    /**
     * @tests InsertFunctionalityTest#testInsert2(). Attempts to insert row into
     *        table with integrity checking when row has incorrect foreign key
     *        value - expecting SQLException
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Functionality test: Attempts to insert row into table with integrity checking when row has incorrect foreign key value - expecting SQLException",
        method = "execute",
        args = {java.lang.String.class}
    )
    @KnownFailure(" SQLite.Exception: error in prepare/compile")
    public void testInsert2() throws SQLException {
        DatabaseCreator.fillParentTable(conn);
        DatabaseCreator.fillFKStrictTable(conn);
        DatabaseCreator.fillFKCascadeTable(conn);
        try {
            statement.execute("INSERT INTO " + DatabaseCreator.FKSTRICT_TABLE
                    + " VALUES(4, 4, 'testInsert')");
           // TODO Foreign key functionality isn't supported 
           // fail("expecting SQLException");
        } catch (SQLException ex) {
            // expected
        }
        try {
            statement.execute("INSERT INTO " + DatabaseCreator.FKCASCADE_TABLE
                    + " VALUES(4, 4, 'testInsert')");
           // TODO Foreign key functionality isn't supported 
           // fail("expecting SQLException");
        } catch (SQLException ex) {
            // expected
        }
    }

    /**
     * @tests InsertFunctionalityTest#testInsert3(). Tests INSERT ... SELECT
     *        functionality
     */
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "Functionality test: Tests INSERT ... SELECT functionality",
            method = "execute",
            args = {java.lang.String.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "Functionality test: Tests INSERT ... SELECT functionality",
            method = "executeQuery",
            args = {java.lang.String.class}
        )
    })
    @KnownFailure(" SQLite.Exception: error in prepare/compile")
    public void testInsert3() throws SQLException {
        DatabaseCreator.fillParentTable(conn);
        DatabaseCreator.fillFKStrictTable(conn);
        statement.execute("INSERT INTO " + DatabaseCreator.TEST_TABLE5
                + " SELECT id AS testId, value AS testValue " + "FROM "
                + DatabaseCreator.FKSTRICT_TABLE + " WHERE name_id = 1");
        ResultSet r = statement.executeQuery("SELECT COUNT(*) FROM "
                + DatabaseCreator.TEST_TABLE5);
        r.next();
        assertEquals("Should be 2 rows", 2, r.getInt(1));
        r.close();
    }

    /**
     * @tests InsertFunctionalityTest#testInsert4(). Tests INSERT ... SELECT
     *        with expressions in SELECT query
     */
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "Functionality test: Tests INSERT ... SELECT with expressions in SELECT query",
            method = "execute",
            args = {java.lang.String.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "Functionality test: Tests INSERT ... SELECT with expressions in SELECT query",
            method = "executeQuery",
            args = {java.lang.String.class}
        )
    })
    @KnownFailure(" SQLite.Exception: error in prepare/compile")
    public void testInsert4() throws SQLException {
        DatabaseCreator.fillSimpleTable1(conn);
        statement.execute("INSERT INTO " + DatabaseCreator.SIMPLE_TABLE2
                + " SELECT id, speed*10 AS speed, size-1 AS size FROM "
                + DatabaseCreator.SIMPLE_TABLE1);
        ResultSet r = statement.executeQuery("SELECT COUNT(*) FROM "
                + DatabaseCreator.SIMPLE_TABLE2 + " AS a JOIN "
                + DatabaseCreator.SIMPLE_TABLE1
                + " AS b ON a.speed = 10*b.speed AND a.size = b.size-1");
        r.next();
        assertEquals("Should be 2 rows", 2, r.getInt(1));
        r.close();
    }

    /**
     * @tests InsertFunctionalityTest#testInsert5(). Inserts multiple rows using
     *        UNION ALL
     */
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "Functionality test: Inserts multiple rows using UNION ALL",
            method = "execute",
            args = {java.lang.String.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "Functionality test: Inserts multiple rows using UNION ALL",
            method = "executeQuery",
            args = {java.lang.String.class}
        )
    })
    public void testInsert5() throws SQLException {
        statement.execute("INSERT INTO " + DatabaseCreator.TEST_TABLE5
                + " SELECT 1 as testId, 2 as testValue "
                + "UNION SELECT 2 as testId, 3 as testValue "
                + "UNION SELECT 3 as testId, 4 as testValue");
        ResultSet r = statement.executeQuery("SELECT COUNT(*) FROM "
                + DatabaseCreator.TEST_TABLE5);
        r.next();
        assertEquals("Should be 3 rows", 3, r.getInt(1));
        r.close();
    }

    /**
     * @tests InsertFunctionalityTest#testInsert6(). Tests INSERT with
     *        PreparedStatement
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Functionality test: Tests INSERT with PreparedStatement",
        method = "executeQuery",
        args = {java.lang.String.class}
    )
    @KnownFailure(" SQLite.Exception: error in prepare")
    public void testInsertPrepared() throws SQLException {
        PreparedStatement stat = conn.prepareStatement("INSERT INTO "
                + DatabaseCreator.TEST_TABLE5 + " VALUES(?, ?)");
        stat.setInt(1, 1);
        stat.setString(2, "1");
        stat.execute();
        stat.setInt(1, 2);
        stat.setString(2, "3");
        stat.execute();
        ResultSet r = statement.executeQuery("SELECT COUNT(*) FROM "
                + DatabaseCreator.TEST_TABLE5
                + " WHERE (testId = 1 AND testValue = '1') "
                + "OR (testId = 2 AND testValue = '3')");
        r.next();
        assertEquals("Incorrect number of records", 2, r.getInt(1));
        r.close();
        stat.close();
    }
}