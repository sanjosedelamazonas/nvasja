package org.sanjose.helper;

import jakarta.activation.DataSource;
import org.simplejavamail.api.email.AttachmentResource;

import java.io.*;

public class EmailAttachment {

    private String filename;
    private byte[] data;
    private String mimeType;

    public EmailAttachment(String filename, byte[] data, String mimeType) {
        this.filename = filename;
        this.data = data;
        this.mimeType = mimeType;
    }

    public EmailAttachment(String filename, byte[] data) {
        this.filename = filename;
        this.data = data;
        this.mimeType = "application/pdf";
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public AttachmentResource asAttachmentResource() {
        return new AttachmentResource(getFilename(), new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(getData());
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                ByteArrayOutputStream bout = new ByteArrayOutputStream(getData().length);
                bout.write(getData());
                return bout;
            }

            @Override
            public String getContentType() {
                return getMimeType();
            }

            @Override
            public String getName() {
                return getFilename();
            }
        });
    }
}
