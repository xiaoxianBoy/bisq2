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

package bisq.wallets.json_rpc;

import bisq.common.proto.Proto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class RpcConfig implements Proto {
    private String hostname;
    private int port;
    private String user;
    private String password;

    @Override
    public bisq.wallets.protobuf.RpcConfig toProto() {
        return bisq.wallets.protobuf.RpcConfig.newBuilder()
                .setHostname(hostname)
                .setPort(port)
                .setUser(user)
                .setPassword(password)
                .build();
    }

    public static RpcConfig fromProto(bisq.wallets.protobuf.RpcConfig proto) {
        return RpcConfig.builder()
                .hostname(proto.getHostname())
                .port(proto.getPort())
                .user(proto.getUser())
                .password(proto.getPassword())
                .build();
    }
}