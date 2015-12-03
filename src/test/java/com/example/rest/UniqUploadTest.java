package com.example.rest;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UniqUploadTest {

    private HttpServer server;
    private WebTarget target;
    private File file;

    @Before
    public void setUp() throws Exception {
        file = new File(Main.getRootDir() + "UniqUploadTest.txt");
        FileUtils.writeStringToFile(file, "UniqUploadTest");
        file.deleteOnExit();

        final String baseURI =  "http://localhost:8085/";
        // start the server
        server = Main.startServer(baseURI);
        // create the client
        final Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class)
                .build();

        target = client.target(baseURI);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testUniqUploadFile() throws IOException {
        final FileDataBodyPart filePart = new FileDataBodyPart("file", file);
        final FormDataMultiPart multipart = (FormDataMultiPart) new FormDataMultiPart().bodyPart(filePart);

        final Response response = target.path("/files").request().post(Entity.entity(multipart, multipart.getMediaType()));

        assertEquals(200, response.getStatus());

        final File server_file = new File(Main.getRootDir() + file.getName());
        file.delete();
        server_file.delete();

        final Optional<?> match
                = Files.walk(Paths.get(Main.getRootDir()))
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().startsWith("UniqUploadTest"))
                .findFirst();
        assertFalse(match.isPresent());
    }
}
