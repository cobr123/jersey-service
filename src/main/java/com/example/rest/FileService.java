package com.example.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/files")
public class FileService {

    private final CopyOnWriteArrayList<LocalFile> cList = LocalFileList.getInstance();

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LocalFile> getAllFiles() {
        return cList.stream().collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LocalFile> getFile(@PathParam("id") long id) {
        final List<LocalFile> list
                = cList.stream()
                .filter(c -> c.getId() == id)
                .collect(Collectors.toList());
        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") long id) {
        Optional<LocalFile> match
                = cList.stream()
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

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormParam("file") InputStream is, @FormParam("name") String name) {
        final long id = LocalFile.getCRC(is);
        final String file_name = name;
        final String path = "./" + name;

        if (getFile(id).size() > 0) {
            String result = "File already exists";
            return Response.status(Response.Status.OK).entity(result).build();
        }
        final LocalFile file = new LocalFile(id, path, file_name);
        cList.add(file);

        try {
            saveFile(is, path);
            String result = "Successfully File Uploaded";
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }
    }

    private void saveFile(final InputStream inputStream, final String fileLocation) throws IOException {
        OutputStream outputStream = null;
        try {
            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(new File(fileLocation));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}