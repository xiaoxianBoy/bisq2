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
package bisq.restApi;

import bisq.restApi.endpoints.KeyPairApi;
import bisq.restApi.error.CustomExceptionMapper;
import bisq.restApi.error.StatusException;
import bisq.restApi.util.StaticFileHandler;
import bisq.application.DefaultApplicationService;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 * Application to start and config the rest service.
 * This creates and rest service at BASE_URL for clients to connect and for users to browse the documentation.
 * <p>
 * Swagger doc are available at <a href="http://localhost:8082/doc/v1/index.html">REST API documentation</a>
 */
@Slf4j
public class RestApiApplication extends ResourceConfig {
    public static final String BASE_URL = "http://localhost:8082/api/v1";

    @Getter
    protected final DefaultApplicationService applicationService;

    public RestApiApplication() {
        applicationService = new DefaultApplicationService(new String[]{"--appName=bisq2_API"});
        applicationService.initialize().join();
    }

    protected static HttpServer httpServer;

    public static void main(String[] args) throws Exception {
        startServer();
    }

    public static void stopServer() {
        httpServer.stop(2);
    }

    public static void startServer() throws Exception {
        // 'config' acts as application in jax-rs
        ResourceConfig app = new RestApiApplication()
                .register(CustomExceptionMapper.class)
                .register(StatusException.StatusExceptionMapper.class)
//                .register(ProtoWriter.class)
//                .register(KeyPairWriter.class)
                .register(KeyPairApi.class)
                .register(SwaggerResolution.class);

        httpServer = JdkHttpServerFactory.createHttpServer(URI.create(BASE_URL), app);
        httpServer.createContext("/doc", new StaticFileHandler("/doc/v1/"));

        // shut down hook
        Runtime.getRuntime().addShutdownHook(new Thread(RestApiApplication::stopServer));

        log.info("Server started at {}.", BASE_URL);

        // block and wait shut down signal, like CTRL+C
        Thread.currentThread().join();

        stopServer();
    }
}