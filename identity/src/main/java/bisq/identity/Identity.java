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

package bisq.identity;

import bisq.common.proto.Proto;
import bisq.network.NetworkId;
import bisq.network.NetworkIdWithKeyPair;
import bisq.security.KeyPairProtoUtil;
import bisq.security.PubKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.security.KeyPair;

@Getter
@ToString
@EqualsAndHashCode
public final class Identity implements Proto {
    public static Identity from(String domainId, Identity identity) {
        return new Identity(domainId,
                identity.getNetworkId(),
                identity.getKeyPair());
    }

    private final String domainId;
    private final NetworkId networkId;
    private final KeyPair keyPair;

    public Identity(String domainId, NetworkId networkId, KeyPair keyPair) {
        this.domainId = domainId;
        this.networkId = networkId;
        this.keyPair = keyPair;
    }

    @Override
    public bisq.identity.protobuf.Identity toProto() {
        return bisq.identity.protobuf.Identity.newBuilder()
                .setDomainId(domainId)
                .setNetworkId(networkId.toProto())
                .setKeyPair(KeyPairProtoUtil.toProto(keyPair))
                .build();
    }

    public static Identity fromProto(bisq.identity.protobuf.Identity proto) {
        return new Identity(proto.getDomainId(),
                NetworkId.fromProto(proto.getNetworkId()),
                KeyPairProtoUtil.fromProto(proto.getKeyPair()));
    }

    public NetworkIdWithKeyPair getNodeIdAndKeyPair() {
        return new NetworkIdWithKeyPair(networkId, keyPair);
    }

    public String getNodeId() {
        return networkId.getNodeId();
    }

    public PubKey getPubKey() {
        return networkId.getPubKey();
    }

    public byte[] getPubKeyHash() {
        return networkId.getPubKey().getHash();
    }
}