/*-
 * ========================LICENSE_START=================================
 * flyway-core
 * ========================================================================
 * Copyright (C) 2010 - 2024 Red Gate Software Ltd
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.flywaydb.core.experimental;

import java.util.List;
import java.util.Map;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.sqlite.SQLiteDatabaseType;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;

public class ExperimentalModeUtils {

    public static boolean isExperimentalModeActivated() {
        return System.getenv("FLYWAY_EXPERIMENTAL") != null && System.getenv("FLYWAY_EXPERIMENTAL").equalsIgnoreCase("true");
    }

    public static boolean canUseExperimentalMode(final Configuration config,  String verb) {
        Map<String, List<String>> acceptedVerbs = Map.of("mongodb", List.of("info", "validate", "migrate", "clean", "baseline"),



            "SQLite", List.of("info", "validate", "migrate", "clean", "undo"));

        String database = getCurrentDatabase(config);

        if (database == null) {
            return false;
        }

        if (!acceptedVerbs.containsKey(database)) {
            return false;
        }

        return acceptedVerbs.get(database).contains(verb);
    }

    private static String getCurrentDatabase(final Configuration config) {
        if (config.getUrl().startsWith("mongodb")) {
            return "mongodb";
        }







        try (final var connectionFactory = new JdbcConnectionFactory(config.getDataSource(), config, null)) {
            if (connectionFactory.getDatabaseType() instanceof SQLiteDatabaseType) {
                return "SQLite";
            }
        }

        return null;
    }
}
