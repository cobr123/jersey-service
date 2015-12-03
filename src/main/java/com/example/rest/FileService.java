package com.example.rest;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Path("/files")
public class FileService {
    final private static ArrayList<LocalFile> cList = new ArrayList<>();

    static private void addFile(final LocalFile file) {
        synchronized (cList) {
            cList.add(file);
        }
    }

    static private ArrayList<LocalFile> getFileList() {
        synchronized (cList) {
            if (cList.isEmpty()) {
                try {
                    Files.walk(Paths.get(Main.getRootDir()))
                            .filter(Files::isRegularFile)
                            .forEach(f -> addFile(new LocalFile(f.toFile())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return cList;
        }
    }


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LocalFile> getAllFiles() {
        return getFileList().stream().collect(Collectors.toList());
    }

    @GET
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LocalFile> getFile(@PathParam("id") long id) {
        final List<LocalFile> list
                = getFileList().stream()
                .filter(c -> c.getId() == id)
                .collect(Collectors.toList());
        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") long id) {
        final Optional<LocalFile> match
                = getFileList().stream()
                .filter(c -> c.getId() == id)
                .findFirst();
        if (match.isPresent()) {
            final File file = new File(match.get().getPath());
            final Response.ResponseBuilder rb = Response.ok(file);
            rb.header("Content-Disposition", "attachment; filename=" + file.getName());
            final Response response = rb.build();
            return response;
        } else {
            return null;
        }
    }

    @GET
    @Path("/name/{name}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LocalFile> getFile(@PathParam("name") String name) {
        final List<LocalFile> list
                = getFileList().stream()
                .filter(c -> c.getName().contains(name))
                .collect(Collectors.toList());
        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream is, @FormDataParam("file") FormDataContentDisposition fileDisposition)  {
        try {
            final File tmpFile = File.createTempFile("uploadFile", "");
            tmpFile.deleteOnExit();
            FileUtils.copyInputStreamToFile(is, tmpFile);
            final long id = LocalFile.getCRC(tmpFile);
            final String file_name = fileDisposition.getFileName();
            final String path = Main.getRootDir() + file_name;

            final Optional<LocalFile> list
                    = getFileList().stream()
                    .filter(c -> c.getId() == id)
                    .findFirst();
            if (list.isPresent()) {
                return Response.ok("File already exists").build();
            }
            String tmpPath = path;
            long cnt = 1;
            while (new File(tmpPath).exists()) {
                tmpPath = path + "(" + cnt + ")";
                ++cnt;
            }
            final LocalFile file = new LocalFile(id, tmpPath, file_name);
            addFile(file);

            Files.copy(Paths.get(tmpFile.getPath()), Paths.get(file.getPath()));
        }catch (final IOException e){
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok("Data uploaded successfully").build();
    }
}