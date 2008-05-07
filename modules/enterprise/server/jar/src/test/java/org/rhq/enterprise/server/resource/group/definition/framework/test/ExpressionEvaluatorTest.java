/*
 * RHQ Management Platform
 * Copyright (C) 2005-2008 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.enterprise.server.resource.group.definition.framework.test;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import org.rhq.enterprise.server.resource.group.definition.framework.ExpressionEvaluator;
import org.rhq.enterprise.server.test.AbstractEJB3Test;

public class ExpressionEvaluatorTest extends AbstractEJB3Test {
    private static final String IS_COMMITTED_SQL = " res.inventoryStatus = org.rhq.core.domain.resource.InventoryStatus.COMMITTED";

    private String[][] successTestCases = {
        { "resource.name = joseph",

        "SELECT res.id FROM Resource res " + "WHERE res.name = :arg1 AND " + IS_COMMITTED_SQL },

        { "resource.version = 1.0",

        "SELECT res.id FROM Resource res " + "WHERE res.version = :arg1 AND " + IS_COMMITTED_SQL },

        { "resource.type.plugin = harry",

        "SELECT res.id FROM Resource res " + "WHERE res.resourceType.plugin = :arg1 AND " + IS_COMMITTED_SQL },

        { "resource.type.name = sally",

        "SELECT res.id FROM Resource res " + "WHERE res.resourceType.name = :arg1 AND " + IS_COMMITTED_SQL },

        {
            "resource.pluginConfiguration[partition] = cluster-1",

            "SELECT res.id FROM Resource res " + "  JOIN res.pluginConfiguration pluginConf, PropertySimple simple "
                + " WHERE simple.name = :arg1 " + "   AND simple.stringValue = :arg2 AND " + IS_COMMITTED_SQL
                + "   AND simple.configuration = pluginConf" },

        {
            "resource.resourceConfiguration[partition] = cluster-1",

            "SELECT res.id FROM Resource res " + "  JOIN res.resourceConfiguration conf, PropertySimple simple "
                + " WHERE simple.name = :arg1 " + "   AND simple.stringValue = :arg2 AND " + IS_COMMITTED_SQL
                + "   AND simple.configuration = conf" },

        {
            "groupBy resource.type.plugin; " + "groupBy resource.type.name",

            "  SELECT res.resourceType.plugin, res.resourceType.name " + " FROM Resource res WHERE "
                + IS_COMMITTED_SQL + " GROUP BY res.resourceType.plugin, res.resourceType.name",

            "  SELECT res.id FROM Resource res " + "   WHERE res.resourceType.plugin = :arg1 "
                + "     AND res.resourceType.name = :arg2 AND " + IS_COMMITTED_SQL },

        {
            "groupBy resource.resourceConfiguration[partition-name]",

            "  SELECT simple.stringValue FROM Resource res "
                + "    JOIN res.resourceConfiguration conf, PropertySimple simple " + "   WHERE simple.name = :arg1 AND "
                + IS_COMMITTED_SQL + " AND simple.configuration = conf " + " GROUP BY simple.stringValue ",

            "SELECT res.id FROM Resource res " + "  JOIN res.resourceConfiguration conf, PropertySimple simple "
                + " WHERE simple.name = :arg1 " + "   AND simple.stringValue = :arg2 AND "
                + IS_COMMITTED_SQL + " AND simple.configuration = conf " },
        {
            "resource.type.name = Windows" + ";" + "resource.trait[Trait.osversion] = 5.1",

            "SELECT res.id FROM Resource res JOIN res.schedules sched JOIN sched.definition def, MeasurementDataTrait trait"
                + " WHERE res.resourceType.name = :arg1 AND def.name = :arg2 AND trait.value = :arg3 AND trait.schedule = sched AND "
                + IS_COMMITTED_SQL + " AND trait.id.timestamp = "
                + " (SELECT max(mdt.id.timestamp) FROM MeasurementDataTrait mdt WHERE sched.id = mdt.schedule.id) " } };

    @Test(groups = "integration.session")
    public void testWellFormedExpressions() throws Exception {
        List<Integer> suppressedCases = Arrays.asList();

        getTransactionManager().begin();
        try {
            for (int i = 0; i < successTestCases.length; i++) {
                if (suppressedCases.contains(i)) {
                    continue;
                }

                String inputExpressions = successTestCases[i][0];
                String expectedTopResult = successTestCases[i][1];
                String expectedGroupResult = "";
                if (successTestCases[i].length == 3) {
                    expectedGroupResult = successTestCases[i][2];
                }

                ExpressionEvaluator evaluator = new ExpressionEvaluator();
                evaluator.setTestMode(true); // to prevent actual query from happening
                for (String expression : inputExpressions.split(";")) {
                    evaluator.addExpression(expression.trim());
                }

                evaluator.execute(); // execute will compute the JPQL statements

                String actualTopResult = evaluator.getComputedJPQLStatement();
                String actualGroupResult = evaluator.getComputedJPQLGroupStatement();

                expectedTopResult = cleanUp(expectedTopResult);
                actualTopResult = cleanUp(actualTopResult);
                expectedGroupResult = cleanUp(expectedGroupResult);
                actualGroupResult = cleanUp(actualGroupResult);

                System.out.println("Expected[" + i + "]: \"" + expectedTopResult + "\"");
                System.out.println("Received[" + i + "]: \"" + actualTopResult + "\"");
                assert expectedTopResult.equalsIgnoreCase(actualTopResult)
                    && expectedGroupResult.equalsIgnoreCase(actualGroupResult) : "TestCase[" + i + "] = \""
                    + inputExpressions + "\" failed. \n" + "Expected Top Result: \"" + expectedTopResult + "\"\n"
                    + "Received Top Result: \"" + actualTopResult + "\"\n" + "Expected Group Result: \""
                    + expectedGroupResult + "\"\n" + "Received Group Result: \"" + actualGroupResult + "\"\n";
            }
        } finally {
            getTransactionManager().rollback();
        }
    }

    private String cleanUp(String result) {
        return result.replaceAll("\\s+", " ").trim();
    }
}