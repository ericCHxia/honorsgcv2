package cn.honorsgc.honorv2.community.rpc;


import cn.honorsgc.honorv2.community.rpc.pb.HduExporterGrpc;
import cn.honorsgc.honorv2.community.rpc.pb.Service;
import cn.honorsgc.honorv2.jwt.JWTAuthenticationFilter;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.logging.Level;

import static cn.hutool.core.lang.Console.log;

@org.springframework.stereotype.Service
public class HduExporterClient {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final HduExporterGrpc.HduExporterBlockingStub blockingStub;

    public HduExporterClient(@Value("${cn.honorsgc.honorv2.community.rpc.address}") String serverAddress) {
        ManagedChannel channel = Grpc.newChannelBuilder(serverAddress, InsecureChannelCredentials.create())
                .build();
        blockingStub = HduExporterGrpc.newBlockingStub(channel);
    }

    public String ExportAttend(List<Long> communityIds) {
        Service.ExportAttendRequest request = Service.ExportAttendRequest.newBuilder()
                .addAllCommunityIds(communityIds)
                .build();
        Service.ExportAttendResponse response;
        try {
            response = blockingStub.exportAttend(request);
        } catch (StatusRuntimeException e) {
            logger.warn("RPC failed: " + e.getStatus());
            return "";
        }
        return response.getUrl();
    }

    public void ImportUsers(String url) {
        Service.ImportUsersRequest request = Service.ImportUsersRequest.newBuilder().setUrl(url).build();
        Service.ImportUsersResponse response;
        try {
            response = blockingStub.importUsers(request);
        } catch (StatusRuntimeException e) {
            log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            throw e;
        }
    }
}
