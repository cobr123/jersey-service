package com.example.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 */
public class Main {
    private static final String FILE_SERVER_ROOT_DIR = "FILE_SERVER_ROOT_DIR";
    private static final String FILE_SERVER_BASE_URI = "FILE_SERVER_BASE_URI";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(final String baseURI) {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig rc = new ResourceConfig().packages("com.example.rest").register(MultiPartFeature.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc);
    }

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final String baseURI = System.getProperty(FILE_SERVER_BASE_URI, "http://localhost:8080/");
        final HttpServer server = startServer(baseURI);
        System.out.println(String.format("Jersey app started with WADL available at "
                + "application.wadl%nHit enter to stop it...", baseURI));
        System.in.read();
    }

    public static String getRootDir() {
        return System.getProperty(FILE_SERVER_ROOT_DIR, "/home/cobr/file_server_root_dir/");
    }

    public static void setRootDir(final String rootDir) {
        System.setProperty(FILE_SERVER_ROOT_DIR, rootDir);
    }
}

