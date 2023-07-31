package com.smart.sparc;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final String boundary = "Volley-" + System.currentTimeMillis();
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";
    private Map<String, String> params;
    private Map<String, DataPart> dataPartParams;
    private Response.Listener<String> mListener;

    public MultipartRequest(int method, String url, Map<String, String> params,
                            Map<String, DataPart> dataPartParams, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.params = params;
        this.dataPartParams = dataPartParams;
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        // Add any headers you need here
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Add normal form data
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
            }

            // Add binary data (image) if available
            if (dataPartParams != null && !dataPartParams.isEmpty()) {
                for (Map.Entry<String, DataPart> entry : dataPartParams.entrySet()) {
                    buildDataPart(dos, entry.getValue(), entry.getKey());
                }
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new VolleyError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String paramName, String value) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(value + lineEnd);
    }

    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataPart, String paramName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + dataPart.getFileName() + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: " + dataPart.getMimeType() + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.write(dataPart.getData());
        dataOutputStream.writeBytes(lineEnd);
    }

    public static class DataPart {
        private String fileName;
        private byte[] data;
        private String mimeType;

        public DataPart(String fileName, byte[] data, String mimeType) {
            this.fileName = fileName;
            this.data = data;
            this.mimeType = mimeType;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getData() {
            return data;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
}
