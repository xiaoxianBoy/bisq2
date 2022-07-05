/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.oracle;

import bisq.common.application.ModuleService;
import bisq.identity.IdentityService;
import bisq.network.NetworkService;
import bisq.oracle.marketprice.MarketPriceService;
import bisq.oracle.ots.OpenTimestampService;
import bisq.persistence.PersistenceService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Getter
public class OracleService implements ModuleService {
    @Getter
    public static class Config {
        private final com.typesafe.config.Config openTimestamp;
        private final com.typesafe.config.Config marketPrice;

        public Config(com.typesafe.config.Config openTimestamp, com.typesafe.config.Config marketPrice) {
            this.openTimestamp = openTimestamp;
            this.marketPrice = marketPrice;
        }

        public static Config from(com.typesafe.config.Config config) {
            return new Config(config.getConfig("openTimestamp"), config.getConfig("marketPrice"));
        }
    }

    private final OpenTimestampService openTimestampService;
    private final MarketPriceService marketPriceService;

    public OracleService(Config config, String applicationVersion, NetworkService networkService, IdentityService identityService, PersistenceService persistenceService) {
        openTimestampService = new OpenTimestampService(OpenTimestampService.Config.from(config.getOpenTimestamp()),
                identityService,
                persistenceService);
        marketPriceService = new MarketPriceService(MarketPriceService.Config.from(config.getMarketPrice()),
                networkService,
                applicationVersion);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ModuleService
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public CompletableFuture<Boolean> initialize() {
        log.info("initialize");
        return openTimestampService.initialize()
                .thenCompose(result -> marketPriceService.initialize());
    }

    public CompletableFuture<Boolean> shutdown() {
        log.info("shutdown");
        return CompletableFuture.completedFuture(true);
    }
}